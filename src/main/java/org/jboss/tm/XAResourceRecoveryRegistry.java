/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.tm;

/**
 * The transaction management system may require assistance from resource
 * managers in order to recover crashed XA transactions. By registering
 * an XAResourceRecovery instance with the XAResourceRecoveryRegistry,
 * resource manager connectors provide a way for the recovery system to
 * callback to them and obtain the necessary information.
 *
 * This is useful for e.g. JDBC connection pools or JCA connectors that
 * don't want to expose connection parameters to the transaction system.
 * The connectors are responsible for instantiating a connection and
 * using it to instantiate a set of XAResources. These are then exposed to
 * the recovery system via the registered XAResourceRecovery instance.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com)
 * @version $Revision$
 * @see XAResourceRecovery
 */
public interface XAResourceRecoveryRegistry
{
    /*
    Implementor's note:
    Although the transaction manager in JBossAS is pluggable, reading the JBossTS
    recovery documentation may give some insight into the design of this
    recovery system interface. The forum thread at
    http://www.jboss.com/index.html?module=bb&op=viewtopic&t=100435
    may also be of interest.
    */


    /**
     * Register an XAResourceRecovery instance with the transaction recovery system.
     * This should be called by deployers that are deploying a new XA aware
     * module that needs recovery support. For example, a database
     * connection pool, JMS adapter or JCA connector.
     *
     * @param recovery The XAResourceRecovery instance to register.
     */
    void addXAResourceRecovery(XAResourceRecovery recovery);

    /**
     * Unregister an XAResourceRecovery instance from the transaction recovery system.
     * This should be called when an XA aware module is undeployed, to inform the
     * recovery system that recovery is no longer required or supported.
     *
     * Note this method may block whilst an ongoing recovery operation is completed.
     * Recovery behavior is undefined if the undeployment does not wait for this
     * operation to complete.
     *
     * @param recovery The XAResourceRecovery instance to unregister.
     * Implementations should fail silent if an attempt is made to unregister
     * an XAResourceRecovery instance that is not currently registered.
     */
    void removeXAResourceRecovery(XAResourceRecovery recovery);
}
