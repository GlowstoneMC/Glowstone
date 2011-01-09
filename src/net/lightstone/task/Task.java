package net.lightstone.task;

public abstract class Task {

	private int ticks, counter;
	private boolean running = true;

	public Task(int ticks) {
		this.ticks = ticks;
		this.counter = ticks;
	}

	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

	public void stop() {
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isStopped() {
		return !running;
	}

	public abstract void execute();

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
