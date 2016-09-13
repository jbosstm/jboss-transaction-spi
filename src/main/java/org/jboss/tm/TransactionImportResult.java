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

/**
 * Returned from importTransaction it has a reference to a transaction and whether the import resulted is its creation.
 */
public class TransactionImportResult {
    private Transaction transaction;
    private boolean subordinateCreated;

    /**
     * Get the transaction
     *
     * @return the imported transaction or null
     */
    public Transaction getTransaction() {
        return transaction;
    }

    /**
     * Initialize the transaction
     *
     * @param transaction
     */
    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    /**
     * Was the transaction referenced in the getTransaction() call created in this import routine.
     *
     * @return
     */
    public boolean isSubordinateCreated() {
        return subordinateCreated;
    }

    /**
     * Set the import status of the subordinate transaction in relation to this call.
     * 
     * @param subordinateCreated
     */
    public void setSubordinateCreated(boolean subordinateCreated) {
        this.subordinateCreated = subordinateCreated;
    }
}
