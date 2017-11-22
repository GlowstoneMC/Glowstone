package net.glowstone.framework;

public @interface GlowProperty {
    boolean interp() default false;
    boolean replicated() default false;
    String repCallback() default "";
    boolean saved() default false;
    String saveCallback() default "";
}
