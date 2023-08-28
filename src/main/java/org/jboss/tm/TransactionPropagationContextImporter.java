/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.tm;

import jakarta.transaction.Transaction;


/**
 *  Implementations of this interface are used for importing a transaction
 *  propagation context into the transaction manager.
 *
 *  @see TransactionPropagationContextFactory
 *  @author <a href="mailto:osh@sparre.dk">Ole Husgaard</a>
 *  @author adrian@jboss.org
 *  @version $Revision: 37459 $
 */
@Deprecated
public interface TransactionPropagationContextImporter
{
   /**
    *  Import the transaction propagation context into the transaction
    *  manager, and return the resulting transaction.
    *  If this transaction propagation context has already been imported
    *  into the transaction manager, this method simply returns the
    *  <code>Transaction</code> representing the transaction propagation
    *  context in the local VM.
    *  Returns <code>null</code> if the transaction propagation context is
    *  <code>null</code>, or if it represents a <code>null</code> transaction.
    *  
    *  @param tpc the transaction propagation context
    *  @return the transaction
    */
   public Transaction importTransactionPropagationContext(Object tpc);
}
