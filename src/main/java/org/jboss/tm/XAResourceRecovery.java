/*
   Copyright The Narayana Authors
   SPDX-License-Identifier: Apache-2.0
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
