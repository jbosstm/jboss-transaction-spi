/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat Middleware LLC, and individual contributors
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

import javax.transaction.xa.XAException;

/**
 * Interface serving to specify way to import transaction to current TM processing.
 * This is used for example by txbridge to import transaction to WFLY transaction context.
 *
 * @author Ondra Chaloupka <ochaloup@redhat.com>
 */
public interface SubordinateTransactionImporter {
    /**
     * Imports transaction with defined xid to current transaction processing.<br>
     *
     * @param xid  xid under which transaction will be imported
     * @return  imported transaction now managed by transaction manager, not null
     * @throws XAException  when issue with importing transaction happens
     */
    javax.transaction.Transaction getTransaction(javax.transaction.xa.Xid xid) throws XAException;
}
