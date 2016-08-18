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

import java.io.*;
import java.util.Collection;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;

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
   implements UserTransaction, UserTransactionProvider, Referenceable, Externalizable
{
   private static final transient AtomicLong OID = new AtomicLong(0);

   private static final transient ThreadLocal<Boolean> isAvailables = new ThreadLocal<Boolean>();

   private static final transient Map<Long, ServerVMClientUserTransaction> userTransactions =
           new HashMap<Long, ServerVMClientUserTransaction>();

   // Static --------------------------------------------------------

   /**
    *  Our singleton instance.
    */
   private final static transient ServerVMClientUserTransaction singleton = new ServerVMClientUserTransaction();

   private long id;

   /**
    *  The <code>TransactionManager</code> we delegate to.
    */
   private volatile transient TransactionManager tm;


   /** Any registry */
   private volatile transient UserTransactionRegistry registry;

   /** The listeners */
   private final transient Collection<UserTransactionStartedListener> listeners = new CopyOnWriteArrayList<UserTransactionStartedListener>();

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
   public ServerVMClientUserTransaction()
   {
      this(TransactionManagerLocator.locateTransactionManager(false), false);
   }
   
   public ServerVMClientUserTransaction(final TransactionManager tm)
   {
      this(tm, true);
   }

   private ServerVMClientUserTransaction(final TransactionManager tm, boolean statefull)
   {
      this.tm = tm;
      id = OID.getAndIncrement();
      
      if (statefull)
          userTransactions.put(id, this);
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

   public void setTransactionManager(TransactionManager tm)
   {
      this.tm = tm;
   }

   //
   // implements interface UserTransaction
   //


   public void begin() throws NotSupportedException, SystemException
   {
      testAvailability();

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
      testAvailability();
      tm.commit();
   }

   public void rollback()
      throws SecurityException,
             IllegalStateException,
             SystemException
   {
      testAvailability();
      tm.rollback();
   }

   public void setRollbackOnly()
      throws IllegalStateException,
             SystemException
   {
      testAvailability();
      tm.setRollbackOnly();
   }

   public int getStatus()
      throws SystemException
   {
      testAvailability();
      return tm.getStatus();
   }

   public void setTransactionTimeout(int seconds)
      throws SystemException
   {
      testAvailability();
      tm.setTransactionTimeout(seconds);
   }

   @Override
   public Reference getReference() throws NamingException {
      return new Reference(ServerVMClientUserTransaction.class.getName(),
          ServerVMClientUserTransactionFactory.class.getCanonicalName(), null);
   }

   @Override
   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeLong(id);
   }

   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      id = in.readLong();

   }

   private Object readResolve() throws ObjectStreamException {
      Object ut = userTransactions.get(id);

      return ut ==  null ? getSingleton() : ut;
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

   public static void setAvailability(boolean available) {
      isAvailables.set(available);
   }

   public static boolean isAvailable() {
      Boolean isAvailable = isAvailables.get();
      if (isAvailable == null) {
        return true; //default is available
      }
      return isAvailable;
   }

   private void testAvailability() {
      if (!isAvailable()) {
        throw new IllegalStateException("UserTransaction is not available within the scope of a bean or method annotated with @Transactional and a Transactional.TxType other than NOT_SUPPORTED or NEVER");
      }
   }
}
