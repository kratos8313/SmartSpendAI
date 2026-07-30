package com.smartspend.ai.utils;

import static org.junit.Assert.*;
import org.junit.Test;

public class OcrUtilsTest {
    @Test public void explicitGrandTotalBeatsSubtotalTaxAndYear() {
        String receipt = "GSTIN 123456789\nSubtotal 450.00\nTax 49.50\nGrand Total ₹499.50\n2026";
        assertEquals(499.50, OcrUtils.extractAmount(receipt), 0.001);
    }
    @Test public void amountPayableBeatsCashTendered() {
        String receipt = "AMOUNT PAYABLE USD 31.25\nCash tendered 50.00\nChange 18.75";
        assertEquals(31.25, OcrUtils.extractAmount(receipt), 0.001);
    }
    @Test public void supportsEuropeanDecimalAmount() {
        assertEquals(19.95, OcrUtils.extractAmount("TOTAL EUR 19,95"), 0.001);
    }
    @Test public void yearIsNotUsedAsFallbackTotal() {
        assertEquals(8.5, OcrUtils.extractAmount("Invoice 2026\nCoffee 8.50"), 0.001);
    }
    @Test public void extractsAndParsesReceiptDates() {
        String european = OcrUtils.extractDate("Invoice date 29/07/2026");
        assertEquals("29/07/2026", european);
        assertTrue(OcrUtils.parseDateMillis(european) > 0);
        String iso = OcrUtils.extractDate("Issued 2026-07-29");
        assertEquals("2026-07-29", iso);
        assertTrue(OcrUtils.parseDateMillis(iso) > 0);
    }
    @Test public void detectsCurrencyMarkers() {
        assertEquals("INR", OcrUtils.detectCurrency("Grand Total ₹499.00"));
        assertEquals("EUR", OcrUtils.detectCurrency("TOTAL EUR 12.20"));
        assertEquals("USD", OcrUtils.detectCurrency("TOTAL $12.20"));
    }
}