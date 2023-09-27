/*
   Copyright The Narayana Authors
   SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.tm.listener;

/**
 * @deprecated WFTC (https://issues.jboss.org/projects/WFTC) replaces this functionality
 *
 * An indication of an event that effects the current transaction
 */
@Deprecated
public enum EventType {
    /**
     * a transaction is associated with the current thread
     */
    ASSOCIATED,

    /**
     * a transaction is just about to be disassociated from the current thread. This
     * can happen because the thread is about to commit, roll back or suspend the
     * transaction that is currently associated with the thread.
     */
    DISASSOCIATING
}
