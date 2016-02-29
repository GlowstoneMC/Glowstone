/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.glowstone.event.filter;

import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_6;

import com.google.common.collect.Lists;
import net.glowstone.event.filter.delegate.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.cause.After;
import org.spongepowered.api.event.filter.cause.All;
import org.spongepowered.api.event.filter.cause.Before;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Last;
import org.spongepowered.api.event.filter.cause.Named;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.filter.data.Has;
import org.spongepowered.api.event.filter.data.Supports;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.filter.type.Include;
import org.spongepowered.api.util.Tuple;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

public class FilterGenerator {

    public static final boolean FILTER_DEBUG = Boolean.parseBoolean(System.getProperty("sponge.filter.debug", "false"));

    public static FilterGenerator getInstance() {
        return Holder.INSTANCE;
    }

    private FilterGenerator() {
    }

    public byte[] generateClass(String name, Method method) {
        name = name.replace('.', '/');
        Parameter[] params = method.getParameters();

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        MethodVisitor mv;

        cw.visit(V1_6, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, name, null, "java/lang/Object",
                new String[] {Type.getInternalName(EventFilter.class)});

        SubtypeFilterDelegate sfilter = null;
        List<FilterDelegate> additional = Lists.newArrayList();
        for (Annotation anno : method.getAnnotations()) {
            Object obj = filterFromAnnotation(anno.annotationType());
            if (obj == null) {
                continue;
            }
            if (obj instanceof SubtypeFilter) {
                if (sfilter != null) {
                    throw new IllegalStateException("Cannot have both @Include and @Exclude annotations present at once");
                }
                sfilter = ((SubtypeFilter) obj).getDelegate(anno);
            } else if (obj instanceof EventTypeFilter) {
                additional.add(((EventTypeFilter) obj).getDelegate(anno));
            }
        }

        if (sfilter != null) {
            sfilter.createFields(cw);
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            if (sfilter != null) {
                sfilter.writeCtor(name, cw, mv);
            }
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "filter", "(" + Type.getDescriptor(Event.class) + ")[Ljava/lang/Object;", null,
                    null);
            mv.visitCode();
            // index of the next available local variable
            int local = 2;
            if (sfilter != null) {
                local = sfilter.write(name, cw, mv, method, local);
            }
            for (FilterDelegate eventFilter : additional) {
                local = eventFilter.write(name, cw, mv, method, local);
            }

            // local var indices of the parameters values
            int[] plocals = new int[params.length - 1];
            for (int i = 1; i < params.length; i++) {
                Parameter param = params[i];
                ParameterFilterSourceDelegate source = null;
                List<ParameterFilterDelegate> paramFilters = Lists.newArrayList();
                for (Annotation anno : param.getAnnotations()) {
                    Object obj = filterFromAnnotation(anno.annotationType());
                    if (obj == null) {
                        continue;
                    }
                    if (obj instanceof ParameterSource) {
                        if (source != null) {
                            throw new IllegalStateException("Cannot have multiple parameter filter source annotations (for " + param.getName() + ")");
                        }
                        source = ((ParameterSource) obj).getDelegate(anno);
                    } else if (obj instanceof ParameterFilter) {
                        paramFilters.add(((ParameterFilter) obj).getDelegate(anno));
                    }
                }
                if (source == null) {
                    throw new IllegalStateException("Cannot have additional parameters filters without a source (for " + param.getName() + ")");
                }
                if (source instanceof AllCauseFilterSourceDelegate && !paramFilters.isEmpty()) {
                    // TODO until better handling for filtering arrays is added
                    throw new IllegalStateException(
                            "Cannot have additional parameters filters without an array source (for " + param.getName() + ")");
                }
                Tuple<Integer, Integer> localState = source.write(cw, mv, method, param, local);
                local = localState.getFirst();
                plocals[i - 1] = localState.getSecond();

                for (ParameterFilterDelegate paramFilter : paramFilters) {
                    paramFilter.write(cw, mv, method, param, plocals[i - 1]);
                }
            }

            // create the return array
            if (params.length == 1) {
                mv.visitInsn(ICONST_1);
            } else {
                mv.visitIntInsn(BIPUSH, params.length);
            }
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            // load the event into the array
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(AASTORE);
            // load all the params into the array
            for (int i = 1; i < params.length; i++) {
                mv.visitInsn(DUP);
                mv.visitIntInsn(BIPUSH, i);
                mv.visitVarInsn(ALOAD, plocals[i - 1]);
                mv.visitInsn(AASTORE);
            }
            mv.visitInsn(ARETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        cw.visitEnd();
        byte[] data = cw.toByteArray();

        if (FILTER_DEBUG) {
            File outDir = new File(".sponge.debug.out");
            File outFile = new File(outDir, name + ".class");
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }
            try (FileOutputStream out = new FileOutputStream(outFile)) {
                out.write(data);
            } catch (IOException ignored) {
                ignored.printStackTrace();
            }
        }

        return data;
    }

