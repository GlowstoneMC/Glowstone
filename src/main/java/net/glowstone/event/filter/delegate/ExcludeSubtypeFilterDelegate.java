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
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.PUTFIELD;

import com.google.common.collect.Sets;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.spongepowered.api.event.filter.type.Exclude;

public class ExcludeSubtypeFilterDelegate extends SubtypeFilterDelegate {

    private final Exclude anno;

    public ExcludeSubtypeFilterDelegate(Exclude anno) {
        this.anno = anno;
    }

    @Override
    public void writeCtor(String name, ClassWriter cw, MethodVisitor mv) {
        // Initialize the class set
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Sets.class), "newHashSet", "()Ljava/util/HashSet;",
                false);
        mv.visitFieldInsn(PUTFIELD, name, "classes", "Ljava/util/Set;");

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, name, "classes", "Ljava/util/Set;");
        for (Class<?> cls : this.anno.value()) {
            // dup the field
            mv.visitInsn(DUP);
            // ldc the type
            mv.visitLdcInsn(Type.getType(cls));
            // add it to the set
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "add", "(Ljava/lang/Object;)Z", true);
            // pop the boolean result leaving just the original field
            mv.visitInsn(POP);
        }
        // pop the original field ref
        mv.visitInsn(POP);
    }

    @Override
    protected int getJumpOp() {
        return Opcodes.IFEQ;
    }

}
