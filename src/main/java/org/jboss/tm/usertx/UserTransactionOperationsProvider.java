/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.tm.usertx;

import jakarta.transaction.UserTransaction;

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
