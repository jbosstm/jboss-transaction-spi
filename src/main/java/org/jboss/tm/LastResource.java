/*
   Copyright The Narayana Authors
   SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.tm;

/**
 * A tagging interface to identify an XAResource that does
 * not support prepare and should be used in the last resource
 * gambit. i.e. It is committed after the resources are
 * prepared. If it fails to commit, roll everybody back.
 * 
 * @author <a href="mailto:adrian@jboss.org">Adrian Brock</a>
 * @version $Revision: 37459 $
 */
public interface LastResource
{
}

