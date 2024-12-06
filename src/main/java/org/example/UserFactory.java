package org.example;

import org.example.threads.Customer;
import org.example.threads.Vendor;

public class UserFactory {
    public static User createUser(String userType) {
        return switch (userType.toLowerCase()) {
            case "customer" -> new Customer();
            case "vendor" -> new Vendor();
            default -> throw new IllegalArgumentException("Unknown user type: " + userType);
        };
    }
}
