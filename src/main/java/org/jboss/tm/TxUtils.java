/*
   Copyright The Narayana Authors
   SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.tm;

import jakarta.transaction.Status;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transaction;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

/**
 * TxUtils.java has utility methods for determining transaction status
 * in various useful ways.
 *
 * @author <a href="mailto:d_jencks@users.sourceforge.net">David Jencks</a>
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @author <a href="mailto:dimitris@jboss.org">Dimitris Andreadis</a>
 * @author adrian@jboss.org
 * @version $Revision: 63569 $
 */
public class TxUtils
{
   private static final String ARJUNA_REAPER_THREAD_NAME =
      "Transaction Reaper Worker";

   /** Transaction Status Strings */
   private static final String[] TxStatusStrings =
   {
      "STATUS_ACTIVE",
      "STATUS_MARKED_ROLLBACK",
      "STATUS_PREPARED",
      "STATUS_COMMITTED",
      "STATUS_ROLLEDBACK",
      "STATUS_UNKNOWN",
      "STATUS_NO_TRANSACTION",
      "STATUS_PREPARING",
      "STATUS_COMMITTING",
      "STATUS_ROLLING_BACK"
   };

   /**
    * Do now allow instances of this class
    */
   private TxUtils()
   {

   }

   public static boolean isActive(Transaction tx)
   {
      if (tx == null)
         return false;
      
      try
      {
         int status = tx.getStatus();
         return isActive(status);
      }
      catch (SystemException error)
      {
         throw new RuntimeException(error);
      }
   }

   public static boolean isActive(TransactionManager tm)
   {
      try
      {
         return isActive(tm.getTransaction());
      }
      catch (SystemException error)
      {
         throw new RuntimeException(error);
      }
   }

   public static boolean isActive()
   {
      return isActive(TransactionManagerLocator.locateTransactionManager());
   }
   
   public static boolean isActive(UserTransaction ut)
   {
      try
      {
         int status = ut.getStatus();
         return isActive(status);
      }
      catch (SystemException error)
      {
         throw new RuntimeException(error);
      }
   }

   public static boolean isActive(int status)
   {
      return status == Status.STATUS_ACTIVE;
   }
   
   public static boolean isUncommitted(Transaction tx)
   {
      if (tx == null)
         return false;
      
      try
      {
         int status = tx.getStatus();
         return isUncommitted(status);
      }
      catch (SystemException error)
      {
         throw new RuntimeException(error);
      }
   }

   public static boolean isUncommitted(TransactionManager tm)
   {
      try
      {
         return isUncommitted(tm.getTransaction());
      }
      catch (SystemException error)
      {
         throw new RuntimeException(error);
      }
   }

   public static boolean isUncommitted()
   {
      return isUncommitted(TransactionManagerLocator.locateTransactionManager());
   }

   public static boolean isUncommitted(UserTransaction ut)
   {
      try
      {
         int status = ut.getStatus();
         return isUncommitted(status);
         
      }
      catch (SystemException error)
      {
         throw new RuntimeException(error);
      }
   }

   public static boolean isUncommitted(int status)
   {
      return status == Status.STATUS_ACTIVE
          || status == Status.STATUS_MARKED_ROLLBACK;
   }
   
   public static boolean isCompleted(Transaction tx)
   {
      if (tx == null)
         return true;
      
      try
      {
         int status = tx.getStatus();
         return isCompleted(status);
      }
      catch (SystemException error)
      {
         throw new RuntimeException(error);
      }
   }

   public static boolean isCompleted(TransactionManager tm)
   {
      try
      {
         return isCompleted(tm.getTransaction());
      }
      catch (SystemException error)
      {
         throw new RuntimeException(error);
      }
   }

   public static boolean isCompleted()
   {
      return isCompleted(TransactionManagerLocator.locateTransactionManager());
   }

   public static boolean isCompleted(UserTransaction ut)
   {
      try
      {
         int status = ut.getStatus();
         return isCompleted(status);
         
      }
      catch (SystemException error)
      {
         throw new RuntimeException(error);
      }
   }
   
   public static boolean isCompleted(int status)
   {
      return status == Status.STATUS_COMMITTED
          || status == Status.STATUS_ROLLEDBACK
          || status == Status.STATUS_NO_TRANSACTION;
   }
   
   public static boolean isRollback(Transaction tx)
   {
      if (tx == null)
         return false;
      
      try
      {
         int status = tx.getStatus();
         return isRollback(status);
      }
      catch (SystemException error)
      {
         throw new RuntimeException(error);
      }
   }

   public static boolean isRollback(TransactionManager tm)
   {
      try
      {
         return isRollback(tm.getTransaction());
      }
      catch (SystemException error)
      {
         throw new RuntimeException(error);
      }
   }

   public static boolean isRollback()
   {
      return isRollback(TransactionManagerLocator.locateTransactionManager());
   }

   public static boolean isRollback(UserTransaction ut)
   {
      try
      {
         int status = ut.getStatus();
         return isRollback(status);
      }
      catch (SystemException error)
      {
         throw new RuntimeException(error);
      }
   }
   
