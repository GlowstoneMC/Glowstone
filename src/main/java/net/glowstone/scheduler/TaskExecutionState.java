package net.glowstone.scheduler;

/**
 * Execution states for tasks.
 */
enum TaskExecutionState {
    /**
     * This task should be run this tick.
     */
    RUN,
    /**
     * This task will run later, keep checking.
     */
    WAIT,
    /**
     * This task will never run again, stop trying.
     */
    STOP,
}
