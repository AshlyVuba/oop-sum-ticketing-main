package za.co.wethinkcode.ticketing.sales;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static za.co.wethinkcode.ticketing.sales.Types.asTicket;

class BoxOfficeTest {

    private BoxOffice office;
    private Ticket general;
    private Ticket vip;
    private Ticket season;

    @BeforeEach
    void setUp() {
        office = new BoxOffice("Ellis Park");
        general = asTicket(new GeneralTicket("TK-1", "Nomsa Khumalo", "C12", 500.0));            // cost 520
        vip = asTicket(new VipTicket("TK-2", "Pieter van Wyk", "V1", 1_500.0, "Lounge"));          // cost 1770, refund 1200
        season = asTicket(new SeasonPass("TK-3", "Ayesha Patel", "B7", 400.0, 5));                 // cost 1800, refund 900
    }

    private void addAll() {
        office.addTicket(general);
        office.addTicket(vip);
        office.addTicket(season);
    }

    @Test
    void venueNameIsStored() {
        assertEquals("Ellis Park", office.getVenueName());
    }

    @Test
    void newOfficeHasNoTickets() {
        assertTrue(office.getTickets().isEmpty());
    }

    @Test
    void addAndFindTicket() {
        addAll();
        assertSame(vip, office.findTicket("TK-2"));
        assertEquals(3, office.getTickets().size());
    }

    @Test
    void findReturnsNullWhenMissing() {
        assertNull(office.findTicket("TK-404"));
    }

    @Test
    void addRejectsDuplicateTicketId() {
        office.addTicket(general);
        Ticket duplicate = asTicket(new GeneralTicket("TK-1", "Someone Else", "D1", 100.0));
        assertThrows(IllegalArgumentException.class, () -> office.addTicket(duplicate));
        assertEquals(1, office.getTickets().size());
    }

    @Test
    void getTicketsCannotBeModifiedFromOutside() {
        addAll();
        assertThrows(UnsupportedOperationException.class, () -> office.getTickets().add(general));
    }

    @Test
    void refundableTicketsContainsOnlyRefundableTickets() {
        addAll();
        List<Refundable> refundable = office.refundableTickets();
        assertEquals(2, refundable.size());
        assertTrue(refundable.contains((Refundable) vip));
        assertTrue(refundable.contains((Refundable) season));
    }

    @Test
    void refundableTicketsIsACopy() {
        addAll();
        office.refundableTickets().clear();
        assertEquals(2, office.refundableTickets().size());
    }

    @Test
    void refundableTicketsIsEmptyWhenThereAreNone() {
        office.addTicket(general);
        assertTrue(office.refundableTickets().isEmpty());
    }

    @Test
    void totalRevenueIsZeroForAnEmptyOffice() {
        assertEquals(0.0, office.totalRevenue(), 0.001);
    }

    @Test
    void totalRevenueSumsEveryTicketsCost() {
        addAll();
        assertEquals(4_090.0, office.totalRevenue(), 0.001);
    }

    @Test
    void totalRevenueGivesTheSameAnswerEveryTimeItIsAsked() {
        addAll();
        office.totalRevenue();
        assertEquals(4_090.0, office.totalRevenue(), 0.001);
    }

    @Test
    void totalRefundLiabilitySumsOnlyRefundableTickets() {
        addAll();
        assertEquals(2_100.0, office.totalRefundLiability(), 0.001);
    }

    @Test
    void totalRefundLiabilityIsZeroWhenNothingIsRefundable() {
        office.addTicket(general);
        assertEquals(0.0, office.totalRefundLiability(), 0.001);
    }
}
