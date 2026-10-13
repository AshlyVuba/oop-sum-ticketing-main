package za.co.wethinkcode.ticketing.service;

import za.co.wethinkcode.ticketing.model.Booking;
import za.co.wethinkcode.ticketing.model.Customer;
import za.co.wethinkcode.ticketing.model.GateScan;
import za.co.wethinkcode.ticketing.model.ScanOutcome;
import za.co.wethinkcode.ticketing.model.TicketCategory;

public class RefundRulesService {

    public static final int MAX_SCANS = 3;
    public static final int MIN_DAYS_BEFORE_EVENT = 2;

    public boolean canRefund(Booking booking, Customer customer) {
        if (customer.isVerified() && !customer.isBlocked() && booking.isPaymentCleared() && !booking.isFraudFlagged() && !booking.isTransferred() && !booking.isRefunded() && !booking.isAdmitted() && (booking.isEventCancelled() || booking.getDaysUntilEvent() >= MIN_DAYS_BEFORE_EVENT) && (booking.getCategory() != TicketCategory.SEASON || customer.isLoyaltyMember())) {
            return true;
        }
        return false;
    }

    public double issueRefund(Booking booking, Customer customer) {
        if (!canRefund(booking, customer)) {
            throw new IllegalStateException(customer.getFullName() + " cannot refund booking " + booking.getBookingRef());
        }
        booking.markRefunded();
        return booking.getAmountPaid();
    }

    public String nextStep(Booking booking) {
        String step;
        if (!booking.isRefunded()) {
            if (!booking.isEventCancelled()) {
                if (!booking.getScans().isEmpty()) {
                    GateScan lastScan = booking.getScans().get(booking.getScans().size() - 1);
                    if (lastScan.getOutcome() == ScanOutcome.ADMITTED) {
                        step = "NO_ACTION";
                    } else {
                        if (lastScan.getOutcome() == ScanOutcome.DUPLICATE_ENTRY) {
                            step = "FLAG_FOR_REVIEW";
                        } else {
                            if (lastScan.getOutcome() == ScanOutcome.INVALID_TICKET) {
                                if (booking.isPaymentCleared()) {
                                    step = "RESEND_TICKET";
                                } else {
                                    step = "FLAG_FOR_REVIEW";
                                }
                            } else {
                                if (booking.getScans().size() >= MAX_SCANS) {
                                    step = "FLAG_FOR_REVIEW";
                                } else {
                                    if (booking.getCategory() == TicketCategory.VIP) {
                                        step = "ESCORT_TO_GATE";
                                    } else {
                                        step = "REDIRECT_TO_GATE";
                                    }
                                }
                            }
                        }
                    }
                } else {
                    step = "AWAIT_FIRST_SCAN";
                }
            } else {
                step = "ISSUE_REFUND";
            }
        } else {
            step = "NO_ACTION";
        }
        return step;
    }
}
