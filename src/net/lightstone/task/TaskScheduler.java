package net.lightstone.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A class which schedules {@link Task}s.
 * @author Graham Edgecombe
 */
public final class TaskScheduler {

	/**
	 * The number of milliseconds between pulses.
	 */
	private static final int PULSE_EVERY = 200;

	/**
	 * The scheduled executor service which backs this scheduler.
	 */
	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	
	/**
	 * A list of new tasks to be added.
	 */
	private final List<Task> newTasks = new ArrayList<Task>();
	
	/**
	 * A list of active tasks.
	 */
	private final List<Task> tasks = new ArrayList<Task>();

	/**
	 * Creates a new task scheduler.
	 */
	public TaskScheduler() {
		executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				pulse();
			}
		}, 0, PULSE_EVERY, TimeUnit.MILLISECONDS);
	}

	/**
	 * Schedules the specified task.
	 * @param task The task.
	 */
	public void schedule(Task task) {
		synchronized (newTasks) {
			newTasks.add(task);
		}
	}

	/**
	 * Adds new tasks and updates existing tasks, removing them if necessary.
	 */
	private void pulse() {
		synchronized (newTasks) {
			for (Task task : newTasks) {
				tasks.add(task);
			}
			newTasks.clear();
		}

		for (Iterator<Task> it = tasks.iterator(); it.hasNext(); ) {
			Task task = it.next();
			if (!task.pulse()) {
				it.remove();
			}
		}
	}

}
