/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.tm;

import jakarta.transaction.Transaction;

/**
 * The interface to implementated for a transaction local implementation
 *
 * @author <a href="mailto:adrian@jboss.org">Adrian Brock</a>
 * @version $Revision: 37459 $
 */
@Deprecated
public interface TransactionLocalDelegate
{
   /**
    * get the transaction local value.
    * 
    * @param local the transaction local
    * @param tx the transcation
    * @return the value
    */
   Object getValue(TransactionLocal local, Transaction tx);

   /**
    * put the value in the transaction local
    * 
    * @param local the transaction local
    * @param tx the transcation
    * @param value the value
    */
   void storeValue(TransactionLocal local, Transaction tx, Object value);

   /**
    * does Transaction contain object?
    * 
    * @param local the transaction local
    * @param tx the transcation
    * @return true if it has the value
    */
   boolean containsValue(TransactionLocal local, Transaction tx);
   
   /**
    * Lock the transaction local in the context of this transaction
    * 
    * @param local the transaction local
    * @param tx the transcation
    * @throws IllegalStateException if the transaction is not active
    * @throws InterruptedException if the thread is interrupted
    */
   void lock(TransactionLocal local, Transaction tx) throws InterruptedException;
   
   /**
    * Unlock the transaction local in the context of this transaction
    * 
    * @param local the transaction local
    * @param tx the transcation
    */
   void unlock(TransactionLocal local, Transaction tx);
}
