/*
   Copyright The Narayana Authors
   SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.tm.listener;

/**
 * @deprecated WFTC (https://issues.jboss.org/projects/WFTC) replaces this functionality
 *
 * An exception type to indicate that the actual transaction type passed into
 * {@link TransactionListenerRegistry#addListener(jakarta.transaction.Transaction, TransactionListener, java.util.EnumSet)}
 * does not support TSR resources
 * (see {@link jakarta.transaction.TransactionSynchronizationRegistry#putResource(Object, Object)})
 */
@Deprecated
public class TransactionTypeNotSupported extends Exception {
    public TransactionTypeNotSupported(String message) {
        super(message);
    }
}
