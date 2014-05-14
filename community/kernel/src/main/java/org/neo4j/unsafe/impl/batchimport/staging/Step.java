/**
 * Copyright (c) 2002-2014 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.unsafe.impl.batchimport.staging;

import org.neo4j.unsafe.impl.batchimport.stats.StepStats;

/**
 * One step in {@link Stage}, where a {@link Stage} is a sequence of steps. Each step works on batches.
 * Batches are typically received from an upstream step, or in this step. If there are more steps
 * {@link #setDownstream(Step) downstream} then processed batches are passed down. Each step has maximum
 * "work-ahead" size where it awaits its downstream step beyond that number.
 *
 * @param <T> the type of batch objects received from upstream.
 */
public interface Step<T>
{
    /**
     * @return name of this step.
     */
    String name();

    /**
     * Receives a batch from upstream, queues it for processing.
     *
     * @param ticket ticket associates with the batch. Tickets are generated by producing steps and must follow
     * each batch all the way through a stage.
     * @param batch the batch object to queue for processing.
     * @return how long it time (millis) was spent waiting for a spot in the queue.
     */
    long receive( long ticket, T batch );

    /**
     * @return statistics about this step at this point in time.
     */
    StepStats stats();

    /**
     * Called by upstream to let this step know that it will not send any more batches.
     */
    void endOfUpstream();

    /**
     * @return {@code true} if this step has received AND processed all batches from upstream, or in
     * the case of a producer, that this step has produced all batches.
     */
    boolean isCompleted();

    /**
     * Called by the {@link Stage} when setting up the stage. This will form a pipeline of steps,
     * making up the stage.
     * @param downstreamStep {@link Step} to send batches to downstream.
     */
    void setDownstream( Step<?> downstreamStep );

    /**
     * Receives a panic, asking to shut down as soon as possible.
     * @param cause cause for the panic.
     */
    void receivePanic( Throwable cause );
}
