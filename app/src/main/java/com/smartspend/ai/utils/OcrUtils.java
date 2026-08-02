package com.smartspend.ai.utils;

import android.graphics.Bitmap;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.smartspend.ai.models.Expense;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class OcrUtils {
    private static final Pattern MONEY = Pattern.compile("(?i)(?<!\\d)(?:₹|\\$|€|£|¥|Rs\\.?|INR|USD|EUR|GBP|JPY|CAD|AUD|SGD|AED)?\\s*([0-9]{1,3}(?:[, ]?[0-9]{3})*(?:[.,][0-9]{2})|[0-9]{1,7})(?!\\d)");
    private OcrUtils() {}
    public interface OcrCallback { void onSuccess(ScannedReceipt receipt); void onFailure(Exception error); }
    public static class ScannedReceipt {
        public String merchantName = "Unknown Merchant";
        public double amount;
        public String date = "";
        public long dateMillis;
        public String suggestedCategory = Expense.CATEGORY_OTHER;
        public String currencyCode = CurrencyUtils.getAccountCurrency();
        public int confidencePercent;
        public String qualityWarning = "";
    }

    public static void scanReceipt(Bitmap bitmap, OcrCallback callback) {
        if (bitmap == null) { callback.onFailure(new IllegalArgumentException("Image is empty")); return; }
        String warning = assessImageQuality(bitmap);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        recognizer.process(InputImage.fromBitmap(bitmap, 0)).addOnSuccessListener(text -> {
            ScannedReceipt receipt = parseReceiptText(text);
            receipt.qualityWarning = warning;
            receipt.confidencePercent = calculateConfidence(receipt);
            callback.onSuccess(receipt);
        }).addOnFailureListener(callback::onFailure).addOnCompleteListener(ignored -> recognizer.close());
    }

    private static ScannedReceipt parseReceiptText(Text visionText) {
        ScannedReceipt receipt = new ScannedReceipt();
        String fullText = visionText.getText();
        receipt.amount = extractAmount(fullText);
        receipt.merchantName = extractMerchantName(visionText);
        receipt.date = extractDate(fullText);
        receipt.dateMillis = parseDateMillis(receipt.date);
        receipt.currencyCode = detectCurrency(fullText);
        receipt.suggestedCategory = AiEngine.autoCategorizeMerchant(receipt.merchantName);
        return receipt;
    }

    static double extractAmount(String text) {
        if (text == null) return 0;
        double best = 0; int bestScore = Integer.MIN_VALUE;
        for (String line : text.split("\\R")) {
            String lower = line.toLowerCase(Locale.US);
            int score = 0;
            if (lower.matches(".*(grand\\s*total|amount\\s*(payable|due)|net\\s*total|balance\\s*due).*")) score += 12;
            else if (lower.contains("total")) score += 8;
            else if (lower.contains("payable") || lower.contains("due")) score += 6;
            if (lower.matches(".*(subtotal|sub total|tax|gst|vat|change|cash|tendered|discount).*")) score -= 9;
            if (lower.matches(".*\\d{1,4}[/.-]\\d{1,2}[/.-]\\d{1,4}.*")) score -= 8;
            if (lower.matches(".*(invoice|phone|tel|gstin|order|reference).*")) score -= 4;
            Matcher matcher = MONEY.matcher(line); double candidate = 0;
            while (matcher.find()) {
                double parsed = parseMoney(matcher.group(1));
                if (parsed > 0 && parsed < 100000000 && !looksLikeYearOrIdentifier(matcher.group(1), parsed)) candidate = parsed;
            }
            if (candidate > 0 && (score > bestScore || (score == bestScore && candidate > best))) { best = candidate; bestScore = score; }
        }
        return best;
    }

    private static boolean looksLikeYearOrIdentifier(String raw, double value) {
        String digits = raw.replaceAll("\\D", "");
        if (!raw.matches(".*[.,]\\d{2}$") && value >= 1900 && value <= 2200) return true;
        return digits.length() > 7;
    }

    private static double parseMoney(String raw) {
        try {
            String value = raw.replace(" ", "");
            if (value.contains(",") && value.contains(".")) value = value.replace(",", "");
            else if (value.matches(".*,[0-9]{2}$")) value = value.replace(',', '.');
            else value = value.replace(",", "");
            return Double.parseDouble(value);
        } catch (NumberFormatException ignored) { return 0; }
    }

    static String detectCurrency(String text) {
        if (text == null) return CurrencyUtils.getAccountCurrency();
        String upper = text.toUpperCase(Locale.US);
        if (upper.contains("₹") || upper.matches("(?s).*(\\bINR\\b|\\bRS\\.?).*")) return "INR";
        if (upper.contains("€") || upper.contains("EUR")) return "EUR";
        if (upper.contains("£") || upper.contains("GBP")) return "GBP";
        if (upper.contains("¥") || upper.contains("JPY")) return "JPY";
        for (String code : new String[]{"CAD", "AUD", "SGD", "AED", "USD"}) if (upper.contains(code)) return code;
        if (upper.contains("$")) return "USD";
        return CurrencyUtils.getAccountCurrency();
    }

    private static String extractMerchantName(Text visionText) {
        for (Text.TextBlock block : visionText.getTextBlocks()) for (Text.Line line : block.getLines()) {
            String value = line.getText().trim(); String lower = value.toLowerCase(Locale.US);
            if (value.length() > 2 && value.length() <= 80 && !value.matches(".*\\d{2}[/.-]\\d{2}[/.-]\\d{2,4}.*")
                    && !lower.matches(".*(invoice|receipt|gst|tax|phone|tel|date|total).*")) return capitalizeWords(value);
        }
        return "Unknown Merchant";
    }

    static String extractDate(String text) {
        Pattern[] patterns = {
            Pattern.compile("\\b\\d{4}[-/.]\\d{1,2}[-/.]\\d{1,2}\\b"),
            Pattern.compile("\\b\\d{1,2}[/.-]\\d{1,2}[/.-]\\d{2,4}\\b"),
            Pattern.compile("\\b\\d{1,2}\\s+(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*\\s+\\d{2,4}\\b", Pattern.CASE_INSENSITIVE)
        };
        if (text != null) for (Pattern pattern : patterns) { Matcher matcher = pattern.matcher(text); if (matcher.find()) return matcher.group(); }
        return "";
    }

    static long parseDateMillis(String value) {
        if (value == null || value.isEmpty()) return 0;
        String[] formats = {"yyyy-M-d", "yyyy/M/d", "yyyy.M.d", "d/M/yyyy", "d-M-yyyy", "d.M.yyyy", "d/M/yy", "d-M-yy", "d MMM yyyy", "d MMMM yyyy", "d MMM yy"};
        for (String format : formats) try {
            java.text.SimpleDateFormat parser = new java.text.SimpleDateFormat(format, Locale.ENGLISH); parser.setLenient(false);
            java.util.Date date = parser.parse(value); if (date != null) return date.getTime();
        } catch (java.text.ParseException ignored) {}
        return 0;
    }

    public static String assessImageQuality(Bitmap bitmap) {
        int width = bitmap.getWidth(), height = bitmap.getHeight();
        if (Math.min(width, height) < 720) return "Low-resolution image. Retake closer, in focus, with the full receipt visible.";
        long contrast = 0, samples = 0;
        int step = Math.max(2, Math.min(width, height) / 160);
        for (int y = step; y < height; y += step) for (int x = step; x < width; x += step) {
            int here = bitmap.getPixel(x, y), left = bitmap.getPixel(x - step, y);
            int a = ((here >> 16) & 255) * 3 + ((here >> 8) & 255) * 6 + (here & 255);
            int b = ((left >> 16) & 255) * 3 + ((left >> 8) & 255) * 6 + (left & 255);
            contrast += Math.abs(a - b); samples++;
        }
        if (samples > 0 && contrast / samples < 35) return "The image may be blurred or low contrast. Retake it in even light and tap to focus.";
        return "";
    }
    private static int calculateConfidence(ScannedReceipt receipt) {
        int score = 10;
        if (receipt.amount > 0) score += 45;
        if (!"Unknown Merchant".equals(receipt.merchantName)) score += 25;
        if (receipt.dateMillis > 0) score += 15;
        if (receipt.qualityWarning.isEmpty()) score += 5;
        return Math.min(score, 100);
    }

    private static String capitalizeWords(String text) {
        StringBuilder output = new StringBuilder();
        for (String word : text.toLowerCase(Locale.getDefault()).split("\\s+")) if (!word.isEmpty()) {
            if (output.length() > 0) output.append(' '); output.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return output.toString();
    }
}
