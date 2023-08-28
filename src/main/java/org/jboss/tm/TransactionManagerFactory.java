/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.tm;

import jakarta.transaction.TransactionManager;

/**
 * A TransactionManager factory.
 * 
 * @author <a href="mailto:dimitris@jboss.org">Dimitris Andreadis</a>
 * @author adrian@jboss.org
 * @version $Revision: 44337 $
 */
@Deprecated
public interface TransactionManagerFactory
{
   /**
    * Gets the TransactionManager
    * 
    * @return the TransactionManager
    */
   TransactionManager getTransactionManager();
}
