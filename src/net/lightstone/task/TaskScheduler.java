package net.lightstone.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class TaskScheduler {

	private static final int PULSE_EVERY = 200;

	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private final List<Task> newTasks = new ArrayList<Task>();
	private final List<Task> tasks = new ArrayList<Task>();

	public TaskScheduler() {
		executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				pulse();
			}
		}, 0, PULSE_EVERY, TimeUnit.MILLISECONDS);
	}

	public void schedule(Task task) {
		synchronized (newTasks) {
			newTasks.add(task);
		}
	}

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
