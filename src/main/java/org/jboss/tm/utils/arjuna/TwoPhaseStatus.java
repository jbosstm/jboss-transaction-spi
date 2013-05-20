package org.jboss.tm.utils.arjuna;

import com.arjuna.ats.arjuna.coordinator.TwoPhaseOutcome;

public enum TwoPhaseStatus {
    PREPARE_OK( TwoPhaseOutcome.PREPARE_OK),
    PREPARE_NOTOK( TwoPhaseOutcome.PREPARE_NOTOK),
    PREPARE_READONLY( TwoPhaseOutcome.PREPARE_READONLY),
    HEURISTIC_ROLLBACK( TwoPhaseOutcome.HEURISTIC_ROLLBACK),
    HEURISTIC_COMMIT( TwoPhaseOutcome.HEURISTIC_COMMIT),
    HEURISTIC_MIXED( TwoPhaseOutcome.HEURISTIC_MIXED),
    HEURISTIC_HAZARD( TwoPhaseOutcome.HEURISTIC_HAZARD),
    FINISH_OK( TwoPhaseOutcome.FINISH_OK),
    FINISH_ERROR( TwoPhaseOutcome.FINISH_ERROR),
    NOT_PREPARED( TwoPhaseOutcome.NOT_PREPARED),
    ONE_PHASE_ERROR( TwoPhaseOutcome.ONE_PHASE_ERROR),
    INVALID_TRANSACTION( TwoPhaseOutcome.INVALID_TRANSACTION),
    PREPARE_ONE_PHASE_COMMITTED( TwoPhaseOutcome.PREPARE_ONE_PHASE_COMMITTED);

    public final int value;

    private TwoPhaseStatus(int value) {
        this.value = value;

    }
}
