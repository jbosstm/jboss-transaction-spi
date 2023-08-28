/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.tm;

import java.lang.reflect.Method;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.transaction.TransactionManager;

import org.jboss.logging.Logger;

/**
 * Locates the transaction manager.
 * 
 * @todo this really belongs in some integration layer with
 *       a more pluggable implementation
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @author <a href="mailto:galder.zamarreno@jboss.com">Galder Zamarreno</a>  
 * @version $Revision: 37459 $
 */
@Deprecated
public class TransactionManagerLocator implements TransactionManagerFactory
{
   /** Logger */
   private static final Logger log = Logger.getLogger(TransactionManagerLocator.class);
   
   /** The JNDI Name */
   private static final String JNDI_NAME = "java:/TransactionManager";
   
   /** The instance */ 
   private static TransactionManagerLocator instance = new TransactionManagerLocator();
   
   /** The transaction manager */
   private TransactionManager tm;
   
   /**
    * No external construction
    */
   private TransactionManagerLocator()
   {
   }
   
   /**
    * Get the locator
    *  
    * @return the locator
    */
   public static TransactionManagerLocator getInstance()
   {
      return instance;
   }
   
   /**
    * Locate the transaction manager
    *  
    * @return the transaction manager
    */
   public static TransactionManager locateTransactionManager()
   {
      return getInstance().locate();
   }
   
   /**
    * Locate the transaction manager
    *  
    * @param throwExceptionIfUnableToLocate if true and transaction manager 
    * cannot be located, a RuntimeException is thrown, otherwise null is 
    * returned.
    *  
    * @return the transaction manager
    */
   public static TransactionManager locateTransactionManager(boolean throwExceptionIfUnableToLocate)
   {
      return getInstance().locate(throwExceptionIfUnableToLocate);
   }
   
   /**
    * Get the transaction manager
    *  
    * @return the transaction manager
    */
   public TransactionManager getTransactionManager()
   {
      return locate();
   }
   
   /**
    * Locate the transaction manager
    * 
    * @return the transaction manager
    */
   public TransactionManager locate()
   {
      return locate(true);
   }
   
   /**
    * Locate the transaction manager
    * 
    * @param throwExceptionIfUnableToLocate if true and transaction manager 
    * cannot be located, a RuntimeException is thrown, otherwise null is 
    * returned.
    * 
    * @return the transaction manager
    */
   public TransactionManager locate(boolean throwExceptionIfUnableToLocate)
   {
      if (tm != null)
         return tm;

      TransactionManager result = tryJNDI();
      if (result == null)
         result = usePrivateAPI();
      if (result == null && throwExceptionIfUnableToLocate)
         throw new RuntimeException("Unable to locate the transaction manager");
      
      return result;
   }   
   
   /**
    * Locate the transaction manager in the well known jndi binding for JBoss
    * 
    * @return the tm from jndi
    */
   protected TransactionManager tryJNDI()
   {
      try
      {
         InitialContext ctx = new InitialContext();
         tm = (TransactionManager) ctx.lookup(JNDI_NAME);
         if (log.isTraceEnabled())
            log.trace("Got a transaction manager from jndi " + tm);
      }
      catch (NamingException e)
      {
         log.debugf("Unable to lookup JNDI name %s reason %s",
             JNDI_NAME, e.getMessage());
      }
      return tm;
   }
   
   /**
    * Use the private api<p>
    * 
    * This is a fallback method for non JBossAS use.
    * 
    * @return the tm from the private api
    */
   protected TransactionManager usePrivateAPI()
   {
      try
      {
         Class<?> clazz = Class.forName("org.jboss.tm.TxManager");
         Method method = clazz.getMethod("getInstance");
         TransactionManager result = (TransactionManager) method.invoke(null, null);
         log.warn("Using the old JBoss transaction manager " + tm);
         return result;
      }
      catch (Exception e)
      {
         log.debugf("Unable to instantiate legacy transaction manager: %s",
             e.getMessage());
         return null;
      }
   }
}
