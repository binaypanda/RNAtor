package in.ganitlabs.rnator.Helpers;

import android.content.Context;

import java.io.IOException;

import in.ganitlabs.rnator.Config;

public class HTMLHelper extends FileHelper implements Config {

    public HTMLHelper(Context context, String name) throws IOException {
        super(context, name, new HTMLVersioning(), HTML_ASSETS_DIR);
    }
}
