package in.ganitlabs.rnator;


import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.List;

import in.ganitlabs.rnator.Helpers.DataBaseHelper;
import in.ganitlabs.rnator.Helpers.Helper;
import in.ganitlabs.rnator.Utils.CustomCursorAdapter;
import in.ganitlabs.rnator.Utils.SeekbarWithIntervals;

public class FragmentMain extends Fragment {
    private boolean isTransCustom;
    private android.support.design.widget.TextInputLayout editTextLayout;
    private android.support.design.widget.TextInputLayout textViewLayout;
    private AutoCompleteTextView textView;
    private TextInputEditText editText;
    private DataBaseHelper dBHelper;

    public static FragmentMain newInstance() {
        return new FragmentMain();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_layout, container, false);
        isTransCustom = false;
        dBHelper = DataBaseHelper.getInstance();

        final CustomCursorAdapter adapter = new CustomCursorAdapter(
                getContext(),
                android.R.layout.simple_spinner_item,
                null,
                new String[]{"name"}
        );
        editText = (TextInputEditText) v.findViewById(R.id.input_custom_trans);
        textView = (AutoCompleteTextView) v.findViewById(R.id.input_search_trans);
        adapter.setCursorToStringConverter(new CustomCursorAdapter.CursorToStringConverter() {
            @Override
            public CharSequence convertToString(Cursor cursor) {
                return cursor.getString(1);
            }
        });
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence charSequence) {
                return dBHelper.getOrganismNames(charSequence);
            }
        });
        textView.setAdapter(adapter);

        editTextLayout = (android.support.design.widget.TextInputLayout) v.findViewById(R.id.input_layout_custom_trans);
        textViewLayout = (android.support.design.widget.TextInputLayout) v.findViewById(R.id.input_layout_search_trans);

        editTextLayout.setVisibility(View.GONE);
        textViewLayout.setVisibility(View.VISIBLE);

        final ImageView imageView = (ImageView) v.findViewById(R.id.input_type);
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getActivity(), imageView);
                popup.getMenuInflater().inflate(R.menu.main_trans_input_type_items, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_search:
                                isTransCustom = false;
                                editText.setText("");
                                editTextLayout.setVisibility(View.GONE);
                                textViewLayout.setVisibility(View.VISIBLE);
                                break;
                            case R.id.item_custom:
                                isTransCustom = true;
                                textView.setText("");
                                textViewLayout.setVisibility(View.GONE);
                                editTextLayout.setVisibility(View.VISIBLE);
                                break;
                        }
                        return true;
                    }
                });
                Object menuHelper;
                Class[] argTypes;
                try {
                    Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                    fMenuHelper.setAccessible(true);
                    menuHelper = fMenuHelper.get(popup);
                    argTypes = new Class[]{boolean.class};
                    menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    popup.show();
                }
            }
        });
        final SeekbarWithIntervals sbiFold = (SeekbarWithIntervals) v.findViewById(R.id.sbiFold);
        final SeekbarWithIntervals sbiRep = (SeekbarWithIntervals) v.findViewById(R.id.sbiRep);

        final Cursor curFold = dBHelper.getFoldChanges();
        final Cursor curRep = dBHelper.getReplicates();

        final List<String> labelsFold = Helper.getLabelsFromFloat(curFold);
        final List<String> labelsRep = Helper.getLabelsFromInt(curRep);

        curFold.close();
        curRep.close();

        sbiFold.setIntervals(labelsFold);
        sbiRep.setIntervals(labelsRep);


        Button button = (Button) v.findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    double transSize = isTransCustom ? Double.valueOf(editText.getText().toString()) : dBHelper.getTransSize(textView.getText().toString());

                    if (transSize < 0) {
                        String message = isTransCustom ? "Invalid (-ve) Transcriptome size." : "Choose from available organisms or use custom transcriptome size.";
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    } else {
                        double fold_change = Double.valueOf(labelsFold.get(sbiFold.getProgress()));
                        int replicates = Integer.valueOf(labelsRep.get((sbiRep.getProgress())));
                        double readSizeMultiplier = dBHelper.getReadSizeMultiplier(fold_change, replicates);
                        Fragment frag = new FragmentResults();
                        Bundle bundle = new Bundle();
                        bundle.putDouble("read_size", transSize * readSizeMultiplier / 3);
                        bundle.putDouble("fold", fold_change);
                        bundle.putInt("rep", replicates);
                        frag.setArguments(bundle);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame, frag);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Enter valid size(in numerals) or try using the Search view", Toast.LENGTH_LONG).show();
                }
            }
        });
        return v;
    }
}

