package com.saucedemo.data;

/**
 * Checkout form data (First Name, Last Name, Postal Code) - single reusable source.
 */
public final class CheckoutData {

    private final String firstName;
    private final String lastName;
    private final String postalCode;

    public CheckoutData(String firstName, String lastName, String postalCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.postalCode = postalCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    /** Default checkout data: John, Doe, 12345 */
    public static CheckoutData defaultData() {
        return new CheckoutData("John", "Doe", "12345");
    }
}
