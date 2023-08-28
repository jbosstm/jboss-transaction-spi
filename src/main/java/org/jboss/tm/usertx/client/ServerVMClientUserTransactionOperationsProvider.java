/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.tm.usertx.client;

import org.jboss.tm.usertx.UserTransactionOperationsProvider;

/**
 * Implementation of user transaction operations provider bound to
 * {@link ServerVMClientUserTransaction}.
 *
 * @author Ondra Chaloupka <ochaloup@redhat.com>
 */
public class ServerVMClientUserTransactionOperationsProvider implements UserTransactionOperationsProvider {

    /**
     * {@inheritDoc}
     */
    public void setAvailability(boolean available) {
        ServerVMClientUserTransaction.setAvailability(available);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getAvailability() {
        return ServerVMClientUserTransaction.isAvailable();
    }

}
