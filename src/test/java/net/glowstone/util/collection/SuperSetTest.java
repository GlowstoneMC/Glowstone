package net.glowstone.util.collection;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Tests for {@link SuperIterator}.
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
        SuperSet<String> superSet = new SuperSet(sets);

        return superSet;
    }

    private void checkContains(Set<String> set, String prefix) {
        for (int i = 0; i < 2 * CASES; i++) {
            if (i < CASES) {
                // Half true
                Assert.assertTrue("SuperSet.contains returned false for " + prefix + " iteration " + i, set.contains(prefix + i));
            } else {
                // Half false
                Assert.assertFalse("SuperSet.contains returned true for set " + prefix + " iteration " + i, set.contains(prefix + i));
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
        Assert.assertEquals("Populated set size test failed", sets.size(), CASES * 2);
        sets.clear();
        Assert.assertEquals("Cleared set size test failed", sets.size(), 0);
    }

}
