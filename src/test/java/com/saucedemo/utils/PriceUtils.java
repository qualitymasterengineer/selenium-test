package com.saucedemo.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Price rounding and calculation utilities for comparing subtotals and totals.
 */
public final class PriceUtils {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private PriceUtils() {}

    /**
     * Round a price value to 2 decimal places.
     */
    public static double roundToTwoDecimals(double value) {
        return BigDecimal.valueOf(value).setScale(SCALE, ROUNDING).doubleValue();
    }

    /**
     * Parse price string (e.g. "$29.99") to double and round to 2 decimals.
     */
    public static double parseAndRound(String priceText) {
        if (priceText == null || priceText.isBlank()) {
            return 0.0;
        }
        String cleaned = priceText.replace("$", "").replace(",", "").trim();
        double value = Double.parseDouble(cleaned);
        return roundToTwoDecimals(value);
    }

    /**
     * Sum of prices (e.g. from cart items) rounded to 2 decimals.
     */
    public static double subtotal(List<Double> prices) {
        double sum = prices.stream().mapToDouble(Double::doubleValue).sum();
        return roundToTwoDecimals(sum);
    }

    /**
     * Total = subtotal + tax, rounded to 2 decimals.
     */
    public static double total(double subtotal, double tax) {
        return roundToTwoDecimals(subtotal + tax);
    }
}
