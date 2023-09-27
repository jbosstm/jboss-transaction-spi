/*
   Copyright The Narayana Authors
   SPDX-License-Identifier: Apache-2.0
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
