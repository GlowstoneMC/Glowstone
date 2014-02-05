/**
 * This package contains the core of Glowstone's threading model.
 *
 * Glowstone has 3 groups of threads:
 * <ol>
 *     <li>World threads. One thread per world</li>
 *     <li>Event/scheduler thread. This thread contains all interaction with Bukkit API and synchronizes all other threads.</li>
 *     <li>Async task thread pool: Pool of threads used to execute async tasks</li>
 *     <li>Netty thread pool. This thread pool is used by Netty for network read/write</li>
 * </ol>
 *
 * Whenever a thread wishes to call an event or perform other interactions with Bukkit API,
 * it calls the {@link net.glowstone.scheduler.GlowScheduler#syncIfNeeded(java.util.concurrent.Callable)}
 *
 * The scheduler thread.synchronizes the world threads, so that each world thread begins a tick at the beginning of a scheduler pulse
 *
 * Operation order:
 * --- Scheduler tick begins ---
 * --- Pulse sessions ---
 * --- run sync tasks, queue async tasks --
 * --- World tick for each world ---
 * --- Run in-tick tasks
 * --- await all worlds complete (in async task) ---
 */
package net.glowstone.scheduler;
