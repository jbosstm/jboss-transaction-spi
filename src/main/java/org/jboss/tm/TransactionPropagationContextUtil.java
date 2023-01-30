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
