/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.tm;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.transaction.Transaction;

/**
 * Comment
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @author adrian@jboss.org
 * @version $Revision: 60868 $
 */
@Deprecated
public class TransactionPropagationContextUtil
{
   private static TransactionPropagationContextFactory tpcFactory;
   private static TransactionPropagationContextImporter tpcImporter;
   private static final String EXPORTER_JNDI_NAME = "java:/TransactionPropagationContextExporter";
   private static final String IMPORTER_JNDI_NAME = "java:/TransactionPropagationContextImporter";

   public static TransactionPropagationContextFactory getTPCFactoryClientSide()
   {
      return tpcFactory;
   }

   public static TransactionPropagationContextFactory getTPCFactory()
   {
      if (tpcFactory == null)
      {
         try
         {
            InitialContext ctx = new InitialContext();
            try
            {
               tpcFactory = (TransactionPropagationContextFactory) ctx.lookup(EXPORTER_JNDI_NAME);
            }
            finally
            {
               ctx.close();
            }
         }
         catch (NamingException e)
         {
            throw new RuntimeException("Unable to lookup " + EXPORTER_JNDI_NAME, e);
         }
      }
      return tpcFactory;
   }

   public static void setTPCFactory(TransactionPropagationContextFactory tpcFactory)
   {
      TransactionPropagationContextUtil.tpcFactory = tpcFactory;
   }

   public static TransactionPropagationContextImporter getTPCImporter()
   {
      if (tpcImporter == null)
      {
         try
         {
            InitialContext ctx = new InitialContext();
            try
            {
               // and the transaction propagation context importer
               tpcImporter = (TransactionPropagationContextImporter) ctx.lookup(IMPORTER_JNDI_NAME);
            }
            finally
            {
               ctx.close();
            }
         }
         catch (NamingException e)
         {
            throw new RuntimeException("Unable to look up " + IMPORTER_JNDI_NAME, e);
         }
      }
      return tpcImporter;
   }

   public static void setTPCImporter(TransactionPropagationContextImporter importer)
   {
      tpcImporter = importer;
   }
   
   public static Transaction importTPC(Object tpc)
   {
      return getTPCImporter().importTransactionPropagationContext(tpc);
   }
}
