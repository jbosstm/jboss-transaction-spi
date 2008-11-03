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
package org.jboss.tm.usertx.client;

import java.util.Collection;
import java.util.EventListener;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.jboss.tm.TransactionManagerLocator;
import org.jboss.tm.usertx.UserTransactionProvider;
import org.jboss.tm.usertx.UserTransactionRegistry;


/**
 *  The client-side UserTransaction implementation for clients
 *  operating in the same VM as the server.
 *  This will delegate all UserTransaction calls to the
 *  <code>TransactionManager</code> of the server.
 *
 *  @author <a href="mailto:osh@sparre.dk">Ole Husgaard</a>
 *  @author adrian@jboss.org
 *  @author <a href="mailto:galder.zamarreno@jboss.com">Galder Zamarreno</a>  
 *  @version $Revision: 37459 $
 */
public class ServerVMClientUserTransaction
   implements UserTransaction, UserTransactionProvider
{
   // Static --------------------------------------------------------

   /**
    *  Our singleton instance.
    */
   private final static ServerVMClientUserTransaction singleton = new ServerVMClientUserTransaction();


   /**
    *  The <code>TransactionManager</code> we delegate to.
    */
   private final TransactionManager tm;


   /** Any registry */
   private volatile UserTransactionRegistry registry;

   /** The listeners */
   private final Collection<UserTransactionStartedListener> listeners = new CopyOnWriteArrayList<UserTransactionStartedListener>();

   /**
    *  Return a reference to the singleton instance.
    *  
    *  @return the singleton
    */
   public static ServerVMClientUserTransaction getSingleton()
   {
      return singleton;
   }


   // Constructors --------------------------------------------------

   /**
    *  Create a new instance.
    */
   private ServerVMClientUserTransaction()
   {
      this(locateTransactionManager());
   }
   
   private static TransactionManager locateTransactionManager()
   {
      try
      {
         return TransactionManagerLocator.locateTransactionManager();
      }
      catch(RuntimeException re)
      {
         return null;
      }
   }
   
   //public constructor for TESTING ONLY
   public ServerVMClientUserTransaction(final TransactionManager tm)
   {
      this.tm = tm;
   }

   // Public --------------------------------------------------------
   
   public boolean isServer()
   {
       return tm != null;
   }
   
   //Registration for TransactionStartedListeners.

   public void registerTxStartedListener(UserTransactionStartedListener txStartedListener)
   {
      if (txStartedListener == null)
         throw new IllegalArgumentException("Null listener");
      listeners.add(txStartedListener);
   }

   public void unregisterTxStartedListener(UserTransactionStartedListener txStartedListener)
   {
      if (txStartedListener == null)
         throw new IllegalArgumentException("Null listener");
      listeners.remove(txStartedListener);
   }

   public void setTransactionRegistry(UserTransactionRegistry registry)
   {
      this.registry = registry;
   }

   //
   // implements interface UserTransaction
   //


   public void begin() throws NotSupportedException, SystemException
   {
      tm.begin();
      
      UserTransactionRegistry registry = this.registry;
      if (registry != null)
         registry.userTransactionStarted();

      try
      {
         for (UserTransactionStartedListener listener : listeners)
            listener.userTransactionStarted();
      }
      catch (SystemException e)
      {
         rollback();
      }
   }

   public void commit()
      throws RollbackException,
             HeuristicMixedException,
             HeuristicRollbackException,
             SecurityException,
             IllegalStateException,
             SystemException
   {
      tm.commit();
   }

   public void rollback()
      throws SecurityException,
             IllegalStateException,
             SystemException
   {
      tm.rollback();
   }

   public void setRollbackOnly()
      throws IllegalStateException,
             SystemException
   {
      tm.setRollbackOnly();
   }

   public int getStatus()
      throws SystemException
   {
      return tm.getStatus();
   }

   public void setTransactionTimeout(int seconds)
      throws SystemException
   {
      tm.setTransactionTimeout(seconds);
   }

   /**
    * UserTransactionStartedListener.
    * 
    * @Deprecated use {@link UserTransactionRegistry} instead
    */
   @Deprecated
   public interface UserTransactionStartedListener extends EventListener 
   {
      void userTransactionStarted() throws SystemException;
   }
}
