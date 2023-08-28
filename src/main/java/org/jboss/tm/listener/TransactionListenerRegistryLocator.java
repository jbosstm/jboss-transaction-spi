/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.tm.listener;

import org.jboss.tm.TransactionManagerLocator;

import jakarta.transaction.TransactionManager;

/**
 * @deprecated WFTC (https://issues.jboss.org/projects/WFTC) replaces this functionality
 *
 * Locate an interface that implements a registration mechanism for transaction lifecycle events {@link EventType}
 */
@Deprecated
public class TransactionListenerRegistryLocator {
    /**
     *
     * @return an interface for registering interest in transaction lifecycle events
     * @throws TransactionListenerRegistryUnavailableException if no such interface has been registered with the SPI
     */
    public static TransactionListenerRegistry getTransactionListenerRegistry()
            throws TransactionListenerRegistryUnavailableException {
        TransactionManager tm = TransactionManagerLocator.locateTransactionManager();

        if (tm instanceof TransactionListenerRegistry) {
            return (TransactionListenerRegistry) tm;
        }

        throw new TransactionListenerRegistryUnavailableException();
    }
}
