/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.tm.usertx;

/**
 * UserTransactionProvider.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public interface UserTransactionProvider
{
   /**
    * Set the user transaction registry
    * 
    * @param registry the registry
    */
   void setTransactionRegistry(UserTransactionRegistry registry);
}
