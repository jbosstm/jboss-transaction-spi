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
package org.jboss.tm.listener.event;

import org.jboss.tm.TransactionManagerLocator;

import javax.transaction.TransactionManager;

/**
 * Locate an interface that implements a registration mechanism for transaction lifecycle events {@link TransactionLifecycleEvent}
 */
public class TransactionLifecycleListenerRegistryLocator {
    /**
     *
     * @return an interface for registering interest in transaction lifecycle events
     * @throws TransactionLifecycleListenerRegistryUnavailableException if no such interface has been registered with the SPI
     */
    public static TransactionLifecycleListenerRegistry getTransactionListenerRegistry()
            throws TransactionLifecycleListenerRegistryUnavailableException {
        TransactionManager tm = TransactionManagerLocator.locateTransactionManager();

        if (tm instanceof TransactionLifecycleListenerRegistry) {
            return (TransactionLifecycleListenerRegistry) tm;
        }

        throw new TransactionLifecycleListenerRegistryUnavailableException();
    }
}
