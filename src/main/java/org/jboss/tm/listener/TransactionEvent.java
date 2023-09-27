/*
   Copyright The Narayana Authors
   SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.tm.listener;

import jakarta.transaction.Transaction;
import java.util.EnumSet;

/**
 * @deprecated WFTC (https://issues.jboss.org/projects/WFTC) replaces this functionality
 */
@Deprecated
public class TransactionEvent {
    private Transaction transaction;
    private EnumSet<EventType> types;

    public TransactionEvent(Transaction transaction,EnumSet<EventType> types) {
        this.transaction = transaction;
        this.types = types;
    }

    /**
     *
     * @return the transaction that the event relates to
     */
    public Transaction getTransaction() {
        return transaction;
    }

    /**
     * @return indication of what kind of changes have occurred
     */
    public EnumSet<EventType> getTypes() { return types; }
}
