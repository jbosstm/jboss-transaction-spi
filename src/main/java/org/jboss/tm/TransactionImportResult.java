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

/**
 * Returned from the {@link ExtendedJBossXATerminator#importTransaction} call. It has a reference to a transaction
 * and an indication of whether or not imported transaction has been seen before
 */
public class TransactionImportResult {
    private ImportedTransaction transaction;
    private boolean subordinateCreated;

    /**
     * @param transaction the imported transaction or null if the import failed
     * @param subordinateCreated indication of whether or not this imported transaction has been seen before.
     */
    public TransactionImportResult(ImportedTransaction transaction, boolean subordinateCreated) {
        this.transaction = transaction;
        this.subordinateCreated = subordinateCreated;
    }

    /**
     * Get the imported transaction
     *
     * @return the imported transaction or null
     */
    public ImportedTransaction getTransaction() {
        return transaction;
    }

    /**
     * Indicate whether or not this imported transaction has been seen before.
     *
     * @return whether or not this imported transaction has been seen before.
     */
    public boolean isNewImportedTransaction() {
        return subordinateCreated;
    }
}
