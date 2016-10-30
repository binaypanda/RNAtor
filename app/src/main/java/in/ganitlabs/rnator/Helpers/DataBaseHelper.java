package in.ganitlabs.rnator.Helpers;



import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import in.ganitlabs.rnator.Config;
import in.ganitlabs.rnator.Utils.TrackedCursorFactory;


public class DataBaseHelper extends SQLiteOpenHelper implements Config {

    private static String DB_PATH;

    private static int DATABASE_VERSION = DB_VERSION;

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    private static DataBaseHelper instance;

    private boolean isCreate, isUpgrade, isDowngrade;

    public DataBaseHelper(Context context) throws IOException {
        super(context, DB_NAME, new TrackedCursorFactory(), DATABASE_VERSION);
        isCreate = false;
        isUpgrade = false;
        isDowngrade = false;
        myContext = context;
        DB_PATH = context.getDatabasePath(DB_NAME).getPath();
        myDataBase = this.getReadableDatabase();

        if(isCreate || isUpgrade) {
            installDB(myContext.getAssets().open(DB_ASSETS_PATH));
        }else {
            instance = this;
        }
        if(isDowngrade) {
            myDataBase.setVersion(DATABASE_VERSION);
        }
    }
    public static DataBaseHelper getInstance(){
        return instance;
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        isCreate = true;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        isUpgrade = true;
    }

    public void installDB(InputStream from) throws IOException {
        close();
        Helper.copyData(from , new FileOutputStream(DB_PATH));
        instance = new DataBaseHelper(myContext);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DATABASE_VERSION = oldVersion;
        isDowngrade = true;
    }

    public int getVersion(){
        return DATABASE_VERSION;
    }
    public Cursor getOrganismNames(CharSequence prefix) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String [] projection = {"_id", "name"};
        String selection = "name LIKE '" + prefix +"%'";
        String sqlTables = "Organism";
        qb.setTables(sqlTables);
        Cursor c = qb.query(myDataBase, projection, selection, null, null, null, null);
        c.moveToFirst();
        return c;
    }
    public Cursor getFoldChanges() {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String [] projection = {"fold_change"};
        String selection = "";
        String sqlTables = "ReadSizeMultiplier";
        qb.setTables(sqlTables);
        Cursor c = qb.query(myDataBase, projection, selection, null, "fold_change", null, null);
        c.moveToFirst();
        return c;
    }
    public Cursor getReplicates() {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String [] projection = {"replicates"};
        String selection = "";
        String sqlTables = "ReadSizeMultiplier";
        qb.setTables(sqlTables);
        Cursor c = qb.query(myDataBase, projection, selection, null, "replicates", null, null);
        c.moveToFirst();
        return c;
    }
    public double getTransSize(String organism_name){
        double genome_size;
        int kingdom_id;
        double transcription;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String selection = "name='"+organism_name+"'";
        String [] projectionOrg = {"_id", "name", "kingdom_id", "genome_size"};
        qb.setTables("Organism");
        Cursor organism = qb.query(myDataBase, projectionOrg, selection, null, null, null, null);
        organism.moveToFirst();
        try {
            genome_size = organism.getDouble(organism.getColumnIndex("genome_size"));
            kingdom_id = organism.getInt(organism.getColumnIndex("kingdom_id"));
        }catch (IndexOutOfBoundsException e){
            return -1;
        }
        qb = new SQLiteQueryBuilder();
        selection = "_id='"+kingdom_id+"'";
        String [] projectionKing = {"_id", "transcription"};
        qb.setTables("Kingdom");
        Cursor kingdom = qb.query(myDataBase, projectionKing, selection, null, null, null, null);
        kingdom.moveToFirst();
        try {
            transcription = kingdom.getDouble(kingdom.getColumnIndex("transcription"));
        }catch(IndexOutOfBoundsException e){
            return -1;
        }
        return genome_size*transcription;
    }
    public double getReadSizeMultiplier(double fold_change, int replicates){
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String [] projection = {"read_size_multiplier"};
        String selection = "fold_change='"+fold_change+"' AND replicates='"+replicates+"'";
        String sqlTables = "ReadSizeMultiplier";
        qb.setTables(sqlTables);
        Cursor c = qb.query(myDataBase, projection, selection, null, null, null, null);
        c.moveToFirst();
        return c.getDouble(c.getColumnIndex("read_size_multiplier"));
    }
}