package za.co.wethinkcode.ticketing.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Booking {

    private final String bookingRef;
    private final double amountPaid;
    private final TicketCategory category;
    private final int daysUntilEvent;
    private boolean eventCancelled;
    private boolean paymentCleared;
    private boolean transferred;
    private boolean fraudFlagged;
    private boolean refunded;
    private final List<GateScan> scans = new ArrayList<>();

    public Booking(String bookingRef, double amountPaid, TicketCategory category, int daysUntilEvent) {
        this.bookingRef = bookingRef;
        this.amountPaid = amountPaid;
        this.category = category;
        this.daysUntilEvent = daysUntilEvent;
        this.paymentCleared = true;
    }

    public String getBookingRef() {
        return bookingRef;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public TicketCategory getCategory() {
        return category;
    }

    public int getDaysUntilEvent() {
        return daysUntilEvent;
    }

    public boolean isEventCancelled() {
        return eventCancelled;
    }

    public void setEventCancelled(boolean eventCancelled) {
        this.eventCancelled = eventCancelled;
    }

    public boolean isPaymentCleared() {
        return paymentCleared;
    }

    public void setPaymentCleared(boolean paymentCleared) {
        this.paymentCleared = paymentCleared;
    }

    public boolean isTransferred() {
        return transferred;
    }

    public void setTransferred(boolean transferred) {
        this.transferred = transferred;
    }

    public boolean isFraudFlagged() {
        return fraudFlagged;
    }

    public void flagFraud() {
        this.fraudFlagged = true;
    }

    public boolean isRefunded() {
        return refunded;
    }

    public void markRefunded() {
        this.refunded = true;
    }

    public void recordScan(GateScan scan) {
        scans.add(scan);
    }

    public List<GateScan> getScans() {
        return Collections.unmodifiableList(scans);
    }

    public boolean isAdmitted() {
        return !scans.isEmpty() && scans.get(scans.size() - 1).getOutcome() == ScanOutcome.ADMITTED;
    }

    @Override
    public String toString() {
        return bookingRef + " (" + category + ", R" + amountPaid + ", " + daysUntilEvent + " days to go)";
    }
}
