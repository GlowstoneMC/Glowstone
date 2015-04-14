package net.glowstone.shiny.event;

// originally based on https://github.com/SpongePowered/SpongeVanilla/blob/master/src/main/java/org/spongepowered/granite/event/GraniteEventFactory.java
/*
 * This file is part of Granite, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <http://github.com/SpongePowered>
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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.AbstractEvent;
import org.spongepowered.api.event.state.StateEvent;
import org.spongepowered.api.util.event.factory.ClassGeneratorProvider;
import org.spongepowered.api.util.event.factory.EventFactory;
import org.spongepowered.api.util.event.factory.FactoryProvider;
import org.spongepowered.api.util.event.factory.NullPolicy;

import java.util.Map;

public class GraniteEventFactory {

    private static final FactoryProvider factoryProvider;
    private static final LoadingCache<Class<?>, EventFactory<?>> factories;

    static {
        factoryProvider = new ClassGeneratorProvider(GraniteEventFactory.class.getPackage().getName() + ".impl");
        factoryProvider.setNullPolicy(NullPolicy.NON_NULL_BY_DEFAULT);

        factories = CacheBuilder.newBuilder()
                .build(new CacheLoader<Class<?>, EventFactory<?>>() {
                    @Override
                    public EventFactory<?> load(Class<?> type) {
                        return factoryProvider.create(type, AbstractEvent.class);
                    }
                });
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static <T extends StateEvent> T createStateEvent(Class<T> type, Game game) {
        Map<String, Object> values = Maps.newHashMapWithExpectedSize(1);
        values.put("game", game);
        return (T) factories.getUnchecked(type).apply(values);
    }

}
