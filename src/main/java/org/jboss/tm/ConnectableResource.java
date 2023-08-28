/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.tm;

public interface ConnectableResource {
    /**
     * Get connection.
     *
     * @return The connection; otherwise <code>null</code> if the method isn't supported
     * @exception Throwable If error occurs
     */
    public Object getConnection() throws Throwable;
}
