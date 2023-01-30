/*
* JBoss, Home of Professional Open Source
* Copyright 2008, Red Hat Middleware LLC, and individual contributors
* as indicated by the @author tags. See the copyright.txt file in the
* distribution for a full listing of individual contributors.
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
