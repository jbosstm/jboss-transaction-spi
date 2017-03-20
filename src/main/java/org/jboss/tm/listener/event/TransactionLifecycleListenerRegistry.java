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
package org.jboss.tm.listener.event;

import javax.transaction.Transaction;
import java.util.EnumSet;

/**
 * A listener interface for registering interest in transaction related events. To obtain an instance of this interface
 * refer to {@link TransactionLifecycleListenerRegistryLocator}
 */
public interface TransactionLifecycleListenerRegistry {
    /**
     * Register for events relating to a transaction.
     *
     * @param transaction the transaction that the listener should get events for
     * @param listener the listener that should receive the events
     * @param typeFilter the types of event the listener should receive
     * @param <E> the enum type of the events
     * @throws TransactionLifecycleTypeNotSupported if the supplied transaction is invalid
     */
    <E extends Enum<E>> void addListener(Transaction transaction, TransactionLifecycleListener<E> listener, EnumSet<E> typeFilter)
            throws TransactionLifecycleTypeNotSupported;

    /**
     * Register for vetoable events relating to a transaction. This provides a mechanism for callers to veto certain transaction
     * related changes. See {@link VetoingTransactionLifecycleListener} for more detail.
     *
     * @param transaction the transaction that the listener should get vetoable events for
     * @param listener the listener that should be called to check whether an event should be vetoed
     * @param typeFilter the types of event the listener should receive
     * @param <E> the enum type of the events
     * @throws TransactionLifecycleTypeNotSupported if the supplied transaction is invalid
     */
    <E extends Enum<E>> void addListener(Transaction transaction, VetoingTransactionLifecycleListener<E> listener, EnumSet<E> typeFilter)
            throws TransactionLifecycleTypeNotSupported;
}
