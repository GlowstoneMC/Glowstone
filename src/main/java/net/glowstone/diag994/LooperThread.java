package net.glowstone.diag994;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;

/**
 * Thread that loops a given task until interrupted (or until JVM shutdown, if it {@link
 * #isDaemon() is a daemon thread}), with the iterations being transactional.
 */
public class LooperThread extends Thread {

  protected final AtomicLong finishedIterations = new AtomicLong(0);
  /**
   * The thread holds this lock whenever it is being serialized or cloned or is running {@link
   * #iterate()} called by {@link #run()}.
   */
  protected final Lock lock = new ReentrantLock(true);
  protected final Condition endOfIteration = lock.newCondition();
  /**
   * The {@link Runnable} that was passed into this thread's constructor, if any.
   */
  @Nullable
  protected Runnable target;

  /**
   * Constructs a net.glowstone.diag994.LooperThread with the given name and target. {@code target} should only be null if
   * called from a subclass that overrides {@link #iterate()}.
   * @param target If not null, the target this thread will run in {@link #iterate()}.
   * @param name the thread name
   */
  public LooperThread(@Nullable final Runnable target, final String name) {
    super(target, name);
    this.target = target;
  }

  /**
   * Constructs a net.glowstone.diag994.LooperThread that belongs to the given {@link ThreadGroup} and has the given
   * target. {@code target} should only be null if called from a subclass that overrides {@link
   * #iterate()}.
   * @param group The ThreadGroup this thread will belong to.
   * @param target If not null, the target this thread will run in {@link #iterate()}.
   */
  public LooperThread(final ThreadGroup group, @Nullable final Runnable target) {
    super(group, target);
    this.target = target;
  }

  /**
   * <p>Constructs a net.glowstone.diag994.LooperThread with the given name and target, belonging to the given {@link
   * ThreadGroup} and having the given preferred stack size. {@code target} should only be null if
   * called from a subclass that overrides {@link #iterate()}.</p>
   * <p>See {@link Thread#Thread(ThreadGroup, Runnable, String, long)} for caveats about
   * specifying the stack size.</p>
   * @param group The ThreadGroup this thread will belong to.
   * @param target If not null, the target this thread will run in {@link #iterate()}.
   * @param name the thread name
   * @param stackSize the desired stack size for the new thread, or zero to indicate that this
   *     parameter is to be ignored.
   */
  public LooperThread(final ThreadGroup group, @Nullable final Runnable target, final String name,
      final long stackSize) {
    super(group, target, name, stackSize);
    this.target = target;
  }

  /**
   * Constructs a net.glowstone.diag994.LooperThread with the given name and belonging to the given {@link ThreadGroup}.
   * Protected because it does not set a target, and thus should only be used in subclasses that
   * override {@link #iterate()}.
   * @param group The ThreadGroup this thread will belong to.
   * @param name the thread name
   */
  protected LooperThread(final ThreadGroup group, final String name) {
    super(group, name);
  }

  /**
   * Constructs a net.glowstone.diag994.LooperThread with the given target. {@code target} should only be null if called
   * from a subclass that overrides {@link #iterate()}.
   * @param target If not null, the target this thread will run in {@link #iterate()}.
   */
  public LooperThread(@Nullable final Runnable target) {
    super(target);
    this.target = target;
  }

  /**
   * Constructs a net.glowstone.diag994.LooperThread with the given name and target, belonging to the given {@link
   * ThreadGroup}. {@code target} should only be null if called from a subclass that overrides
   * {@link #iterate()}.
   * @param group The ThreadGroup this thread will belong to.
   * @param target If not null, the target this thread will run in {@link #iterate()}.
   * @param name the thread name
   */
  public LooperThread(final ThreadGroup group, @Nullable final Runnable target, final String name) {
    super(group, target, name);
    this.target = target;
  }

  /**
   * Constructs a net.glowstone.diag994.LooperThread with the given name. Protected because it does not set a target, and
   * thus should only be used in subclasses that override {@link #iterate()}.
   * @param name the thread name
   */
  protected LooperThread(final String name) {
    super(name);
  }

  /**
   * Constructs a net.glowstone.diag994.LooperThread with all properties as defaults. Protected because it does not set a
   * target, and thus should only be used in subclasses that override {@link #iterate()}.
   */
  protected LooperThread() {
  }

  /**
   * The task that will be iterated until it returns false. Cannot be abstract for serialization
   * reasons, but must be overridden in subclasses if they are instantiated without a target {@link
   * Runnable}.
   * @return true if this thread should iterate again.
   * @throws InterruptedException if interrupted in mid-execution.
   * @throws UnsupportedOperationException if this method has not been overridden and {@link
   *     #target} was not set to non-null during construction.
   */
  @SuppressWarnings("BooleanMethodIsAlwaysInverted") protected boolean iterate()
      throws InterruptedException {
    if (target == null) {
      throw new UnsupportedOperationException("This method should be overridden, or else this "
          + "thread should have been created with a Serializable target!");
    } else {
      target.run();
      return true;
    }
  }

  /**
   * Runs {@link #iterate()} until either it returns false or this thread is interrupted.
   */
  @Override public final void run() {
    while (true) {
      try {
        lock.lockInterruptibly();
        try {
          final boolean shouldContinue = iterate();
          finishedIterations.getAndIncrement();
          if (!shouldContinue) {
            break;
          }
        } finally {
          endOfIteration.signalAll();
          lock.unlock();
        }
      } catch (final InterruptedException ignored) {
        interrupt();
        break;
      }
    }
  }

  /**
   * Wait for the next iteration to finish, with a timeout. May wait longer in the event of a
   * spurious wakeup.
   * @param time the maximum time to wait
   * @param unit the time unit of the {@code time} argument
   * @return {@code false}  the waiting time detectably elapsed before an iteration finished, else
   *     {@code true}
   * @throws InterruptedException if thrown by {@link Condition#await(long, TimeUnit)}
   */
  public boolean awaitIteration(final long time, final TimeUnit unit) throws InterruptedException {
    final long previousFinishedIterations = finishedIterations.get();
    lock.lock();
    try {
      while (!isInterrupted() && (getState() != State.TERMINATED) && (finishedIterations.get()
          == previousFinishedIterations)) {
        endOfIteration.await(time, unit);
      }
      return finishedIterations.get() != previousFinishedIterations;
    } finally {
      lock.unlock();
    }
  }
}
