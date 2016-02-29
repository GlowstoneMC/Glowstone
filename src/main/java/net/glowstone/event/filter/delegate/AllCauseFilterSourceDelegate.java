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
package net.glowstone.event.filter.delegate;

import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.filter.cause.All;

import java.lang.reflect.Parameter;

public class AllCauseFilterSourceDelegate extends CauseFilterSourceDelegate {

    private final All anno;

    public AllCauseFilterSourceDelegate(All anno) {
        this.anno = anno;
    }

    @Override
    protected void insertCauseCall(MethodVisitor mv, Parameter param, Class<?> targetType) {
        if (targetType.isArray()) {
            mv.visitLdcInsn(Type.getType(targetType.getComponentType()));
        } else {
            throw new IllegalStateException(
                    "Parameter " + param.getName() + " is marked with @All but is not an array type");
        }
        mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Cause.class), "allOf",
                "(Ljava/lang/Class;)Ljava/util/List;", false);
    }

    @Override
    protected void insertTransform(MethodVisitor mv, Parameter param, Class<?> targetType, int local) {
        if (this.anno.ignoreEmpty()) {
            // Check that the list is not empty
            mv.visitVarInsn(ALOAD, local);
            Label success = new Label();
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "isEmpty", "()Z", true);
            mv.visitJumpInsn(IFEQ, success);
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
            mv.visitLabel(success);
        }

        mv.visitVarInsn(ALOAD, local);
        mv.visitInsn(ICONST_0);
        mv.visitTypeInsn(ANEWARRAY, Type.getInternalName(targetType.getComponentType()));
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "toArray", "([Ljava/lang/Object;)[Ljava/lang/Object;", true);
        mv.visitVarInsn(ASTORE, local);
    }

}
