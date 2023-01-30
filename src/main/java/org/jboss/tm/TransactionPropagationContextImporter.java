/*
  * JBoss, Home of Professional Open Source
  * Copyright 2005, JBoss Inc., and individual contributors as indicated
  * by the @authors tag. See the copyright.txt in the distribution for a
  * full listing of individual contributors.
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
