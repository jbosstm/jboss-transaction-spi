/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
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

import javax.transaction.NotSupportedException;
import javax.transaction.Transaction;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;

public interface ExtendedJBossXATerminator {
	/**
	 * A thread safe method to convert an imported Xid into a Transaction object. If the transaction does not exist
	 * then a subordinate transaction for the xid will be created and associated with the global transaction.
	 *
	 * In the case where a new subordinate is created then the timeout parameter will be used. If timeout is non zero
	 * then the inflowed transaction will survive for that period before being
	 * eligible to be aborted. If timeout is zero then no timeout will be associated with the subordinate transaction.
	 *
	 * @param xid
	 *            The global Xid of the target transaction to lookup
	 * @param timeoutIfNew
	 *            The timeout associated with the global transaction if one was created.
	 *
	 * @return
	 *            A wrapper containing the transaction object corresponding to the Xid or null if there is no such transaction
	 *
	 * @throws XAException with code XA_RBROLLBACK if the transaction has already aborted
	 */
	TransactionImportResult importTransaction(Xid xid, int timeoutIfNew) throws XAException;

	/**
	 * Convert an imported Xid into a Transaction object.
	 *
	 * @param xid the global Xid of the target transaction to lookup
	 *
	 * @return A transaction object corresponding to the Xid or null if there is no such transaction
	 *
	 * @throws XAException with code XA_RBROLLBACK if the transaction has already aborted
	 */
	Transaction getTransaction(Xid xid) throws XAException;

    /**
     * Look up a transaction by its id
     *
     * @param uid an id that uniquelly identifies a transaction
     * @return
     * @deprecated deprecating based of addition of wildfly transaction client.
     *   This method is no longer needed for integration purposes with WildFly.
     *   You can expect its removal in some of the next releases.
     */
	@Deprecated
	Transaction getTransactionById(Object uid);

    /**
     * Obtain the unique id of the currently associated transaction
     *
     * @return the id or null if no transaction is associated with the calling thread
     * @deprecated deprecating based of addition of wildfly transaction client.
     *   This method is no longer needed for integration purposes with WildFly.
     *   You can expect its removal in some of the next releases.
     */
	@Deprecated
	Object getCurrentTransactionId();

    /**
     * Lookup an imported transaction by its Xid
     *
     * @param xid the Xid of the transaction to lookup
     * @return the corresponding imported transaction object or null
     * @throws XAException if the transaction is known to have rolled back
     * @deprecated deprecating based of addition of wildfly transaction client.
     *   This method is no longer needed for integration purposes with WildFly.
     *   You can expect its removal in some of the next releases.
     */
	@Deprecated
	ImportedTransaction getImportedTransaction(Xid xid) throws XAException;

    /**
     * Get a list of Xids that are potentially recoverable.
     *
     * @param recoverInFlight indicates whether or not to include transactions that have not begun 2PC
     * @param parentNodeName If not null then only recover transactions for this node
     * @param recoveryFlags One of TMSTARTRSCAN, TMENDRSCAN, TMNOFLAGS. TMNOFLAGS must be used when no other flags are
     *                      set in the parameter. These constants are defined in javax.transaction.xa.XAResource interface
     * @return an array of Xids to recover
     * @throws XAException An error has occurred. Possible values are XAER_RMERR, XAER_RMFAIL, XAER_INVAL, and XAER_PROTO.
     * @throws UnsupportedOperationException Operation is not supported in the context. This happens when Narayana runs on JTS.
     * @deprecated deprecating based of addition of wildfly transaction client.
     *   This method is no longer needed for integration purposes with WildFly.
     *   You can expect its removal in some of the next releases.
     */
    @Deprecated
    Xid[] getXidsToRecoverForParentNode(boolean recoverInFlight, final String parentNodeName, int recoveryFlags) throws XAException;

    /**
     * Return a list of indoubt transactions. This may include those
     * transactions that are currently in-flight (the 2PC phase has not been reached) and do not need
     * recovery invoked on them.
     *
     * @param parentNodeName
     * 				If not null then only recover transactions for this node
     * @throws XAException
     *             thrown if any error occurs.
     * @return a list of potentially indoubt transactions or <code>null</code>.
     */
    Xid[] doRecover (Xid xid, String parentNodeName) throws XAException, NotSupportedException;

    /**
     * Test whether or not the {@link ExtendedJBossXATerminator#doRecover(Xid, String)} call will throw NotSupportedException
     * @return
     * @deprecated deprecating based of addition of wildfly transaction client.
     *   This method is no longer needed for integration purposes with WildFly.
     *   You can expect its removal in some of the next releases.
     */
    @Deprecated
    boolean isRecoveryByNodeOrXidSupported();

    /**
     * forget about this imported transaction.
     * @param xid the global Xid of the transaction.
     * @throws XAException thrown if there are any errors.
     */
    void removeImportedTransaction(Xid xid) throws XAException;
}
