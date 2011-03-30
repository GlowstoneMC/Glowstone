package net.lightstone.task;

/**
 * Represents a task which is executed periodically.
 * @author Graham Edgecombe
 */
public abstract class Task {

	/**
	 * The number of ticks between calls to the {@link #execute()} method.
	 */
	private int ticks;

	/**
	 * The current count of remaining ticks.
	 */
	private int counter;

	/**
	 * A flag which indicates if this task is running.
	 */
	private boolean running = true;

	/**
	 * Creates a new task with the specified number of ticks between
	 * consecutive calls to {@link #execute()}.
	 * @param ticks The number of ticks.
	 */
	public Task(int ticks) {
		this.ticks = ticks;
		this.counter = ticks;
	}

	/**
	 * Sets the number of ticks.
	 * @param ticks The number of ticks.
	 */
	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

	/**
	 * Stops this task.
	 */
	public void stop() {
		running = false;
	}

	/**
	 * Checks if this task is running.
	 * @return {@code true} if so, {@code false} if not.
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Checks if this task has stopped.
	 * @return {@code true} if so, {@code false} if not.
	 */
	public boolean isStopped() {
		return !running;
	}

	/**
	 * Called when this task should be executed.
	 */
	public abstract void execute();

	/**
	 * Called every 'pulse' which is around 200ms in Minecraft. This method
	 * updates the counters and calls {@link #execute()} if necessary.
	 * @return The {@link #isRunning()} flag.
	 */
	boolean pulse() {
		if (!running)
			return false;

		if (--counter == 0) {
			counter = ticks;
			execute();
		}

		return running;
	}

}
