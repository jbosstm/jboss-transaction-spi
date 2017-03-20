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
package org.jboss.tm.usertx;

import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionSynchronizationRegistryImple;
import com.arjuna.ats.jbossatx.jta.TransactionManagerDelegate;

import org.jboss.tm.listener.event.AssociationLifecycleEventType;
import org.jboss.tm.listener.event.TransactionLifecycleListener;
import org.jboss.tm.listener.event.TransactionLifecycleListenerRegistry;
import org.jboss.tm.listener.event.TransactionLifecycleListenerRegistryLocator;
import org.jboss.tm.listener.event.TransactionLifecycleListenerRegistryUnavailableException;
import org.jboss.tm.listener.event.TransactionLifecycleTypeNotSupported;
import org.jboss.tm.usertx.client.ServerVMClientUserTransaction;
import org.junit.Test;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import java.util.EnumSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TransactionLifecycleListenerRegistryTest {
    private enum CompletionType {
        CMT,
        BMTCOMMIT,
        BMTROLLBACK,
        CMTSUSPEND
    }

    @Test
    public void testResume() throws SystemException, InvalidTransactionException {
        TransactionManager tm = new TransactionManagerDelegate();
        tm.resume(null); // JBTM-2385 used to cause an NPE 
    }

    private EnumSet<AssociationLifecycleEventType> runTxn(TransactionManager tm) throws SystemException, TransactionLifecycleTypeNotSupported, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        ServerVMClientUserTransaction userTransaction = new ServerVMClientUserTransaction(tm);
        TransactionLifecycleListenerRegistry listenerRegistration = (TransactionLifecycleListenerRegistry) tm;
        final EnumSet<AssociationLifecycleEventType> log = EnumSet.noneOf(AssociationLifecycleEventType.class);

        TransactionLifecycleListener<AssociationLifecycleEventType> listener = transactionEvent -> {
            log.add((AssociationLifecycleEventType) transactionEvent.getReason());

            System.out.printf("TransactionEvent: %s%n", transactionEvent.getReason());
        };

        tm.suspend(); // clean the thread

        userTransaction.begin();
        listenerRegistration.addListener(tm.getTransaction(), listener, EnumSet.allOf(AssociationLifecycleEventType.class));
        userTransaction.commit();

        return log;
    }

    @Test
    public void testIllegalCommit() throws SystemException, TransactionLifecycleTypeNotSupported, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        TransactionManager tm = new TMDelegate();

        runTxn(tm);

        try {
            tm.commit();
            fail("Commit finished transaction should have failed");
        } catch (IllegalStateException ignore) {
        }
    }

    @Test
    public void testIllegalRollback() throws SystemException, TransactionLifecycleTypeNotSupported, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        TransactionManager tm = new TMDelegate();

        runTxn(tm);

        try {
            tm.rollback();
            fail("Rollback finished transaction should have failed");
        } catch (IllegalStateException ignore) {
        }
    }

    @Test
    public void testLifecycle() throws SystemException, TransactionLifecycleTypeNotSupported, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        TransactionManager tm = new TMDelegate();
        EnumSet<AssociationLifecycleEventType> log = runTxn(tm);

        assertTrue(log.containsAll(EnumSet.of(AssociationLifecycleEventType.ASSOCIATED, AssociationLifecycleEventType.DISASSOCIATING)));
    }

    @Test
    public void test() throws SystemException, NotSupportedException, RollbackException, TransactionLifecycleTypeNotSupported, InterruptedException, InvalidTransactionException, HeuristicRollbackException, HeuristicMixedException {

        TransactionManager tm = new TMDelegate();
        ServerVMClientUserTransaction userTransaction = new ServerVMClientUserTransaction(tm);

        userTransaction.setTransactionTimeout(1);

       for (CompletionType completionType : CompletionType.values()) {
            TransactionLifecycleListenerRegistry listenerRegistration = (TransactionLifecycleListenerRegistry) tm;

            userTransaction.begin();

            // The TSR for interposed synchronizations
            final TransactionSynchronizationRegistry tsr = new TransactionSynchronizationRegistryImple();
            final TxLifecycleListener listener = new TxLifecycleListener(listenerRegistration);

            if (completionType != CompletionType.CMTSUSPEND) {
                tsr.registerInterposedSynchronization(listener);
            } else {
                tm.getTransaction().registerSynchronization(listener);
            }

            listenerRegistration.addListener(tm.getTransaction(), listener, EnumSet.allOf(AssociationLifecycleEventType.class));

            if (completionType == CompletionType.CMTSUSPEND) {
                Transaction suspended = tm.suspend();
                Thread.sleep(2000);
                assertTrue(listener.shouldDisassoc());
                tm.resume(suspended);
            } else {
                Thread.sleep(2000);
                assertFalse(listener.shouldDisassoc());
            }

            assertTrue(listener.singleCallAC());

            assertTrue(Status.STATUS_ROLLEDBACK == userTransaction.getStatus());

            // Apps need to call rollback anyway to clear the association from
            // the
            // thread
            // https://community.jboss.org/thread/92489
            if (completionType == CompletionType.BMTCOMMIT) {
                try {
                    userTransaction.commit();
                    fail("Should not have been able to commit for completion type " + completionType);
                } catch (RollbackException ignore) {
                }
            } else if (completionType == CompletionType.BMTROLLBACK) {
                userTransaction.rollback();
            } else if (completionType == CompletionType.CMT) {
                // This is possible in CMT mode
                // If they did check the status, it is still expected that a CMT
                // calls suspend at least when a tx is marked as completed to
                // clear
                // it from the thread
                tm.suspend();
            }

            assertTrue(listener.singleCallAC());
            if (completionType == CompletionType.CMTSUSPEND) {
                assertFalse("should not have disassociated for completion type " + completionType, listener.shouldDisassoc());
            } else {
                assertTrue("should have disassociated for completion type " + completionType, listener.shouldDisassoc());
            }

            assertTrue("txn should have closed for completion type " + completionType, listener.isClosed());
        }
    }
}
