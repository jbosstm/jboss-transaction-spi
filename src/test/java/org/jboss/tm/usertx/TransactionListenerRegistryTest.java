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

import com.arjuna.ats.jbossatx.jta.TransactionManagerDelegate;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;
import jakarta.transaction.TransactionManager;
import org.jboss.tm.usertx.client.ServerVMClientUserTransaction;
import org.junit.Test;

import static org.junit.Assert.*;

public class TransactionListenerRegistryTest {
    @Test
    public void testResume() throws SystemException, InvalidTransactionException {
        TransactionManager tm = new TransactionManagerDelegate();
        tm.resume(null); // JBTM-2385 used to cause an NPE 
    }

    private void runTxn(TransactionManager tm) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        ServerVMClientUserTransaction userTransaction = new ServerVMClientUserTransaction(tm);

        tm.suspend(); // clean the thread

        userTransaction.begin();
        userTransaction.commit();
    }

    @Test
    public void testIllegalCommit() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        TransactionManager tm = new TransactionManagerDelegate();

        runTxn(tm);

        try {
            tm.commit();
            fail("Commit finished transaction should have failed");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    @Test
    public void testIllegalRollback() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        TransactionManager tm = new TransactionManagerDelegate();

        runTxn(tm);

        try {
            tm.rollback();
            fail("Rollback finished transaction should have failed");
        } catch (IllegalStateException e) {
            // expected
        }
    }
}
