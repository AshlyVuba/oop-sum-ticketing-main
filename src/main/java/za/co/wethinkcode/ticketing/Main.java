package za.co.wethinkcode.ticketing;

import za.co.wethinkcode.ticketing.model.Booking;
import za.co.wethinkcode.ticketing.model.Customer;
import za.co.wethinkcode.ticketing.model.GateScan;
import za.co.wethinkcode.ticketing.model.ScanOutcome;
import za.co.wethinkcode.ticketing.model.TicketCategory;
import za.co.wethinkcode.ticketing.service.RefundRulesService;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class Main {

    private static final String DATABASE_FILE = "ticketing.db";

    public static void main(String[] args) throws SQLException {
        RefundRulesService rules = new RefundRulesService();

        Customer customer = new Customer("CUS-07", "Thabo Mokoena", true);
        Booking booking = new Booking("BK-100245", 1500.00, TicketCategory.VIP, 10);

        System.out.println("Can " + customer + " refund " + booking + "? " + rules.canRefund(booking, customer));

        booking.recordScan(new GateScan(LocalDateTime.now(), ScanOutcome.WRONG_GATE, "Went to Gate C"));
        System.out.println("Next step for " + booking.getBookingRef() + ": " + rules.nextStep(booking));

        new File(DATABASE_FILE).delete();
        try (Connection connection = Database.connect(DATABASE_FILE)) {
            DatabaseSchema.createSchema(connection);
            System.out.println("Database ready at " + DATABASE_FILE);
        }
    }
}
