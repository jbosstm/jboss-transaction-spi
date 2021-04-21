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

package org.jboss.tm.usertx;

import javax.transaction.UserTransaction;

/**
 * Interface of provider which offers additional
 * operations above {@link UserTransaction}.
 *
 * @author Ondra Chaloupka <ochaloup@redhat.com>
 */
public interface UserTransactionOperationsProvider {
    /**
     * <p>
     * Narayana pushes information about availability of user transaction
     * at current thread.
     * <p>
     * For example UserTransaction is not available within particular scopes
     * that's for example for @Transactional CDI bean and a TxType other than NOT_SUPPORTED or NEVER.
     *
     * @param available  boolean parameter setting transaction availability
     */
    void setAvailability(boolean available);

    /**
     * Narayana gathers information on availability of user transaction
     * at current thread.
     *
     * @return current availability of transaction
     */
    boolean getAvailability();

}