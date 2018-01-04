/**
 * This package contains the core of Glowstone's threading model.
 *
 * <p>Glowstone has 4 groups of threads:
 * <ul>
 *     <li>World threads. One thread per world</li>
 *     <li>Event/scheduler thread. This thread contains all interaction with Bukkit API and
 *         synchronizes all other threads.</li>
 *     <li>Async task thread pool. Pool of threads used to execute async tasks</li>
 *     <li>Netty thread pool. This thread pool is used by Netty for network read/write</li>
 * </ul>
 *
 * <p>Whenever a thread wishes to call an event or perform other interactions with Bukkit API, it
 * calls the {@link net.glowstone.scheduler.GlowScheduler#syncIfNeeded(
 * java.util.concurrent.Callable)}.
 *
 * <p>The scheduler thread synchronizes the world threads, so that each world thread begins a tick
 * at the beginning of a scheduler pulse.
 *
 * <p>Operation order:
 * <ol>
 *  <li>Scheduler tick begins</li>
 *  <li>Pulse sessions</li>
 *  <li>run sync tasks, queue async tasks</li>
 *  <li>World tick for each world</li>
 *  <li>Run in-tick tasks</li>
 *  <li>await all worlds complete (in async task)</li>
 * </ol>
 */
package net.glowstone.scheduler;
