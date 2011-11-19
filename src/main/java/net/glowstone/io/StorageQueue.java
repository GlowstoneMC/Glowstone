package net.glowstone.io;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class StorageQueue extends Thread {
    private final BlockingQueue<StorageOperation> pending = new LinkedBlockingQueue<StorageOperation>();
    private final List<ParallelTaskThread> active = new ArrayList<ParallelTaskThread>();
    private boolean running = false;

    @Override
    public void run() {
        StorageOperation op;
        while (!isInterrupted()) {
            try {
                if ((op = pending.take()) != null) {
                    op.run();
                }
            } catch (InterruptedException e) {
                break;
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
                if (op.queueMultiple() || !pending.contains(op)) {
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
        private final Queue<StorageOperation> ops = new LinkedList<StorageOperation>();
        public ParallelTaskThread(StorageOperation op) {
            ops.add(op);
        }

        @Override
        public void run() {
            active.add(this);
            try {
                StorageOperation op;
                while (!isInterrupted() && (op = ops.poll()) != null) {
                    op.run();
                }
            } finally {
                active.remove(this);
            }
        }

        public void addOperation(StorageOperation op) {
            if (!isAlive() || isInterrupted())
                throw new IllegalStateException("Thread is not running");
            ops.offer(op);
        }

        @Override
        public synchronized boolean equals(Object other) {
            if (!(other instanceof ParallelTaskThread)) {
                return false;
            }
            StorageOperation op = ops.peek();
            return op != null && op.equals(((ParallelTaskThread) other).ops.peek());
        }
    }
}
