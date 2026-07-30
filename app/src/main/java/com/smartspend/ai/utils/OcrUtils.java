package com.smartspend.ai.utils;

import android.graphics.Bitmap;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.smartspend.ai.models.Expense;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OcrUtils {

    public interface OcrCallback {
        void onSuccess(ScannedReceipt receipt);
        void onFailure(Exception e);
    }

    public static class ScannedReceipt {
        public String merchantName;
        public double amount;
        public String date;
        public long dateMillis;

        public String suggestedCategory;
    }

    public static void scanReceipt(Bitmap bitmap, OcrCallback callback) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        recognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    ScannedReceipt receipt = parseReceiptText(visionText);
                    callback.onSuccess(receipt);
                })
                .addOnFailureListener(callback::onFailure)
                .addOnCompleteListener(ignored -> recognizer.close());
    }

    private static ScannedReceipt parseReceiptText(Text visionText) {
        ScannedReceipt receipt = new ScannedReceipt();
        String fullText = visionText.getText();


        // Extract amount - look for total amount patterns
        receipt.amount = extractAmount(fullText);

        // Extract merchant name (usually first few lines)
        receipt.merchantName = extractMerchantName(visionText);

        // Extract date
        receipt.date = extractDate(fullText);
        receipt.dateMillis = parseDateMillis(receipt.date);

        // Auto-categorize
        receipt.suggestedCategory = AiEngine.autoCategorizeMerchant(receipt.merchantName);

        return receipt;
    }

    static double extractAmount(String text) {
        // Pattern: Total, Grand Total, Amount, TOTAL
        String[] totalKeywords = {"grand total", "total amount", "total", "amount", "net amount", "payable"};
        String lowerText = text.toLowerCase();

        for (String keyword : totalKeywords) {
            int index = lowerText.lastIndexOf(keyword);
            if (index != -1) {
                String afterKeyword = text.substring(index + keyword.length(), Math.min(index + keyword.length() + 50, text.length()));
                double amount = extractNumberFromString(afterKeyword);
                if (amount > 0) return amount;
            }
        }

        // Fallback: find the largest number in the text (likely the total)
        Pattern amountPattern = Pattern.compile("(?:₹|Rs\\.?|INR)?\\s*(\\d{1,6}(?:[,.]\\d{2,3})*)");
        Matcher matcher = amountPattern.matcher(text);
        double maxAmount = 0;
        while (matcher.find()) {
            try {
                String numStr = matcher.group(1).replace(",", "");
                double amount = Double.parseDouble(numStr);
                if (amount > maxAmount && amount < 1000000) maxAmount = amount;
            } catch (NumberFormatException ignored) {}
        }
        return maxAmount;
    }

    private static double extractNumberFromString(String text) {
        Pattern pattern = Pattern.compile("(?:₹|Rs\\.?|INR)?\\s*(\\d{1,6}(?:[,.]\\d{2,3})*)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1).replace(",", ""));
            } catch (NumberFormatException ignored) {}
        }
        return 0;
    }

    private static String extractMerchantName(Text visionText) {
        // Merchant is typically in the first block, largest text
        if (visionText.getTextBlocks().isEmpty()) return "Unknown Merchant";

        // Get first non-empty line
        for (Text.TextBlock block : visionText.getTextBlocks()) {
            for (Text.Line line : block.getLines()) {
                String text = line.getText().trim();
                if (text.length() > 2 && !text.matches(".*\\d{2}/\\d{2}/\\d{2,4}.*")) {
                    return capitalizeWords(text);
                }
            }
        }
        return "Unknown Merchant";
    }

    static String extractDate(String text) {
        // Patterns: DD/MM/YYYY, DD-MM-YYYY, DD.MM.YYYY
        Pattern[] datePatterns = {
                Pattern.compile("(\\d{1,2})[/\\-\\.](\\d{1,2})[/\\-\\.](\\d{2,4})"),
                Pattern.compile("(\\d{1,2})\\s+(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s+(\\d{2,4})", Pattern.CASE_INSENSITIVE)
        };

        for (Pattern pattern : datePatterns) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.group(0);
            }
        }
        return "";
    }

    static long parseDateMillis(String value) {
        if (value == null || value.isEmpty()) return 0;
        String[] formats = {"d/M/yyyy", "d-M-yyyy", "d.M.yyyy", "d/M/yy", "d-M-yy", "d MMM yyyy", "d MMM yy"};
        for (String format : formats) {
            try {
                java.text.SimpleDateFormat parser = new java.text.SimpleDateFormat(format, java.util.Locale.ENGLISH);
                parser.setLenient(false);
                java.util.Date date = parser.parse(value);
                if (date != null) return date.getTime();
            } catch (java.text.ParseException ignored) { }
        }
        return 0;
    }
    private static String capitalizeWords(String text) {
        String[] words = text.toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) sb.append(word.substring(1));
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }
}
