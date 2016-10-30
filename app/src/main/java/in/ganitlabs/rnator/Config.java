package in.ganitlabs.rnator;


import android.annotation.SuppressLint;

import java.util.HashMap;

@SuppressLint("UseSparseArrays")
public interface Config{

    long SPLASH_TIME_MIN = 1500;

    String DB_NAME = "rnator.db";

    int DB_VERSION = 1;

    String DB_ASSETS_PATH = "databases/" + DB_NAME;

    String HTML_ASSETS_DIR = "htmls/";

    String IMAGE_ASSETS_DIR = "images/";

    String HOST = "http://www.ganitlabs.in";

    String Downloads_API= "/rnator/downloads/";

    String Versions_API = "/rnator/versions/";


    HashMap<Integer, String> HTML_MENU_FILE_MAPPING = new HashMap<Integer, String>(){{
        put(R.id.faq, "faq.html");
        put(R.id.ref, "references.html");
        put(R.id.about, "about.html");
    }};

    String[] IMAGE_NAMES = new String[]{"download_v1.jpg"};

    HashMap<Integer, Class> ROUTES = new HashMap<Integer, Class>(){{
        put(R.id.main, FragmentMain.class);
        put(R.id.updates, FragmentUpdates.class);
        for(int id:HTML_MENU_FILE_MAPPING.keySet()){
            put(id, FragmentHTML.class);
        }
    }};
}
