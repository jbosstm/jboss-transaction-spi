/*
   Copyright The Narayana Authors
   SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.tm;

import jakarta.resource.spi.XATerminator;
import jakarta.resource.spi.work.Work;
import jakarta.resource.spi.work.WorkCompletedException;
import javax.transaction.xa.Xid;

/**
 * Extends XATerminator to include registration calls
 *
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 37459 $
 */
public interface JBossXATerminator extends XATerminator
{
   /**
    * Invoked for transaction inflow of work
    * 
    * @param work the work starting
    * @param xid the xid of the work
    * @param timeout the transaction timeout
    * @throws WorkCompletedException with error code WorkException.TX_CONCURRENT_WORK_DISALLOWED
    *         when work is already present for the xid or whose completion is in progress, only
    *         the global part of the xid must be used for this check.
    */
   void registerWork(Work work, Xid xid, long timeout) throws WorkCompletedException;
   
   /**
    * Invoked for transaction inflow of work
    * 
    * @param work the work starting
    * @param xid the xid of the work
    * @throws WorkCompletedException with error code WorkException.TX_RECREATE_FAILED if it is unable to recreate the transaction context
    */
   void startWork(Work work, Xid xid) throws WorkCompletedException;

   /**
    * Invoked when transaction inflow work ends
    * 
    * @param work the work ending
    * @param xid the xid of the work
    */
   void endWork(Work work, Xid xid);

   /**
    * Invoked when the work fails
    * 
    * @param work the work ending
    * @param xid the xid of the work
    */
   void cancelWork(Work work, Xid xid);
}
