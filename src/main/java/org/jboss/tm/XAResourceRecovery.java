/*
  * JBoss, Home of Professional Open Source
  * Copyright 2008, JBoss Inc., and individual contributors as indicated
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

import javax.transaction.xa.XAResource;

/**
 * During recovery of crashed XA transactions, the transaction system may use instances
 * of this interface to obtain XAResources on which to perform recovery calls.
 * Resource managers should register instances of this interface with the transaction
 * recovery system via an XAResourceRecoveryRegistry.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com)
 * @version $Revision$
 * @see XAResourceRecoveryRegistry
 */
public interface XAResourceRecovery
{
    /**
     * Provides XAResource(s) to the transaction system for recovery purposes.
     *
     * @return An array of XAResource objects for use in transaction recovery
     * In most cases the implementation will need to return only a single XAResource in the array.
     * For more sophisticated cases, such as where multiple different connection types are supported,
     * it may be necessary to return more than one.
     *
     * The Resource should be instantiated in such a way as to carry the necessary permissions to
     * allow transaction recovery. For some deployments it may therefore be necessary or desirable to
     * provide resource(s) based on e.g. database connection parameters such as username other than those
     * used for the regular application connections to the same resource manager. 
     */
    public XAResource[] getXAResources();
}
