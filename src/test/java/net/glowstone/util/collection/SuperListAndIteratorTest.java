package net.glowstone.util.collection;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Tests for {@link SuperList}, {@link SuperIterator} and {@link SuperListIterator}.
 */
public class SuperListAndIteratorTest {

    private static final int PARENT_COUNT = 3;
    private static final int ELEMENTS_PER_PARENT = 10;

    /**
     * Creates a new SuperList with PARENT_COUNT parents and PARENT_COUNT * ELEMENTS_PER_PARENT integers ranging from 0 to PARENT_COUNT * ELEMENTS_PER_PARENT - 1.
     */
    private SuperList<Integer> generateTestList() {
        List<List<Integer>> lists = new ArrayList<>();

        for (int listId = 0; listId < PARENT_COUNT; listId++) {
            List<Integer> list = new ArrayList<>();

            for (int num = 0; num < ELEMENTS_PER_PARENT; num++) {
                list.add(listId * ELEMENTS_PER_PARENT + num);
            }

            lists.add(list);
        }

        return new SuperList<>(lists);
    }

    @Test
    public void sizeTest() {
        SuperList<Integer> list = generateTestList();
        Assert.assertEquals("Parent count test failed", list.getParents().size(), PARENT_COUNT);
        Assert.assertEquals("Populated size test failed", PARENT_COUNT * ELEMENTS_PER_PARENT, list.size());
        list.clear();
        Assert.assertEquals("List couldn't be cleared", 0, list.size());
    }

    @Test
    public void iterationTest() {
        Iterator<Integer> it = generateTestList().iterator();
        for (int i = 0; i < PARENT_COUNT * ELEMENTS_PER_PARENT; i++) {
            Assert.assertEquals("Iterator hasNext returns false before reaching the end", true, it.hasNext());
            Assert.assertEquals("Mismatch on position " + i, i, it.next().intValue());
        }
        Assert.assertEquals("Iterator hasNext returns true after reaching the end", false, it.hasNext());
    }

    @Test
    public void iteratorRemovalTest() {
        SuperList<Integer> list = generateTestList();
        Iterator<Integer> it = list.iterator();
        for (int i = 0; i < PARENT_COUNT * ELEMENTS_PER_PARENT; i++) {
            it.next();
            it.remove();
            Assert.assertEquals("Item at index " + i + " wasn't properly removed", PARENT_COUNT * ELEMENTS_PER_PARENT - i - 1, list.size());
        }
    }

    @Test
    public void addTest() {
        List<Integer> parentList = new ArrayList<>();
        List<Integer> superList = new SuperList<>(ImmutableList.of(parentList));

        for (int i = 0; i < ELEMENTS_PER_PARENT; i++) {
            superList.add(i);
        }

        for (int i = 0; i < ELEMENTS_PER_PARENT; i++) {
            Assert.assertEquals("Could not add element " + i, superList.get(i).intValue(), i);
        }
    }
}
