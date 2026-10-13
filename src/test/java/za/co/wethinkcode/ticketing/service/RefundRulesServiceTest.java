package za.co.wethinkcode.ticketing.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.co.wethinkcode.ticketing.model.Booking;
import za.co.wethinkcode.ticketing.model.Customer;
import za.co.wethinkcode.ticketing.model.GateScan;
import za.co.wethinkcode.ticketing.model.ScanOutcome;
import za.co.wethinkcode.ticketing.model.TicketCategory;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RefundRulesServiceTest {

    private RefundRulesService rules;
    private Customer customer;
    private Booking booking;

    @BeforeEach
    void setUp() {
        rules = new RefundRulesService();
        customer = new Customer("CUS-01", "Lerato Dube", true);
        booking = new Booking("BK-000001", 500.0, TicketCategory.GENERAL, 10);
    }

    private void scan(Booking target, ScanOutcome outcome) {
        target.recordScan(new GateScan(LocalDateTime.of(2026, 10, 1, 19, 0), outcome, ""));
    }

    // --- canRefund ---

    @Test
    void allowsRefundWhenAllConditionsAreMet() {
        assertTrue(rules.canRefund(booking, customer));
    }

    @Test
    void deniesRefundWhenCustomerIsNotVerified() {
        Customer unverified = new Customer("CUS-02", "Sipho Dlamini", false);

        assertFalse(rules.canRefund(booking, unverified));
    }

    @Test
    void deniesRefundWhenCustomerIsBlocked() {
        customer.setBlocked(true);

        assertFalse(rules.canRefund(booking, customer));
    }

    @Test
    void deniesRefundWhenPaymentHasNotCleared() {
        booking.setPaymentCleared(false);

        assertFalse(rules.canRefund(booking, customer));
    }

    @Test
    void deniesRefundWhenBookingIsFlaggedForFraud() {
        booking.flagFraud();

        assertFalse(rules.canRefund(booking, customer));
    }

    @Test
    void deniesRefundWhenTicketWasTransferred() {
        booking.setTransferred(true);

        assertFalse(rules.canRefund(booking, customer));
    }

    @Test
    void deniesRefundWhenAlreadyRefunded() {
        booking.markRefunded();

        assertFalse(rules.canRefund(booking, customer));
    }

    @Test
    void deniesRefundWhenTheHolderWasAlreadyAdmitted() {
        scan(booking, ScanOutcome.ADMITTED);

        assertFalse(rules.canRefund(booking, customer));
    }

    @Test
    void allowsRefundWhenTheLastScanWasNotAnAdmission() {
        scan(booking, ScanOutcome.WRONG_GATE);

        assertTrue(rules.canRefund(booking, customer));
    }

    @Test
    void deniesRefundOneDayBeforeTheEvent() {
        Booking late = new Booking("BK-000002", 500.0, TicketCategory.GENERAL, 1);

        assertFalse(rules.canRefund(late, customer));
    }

    @Test
    void allowsRefundExactlyTwoDaysBeforeTheEvent() {
        Booking border = new Booking("BK-000003", 500.0, TicketCategory.GENERAL, 2);

        assertTrue(rules.canRefund(border, customer));
    }

    @Test
    void allowsRefundInsideTheWindowWhenTheEventIsCancelled() {
        Booking late = new Booking("BK-000004", 500.0, TicketCategory.GENERAL, 0);
        late.setEventCancelled(true);

        assertTrue(rules.canRefund(late, customer));
    }

    @Test
    void deniesSeasonPassRefundForNonLoyaltyMembers() {
        Booking season = new Booking("BK-000005", 2000.0, TicketCategory.SEASON, 20);

        assertFalse(rules.canRefund(season, customer));
    }

    @Test
    void allowsSeasonPassRefundForLoyaltyMembers() {
        Booking season = new Booking("BK-000006", 2000.0, TicketCategory.SEASON, 20);
        customer.setLoyaltyMember(true);

        assertTrue(rules.canRefund(season, customer));
    }

    @Test
    void vipRefundDoesNotNeedLoyaltyMembership() {
        Booking vip = new Booking("BK-000007", 1500.0, TicketCategory.VIP, 20);

        assertTrue(rules.canRefund(vip, customer));
    }

    // --- issueRefund ---

    @Test
    void issueRefundReturnsTheAmountPaidAndMarksTheBookingRefunded() {
        double refunded = rules.issueRefund(booking, customer);

        assertEquals(500.0, refunded, 0.001);
        assertTrue(booking.isRefunded());
    }

    @Test
    void issueRefundThrowsWhenTheRefundIsNotAllowed() {
        customer.setBlocked(true);

        assertThrows(IllegalStateException.class, () -> rules.issueRefund(booking, customer));
        assertFalse(booking.isRefunded());
    }

    @Test
    void aBookingCannotBeRefundedTwice() {
        rules.issueRefund(booking, customer);

        assertThrows(IllegalStateException.class, () -> rules.issueRefund(booking, customer));
    }

    // --- nextStep ---

    @Test
    void awaitsTheFirstScanWhenThereAreNoScans() {
        assertEquals("AWAIT_FIRST_SCAN", rules.nextStep(booking));
    }

    @Test
    void issuesARefundWhenTheEventIsCancelled() {
        booking.setEventCancelled(true);

        assertEquals("ISSUE_REFUND", rules.nextStep(booking));
    }

    @Test
    void aCancelledEventTakesPriorityOverScans() {
        booking.setEventCancelled(true);
        scan(booking, ScanOutcome.ADMITTED);

        assertEquals("ISSUE_REFUND", rules.nextStep(booking));
    }

    @Test
    void doesNothingOnceTheBookingHasBeenRefunded() {
        booking.setEventCancelled(true);
        booking.markRefunded();

        assertEquals("NO_ACTION", rules.nextStep(booking));
    }

    @Test
    void doesNothingWhenTheHolderWasAdmitted() {
        scan(booking, ScanOutcome.ADMITTED);

        assertEquals("NO_ACTION", rules.nextStep(booking));
    }

    @Test
    void flagsADuplicateEntryForReview() {
        scan(booking, ScanOutcome.DUPLICATE_ENTRY);

        assertEquals("FLAG_FOR_REVIEW", rules.nextStep(booking));
    }

    @Test
    void resendsTheTicketWhenInvalidAndPaymentHasCleared() {
        scan(booking, ScanOutcome.INVALID_TICKET);

        assertEquals("RESEND_TICKET", rules.nextStep(booking));
    }

    @Test
    void flagsAnInvalidTicketWhenPaymentHasNotCleared() {
        booking.setPaymentCleared(false);
        scan(booking, ScanOutcome.INVALID_TICKET);

        assertEquals("FLAG_FOR_REVIEW", rules.nextStep(booking));
    }

    @Test
    void redirectsAGeneralTicketHolderWhoWentToTheWrongGate() {
        scan(booking, ScanOutcome.WRONG_GATE);

        assertEquals("REDIRECT_TO_GATE", rules.nextStep(booking));
    }

    @Test
    void escortsAVipHolderWhoWentToTheWrongGate() {
        Booking vip = new Booking("BK-000008", 1500.0, TicketCategory.VIP, 10);
        scan(vip, ScanOutcome.WRONG_GATE);

        assertEquals("ESCORT_TO_GATE", rules.nextStep(vip));
    }

    @Test
    void flagsAHolderWhoHasBeenToTheWrongGateThreeTimes() {
        scan(booking, ScanOutcome.WRONG_GATE);
        scan(booking, ScanOutcome.WRONG_GATE);
        scan(booking, ScanOutcome.WRONG_GATE);

        assertEquals("FLAG_FOR_REVIEW", rules.nextStep(booking));
    }

    @Test
    void stillRedirectsOnTheSecondWrongGateScan() {
        scan(booking, ScanOutcome.WRONG_GATE);
        scan(booking, ScanOutcome.WRONG_GATE);

        assertEquals("REDIRECT_TO_GATE", rules.nextStep(booking));
    }

    @Test
    void onlyTheLastScanDecidesTheNextStep() {
        scan(booking, ScanOutcome.INVALID_TICKET);
        scan(booking, ScanOutcome.ADMITTED);

        assertEquals("NO_ACTION", rules.nextStep(booking));
    }
}
