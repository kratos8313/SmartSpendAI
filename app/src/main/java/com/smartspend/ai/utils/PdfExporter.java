package com.smartspend.ai.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import androidx.core.content.FileProvider;
import com.smartspend.ai.models.Expense;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class PdfExporter {
    private PdfExporter() {}
    public static Intent exportMonthlyReport(Context context, List<Expense> expenses) throws Exception {
        MonthlyReportSummary summary = MonthlyReportSummary.from(expenses, Collections.emptyList(), 0, CurrencyUtils.getAccountCurrency());
        return exportMonthlyReport(context, expenses, summary, summary.deterministicNarrative());
    }

    public static Intent exportMonthlyReport(Context context, List<Expense> expenses,
            MonthlyReportSummary summary, String narrative) throws Exception {
        List<Expense> safeExpenses = expenses == null ? Collections.emptyList() : expenses;
        if (summary == null) summary = MonthlyReportSummary.from(safeExpenses, Collections.emptyList(), 0, CurrencyUtils.getAccountCurrency());
        File directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (directory == null) throw new IllegalStateException("Documents directory unavailable");
        if (!directory.exists() && !directory.mkdirs()) throw new IllegalStateException("Cannot create documents directory");
        String month = new SimpleDateFormat("yyyy-MM", Locale.US).format(new Date());
        File output = new File(directory, "SmartSpend-Report-" + month + ".pdf");
        PdfDocument document = new PdfDocument();
        Paint title = new Paint(); title.setTextSize(22); title.setFakeBoldText(true);
        Paint text = new Paint(); text.setTextSize(11);
        Paint header = new Paint(); header.setTextSize(12); header.setFakeBoldText(true);
        int pageNumber = 1, index = 0;
        do {
            PdfDocument.Page page = document.startPage(new PdfDocument.PageInfo.Builder(595, 842, pageNumber).create());
            Canvas canvas = page.getCanvas();
            canvas.drawText("SmartSpend - Monthly Report", 40, 50, title);
            canvas.drawText("Period: " + new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date()), 40, 76, text);
            canvas.drawText("Total: " + CurrencyUtils.formatAmount(summary.total, summary.currencyCode), 40, 98, header);
            canvas.drawText(summary.budget > 0 ? String.format(Locale.getDefault(), "Budget: %s (%.0f%% used)", CurrencyUtils.formatAmount(summary.budget, summary.currencyCode), summary.budgetPercent) : "Budget: not set", 40, 118, text);
            canvas.drawText("Top category: " + summary.topCategory, 40, 138, text);
            int y = 166;
            if (pageNumber == 1 && narrative != null && !narrative.isBlank()) {
                canvas.drawText("Smart summary", 40, y, header); y += 18;
                for (String line : wrap(narrative.replace("•", "-"), 82)) { canvas.drawText(line, 40, y, text); y += 16; if (y > 250) break; }
                y += 10;
            }
            canvas.drawText("Date", 40, y, header); canvas.drawText("Description", 130, y, header);
            canvas.drawText("Category", 350, y, header); canvas.drawText("Amount", 465, y, header); y += 22;
            while (index < safeExpenses.size() && y < 800) {
                Expense expense = safeExpenses.get(index++);
                canvas.drawText(DateUtils.formatShortDate(expense.getDate()), 40, y, text);
                canvas.drawText(ellipsize(expense.getTitle(), 28), 130, y, text);
                canvas.drawText(ellipsize(expense.getCategory(), 14), 350, y, text);
                canvas.drawText(CurrencyUtils.formatAmount(expense.getAmount(), expense.getCurrency()), 465, y, text);
                y += 20;
            }
            canvas.drawText("Page " + pageNumber, 510, 825, text);
            document.finishPage(page); pageNumber++;
        } while (index < safeExpenses.size());
        try (FileOutputStream stream = new FileOutputStream(output)) { document.writeTo(stream); }
        document.close();
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", output);
        return new Intent(Intent.ACTION_SEND).setType("application/pdf").putExtra(Intent.EXTRA_STREAM, uri)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    private static List<String> wrap(String value, int length) {
        java.util.ArrayList<String> lines = new java.util.ArrayList<>();
        for (String paragraph : value.split("\\R")) {
            String remaining = paragraph.trim();
            while (remaining.length() > length) { int cut = remaining.lastIndexOf(' ', length); if (cut < 1) cut = length; lines.add(remaining.substring(0, cut)); remaining = remaining.substring(cut).trim(); }
            if (!remaining.isEmpty()) lines.add(remaining);
        }
        return lines;
    }
    private static String ellipsize(String value, int length) {
        if (value == null) return ""; return value.length() <= length ? value : value.substring(0, length - 1) + "…";
    }
}
