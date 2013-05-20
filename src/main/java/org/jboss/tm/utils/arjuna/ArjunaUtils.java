package org.jboss.tm.utils.arjuna;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.coordinator.TwoPhaseOutcome;
import com.arjuna.ats.arjuna.coordinator.TxStats;
import com.arjuna.ats.arjuna.coordinator.TxStatsMBean;
import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionImple;
import com.arjuna.ats.internal.jta.transaction.arjunacore.jca.SubordinationManager;
import com.arjuna.ats.internal.jta.transaction.arjunacore.jca.TransactionImporter;
import com.arjuna.ats.internal.jta.transaction.arjunacore.jca.TransactionImporterImple;
import com.arjuna.ats.internal.jta.transaction.arjunacore.jca.XATerminatorImple;
import com.arjuna.ats.jbossatx.jta.RecoveryManagerService;
import com.arjuna.ats.jta.utils.XAHelper;
import org.jboss.tm.XAResourceRecoveryRegistry;

import javax.resource.spi.XATerminator;
import javax.transaction.Transaction;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ArjunaUtils {
    public static final TransactionImple getTransaction(Uid id) {
        return TransactionImple.getTransaction(id);
    }

    public static final Transaction getTransaction() {
        return TransactionImple.getTransaction();
    }

    public static final Uid getUid(Transaction transaction) {
        if (transaction instanceof TransactionImple)
            return ((TransactionImple) transaction).get_uid();

        throw new RuntimeException("wrong transaction type");
    }

    public static Xid[] getXidsToRecoverForParentNode(final String parentNodeName, int recoveryFlags) throws XAException {
        final Set<Xid> xidsToRecover = new HashSet<Xid>();
        final TransactionImporter transactionImporter = SubordinationManager.getTransactionImporter();
        if (transactionImporter instanceof TransactionImporterImple) {
            final Set<Xid> inFlightXids = ((TransactionImporterImple) transactionImporter).getInflightXids(parentNodeName);
            if (inFlightXids != null) {
                xidsToRecover.addAll(inFlightXids);
            }
        }
        final XATerminator xaTerminator = SubordinationManager.getXATerminator();
        if (xaTerminator instanceof XATerminatorImple) {
            final Xid[] inDoubtTransactions = ((XATerminatorImple) xaTerminator).doRecover(null, parentNodeName);
            if (inDoubtTransactions != null) {
                xidsToRecover.addAll(Arrays.asList(inDoubtTransactions));
            }
        } else {
            final Xid[] inDoubtTransactions = xaTerminator.recover(recoveryFlags);
            if (inDoubtTransactions != null) {
                xidsToRecover.addAll(Arrays.asList(inDoubtTransactions));
            }
        }
        return xidsToRecover.toArray(new Xid[0]);
    }

    /**
     * Returns a {@link com.arjuna.ats.internal.jta.transaction.arjunacore.jca.SubordinateTransaction} associated with the passed {@link javax.transaction.xa.Xid}.
     * If there's no such transaction, then this method returns null.
     *
     * @param xid The {@link javax.transaction.xa.Xid}
     * @return
     * @throws javax.transaction.xa.XAException
     */
    public static SubordinateTransaction getImportedTransaction(final Xid xid) throws XAException {
        final TransactionImporter transactionImporter = SubordinationManager.getTransactionImporter();
        com.arjuna.ats.internal.jta.transaction.arjunacore.jca.SubordinateTransaction subordinateTransaction = transactionImporter.getImportedTransaction(xid);
        return (SubordinateTransaction) subordinateTransaction;
    }

    /**
     * Imports a {@link javax.transaction.Transaction} into the {@link com.arjuna.ats.internal.jta.transaction.arjunacore.jca.SubordinationManager} and associates it with the
     * passed {@link org.jboss.ejb.client.XidTransactionID#getXid()}  Xid}. Returns the imported transaction
     *
     * @param xid The {@link javax.transaction.xa.Xid}
     * @param txTimeout The transaction timeout
     * @return
     * @throws javax.transaction.xa.XAException
     */
    public static Transaction importTransaction(final Xid xid, final int txTimeout) throws XAException {
        final TransactionImporter transactionImporter = SubordinationManager.getTransactionImporter();
        return transactionImporter.importTransaction(xid, txTimeout);
    }

    // see com.arjuna.ats.internal.jta.transaction.arjunacore.jca.TransactionImporter
    /**
     * Remove the subordinate (imported) transaction.
     *
     * @param xid
     *            the global transaction.
     *
     * @throws javax.transaction.xa.XAException
     *             thrown if there are any errors.
     */
    public static void removeImportedTransaction(Xid xid) throws XAException {
        SubordinationManager.getTransactionImporter().removeImportedTransaction(xid);
    }

    public static void addSerializableXAResourceDeserializer(XAResourceRecoveryRegistry xaResourceRecoveryRegistry,
                                                             SerializableXAResourceDeserializer serializableXAResourceDeserializer) {
        // TODO extend org.jboss.tm.XAResourceRecoveryRegistry adding this method and make sure com.arjuna.ats.jbossatx.jta.RecoveryManagerService works with it
        RecoveryManagerService recoveryManagerService = (RecoveryManagerService) xaResourceRecoveryRegistry;
        recoveryManagerService.addSerializableXAResourceDeserializer(serializableXAResourceDeserializer);
    }

    public static String xidToString(Xid xid) {
        return XAHelper.xidToString(xid);
    }

    public static XATerminator getXATerminator() {
        return  SubordinationManager.getXATerminator();
    }

    public static void doRecovery(XATerminator xaTerminator) throws XAException {
        if (xaTerminator instanceof XATerminatorImple) {
            // this comes from tryRecoveryForImportedTransaction#XidTransactionManagementTask - investigate

            // We intentionally pass null for Xid since passing the specific Xid doesn't seem to work for some reason.
            // As for null for parentNodeName, we do that intentionally since we aren't aware of the parent node on which
            // the transaction originated
            ((XATerminatorImple) xaTerminator).doRecover(null, null);
        } else {
            xaTerminator.recover(XAResource.TMSTARTRSCAN);
        }
    }

    public static TwoPhaseStatus toStatus(int value) {
        switch (value) {
            case TwoPhaseOutcome.PREPARE_OK:
                return TwoPhaseStatus.PREPARE_OK;
            case TwoPhaseOutcome.PREPARE_NOTOK:
                return TwoPhaseStatus.PREPARE_NOTOK;
            case TwoPhaseOutcome.PREPARE_READONLY:
                return TwoPhaseStatus.PREPARE_READONLY;
            case TwoPhaseOutcome.HEURISTIC_ROLLBACK:
                return TwoPhaseStatus.HEURISTIC_ROLLBACK;
            case TwoPhaseOutcome.HEURISTIC_COMMIT:
                return TwoPhaseStatus.HEURISTIC_COMMIT;
            case TwoPhaseOutcome.HEURISTIC_MIXED:
                return TwoPhaseStatus.HEURISTIC_MIXED;
            case TwoPhaseOutcome.HEURISTIC_HAZARD:
                return TwoPhaseStatus.HEURISTIC_HAZARD;
            case TwoPhaseOutcome.FINISH_OK:
                return TwoPhaseStatus.FINISH_OK;
            default: /* fallthru */
            case TwoPhaseOutcome.FINISH_ERROR:
                return TwoPhaseStatus.FINISH_ERROR;
            case TwoPhaseOutcome.NOT_PREPARED:
                return TwoPhaseStatus.NOT_PREPARED;
            case TwoPhaseOutcome.ONE_PHASE_ERROR:
                return TwoPhaseStatus.ONE_PHASE_ERROR;
            case TwoPhaseOutcome.INVALID_TRANSACTION:
                return TwoPhaseStatus.INVALID_TRANSACTION;
            case TwoPhaseOutcome.PREPARE_ONE_PHASE_COMMITTED:
                return TwoPhaseStatus.PREPARE_ONE_PHASE_COMMITTED;
        }
    }

    public static TxStatsMBean getTxStats() {
        return TxStats.getInstance();
    }
}