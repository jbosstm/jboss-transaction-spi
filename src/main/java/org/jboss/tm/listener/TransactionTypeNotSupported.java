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

/**
 * @deprecated WFTC (https://issues.jboss.org/projects/WFTC) replaces this functionality
 *
 * An exception type to indicate that the actual transaction type passed into
 * {@link TransactionListenerRegistry#addListener(javax.transaction.Transaction, TransactionListener, java.util.EnumSet)}
 * does not support TSR resources
 * (see {@link javax.transaction.TransactionSynchronizationRegistry#putResource(Object, Object)})
 */
@Deprecated
public class TransactionTypeNotSupported extends Exception {
    public TransactionTypeNotSupported(String message) {
        super(message);
    }
}
