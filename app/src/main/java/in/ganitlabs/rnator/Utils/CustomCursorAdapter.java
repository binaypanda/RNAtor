package in.ganitlabs.rnator.Utils;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;

public class CustomCursorAdapter extends SimpleCursorAdapter {

    public CustomCursorAdapter(Context context, int layout, Cursor c, String[] from) {
        super(context, layout, c, from, new int[] { android.R.id.text1 }, 0);
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.swapCursor(cursor);
    }
}