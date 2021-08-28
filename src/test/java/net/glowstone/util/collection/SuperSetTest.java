package net.glowstone.util.collection;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link SuperSet}.
 */
public class SuperSetTest {

    private static final int CASES = 10;

    private void populateSet(Set<String> set, String prefix) {
        for (int i = 0; i < CASES; i++) {
            set.add(prefix + i);
        }
    }

    private Set<String> generateTestSet() {
        Set<String> setA = new HashSet<>();
        populateSet(setA, "A");

        Set<String> setB = new HashSet<>();
        populateSet(setB, "B");

        Set<String> duplicatedSetB = new HashSet<>();
        populateSet(duplicatedSetB, "B");

        List<Set<String>> sets = ImmutableList.of(setA, setB, duplicatedSetB);

        return new SuperSet<>(sets);
    }

    private void checkContains(Set<String> set, String prefix) {
        for (int i = 0; i < 2 * CASES; i++) {
            if (i < CASES) {
                // Half true
                assertThat("SuperSet.contains returned false for " + prefix + " iteration " + i,
                    set, hasItem(prefix + i));
            } else {
                // Half false
                assertThat("SuperSet.contains returned true for set " + prefix + " iteration " + i,
                    set, not(hasItem(prefix + i)));
            }
        }
    }

    @Test
    public void containsTest() {
        Set<String> sets = generateTestSet();

        checkContains(sets, "A");
        checkContains(sets, "B");
    }

    @Test
    public void sizeTest() {
        Set<String> sets = generateTestSet();
        assertThat("Populated set size test failed", CASES << 1, is(sets.size()));
        sets.clear();
        assertThat("Cleared set size test failed", 0, is(sets.size()));
    }

}
