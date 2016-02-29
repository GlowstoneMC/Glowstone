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
package net.glowstone.event;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.F_APPEND;
import static org.objectweb.asm.Opcodes.F_SAME;
import static org.objectweb.asm.Opcodes.F_SAME1;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_6;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.glowstone.event.filter.EventFilter;
import net.glowstone.event.filter.FilterFactory;
import net.glowstone.event.gen.DefineableClassLoader;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.spongepowered.api.event.Event;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

public final class ClassEventListenerFactory implements AnnotatedEventListener.Factory {

    private final AtomicInteger id = new AtomicInteger();
    private final DefineableClassLoader classLoader;
    private final LoadingCache<Method, Class<? extends AnnotatedEventListener>> cache = CacheBuilder.newBuilder()
            .concurrencyLevel(1)
            .weakValues()
            .build(new CacheLoader<Method, Class<? extends AnnotatedEventListener>>() {

                @Override
                public Class<? extends AnnotatedEventListener> load(Method method) throws Exception {
                    return createClass(method);
                }
            });
    private FilterFactory filterFactory;

    private final String targetPackage;

    public ClassEventListenerFactory(String targetPackage, FilterFactory factory, DefineableClassLoader classLoader) {
        checkNotNull(targetPackage, "targetPackage");
        checkArgument(!targetPackage.isEmpty(), "targetPackage cannot be empty");
        this.targetPackage = targetPackage + '.';
        this.filterFactory = checkNotNull(factory, "filterFactory");
        this.classLoader = checkNotNull(classLoader, "classLoader");
    }

    @Override
    public AnnotatedEventListener create(Object handle, Method method) throws Exception {
        return this.cache.get(method)
                .getConstructor(method.getDeclaringClass())
                .newInstance(handle);
    }

    private Class<? extends AnnotatedEventListener> createClass(Method method) throws Exception {
        Class<?> handle = method.getDeclaringClass();
        Class<?> eventClass = method.getParameterTypes()[0];
        String name = this.targetPackage
                + eventClass.getSimpleName() + "Listener_" + handle.getSimpleName() + '_' + method.getName()
                + this.id.incrementAndGet();
        Class<? extends EventFilter> filter = this.filterFactory.createFilter(method);

        if (filter == null && method.getParameterCount() != 1) {
            // basic sanity check
            throw new IllegalStateException("Failed to generate EventFilter for non trivial filtering operation.");
        }
        if (filter != null) {
            filter.newInstance();
            return this.classLoader.defineClass(name, generateClass(name, handle, method, eventClass, filter));
        } else {
            return this.classLoader.defineClass(name, generateClass(name, handle, method, eventClass));
        }
    }

    private static final String BASE_HANDLER = Type.getInternalName(AnnotatedEventListener.class);
    private static final String HANDLE_METHOD_DESCRIPTOR = '(' + Type.getDescriptor(Event.class) + ")V";
    private static final String FILTER_DESCRIPTOR = "(" + Type.getDescriptor(Event.class) + ")[Ljava/lang/Object;";

    private static byte[] generateClass(String name, Class<?> handle, Method method, Class<?> eventClass, Class<? extends EventFilter> filter) {
        name = name.replace('.', '/');
        final String handleName = Type.getInternalName(handle);
        final String handleDescriptor = Type.getDescriptor(handle);
        final String filterName = Type.getInternalName(filter);
        String eventDescriptor = "(";
        for (int i = 0; i < method.getParameterCount(); i++) {
            eventDescriptor += Type.getDescriptor(method.getParameterTypes()[i]);
        }
        eventDescriptor += ")V";

        ClassWriter cw = new ClassWriter(0);
        MethodVisitor mv;
        FieldVisitor fv;

        cw.visit(V1_6, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, name, null, BASE_HANDLER, null);
        {
            fv = cw.visitField(ACC_PRIVATE + ACC_STATIC, "FILTER", "L" + filterName + ";", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
            mv.visitLabel(l0);
            mv.visitLdcInsn(Type.getType(filter));
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "newInstance", "()Ljava/lang/Object;", false);
            mv.visitTypeInsn(CHECKCAST, filterName);
            mv.visitFieldInsn(PUTSTATIC, name, "FILTER", "L" + filterName + ";");
            mv.visitLabel(l1);
            Label l3 = new Label();
            mv.visitJumpInsn(GOTO, l3);
            mv.visitLabel(l2);
            mv.visitFrame(F_SAME1, 0, null, 1, new Object[] {"java/lang/Exception"});
            mv.visitVarInsn(ASTORE, 0);
            mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(SpongeEventManager.class), "getLogger", "()" + Type.getDescriptor(Logger.class), false);
            mv.visitLdcInsn("Error initializing event filter");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Logger.class), "error", "(Ljava/lang/String;Ljava/lang/Throwable;)V", true);
            Label l5 = new Label();
            mv.visitLabel(l5);
            mv.visitLineNumber(220, l5);
            mv.visitInsn(ACONST_NULL);
            mv.visitFieldInsn(PUTSTATIC, name, "FILTER", "L" + filterName + ";");
            mv.visitLabel(l3);
            mv.visitFrame(F_SAME, 0, null, 0, null);
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", '(' + handleDescriptor + ")V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, BASE_HANDLER, "<init>", "(Ljava/lang/Object;)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "handle", HANDLE_METHOD_DESCRIPTOR, null, new String[] {"java/lang/Exception"});
            mv.visitCode();
            mv.visitFieldInsn(GETSTATIC, name, "FILTER", "L" + filterName + ";");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(EventFilter.class), "filter", FILTER_DESCRIPTOR, true);
            mv.visitVarInsn(ASTORE, 2);
            mv.visitVarInsn(ALOAD, 2);
            Label l2 = new Label();
            mv.visitJumpInsn(IFNULL, l2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, name, "handle", "Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, handleName);
            for (int i = 0; i < method.getParameterCount(); i++) {
                mv.visitVarInsn(ALOAD, 2);
                mv.visitIntInsn(BIPUSH, i);
                mv.visitInsn(AALOAD);
                mv.visitTypeInsn(CHECKCAST, Type.getInternalName(method.getParameterTypes()[i]));
            }
            mv.visitMethodInsn(INVOKEVIRTUAL, handleName, method.getName(), eventDescriptor, false);
            mv.visitLabel(l2);
            mv.visitFrame(F_APPEND, 1, new Object[] {"[Ljava/lang/Object;"}, 0, null);
            mv.visitInsn(RETURN);
            mv.visitMaxs(4, 3);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }

    private static byte[] generateClass(String name, Class<?> handle, Method method, Class<?> eventClass) {
        name = name.replace('.', '/');
        final String handleName = Type.getInternalName(handle);
        final String handleDescriptor = Type.getDescriptor(handle);
        final String eventName = Type.getInternalName(eventClass);

        ClassWriter cw = new ClassWriter(0);
        MethodVisitor mv;

        cw.visit(V1_6, ACC_PUBLIC + ACC_FINAL + ACC_SUPER, name, null, BASE_HANDLER, null);

        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", '(' + handleDescriptor + ")V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, BASE_HANDLER, "<init>", "(Ljava/lang/Object;)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "handle", HANDLE_METHOD_DESCRIPTOR, null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, name, "handle", "Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, handleName);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, eventName);
            mv.visitMethodInsn(INVOKEVIRTUAL, handleName, method.getName(), "(L" + eventName + ";)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }

}
