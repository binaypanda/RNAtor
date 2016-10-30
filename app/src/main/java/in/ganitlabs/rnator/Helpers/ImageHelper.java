package in.ganitlabs.rnator.Helpers;

import android.content.Context;

import java.io.IOException;

import in.ganitlabs.rnator.Config;

public class ImageHelper extends FileHelper implements Config {

    public ImageHelper(Context context, String name) throws IOException {
        super(context, name, new ImageVersioning(), IMAGE_ASSETS_DIR);
    }
}
