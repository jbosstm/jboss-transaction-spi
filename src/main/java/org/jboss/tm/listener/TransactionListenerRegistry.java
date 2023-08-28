/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.tm.listener;

import jakarta.transaction.Transaction;
import java.util.EnumSet;

/**
 * @deprecated WFTC (https://issues.jboss.org/projects/WFTC) replaces this functionality
 *
 * A listener interface for transaction related events. To obtain an instance of this interface
 * refer to {@link org.jboss.tm.listener.TransactionListenerRegistryLocator}
 */
@Deprecated
public interface TransactionListenerRegistry {
    /**
     * @param transaction the transaction that the caller is interested in receiving events for (must not be null)
     * @param listener an object that will be invoked when events of type {@link EventType}
     *                 occur
     * @param types a collection of events that the listener is interested in
     * @throws TransactionTypeNotSupported if the passed in transaction type does not support the listeners feature
     */
    void addListener (Transaction transaction, TransactionListener listener, EnumSet<EventType> types) throws TransactionTypeNotSupported;
}
