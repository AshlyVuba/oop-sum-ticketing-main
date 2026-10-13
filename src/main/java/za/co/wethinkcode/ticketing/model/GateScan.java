package za.co.wethinkcode.ticketing.model;

import java.time.LocalDateTime;

public class GateScan {

    private final LocalDateTime scannedAt;
    private final ScanOutcome outcome;
    private final String gateNote;

    public GateScan(LocalDateTime scannedAt, ScanOutcome outcome, String gateNote) {
        this.scannedAt = scannedAt;
        this.outcome = outcome;
        this.gateNote = gateNote;
    }

    public LocalDateTime getScannedAt() {
        return scannedAt;
    }

    public ScanOutcome getOutcome() {
        return outcome;
    }

    public String getGateNote() {
        return gateNote;
    }

    @Override
    public String toString() {
        return outcome + " at " + scannedAt;
    }
}
