package in.ganitlabs.rnator.Helpers;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import in.ganitlabs.rnator.R;

public class Helper {

    static void copyData(InputStream from, OutputStream to) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = from.read(buffer)) > 0) {
            to.write(buffer, 0, length);
        }
        to.flush();
        to.close();
        from.close();
    }

    public static java.util.List<String> getLabelsFromFloat(Cursor c) {
        c.moveToFirst();
        ArrayList<String> res = new ArrayList<>();
        for (int i = 0; i < c.getCount(); i++) {
            res.add(i, String.valueOf(c.getFloat(0)));
            c.moveToNext();
        }
        return res;
    }

    public static java.util.List<String> getLabelsFromInt(Cursor c) {
        c.moveToFirst();
        ArrayList<String> res = new ArrayList<>();
        for (int i = 0; i < c.getCount(); i++) {
            res.add(i, String.valueOf(c.getInt(0)));
            c.moveToNext();
        }
        return res;
    }

    public static void addTable(Document document, int colSize, String[] cellStrings) throws DocumentException {
        PdfPTable table = new PdfPTable(colSize);
//        int count = 0;
        for (String cellVal : cellStrings) {
            PdfPCell cell = new PdfPCell(new Phrase(cellVal));
//            if (rowSpanMap.containsKey(count)) {
//                cell.setRowspan(rowSpanMap.get(count));
//            }
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
//            count++;
        }
        try {
            document.add(table);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static RelativeLayout createCell(Context context, String cellVal) {
        View container = LayoutInflater.from(context).inflate(R.layout.result_table_cell_layout, null);
        RelativeLayout rlTable = (RelativeLayout) container.findViewById(R.id.rlTable);
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
//        lp.rowSpec = GridLayout.spec(0, rowSpan);
        lp.setGravity(Gravity.FILL);
        rlTable.setLayoutParams(lp);
        TextView tvTable = (TextView) container.findViewById(R.id.tvTable);
        tvTable.setText(cellVal);
        return rlTable;
    }

    public static String getHTMLTable(int colSize, String[] cellStrings) {
        String result = "";
        result += "<table style=\"border-spacing:0;\">";
        for(int i = 0; i < Math.ceil(cellStrings.length/(float)colSize);i++){
            result += "<tr>";
            for(int j = 0; j < colSize; j++){
                int count = i*5 + j;
                if(count >= cellStrings.length){break;}
                result += "<td style=\"border:1px solid black;\"";
//                result += rowSpanMap.containsKey(count)?" rowspan=\"" + rowSpanMap.get(count) + "\"":"";
                result += ">" + cellStrings[count] + "</td>";
            }
            result += "</tr>";
        }
        result += "</table>";
        return result;
    }
}
