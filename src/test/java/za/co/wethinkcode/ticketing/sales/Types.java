package za.co.wethinkcode.ticketing.sales;

/**
 * Until you add `extends Ticket` and `implements Refundable`, Java will not let the tests treat
 * your tickets as a Ticket or a Refundable. Converting through Object lets the whole project
 * compile before you start; the conversion only succeeds once your class hierarchy is right.
 */
final class Types {

    private Types() {
    }

    static Ticket asTicket(Object ticket) {
        return (Ticket) ticket;
    }

    static Refundable asRefundable(Object ticket) {
        return (Refundable) ticket;
    }
}
