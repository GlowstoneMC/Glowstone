package net.glowstone.util.collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.testng.annotations.Test;

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
            assertThat("Iterator hasNext prematurely returned false", iterator.hasNext(), is(true));
            assertThat("Iterator returned an invalid object", iterator.next(), is(prefix + i));
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

        assertThat("Expected hasNext = false", iterator.hasNext(), is(false));
    }

}
