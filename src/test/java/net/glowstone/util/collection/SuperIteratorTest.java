package net.glowstone.util.collection;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Tests for {@link SuperIterator}.
 */
public class SuperIteratorTest {

    private void populateList(List<String> list, String prefix) {
        for (int i = 0; i < 10; i++) {
            list.add(prefix + i);
        }
    }

    private void checkIterator(Iterator<String> iterator, String prefix) {
        for (int i = 0; i < 10; i++) {
            Assert.assertTrue("Iterator hasNext prematurely returned false", iterator.hasNext());
            Assert.assertEquals("Iterator returned an invalid object", prefix + i, iterator.next());
        }
    }

    @Test
    public void test() {
        List<String> listA = new ArrayList<>();
        populateList(listA, "A");

        List<String> listB = new ArrayList<>();
        populateList(listB, "B");

        List<List<String>> lists = ImmutableList.of(listA, listB);
        Iterator<String> iterator = new SuperIterator(lists);

        checkIterator(iterator, "A");
        checkIterator(iterator, "B");

        Assert.assertFalse("Expected hasNext = false", iterator.hasNext());
    }

}
