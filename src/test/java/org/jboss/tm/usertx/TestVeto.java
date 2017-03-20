package org.jboss.tm.usertx;

import org.jboss.tm.listener.event.AssociationLifecycleEventType;
import org.jboss.tm.listener.event.TransactionLifecycleListener;
import org.jboss.tm.listener.event.VetoingTransactionLifecycleListener;
import org.junit.Test;

import javax.transaction.SystemException;
import javax.transaction.Transaction;

import java.util.EnumSet;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestVeto {
    @Test
    public void testVeto() throws Exception {
        TMDelegate tm = new TMDelegate();
        final boolean[] vetod = {false};

        VetoingTransactionLifecycleListener<RollingbackLifecycleEventType> vetoingTransactionLifecycleListener = transactionEvent -> vetod[0] = true;

        tm.begin();

        try {
            // add a listener which can veto an about to rollback event
            tm.addListener(tm.getTransaction(), vetoingTransactionLifecycleListener, EnumSet.allOf(RollingbackLifecycleEventType.class));

            tm.rollback();
            fail("expected a SecurityException but got nothing"); // vetoing listener should have triggered
        } catch (SecurityException e) {
            assertTrue(vetod[0]);
            tm.commit();
        } catch (SystemException e) {
            fail("expected a SecurityException but got " + e.getMessage());
        }
    }

    @Test
    public void testTM() throws Exception {
        TMDelegate tm = new TMDelegate();
        final boolean[] associating = {false};
        final boolean[] associated = {false};

        TransactionLifecycleListener<AssociationLifecycleEventType> l1 = transactionEvent -> associated[0] = true;
        TransactionLifecycleListener<AssociatingLifecycleEventType> l2 = transactionEvent -> associating[0] = true;

        tm.begin();

        // add a listener for thread to transaction association/disassociation events
        tm.addListener(tm.getTransaction(), l1, EnumSet.allOf(AssociationLifecycleEventType.class));
        assertTrue("Should have got a defered AssociationLifecycleEventType.ASSOCIATED event", associated[0]);

        // add a listener for a new kind of event type
        tm.addListener(tm.getTransaction(), l2, EnumSet.allOf(AssociatingLifecycleEventType.class));

        Transaction txn = tm.suspend();

        tm.resume(txn); // this should notify any listeners that a thread is about to be associated with txn
        assertTrue("Should have got a defered AssociatingLifecycleEventType.ASSOCIATING event", associating[0]);

        tm.commit();
    }
}