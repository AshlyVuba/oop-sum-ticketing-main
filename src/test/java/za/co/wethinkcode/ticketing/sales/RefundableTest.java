package za.co.wethinkcode.ticketing.sales;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static za.co.wethinkcode.ticketing.sales.Types.asRefundable;
import static za.co.wethinkcode.ticketing.sales.Types.asTicket;

class RefundableTest {

    @Test
    void onlyVipAndSeasonAreRefundable() {
        assertTrue(Refundable.class.isAssignableFrom(VipTicket.class));
        assertTrue(Refundable.class.isAssignableFrom(SeasonPass.class));
        assertFalse(Refundable.class.isAssignableFrom(GeneralTicket.class),
                "GeneralTicket is non-refundable");
        assertFalse(Refundable.class.isAssignableFrom(Ticket.class),
                "Ticket itself should not be Refundable");
    }

    @Test
    void vipRefundIsEightyPercentOfThePrice() {
        Refundable vip = asRefundable(new VipTicket("TK-2", "A", "V1", 1_500.0, "Lounge"));
        assertEquals(1_200.0, vip.refundAmount(), 0.001);
        assertEquals(7, vip.refundWindowDays());
    }

    @Test
    void seasonRefundIsHalfOfTheTotalCost() {
        Refundable season = asRefundable(new SeasonPass("TK-3", "A", "B7", 400.0, 5));
        assertEquals(900.0, season.refundAmount(), 0.001);
        assertEquals(14, season.refundWindowDays());
    }

    @Test
    void refundAmountFollowsPriceChanges() {
        VipTicket vip = new VipTicket("TK-2", "A", "V1", 1_500.0, "Lounge");
        asTicket(vip).updatePrice(2_000.0);
        assertEquals(1_600.0, asRefundable(vip).refundAmount(), 0.001);
    }
}
