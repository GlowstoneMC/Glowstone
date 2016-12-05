package net.glowstone.util.loot;

import lombok.Data;
import net.glowstone.util.ReflectionProcessor;

import java.util.Optional;

@Data
public class ReflectiveValue<T> {

    private final Optional<String> line;
    private final Optional<T> value;

    public ReflectiveValue(T value) {
        this.value = Optional.of(value);
        this.line = Optional.empty();
    }

    public ReflectiveValue(String line) {
        this.value = Optional.empty();
        this.line = Optional.of(line);
    }

    public Object process(Object... context) {
        if (line.isPresent()) {
            return new ReflectionProcessor(line.get(), context).process();
        } else {
            return value.get();
        }
    }
}
