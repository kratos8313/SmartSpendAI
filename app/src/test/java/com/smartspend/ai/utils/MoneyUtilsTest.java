package com.smartspend.ai.utils;

import static org.junit.Assert.*;
import org.junit.Test;

public class MoneyUtilsTest {
    @Test public void normalizeUsesBankersRounding() {
        assertEquals(10.02, MoneyUtils.normalize(10.015), 0.0001);
        assertEquals(10.00, MoneyUtils.normalize(10.005), 0.0001);
    }

    @Test public void normalizeUsesCurrencyFractionDigits() {
        assertEquals(10.0, MoneyUtils.normalize(10.49, "JPY"), 0.0001);
        assertEquals(10.126, MoneyUtils.normalize(10.1255, "KWD"), 0.0001);
    }

    @Test public void rejectsNonFiniteValues() {
        assertThrows(IllegalArgumentException.class, () -> MoneyUtils.normalize(Double.NaN));
        assertFalse(MoneyUtils.isPositive(Double.POSITIVE_INFINITY));
        assertFalse(MoneyUtils.isPositive(0));
    }
}