package com.smartspend.ai.utils;

import static org.junit.Assert.*;
import org.junit.Test;

public class OcrUtilsTest {
    @Test public void extractsExplicitTotalBeforeUnrelatedLargeNumbers() {
        String receipt = "GSTIN 123456789\nSubtotal 450.00\nGrand Total ₹499.50\n2026";
        assertEquals(499.50, OcrUtils.extractAmount(receipt), 0.001);
    }

    @Test public void extractsAndParsesReceiptDate() {
        String value = OcrUtils.extractDate("Invoice date 29/07/2026");
        assertEquals("29/07/2026", value);
        assertTrue(OcrUtils.parseDateMillis(value) > 0);
    }
}