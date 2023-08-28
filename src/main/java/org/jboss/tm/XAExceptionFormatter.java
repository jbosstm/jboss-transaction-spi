/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.tm;

import org.jboss.logging.Logger;
import javax.transaction.xa.XAException;

/**
 * XAExceptionFormatter
 *
 * @author <a href="mailto:d_jencks@users.sourceforge.net">David Jencks</a>
 * @version $Revision: 37459 $
 */
@Deprecated
public interface XAExceptionFormatter
{
   /**
    * Format the exception and log it
    * 
    * @param xae the exception
    * @param log the log
    */
   void formatXAException(XAException xae, Logger log);
}
