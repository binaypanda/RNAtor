package in.ganitlabs.rnator.Utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteQuery;

import java.util.ArrayList;
import java.util.List;

public class TrackedCursor extends SQLiteCursor {

    private static List<Cursor> openCursors = new ArrayList<>();

    TrackedCursor(SQLiteCursorDriver driver, String editTable, SQLiteQuery query) {
        super(driver, editTable, query);
        openCursors.add(this);
    }

    @Override
    public void close() {
        super.close();
        openCursors.remove(this);
    }

    public static void closeAll(){
        for(int i=0; i < openCursors.size(); i++){
            openCursors.get(i).close();
        }
    }
}