   public static boolean isRollback(int status)
   {
      return status == Status.STATUS_MARKED_ROLLBACK
          || status == Status.STATUS_ROLLING_BACK
          || status == Status.STATUS_ROLLEDBACK;      
   }
   
   /**
    * Converts a tx Status index to a String
    * 
    * @see jakarta.transaction.Status
    * 
    * @param status the Status index
    * @return status as String or "STATUS_INVALID(value)"
    */
   public static String getStatusAsString(int status)
   {
      if (status >= Status.STATUS_ACTIVE && status <= Status.STATUS_ROLLING_BACK)
      {
         return TxStatusStrings[status];
      }
      else
      {
         return "STATUS_INVALID(" + status + ")";
      }
   }
   
   /**
    * Converts a XAResource flag to a String
    * 
    * @see javax.transaction.xa.XAResource
    * 
    * @param flags the flags passed in to start(), end(), recover() 
    * @return the flags in String form
    */
   public static String getXAResourceFlagsAsString(int flags)
   {
      if (flags == XAResource.TMNOFLAGS)
      {
         return "|TMNOFLAGS";
      }
      else
      {
         StringBuffer sbuf = new StringBuffer(64);
         
         if ((flags & XAResource.TMONEPHASE) != 0)
         {
            sbuf.append("|TMONEPHASE");
         }
         if ((flags & XAResource.TMJOIN) != 0)
         {
            sbuf.append("|TMJOIN");
         }
         if ((flags & XAResource.TMRESUME) != 0)
         {
            sbuf.append("|TMRESUME");
         }
         if ((flags & XAResource.TMSUCCESS) != 0)
         {
            sbuf.append("|TMSUCCESS");
         }
         if ((flags & XAResource.TMFAIL) != 0)
         {
            sbuf.append("|TMFAIL");
         }
         if ((flags & XAResource.TMSUSPEND) != 0)
         {
            sbuf.append("|TMSUSPEND");
         }
         if ((flags & XAResource.TMSTARTRSCAN) != 0)
         {
            sbuf.append("|TMSTARTRSCAN");
         }
         if ((flags & XAResource.TMENDRSCAN) != 0)
         {
            sbuf.append("|TMENDRSCAN");
         }
         return sbuf.toString();
      }
   }
   
   /**
    * Converts a XAException error code to a string.
    * 
    * @see javax.transaction.xa.XAException
    * 
    * @param errorCode an XAException error code
    * @return the error code in String form.
    * 
    */
   public static String getXAErrorCodeAsString(int errorCode)
   {
      switch (errorCode)
      {
      case XAException.XA_HEURCOM:
         return "XA_HEURCOM";
      case XAException.XA_HEURHAZ:
         return "XA_HEURHAZ";
      case XAException.XA_HEURMIX:
         return "XA_HEURMIX";
      case XAException.XA_HEURRB:
         return "XA_HEURRB";
      case XAException.XA_NOMIGRATE:
         return "XA_NOMIGRATE";
      case XAException.XA_RBCOMMFAIL:
         return "XA_RBCOMMFAIL";
      case XAException.XA_RBDEADLOCK:
         return "XA_RBDEADLOCK";
      case XAException.XA_RBINTEGRITY:
         return "XA_RBINTEGRITY";
      case XAException.XA_RBOTHER:
         return "XA_RBOTHER";
      case XAException.XA_RBPROTO:
         return "XA_RBPROTO";
      case XAException.XA_RBROLLBACK:
         return "XA_RBROLLBACK";
      case XAException.XA_RBTIMEOUT:
         return "XA_RBTIMEOUT";
      case XAException.XA_RBTRANSIENT:
         return "XA_RBTRANSIENT";
      case XAException.XA_RDONLY:
         return "XA_RDONLY";
      case XAException.XA_RETRY:
         return "XA_RETRY";
      case XAException.XAER_ASYNC:
         return "XAER_ASYNC";
      case XAException.XAER_DUPID:
         return "XAER_DUPID";
      case XAException.XAER_INVAL:
         return "XAER_INVAL";
      case XAException.XAER_NOTA:
         return "XAER_NOTA";
      case XAException.XAER_OUTSIDE:
         return "XAER_OUTSIDE";
      case XAException.XAER_PROTO:
         return "XAER_PROTO";
      case XAException.XAER_RMERR:
         return "XAER_RMERR";
      case XAException.XAER_RMFAIL:
         return "XAER_RMFAIL";
      default:
         return "XA_UNKNOWN(" + errorCode + ")";
      }
   }

   /**
    * Determine whether the calling thread is the transaction manager
    * and is processing a transaction timeout:
    *
    * @return true if the current thread was created by the TM and is handling
    * a transaction timeout, otherwise return false
    */
   public static boolean isTransactionManagerTimeoutThread() {
     String currentThreadName = Thread.currentThread().getName();
     
     return currentThreadName != null &&
         currentThreadName.startsWith(ARJUNA_REAPER_THREAD_NAME);
   }
}
