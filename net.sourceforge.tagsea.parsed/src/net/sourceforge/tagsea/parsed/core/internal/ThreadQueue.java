/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada
 * and IBM Corporation. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package net.sourceforge.tagsea.parsed.core.internal;

import java.util.LinkedList;

import net.sourceforge.tagsea.parsed.ParsedWaypointPlugin;

/**
 * Queues up runnables to run in a single thread.
 * @author Del Myers
 *
 */
public class ThreadQueue {
	private static final Thread T = new Thread("Thread queue") {
		public void run() {
			while (true) {
				Runnable[] currentQueue;
				synchronized (ThreadQueue.queue) {
					while (ThreadQueue.queue.size() == 0) {
						try {
							ThreadQueue.queue.wait();
						} catch (InterruptedException e) {
							interrupt();
						}
					}
					currentQueue = ThreadQueue.queue.toArray(new Runnable[ThreadQueue.queue.size()]);
					ThreadQueue.queue.clear();
				}
				for (Runnable r : currentQueue) {
					try {
						r.run();
					} catch (Exception e) {
						ParsedWaypointPlugin.getDefault().log(e);
					}
				}
			}
		}
	};
	private static final LinkedList<Runnable> queue = new LinkedList<Runnable>();
	public static void queue(Runnable r) {
		if (!T.isAlive()) {
			T.start();
		}
		synchronized (queue) {
			queue.add(r);
			queue.notify();
		}
	}

}
