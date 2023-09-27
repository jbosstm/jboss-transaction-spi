/*
   Copyright The Narayana Authors
   SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.tm;

import jakarta.transaction.Transaction;


/**
 *  Implementations of this interface are used for getting
 *  a transaction propagation context at the client-side.
 *  We need a specific implementation of this interface for
 *  each kind of DTM we are going to interoperate with. (So
 *  we may have 20 new classes if we are going to interoperate
 *  with 20 different kinds of distributed transaction
 *  managers.)
 *  The reason for having the methods in this interface return
 *  Object is that we do not really know what kind of transaction
 *  propagation context is returned.
 *
 *  @see TransactionPropagationContextImporter
 *  @author <a href="mailto:osh@sparre.dk">Ole Husgaard</a>
 *  @author adrian@jboss.org
 *  @version $Revision: 37459 $
 */
@Deprecated
public interface TransactionPropagationContextFactory
{
   /**
    *  Return a transaction propagation context for the transaction
    *  currently associated with the invoking thread, or <code>null</code>
    *  if the invoking thread is not associated with a transaction.
    *  
    *  @return the context
    */
   public Object getTransactionPropagationContext();

   /**
    *  Return a transaction propagation context for the transaction
    *  given as an argument, or <code>null</code>
    *  if the argument is <code>null</code> or of a type unknown to
    *  this factory.
    *  
    *  @param tx the transaction
    *  @return the context
    */
   public Object getTransactionPropagationContext(Transaction tx);

}

