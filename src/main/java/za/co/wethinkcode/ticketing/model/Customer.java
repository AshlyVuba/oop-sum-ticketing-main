package za.co.wethinkcode.ticketing.model;

public class Customer {

    private final String customerId;
    private final String fullName;
    private final boolean verified;
    private boolean blocked;
    private boolean loyaltyMember;

    public Customer(String customerId, String fullName, boolean verified) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.verified = verified;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean isVerified() {
        return verified;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isLoyaltyMember() {
        return loyaltyMember;
    }

    public void setLoyaltyMember(boolean loyaltyMember) {
        this.loyaltyMember = loyaltyMember;
    }

    @Override
    public String toString() {
        return fullName + " (" + customerId + ")";
    }
}
