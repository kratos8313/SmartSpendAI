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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class PdfExporter {
    private PdfExporter() {}

    public static Intent exportMonthlyReport(Context context, List<Expense> expenses) throws Exception {
        File directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (directory == null) throw new IllegalStateException("Documents directory unavailable");
        if (!directory.exists() && !directory.mkdirs()) throw new IllegalStateException("Cannot create documents directory");
        String month = new SimpleDateFormat("yyyy-MM", Locale.US).format(new Date());
        File output = new File(directory, "SmartSpend-Report-" + month + ".pdf");

        PdfDocument document = new PdfDocument();
        Paint titlePaint = new Paint(); titlePaint.setTextSize(22); titlePaint.setFakeBoldText(true);
        Paint textPaint = new Paint(); textPaint.setTextSize(11);
        Paint headerPaint = new Paint(); headerPaint.setTextSize(12); headerPaint.setFakeBoldText(true);
        int pageNumber = 1;
        int index = 0;
        double total = 0;
        for (Expense expense : expenses) total += expense.getAmount();
        do {
            PdfDocument.PageInfo info = new PdfDocument.PageInfo.Builder(595, 842, pageNumber).create();
            PdfDocument.Page page = document.startPage(info);
            Canvas canvas = page.getCanvas();
            canvas.drawText("SmartSpend AI — Monthly Report", 40, 50, titlePaint);
            canvas.drawText("Period: " + new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date()), 40, 78, textPaint);
            canvas.drawText("Total: " + CurrencyUtils.formatAmount(total), 40, 98, headerPaint);
            canvas.drawText("Date", 40, 132, headerPaint);
            canvas.drawText("Description", 130, 132, headerPaint);
            canvas.drawText("Category", 350, 132, headerPaint);
            canvas.drawText("Amount", 470, 132, headerPaint);
            int y = 154;
            while (index < expenses.size() && y < 800) {
                Expense expense = expenses.get(index++);
                canvas.drawText(DateUtils.formatShortDate(expense.getDate()), 40, y, textPaint);
                canvas.drawText(ellipsize(expense.getTitle(), 28), 130, y, textPaint);
                canvas.drawText(ellipsize(expense.getCategory(), 14), 350, y, textPaint);
                canvas.drawText(CurrencyUtils.formatAmount(expense.getAmount()), 470, y, textPaint);
                y += 20;
            }
            canvas.drawText("Page " + pageNumber, 510, 825, textPaint);
            document.finishPage(page);
            pageNumber++;
        } while (index < expenses.size());

        try (FileOutputStream stream = new FileOutputStream(output)) { document.writeTo(stream); }
        document.close();
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", output);
        return new Intent(Intent.ACTION_SEND).setType("application/pdf")
                .putExtra(Intent.EXTRA_STREAM, uri).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    private static String ellipsize(String value, int length) {
        if (value == null) return "";
        return value.length() <= length ? value : value.substring(0, length - 1) + "…";
    }
}