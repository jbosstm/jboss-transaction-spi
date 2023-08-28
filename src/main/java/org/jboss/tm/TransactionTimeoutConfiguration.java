/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.tm;

import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;

/**
 * The interface to implementated by a transaction manager
 * that supports retrieving the current threads transaction timeout
 *
 * @author <a href="mailto:adrian@jboss.org">Adrian Brock</a>
 * @version $Revision: 37459 $
 */
public interface TransactionTimeoutConfiguration
{
   /**
    * Get the transaction timeout.
    * 
    * @return the timeout in seconds associated with this thread
    * @throws SystemException for any error
    */
   int getTransactionTimeout() throws SystemException;

   /**
    * Get the time left before transaction timeout
    * 
    * @param errorRollback throw an error if the transaction is marked for rollback
    * @return the remaining in the current transaction or -1
    * if there is no transaction
    * @throws RollbackException if the transaction is marked for rollback and
    * errorRollback is true
    */
   long getTimeLeftBeforeTransactionTimeout(boolean errorRollback) throws RollbackException;
}
