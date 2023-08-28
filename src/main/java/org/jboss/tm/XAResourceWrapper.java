/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
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
