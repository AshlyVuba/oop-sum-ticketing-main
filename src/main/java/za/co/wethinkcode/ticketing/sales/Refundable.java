package za.co.wethinkcode.ticketing.sales;

/**
 * A ticket that can be refunded before the event. Provided for you - do not modify.
 */
public interface Refundable {

    /**
     * Returns the amount, in Rands, that the holder gets back if the ticket is refunded.
     *
     * @return refund amount
     */
    double refundAmount();

    /**
     * Returns how many days before the event a refund can still be requested.
     *
     * @return refund window in days
     */
    int refundWindowDays();
}
