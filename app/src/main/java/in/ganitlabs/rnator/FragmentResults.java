package in.ganitlabs.rnator;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

import in.ganitlabs.rnator.Helpers.Helper;

public class FragmentResults extends Fragment implements Config{
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_results_layout, container, false);
        final Context context = getContext();
        Bundle bundle = this.getArguments();

        final double x = bundle.getDouble("read_size");
        final double fold = bundle.getDouble("fold");
        final int rep = bundle.getInt("rep");

        final String rest = "Use " + Math.ceil(x) + " million reads to generate optimum results at " +
                fold + " fold-change and " + rep + " replicates.\n";

        TextView textView = (TextView) v.findViewById(R.id.tvRes);
        textView.setText(rest);

        final int colSize = 5;

        final String[] cellStrings = new String[]
                {"Series", "System", "Kit", "Samples/\nFlowcell", "Samples/\nLane",
                        "MiniSeq", "MiniSeq", "kit v2", "" + (int) Math.ceil(30 / x), "" + (int) Math.ceil(30 / (x * 8)),
                        "","","kit v3", "" + (int) Math.ceil(40 / x), "" + (int) Math.ceil(5 / x),
                        "NextSeq", "NextSeq\n500", "", "" + (int) Math.ceil(700 / x), "" + (int) Math.ceil(175 / (x * 2)),
                        "HiSeq", "HiSeq 3000", "", "" + (int) Math.ceil(2000 / x), "" + (int) Math.ceil(250 / x),
                        "","HiSeq 4000","", "" + (int) Math.ceil(4000 / x), "" + (int) Math.ceil(500 / x),
                        "","HiSeq 2500", "HISEQ\nSBS V4", "" + (int) Math.ceil(4000 / x), "" + (int) Math.ceil(500 / x),
                        "","","TRUSEQ\nSBS V3", "" + (int) Math.ceil(3000 / x), "" + (int) Math.ceil(375 / x)};

        GridLayout table = (GridLayout) v.findViewById(R.id.table);
        table.setColumnCount(colSize);
        table.setRowCount((int)(Math.ceil(cellStrings.length/(double)colSize)));
        for (String cellVal : cellStrings) {
            RelativeLayout rlTable = Helper.createCell(context, cellVal);
            table.addView(rlTable);
        }
        Button btnMail = (Button) v.findViewById(R.id.btnMail);
        btnMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+ File.separator + "result.html";
                String res = "<!DOCTYPE html><html><body>"+Helper.getHTMLTable(colSize, cellStrings)+"</body></html>";
                try {
                    OutputStreamWriter fis = new OutputStreamWriter(new FileOutputStream(fileName));
                    fis.write(res);
                    fis.close();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("*/*");
                    File file = new File(fileName);
                    if (!file.exists() || !file.canRead()) {
                        Toast.makeText(context, "Attachment Error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    startActivityForResult(Intent.createChooser(intent, "Choose Application.."),0);

                } catch (FileNotFoundException e) {
                    Toast.makeText(context,"No documents directory on internal storage", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (IOException e) {
                    Toast.makeText(context,"Can't write to file in documents directory.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
        Button btnPdf = (Button) v.findViewById(R.id.btnPdf);
        btnPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    File mfile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                    if (mfile == null || !mfile.exists()){
                        Toast.makeText(context, "Didn't get Documents directory", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Document document = new Document();
                    PdfWriter.getInstance(document, new FileOutputStream(mfile.getPath() + File.separator +"RNAtor_" + new Date().toString().replace(" ", "_")+ ".pdf"));
                    document.open();

                    document.addTitle("RNAtor");
                    document.addSubject("Sequencing Recommendations");
                    document.addAuthor("Shruti Kane");
                    document.addCreator("Himanshu Garg");

                    Paragraph preface = new Paragraph();
                    Paragraph empty = new Paragraph(" ");

                    preface.add(empty);
                    preface.add(new Paragraph("DNA Sequencing Recommendations by RNAtor ( GANIT Labs )", catFont));
                    preface.add(empty);
                    preface.add(new Paragraph(rest, smallBold));
                    preface.add(empty);

                    document.add(preface);

                    Helper.addTable(document, colSize, cellStrings);

                    document.close();

                    MediaScannerConnection.scanFile(context,
                        new String[]{mfile.toString()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String s, Uri uri) {
                            }
                        });
                    Toast.makeText(context, "Done. Can be found in the Downloads folder.", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Directory not found.", Toast.LENGTH_SHORT).show();
                } catch (DocumentException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error while writing pdf.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }
}
