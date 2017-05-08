package net.glowstone.util;

public class Typeless<T> {
    private T t;

    public void s(T t) {
        this.t = t;
    }
    public T g() {
        return t;
    }
}
