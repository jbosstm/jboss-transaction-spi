/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.tm.listener;

import org.jboss.tm.listener.event.TransactionLifecycleEventType;

/**
 * @deprecated use {@link TransactionLifecycleEventType}} instead
 *
 * An indication of an event that effects the current transaction
 */
@Deprecated
public enum EventType {
    /**
     * a transaction is associated with the current thread
     */
    ASSOCIATED,

    /**
     * a transaction is just about to be disassociated from the current thread. This
     * can happen because the thread is about to commit, roll back or suspend the
     * transaction that is currently associated with the thread.
     */
    DISASSOCIATING
}
