package net.glowstone.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import java.util.Iterator;
import lombok.Getter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

@Getter
public class RectangularRegion {
    private final Location lowCorner;
    private final Location highCorner;
    private final int widthX;
    private final int widthY;
    private final int widthZ;

    /**
     * Creates a new region bounded by the two opposing locations.
     * @param from The first bounding corner.
     * @param to The second bounding corner.
     */
    public RectangularRegion(Location from, Location to) {
        Preconditions.checkArgument(
                from.getWorld() == to.getWorld(),
                "The given locations do not have matching worlds."
        );
        this.lowCorner = new Location(
                from.getWorld(),
                Double.min(from.getX(), to.getX()),
                Double.min(from.getY(), to.getY()),
                Double.min(from.getZ(), to.getZ())
        );
        this.highCorner = new Location(
                from.getWorld(),
                Double.max(from.getX(), to.getX()),
                Double.max(from.getY(), to.getY()),
                Double.max(from.getZ(), to.getZ())
        );
        this.widthX = highCorner.getBlockX() - lowCorner.getBlockX();
        this.widthY = highCorner.getBlockY() - lowCorner.getBlockY();
        this.widthZ = highCorner.getBlockZ() - lowCorner.getBlockZ();
    }

    /**
     * Creates a new region at the given corner with the same dimensions as this region.
     * @param lowCorner The corner to base the new region off of.
     * @return The new region.
     */
    public RectangularRegion moveTo(Location lowCorner) {
        return new RectangularRegion(
                lowCorner,
                new Location(
                        lowCorner.getWorld(),
                        lowCorner.getBlockX() + widthX,
                        lowCorner.getBlockY() + widthY,
                        lowCorner.getBlockZ() + widthZ
                )
        );
    }

    /**
     * Returns an iterable over all block locations within the region, with the iterable's
     * directionality determined by the given arguments.
     * @param directionX The direction of iteration along the X axis.
     * @param directionY The direction of iteration along the Y axis.
     * @param directionZ The direction of iteration along the Z axis.
     * @return The new iterable.
     */
    public Iterable<Location> blockLocations(IterationDirection directionX,
                                             IterationDirection directionY,
                                             IterationDirection directionZ) {
        return new LocationIterable(lowCorner, directionX.iterable(widthX),
                directionY.iterable(widthY), directionZ.iterable(widthZ));
    }

    private static class LocationIterable implements Iterable<Location> {
        private final Location lowCorner;
        private final Iterable<Integer> iterableX;
        private final Iterable<Integer> iterableY;
        private final Iterable<Integer> iterableZ;

        private LocationIterable(Location lowCorner, Iterable<Integer> iterableX,
                                 Iterable<Integer> iterableY, Iterable<Integer> iterableZ) {
            this.lowCorner = lowCorner;
            this.iterableX = iterableX;
            this.iterableY = iterableY;
            this.iterableZ = iterableZ;
        }

        @NotNull
        @Override
        public Iterator<Location> iterator() {
            return new LocationIterator(lowCorner, iterableX, iterableY, iterableZ);
        }
    }

    private static class LocationIterator extends AbstractIterator<Location> {
        private final Location lowCorner;
        private final Iterator<Integer> iteratorX;
        private final Iterable<Integer> iterableY;
        private final Iterable<Integer> iterableZ;

        private Iterator<Integer> iteratorY;
        private Iterator<Integer> iteratorZ;

        private int x;
        private int y;

        private LocationIterator(Location lowCorner, Iterable<Integer> iterableX,
                                 Iterable<Integer> iterableY, Iterable<Integer> iterableZ) {
            this.lowCorner = lowCorner;

            this.iterableY = iterableY;
            this.iterableZ = iterableZ;

            this.iteratorX = iterableX.iterator();
            this.iteratorY = iterableY.iterator();
            this.iteratorZ = iterableZ.iterator();

            this.x = this.iteratorX.next();
            this.y = this.iteratorY.next();
        }

        @Override
        protected Location computeNext() {
            if (iteratorZ.hasNext()) {
                return getLocation();
            }
            if (iteratorY.hasNext()) {
                iteratorZ = iterableZ.iterator();
                y = iteratorY.next();
                return getLocation();
            }
            if (iteratorX.hasNext()) {
                iteratorY = iterableY.iterator();
                iteratorZ = iterableZ.iterator();
                x = iteratorX.next();
                y = iteratorY.next();
                return getLocation();
            }
            return endOfData();
        }

        private Location getLocation() {
            return new Location(lowCorner.getWorld(), lowCorner.getBlockX() + x,
                    lowCorner.getBlockY() + y, lowCorner.getBlockZ() + iteratorZ.next());
        }
    }

    public enum IterationDirection {
        FORWARDS {
            @Override
            public Iterable<Integer> iterable(int max) {
                return new ForwardsAxisIterable(max);
            }
        },
        BACKWARDS {
            @Override
            public Iterable<Integer> iterable(int max) {
                return new BackwardsAxisIterable(max);
            }
        };

        public abstract Iterable<Integer> iterable(int max);
    }

    private static class ForwardsAxisIterable implements Iterable<Integer> {
        private final int max;

        private ForwardsAxisIterable(int max) {
            this.max = max;
        }

        @NotNull
        @Override
        public Iterator<Integer> iterator() {
            return new ForwardsAxisIterator(max);
        }
    }

    private static class ForwardsAxisIterator extends AbstractIterator<Integer> {
        private final int max;
        private int current;

        public ForwardsAxisIterator(int max) {
            this.max = max;
            this.current = 0;
        }

        @Override
        protected Integer computeNext() {
            if (current <= max) {
                // Could use a post-increment operator here, but this is a bit more clear in
                // getting the meaning across.
                int retval = current;
                current++;
                return retval;
            }
            return endOfData();
        }
    }

    private static class BackwardsAxisIterable implements Iterable<Integer> {
        private final int max;

        private BackwardsAxisIterable(int max) {
            this.max = max;
        }

        @NotNull
        @Override
        public Iterator<Integer> iterator() {
            return new BackwardsAxisIterator(max);
        }
    }

    private static class BackwardsAxisIterator extends AbstractIterator<Integer> {
        private int current;

        public BackwardsAxisIterator(int length) {
            this.current = length;
        }

        @Override
        protected Integer computeNext() {
            if (current >= 0) {
                // Could use a post-decrement operator here, but this is a bit more clear in
                // getting the meaning across.
                int retval = current;
                current--;
                return retval;
            }
            return endOfData();
        }
    }
}
