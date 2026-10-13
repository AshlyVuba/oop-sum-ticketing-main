package za.co.wethinkcode.ticketing.sales;

public class VipTicket {

    public static final double VIP_SURCHARGE = 0.15;
    public static final double BOOKING_FEE = 45.00;
    public static final double REFUND_RATE = 0.80;
    public static final int REFUND_WINDOW_DAYS = 7;
    public static final String ENTRY_GATE = "Gate A";

    // TODO: add the class hierarchy and the private field(s)

    public VipTicket(String ticketId, String holderName, String seatLabel, double price, String perks) {
        // TODO: implement the constructor
        throw new UnsupportedOperationException("TODO");
    }

    public String getPerks() {
        throw new UnsupportedOperationException("TODO");
    }

    public double totalCost() {
        throw new UnsupportedOperationException("TODO");
    }

    public String entryGate() {
        throw new UnsupportedOperationException("TODO");
    }

    public String ticketClass() {
        throw new UnsupportedOperationException("TODO");
    }

    public double refundAmount() {
        throw new UnsupportedOperationException("TODO");
    }

    public int refundWindowDays() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("TODO");
    }
}
