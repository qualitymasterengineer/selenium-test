package com.saucedemo.data;

/**
 * Test user credentials for SauceDemo (from env/config or fixed values).
 */
public final class UserCredentials {

    private final String username;
    private final String password;

    public UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static UserCredentials standardUser(String password) {
        return new UserCredentials("standard_user", password);
    }

    public static UserCredentials lockedOutUser(String password) {
        return new UserCredentials("locked_out_user", password);
    }

    public static UserCredentials problemUser(String password) {
        return new UserCredentials("problem_user", password);
    }

    public static UserCredentials performanceGlitchUser(String password) {
        return new UserCredentials("performance_glitch_user", password);
    }

    /** Default: standard_user / secret_sauce (from config). */
    public static UserCredentials defaultUser(String defaultPassword) {
        return new UserCredentials("standard_user", defaultPassword);
    }
}
