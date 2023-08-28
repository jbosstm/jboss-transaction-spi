/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.tm.usertx;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

import org.jboss.logging.Logger;

/**
 * UserTransactionRegistry.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class UserTransactionRegistry
{
   /** The log */
   private static final Logger log = Logger.getLogger(UserTransactionRegistry.class);
   
   /** The providers */
   private Collection<UserTransactionProvider> providers = new CopyOnWriteArraySet<UserTransactionProvider>();
   
   /** The listeners */
   private Collection<UserTransactionListener> listeners = new CopyOnWriteArraySet<UserTransactionListener>();
   
   /**
    * Add a listener
    * 
    * @param listener the listener
    * @throws IllegalArgumentException for a null listener
    */
   public void addListener(UserTransactionListener listener)
   {
      if (listener == null)
         throw new IllegalArgumentException("Null listener");
      listeners.add(listener);
      log.debug(this + " addListener " + listener);
   }
   
   /**
    * Remove a listener
    * 
    * @param listener the listener
    * @throws IllegalArgumentException for a null listener
    */
   public void removeListener(UserTransactionListener listener)
   {
      if (listener == null)
         throw new IllegalArgumentException("Null listener");
      listeners.remove(listener);
      log.debug(this + " removeListener " + listener);
   }
   
   /**
    * Add a provider
    * 
    * @param provider the provider
    * @throws IllegalArgumentException for a null provider
    */
   public void addProvider(UserTransactionProvider provider)
   {
      if (provider == null)
         throw new IllegalArgumentException("Null provider");
      provider.setTransactionRegistry(this);
      providers.add(provider);
      log.debug(this + " addProvider " + provider);
   }
   
   /**
    * Remove a provider
    * 
    * @param provider the provider
    * @throws IllegalArgumentException for a null provider
    */
   public void removeProvider(UserTransactionProvider provider)
   {
      if (provider == null)
         throw new IllegalArgumentException("Null provider");
      if (providers.remove(provider))
      {
         provider.setTransactionRegistry(null);
         log.debug(this + " removeProvider " + provider);
      }
   }

   /**
    * Fire a user transaction started event
    */
   public void userTransactionStarted() 
   {
      if (listeners.isEmpty() == false)
      {
         for (UserTransactionListener listener : listeners)
         {
            if (listener != null)
            {
               try
               {
                  listener.userTransactionStarted();
               }
               catch (Throwable t)
               {
                  log.warn("Error notifying listener " + listener + " of userTransactionStarted", t);
               }
            }
         }
      }
   }
}
