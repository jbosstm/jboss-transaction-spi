/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.tm.listener;

/**
 * @deprecated WFTC (https://issues.jboss.org/projects/WFTC) replaces this functionality
 *
 * An exception type thrown by {@link org.jboss.tm.listener.TransactionListenerRegistryLocator} if the registry
 * is unavailable
 */
@Deprecated
public class TransactionListenerRegistryUnavailableException extends Exception {
     public TransactionListenerRegistryUnavailableException(String message) {
        super(message);
    }

    public TransactionListenerRegistryUnavailableException() {
    }
}
