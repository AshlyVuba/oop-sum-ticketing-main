package za.co.wethinkcode.ticketing.sales;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;
import static za.co.wethinkcode.ticketing.sales.Types.asTicket;

class TicketTest {

    private Ticket ticket;

    @BeforeEach
    void setUp() {
        ticket = asTicket(new GeneralTicket("TK-1001", "Nomsa Khumalo", "C12", 500.0));
    }

    @Test
    void ticketIsAbstract() {
        assertTrue(Modifier.isAbstract(Ticket.class.getModifiers()),
                "Ticket should be abstract - every ticket is a specific kind");
    }

    @Test
    void ticketDeclaresAbstractPricingAndEntryMethods() throws NoSuchMethodException {
        assertTrue(Modifier.isAbstract(Ticket.class.getDeclaredMethod("totalCost").getModifiers()));
        assertTrue(Modifier.isAbstract(Ticket.class.getDeclaredMethod("entryGate").getModifiers()));
        assertTrue(Modifier.isAbstract(Ticket.class.getDeclaredMethod("ticketClass").getModifiers()));
    }

    @Test
    void allTicketFieldsArePrivate() throws NoSuchFieldException {
        for (String name : new String[]{"ticketId", "holderName", "seatLabel", "price", "used"}) {
            assertTrue(Modifier.isPrivate(Ticket.class.getDeclaredField(name).getModifiers()), name + " should be private");
        }
    }

    @Test
    void ticketIdAndHolderAreImmutable() throws NoSuchFieldException {
        assertTrue(Modifier.isFinal(Ticket.class.getDeclaredField("ticketId").getModifiers()));
        assertTrue(Modifier.isFinal(Ticket.class.getDeclaredField("holderName").getModifiers()));
    }

    @Test
    void noTicketFieldIsStatic() {
        assertTrue(Ticket.class.getDeclaredFields().length > 0, "Ticket does not declare any fields yet");
        for (Field field : Ticket.class.getDeclaredFields()) {
            assertFalse(Modifier.isStatic(field.getModifiers()),
                    field.getName() + " is static, so every ticket would share it");
        }
    }

    @Test
    void constructorStoresValues() {
        assertEquals("TK-1001", ticket.getTicketId());
        assertEquals("Nomsa Khumalo", ticket.getHolderName());
        assertEquals("C12", ticket.getSeatLabel());
        assertEquals(500.0, ticket.getPrice(), 0.001);
    }

    @Test
    void newTicketIsNotUsed() {
        assertFalse(ticket.isUsed());
    }

    @Test
    void markUsedMarksTheTicketAsUsed() {
        ticket.markUsed();
        assertTrue(ticket.isUsed());
    }

    @Test
    void constructorRejectsZeroOrNegativePrice() {
        assertThrows(IllegalArgumentException.class,
                () -> new GeneralTicket("TK-1", "A", "B1", 0));
        assertThrows(IllegalArgumentException.class,
                () -> new GeneralTicket("TK-2", "A", "B2", -1));
    }

    @Test
    void updatePriceRejectsZeroOrNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> ticket.updatePrice(0));
        assertEquals(500.0, ticket.getPrice(), 0.001, "a rejected update must not change the price");
    }

    @Test
    void updatePriceChangesThePrice() {
        ticket.updatePrice(650.0);
        assertEquals(650.0, ticket.getPrice(), 0.001);
    }

    @Test
    void updateSeatLabelChangesTheSeat() {
        ticket.updateSeatLabel("D4");
        assertEquals("D4", ticket.getSeatLabel());
    }

    @Test
    void cannotChangeSeatOnceTheTicketIsUsed() {
        ticket.markUsed();
        assertThrows(IllegalStateException.class, () -> ticket.updateSeatLabel("D4"));
        assertEquals("C12", ticket.getSeatLabel());
    }

    @Test
    void toStringIncludesTicketIdAndHolder() {
        String text = ticket.toString();
        assertTrue(text.contains("TK-1001"));
        assertTrue(text.contains("Nomsa Khumalo"));
    }

    @Test
    void twoTicketsDoNotShareState() {
        Ticket other = asTicket(new GeneralTicket("TK-2002", "Pieter van Wyk", "A1", 300.0));
        other.markUsed();
        other.updatePrice(999.0);

        assertEquals("TK-1001", ticket.getTicketId());
        assertEquals("Nomsa Khumalo", ticket.getHolderName());
        assertEquals(500.0, ticket.getPrice(), 0.001);
        assertFalse(ticket.isUsed());
    }
}
