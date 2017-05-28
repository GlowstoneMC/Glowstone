package net.glowstone.util;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class IsFloatCloseTo extends TypeSafeMatcher<Float> {
    private final float delta;
    private final float value;

    public IsFloatCloseTo(float value, float error) {
        this.delta = error;
        this.value = value;
    }

    @Override
    protected boolean matchesSafely(Float item) {
        return this.actualDelta(item) <= 0.0D;
    }

    @Override
    public void describeMismatchSafely(Float item, Description mismatchDescription) {
        mismatchDescription.appendValue(item).appendText(" differed by ").appendValue(this.actualDelta(item));
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a numeric value within ").appendValue(this.delta).appendText(" of ").appendValue(this.value);
    }

    private float actualDelta(Float item) {
        return Math.abs(item - this.value) - this.delta;
    }

    @Factory
    public static Matcher<Float> closeTo(float operand, float error) {
        return new IsFloatCloseTo(operand, error);
    }
}
