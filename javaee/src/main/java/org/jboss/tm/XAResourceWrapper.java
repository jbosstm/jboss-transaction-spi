/*
 * JBoss, Home of Professional Open Source.
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
package org.jboss.tm;

import javax.transaction.xa.XAResource;

/**
 * A serializable XAResource wrapper.
 * 
 * @author <a href="jesper.pedersen@jboss.org">Jesper Pedersen</a>
 * @version $Revision: 1.1 $
 */
public interface XAResourceWrapper extends XAResource
{
   /**
    * Get the XAResource that is being wrapped
    * @return The XAResource
    */
   public XAResource getResource();

   /**
    * Get product name
    * @return Product name of the instance that created the wrapper if defined; otherwise <code>null</code>
    */
   public String getProductName();

   /**
    * Get product version
    * @return Product version of the instance that created the warpper if defined; otherwise <code>null</code>
    */
   public String getProductVersion();

   /**
    * Get jndi name
    * @return The value if defined; otherwise <code>null</code>
    */
   public String getJndiName();
}
