package za.co.wethinkcode.ticketing.sales;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;
import static za.co.wethinkcode.ticketing.sales.Types.asTicket;

class TicketTypesTest {

    private GeneralTicket general;
    private VipTicket vip;
    private SeasonPass season;

    @BeforeEach
    void setUp() {
        general = new GeneralTicket("TK-1", "Nomsa Khumalo", "C12", 500.0);
        vip = new VipTicket("TK-2", "Pieter van Wyk", "V1", 1_500.0, "Lounge and parking");
        season = new SeasonPass("TK-3", "Ayesha Patel", "B7", 400.0, 5);
    }

    @Test
    void allTypesAreTickets() {
        assertInstanceOf(Ticket.class, general);
        assertInstanceOf(Ticket.class, vip);
        assertInstanceOf(Ticket.class, season);
    }

    // --- GeneralTicket ---

    @Test
    void generalCostIsPricePlusBookingFee() {
        assertEquals(GeneralTicket.BOOKING_FEE + 500.0, general.totalCost(), 0.001);
        assertEquals(520.0, general.totalCost(), 0.001);
    }

    @Test
    void generalGateAndClass() {
        assertEquals("Gate C", general.entryGate());
        assertEquals("General", general.ticketClass());
    }

    @Test
    void generalCostFollowsPriceChanges() {
        asTicket(general).updatePrice(800.0);
        assertEquals(820.0, general.totalCost(), 0.001);
    }

    @Test
    void generalToStringIncludesClassAndId() {
        assertTrue(general.toString().contains("General"));
        assertTrue(general.toString().contains("TK-1"));
    }

    // --- VipTicket ---

    @Test
    void vipCostIsSurchargedPricePlusBookingFee() {
        // 1500 * 1.15 = 1725, plus the 45 booking fee
        assertEquals(1_770.0, vip.totalCost(), 0.001);
    }

    @Test
    void vipGateAndClass() {
        assertEquals("Gate A", vip.entryGate());
        assertEquals("VIP", vip.ticketClass());
    }

    @Test
    void vipPerksAreStored() {
        assertEquals("Lounge and parking", vip.getPerks());
    }

    @Test
    void vipPerksArePrivateAndImmutable() throws NoSuchFieldException {
        int modifiers = VipTicket.class.getDeclaredField("perks").getModifiers();
        assertTrue(Modifier.isPrivate(modifiers) && Modifier.isFinal(modifiers));
    }

    @Test
    void vipToStringIncludesClassAndPerks() {
        assertTrue(vip.toString().contains("VIP"));
        assertTrue(vip.toString().contains("Lounge and parking"));
    }

    // --- SeasonPass ---

    @Test
    void seasonCostIsPricePerEventTimesEventsLessDiscount() {
        // 400 * 5 events = 2000, less 10% = 1800
        assertEquals(1_800.0, season.totalCost(), 0.001);
    }

    @Test
    void seasonGateAndClass() {
        assertEquals("Gate B", season.entryGate());
        assertEquals("Season", season.ticketClass());
    }

    @Test
    void seasonEventCountIsStored() {
        assertEquals(5, season.getEventCount());
    }

    @Test
    void seasonEventCountIsPrivateAndImmutable() throws NoSuchFieldException {
        int modifiers = SeasonPass.class.getDeclaredField("eventCount").getModifiers();
        assertTrue(Modifier.isPrivate(modifiers) && Modifier.isFinal(modifiers));
    }

    @Test
    void seasonRejectsAnEventCountBelowOne() {
        assertThrows(IllegalArgumentException.class, () -> new SeasonPass("TK-9", "A", "B", 400.0, 0));
        assertThrows(IllegalArgumentException.class, () -> new SeasonPass("TK-9", "A", "B", 400.0, -3));
    }

    @Test
    void seasonToStringIncludesClassAndEventCount() {
        assertTrue(season.toString().contains("Season"));
        assertTrue(season.toString().contains("5"));
    }

    // --- Through a Ticket reference ---

    @Test
    void entryGateThroughTicketReference() {
        Ticket[] tickets = {asTicket(general), asTicket(vip), asTicket(season)};
        String[] expected = {"Gate C", "Gate A", "Gate B"};
        for (int i = 0; i < tickets.length; i++) {
            assertEquals(expected[i], tickets[i].entryGate());
        }
    }
}
