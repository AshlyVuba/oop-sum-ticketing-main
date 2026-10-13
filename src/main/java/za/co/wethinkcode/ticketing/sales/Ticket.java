package za.co.wethinkcode.ticketing.sales;

public class Ticket {

    // TODO: declare the private fields

    public Ticket(String ticketId, String holderName, String seatLabel, double price) {
        // TODO: implement the constructor
        throw new UnsupportedOperationException("TODO");
    }

    public String getTicketId() {
        throw new UnsupportedOperationException("TODO");
    }

    public String getHolderName() {
        throw new UnsupportedOperationException("TODO");
    }

    public String getSeatLabel() {
        throw new UnsupportedOperationException("TODO");
    }

    public void updateSeatLabel(String seatLabel) {
        throw new UnsupportedOperationException("TODO");
    }

    public double getPrice() {
        throw new UnsupportedOperationException("TODO");
    }

    public void updatePrice(double price) {
        throw new UnsupportedOperationException("TODO");
    }

    public boolean isUsed() {
        throw new UnsupportedOperationException("TODO");
    }

    public void markUsed() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("TODO");
    }

    /** Returns the total the customer pays for this ticket, in Rands. */
    public double totalCost() {
        throw new UnsupportedOperationException("TODO");
    }

    /** Returns the gate this ticket's holder must use. */
    public String entryGate() {
        throw new UnsupportedOperationException("TODO");
    }

    /** Returns a short label for this kind of ticket, e.g. "General". */
    public String ticketClass() {
        throw new UnsupportedOperationException("TODO");
    }
}
