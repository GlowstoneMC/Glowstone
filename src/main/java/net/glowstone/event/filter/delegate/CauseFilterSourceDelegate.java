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

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.util.Tuple;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public abstract class CauseFilterSourceDelegate implements ParameterFilterSourceDelegate {

    @Override
    public Tuple<Integer, Integer> write(ClassWriter cw, MethodVisitor mv, Method method, Parameter param, int local) {
        // Get the cause
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Event.class), "getCause",
                "()" + Type.getDescriptor(Cause.class), true);

        Class<?> targetType = param.getType();

        insertCauseCall(mv, param, targetType);
        int paramLocal = local++;
        mv.visitVarInsn(ASTORE, paramLocal);

        insertTransform(mv, param, targetType, paramLocal);

        return new Tuple<>(local, paramLocal);
    }

    protected abstract void insertCauseCall(MethodVisitor mv, Parameter param, Class<?> targetType);

    protected abstract void insertTransform(MethodVisitor mv, Parameter param, Class<?> targetType, int local);
}
