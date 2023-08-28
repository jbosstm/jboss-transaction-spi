/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.tm.listener;

/**
 * @deprecated WFTC (https://issues.jboss.org/projects/WFTC) replaces this functionality
 *
 * A listener callback for transaction related events
 */
@Deprecated
public interface TransactionListener {
    /**
     * The callback for notifying registered listeners of a transaction related event. Use
     * {@link TransactionListenerRegistry#addListener(jakarta.transaction.Transaction, TransactionListener, java.util.EnumSet)}
     * to register for transaction related events
     * @param transactionEvent indication of what kind of change has occurred
     */
    void onEvent(TransactionEvent transactionEvent);
}
