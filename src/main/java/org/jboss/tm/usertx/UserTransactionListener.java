/*
   Copyright The Narayana Authors
   SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.tm.usertx;

import java.util.EventListener;

import jakarta.transaction.SystemException;

/**
 * UserTransactionListener.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public interface UserTransactionListener extends EventListener
{
   void userTransactionStarted() throws SystemException;
}
