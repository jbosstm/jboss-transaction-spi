/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
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
package org.jboss.tm.listener;

import javax.transaction.Transaction;
import java.util.EnumSet;

public interface TransactionListenerRegistry {
    /**
     * @param transaction the transaction that the caller is interested in receiving events for (must not be null)
     * @param listener an object that will be invoked when events of type {@link EventType}
     *                 occur
     * @param types a collection of events that the listener is interested in
     * @throws TransactionTypeNotSupported if the passed in transaction type does not support the listeners feature
     */
    void addListener (Transaction transaction, TransactionListener listener, EnumSet<EventType> types) throws TransactionTypeNotSupported;
}
