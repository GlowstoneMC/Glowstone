package net.glowstone.shiny.guice;

// based on https://github.com/SpongePowered/Sponge/blob/master/src/main/java/org/spongepowered/mod/guice/ConfigDirAnnotation.java

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

import org.spongepowered.api.service.config.ConfigDir;

import java.lang.annotation.Annotation;

// This is strange, but required for Guice and annotations with values.
class ConfigDirAnnotation implements ConfigDir {

    boolean shared;

    ConfigDirAnnotation(boolean isShared) {
        this.shared = isShared;
    }

    @Override
    public boolean sharedRoot() {
        return this.shared;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return ConfigDir.class;
    }

    // See Javadocs for java.lang.annotation.Annotation for specification of equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof ConfigDir)) {
            return false;
        }

        ConfigDir that = (ConfigDir) o;

        return sharedRoot() == that.sharedRoot();
    }

    @Override
    public int hashCode() {
        return (127 * "sharedRoot".hashCode()) ^ Boolean.valueOf(sharedRoot()).hashCode();
    }

    @Override
    public String toString() {
        return "@org.spongepowered.api.service.config.ConfigDir("
                + "sharedRoot=" + this.shared
                + ')';
    }
}
