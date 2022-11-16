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

import com.arjuna.ats.arjuna.common.RecoveryEnvironmentBean;
import com.arjuna.ats.jta.common.JTAEnvironmentBean;
import com.arjuna.ats.jta.common.jtaPropertyManager;
import com.arjuna.ats.jta.utils.JNDIManager;
import com.arjuna.common.internal.util.propertyservice.BeanPopulator;

import jakarta.transaction.RollbackException;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transaction;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.TransactionSynchronizationRegistry;
import jakarta.transaction.UserTransaction;
import org.jboss.tm.TransactionManagerLocator;
import org.jboss.tm.usertx.client.ServerVMClientUserTransaction;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.naming.InitialContext;
import java.util.Hashtable;

import static org.junit.Assert.*;

public class SPIUnitTest {
    private static InitialContext initialContext;

    public static void initEnv() throws Exception {
        // TODO add a JTS verson of the test
        final JTAEnvironmentBean jtaEnvironmentBean = jtaPropertyManager.getJTAEnvironmentBean();

        jtaEnvironmentBean.setTransactionManagerClassName(com.arjuna.ats.jbossatx.jta.TransactionManagerDelegate.class.getName());

        final com.arjuna.ats.jbossatx.jta.TransactionManagerService service = new com.arjuna.ats.jbossatx.jta.TransactionManagerService();
        final ServerVMClientUserTransaction userTransaction = new ServerVMClientUserTransaction(service.getTransactionManager());
        final TransactionSynchronizationRegistry tsr = new com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionSynchronizationRegistryImple();
        final UserTransactionRegistry userTransactionRegistry = new UserTransactionRegistry();

        userTransactionRegistry.addProvider(userTransaction);
        service.setTransactionSynchronizationRegistry(tsr);

        jtaEnvironmentBean.setUserTransaction(userTransaction);
        jtaEnvironmentBean.setTransactionManager(service.getTransactionManager());
        jtaEnvironmentBean.setTransactionSynchronizationRegistry(tsr);

        JNDIManager.bindJTATransactionManagerImplementation(initialContext);
        JNDIManager.bindJTATransactionSynchronizationRegistryImplementation(initialContext);
        JNDIManager.bindJTAUserTransactionImplementation(initialContext);
    }

    private UserTransaction getUserTransaction() {
        return jtaPropertyManager.getJTAEnvironmentBean().getUserTransaction();
    }

    private TransactionManager getTransactionManager() {
        return jtaPropertyManager.getJTAEnvironmentBean().getTransactionManager();
    }

    @BeforeClass
    public static void setUp() throws Exception {
        Hashtable<String, String> props = JndiProvider.start();

        initialContext =  new InitialContext(props);

        initEnv();

        BeanPopulator.getDefaultInstance(RecoveryEnvironmentBean.class).setRecoveryBackoffPeriod(1);
    }

    @After
    public void cleanThread() {
        TransactionManager tm = TransactionManagerLocator.locateTransactionManager();

        try {
            Transaction txn = tm.getTransaction();

            if (txn != null)
                txn.rollback();
        } catch (SystemException | Error ignore) {
        }
    }

    @Test(expected = RollbackException.class)
    public void testTimeout() throws Exception {
        final int timeout = 2;
        UserTransaction txn = getUserTransaction();

        txn.setTransactionTimeout(timeout);
        txn.begin();

        Thread.sleep(timeout * 1000 + 1000);
        txn.commit();
        fail("committing a timed out transaction should have thrown a RollbackException");
    }

    @Test
    public void testTransaction() throws Exception {
        UserTransaction txn = getUserTransaction();

        txn.begin();

        assertNotNull(txn);
        assertEquals(Status.STATUS_ACTIVE, txn.getStatus());

        txn.commit();

        // the transaction should have been disassociated
        assertEquals(Status.STATUS_NO_TRANSACTION, txn.getStatus());

        txn.begin();
        txn.commit();
        assertEquals(Status.STATUS_NO_TRANSACTION, txn.getStatus());
    }

    @Test
    public void testSynchronization() throws Exception {
        TestSynchronization synch =  new TestSynchronization() ;

        UserTransaction txn = getUserTransaction();
        TransactionManager transactionManager = getTransactionManager();

        txn.begin();

        transactionManager.getTransaction().registerSynchronization(synch);

        txn.commit();

        assertTrue(synch.beforeCalled);
        assertTrue(synch.afterCalled);
    }

    static private class TestSynchronization implements Synchronization {
        boolean beforeCalled = false;
        boolean afterCalled = false;

        @Override
        public void beforeCompletion() {
            beforeCalled = true;
        }

        @Override
        public void afterCompletion(int i) {
            afterCalled = true;
        }
    }
}
