package net.glowstone.io;

import java.util.Vector;

public class StorageQueue extends Thread {
    private final Vector<StorageOperation> pending = new Vector<StorageOperation>();
    private final Vector<ParallelTaskThread> active = new Vector<ParallelTaskThread>();
    private boolean running = false;

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            synchronized (pending) {
                if (pending.size() > 0) {
                    StorageOperation op = pending.remove(0);
                    if (op != null) {
                        op.run();
                    }
                }
            }
        }
        running = false;
    }

    public synchronized void queue(StorageOperation op) {
        if (!running) {
                throw new IllegalStateException(
                        "Cannot queue tasks while thread is not running");
        }
        if (op.isParallel()) {
            synchronized (active) {
                ParallelTaskThread thread = new ParallelTaskThread(op);
                if (!active.contains(thread)) {
                   thread.start();
                } else if (op.queueMultiple()) {
                    active.get(active.indexOf(thread)).addOperation(op);
                }
            }
        } else {
            synchronized (pending) {
                if (!pending.contains(op) || op.queueMultiple()) {
                    pending.add(op);
                }
            }
        }
    }


    public void reset() {
        end();
        start();
    }

    public void end() {
        interrupt();
        running = false;
        pending.clear();
        synchronized (active) {
            for (ParallelTaskThread thread : active) {
                thread.interrupt();
            }
        }
    }

    @Override
    public void start() {
        running = true;
        super.start();
    }

    class ParallelTaskThread extends Thread {
        private final Vector<StorageOperation> ops = new Vector<StorageOperation>();
        public ParallelTaskThread(StorageOperation op) {
            ops.add(op);
        }

        @Override
        public void run() {
            active.add(this);
            try {
                while (!interrupted() && ops.size() > 0) {
                    StorageOperation op = ops.remove(0);
                    if (op != null) {
                            op.run();
                    }
                }
            } finally {
                active.remove(this);
            }
        }

        public void addOperation(StorageOperation op) {
            if (!isAlive() || isInterrupted())
                throw new IllegalStateException("Thread is not running");
            ops.add(op);
        }

        @Override
        public synchronized boolean equals(Object other) {
            if (!(other instanceof ParallelTaskThread)) {
                return false;
            }
            return ops.size() > 0 && ((ParallelTaskThread) other).ops.size() > 0 && ops.get(0).equals(((ParallelTaskThread) other).ops.get(0));
        }
    }
}
