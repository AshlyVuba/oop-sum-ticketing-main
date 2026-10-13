package za.co.wethinkcode.ticketing.sales;

public class SeasonPass {

    public static final double SEASON_DISCOUNT = 0.10;
    public static final double REFUND_RATE = 0.50;
    public static final int REFUND_WINDOW_DAYS = 14;
    public static final String ENTRY_GATE = "Gate B";

    // TODO: add the class hierarchy and the private field(s)

    public SeasonPass(String ticketId, String holderName, String seatLabel, double price, int eventCount) {
        // TODO: implement the constructor
        throw new UnsupportedOperationException("TODO");
    }

    public int getEventCount() {
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