    private static Object filterFromAnnotation(Class<? extends Annotation> cls) {
        Object filter;
        if ((filter = SubtypeFilter.valueOf(cls)) != null)
            return filter;
        if ((filter = EventTypeFilter.valueOf(cls)) != null)
            return filter;
        if ((filter = ParameterSource.valueOf(cls)) != null)
            return filter;
        if ((filter = ParameterFilter.valueOf(cls)) != null)
            return filter;
        return null;
    }

    private static enum SubtypeFilter {
        INCLUDE(Include.class),
        EXCLUDE(Exclude.class),;

        private final Class<? extends Annotation> cls;

        private SubtypeFilter(Class<? extends Annotation> cls) {
            this.cls = cls;
        }

        public SubtypeFilterDelegate getDelegate(Annotation anno) {
            if (this == INCLUDE) {
                return new IncludeSubtypeFilterDelegate((Include) anno);
            } else if (this == EXCLUDE) {
                return new ExcludeSubtypeFilterDelegate((Exclude) anno);
            }
            throw new UnsupportedOperationException();
        }

        public static SubtypeFilter valueOf(Class<? extends Annotation> cls) {
            for (SubtypeFilter value : values()) {
                if (value.cls.equals(cls)) {
                    return value;
                }
            }
            return null;
        }
    }

    private static enum EventTypeFilter {
        CANCELLATION(IsCancelled.class),;

        private final Class<? extends Annotation> cls;

        private EventTypeFilter(Class<? extends Annotation> cls) {
            this.cls = cls;
        }

        public FilterDelegate getDelegate(Annotation anno) {
            if (this == CANCELLATION) {
                return new CancellationEventFilterDelegate((IsCancelled) anno);
            }
            throw new UnsupportedOperationException();
        }

        public static EventTypeFilter valueOf(Class<? extends Annotation> cls) {
            for (EventTypeFilter value : values()) {
                if (value.cls.equals(cls)) {
                    return value;
                }
            }
            return null;
        }
    }

    private static enum ParameterSource {
        CAUSE_FIRST(First.class),
        CAUSE_LAST(Last.class),
        CAUSE_BEFORE(Before.class),
        CAUSE_AFTER(After.class),
        CAUSE_ALL(All.class),
        CAUSE_ROOT(Root.class),
        CAUSE_NAMED(Named.class);

        private final Class<? extends Annotation> cls;

        private ParameterSource(Class<? extends Annotation> cls) {
            this.cls = cls;
        }

        public ParameterFilterSourceDelegate getDelegate(Annotation anno) {
            if (this == CAUSE_FIRST) {
                return new FirstCauseFilterSourceDelegate((First) anno);
            }
            if (this == CAUSE_LAST) {
                return new LastCauseFilterSourceDelegate((Last) anno);
            }
            if (this == CAUSE_BEFORE) {
                return new BeforeCauseFilterSourceDelegate((Before) anno);
            }
            if (this == CAUSE_AFTER) {
                return new AfterCauseFilterSourceDelegate((After) anno);
            }
            if (this == CAUSE_ALL) {
                return new AllCauseFilterSourceDelegate((All) anno);
            }
            if (this == CAUSE_ROOT) {
                return new RootCauseFilterSourceDelegate((Root) anno);
            }
            if (this == CAUSE_NAMED) {
                return new NamedCauseFilterSourceDelegate((Named) anno);
            }
            throw new UnsupportedOperationException();
        }

        public static ParameterSource valueOf(Class<? extends Annotation> cls) {
            for (ParameterSource value : values()) {
                if (value.cls.equals(cls)) {
                    return value;
                }
            }
            return null;
        }
    }

    private static enum ParameterFilter {
        SUPPORTS(Supports.class),
        HAS(Has.class),;

        private final Class<? extends Annotation> cls;

        private ParameterFilter(Class<? extends Annotation> cls) {
            this.cls = cls;
        }

        public ParameterFilterDelegate getDelegate(Annotation anno) {
            if (this == SUPPORTS) {
                return new SupportsDataFilterDelegate((Supports) anno);
            }
            if (this == HAS) {
                return new HasDataFilterDelegate((Has) anno);
            }
            throw new UnsupportedOperationException();
        }

        public static ParameterFilter valueOf(Class<? extends Annotation> cls) {
            for (ParameterFilter value : values()) {
                if (value.cls.equals(cls)) {
                    return value;
                }
            }
            return null;
        }
    }

    private static final class Holder {

        private static final FilterGenerator INSTANCE = new FilterGenerator();
    }

}
