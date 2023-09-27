/*
   Copyright The Narayana Authors
   SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.tm.usertx;

import org.jboss.tm.listener.*;

import jakarta.transaction.Synchronization;
import java.util.EnumSet;

@Deprecated
public class TxListener implements Synchronization, TransactionListener {
    private volatile int assoc = 0; // we don't get a listener callback for the initial association
    private volatile boolean closed = false;
    private volatile int acCalled = 0;
    private TransactionListenerRegistry registry;
    private volatile int registrationCount = 2;
    private boolean hasEvents = false;

    TxListener(TransactionListenerRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void beforeCompletion() {
    }

    @Override
    public void afterCompletion(int status) {
        acCalled++;
        if (assoc == 0) {
            closed = true; // safe to close because the app thread has finished with the txn
        }
    }

    @Override
    public void onEvent(TransactionEvent transactionEvent) {
        hasEvents = true;

        if (transactionEvent.getTypes().contains(EventType.ASSOCIATED)) {
            if (registry != null && registrationCount > 0) {
                // test that callbacks can register listeners
                L2 listener = new L2();

                registrationCount -= 1;

                try {
                    registry.addListener(transactionEvent.getTransaction(), listener, EnumSet.allOf(EventType.class));
                } catch (TransactionTypeNotSupported e) {
                    throw new RuntimeException(e);
                }
            }
            assoc++;
        }

        if (transactionEvent.getTypes().contains(EventType.DISASSOCIATING)) {
            assoc--;
            assert (assoc == 0);
            if (acCalled == 1) {
                closed = true; // safe to close now that the AC has ran
            }
        }
    }

    public void reset() {
        assoc = 1;
        closed = false;
        acCalled = 0;
    }

    public boolean isClosed() {
        return closed;
    }

    /**
     * @return true if the txn has been disassociated and the AC has been called just once
     */
    public boolean shouldDisassoc() {
        return assoc == 0 && singleCallAC();
    }

    public boolean singleCallAC() {
        return acCalled == 1;
    }

    public boolean hasEvents() {
        return hasEvents;
    }

    public void clearEvents() {
        hasEvents = false;
    }

    private static class L2 implements TransactionListener {
        @Override
        public void onEvent(TransactionEvent transactionEvent) {
        }
    }
}
