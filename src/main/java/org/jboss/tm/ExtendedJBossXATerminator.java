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

import javax.transaction.Transaction;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;

public interface ExtendedJBossXATerminator extends JBossXATerminator {
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
}
