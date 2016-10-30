package in.ganitlabs.rnator.Helpers;

import android.content.Context;

import org.jsoup.Jsoup;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


interface VersioningType {
    int findAssetVersion(Context context, String fPath) throws IOException;
    int findInternalVersion(Context context, String fPath) throws IOException;
}

class HTMLVersioning implements VersioningType{

    private int getVersionFromHTML(InputStream is) throws IOException {
        return Integer.valueOf(Jsoup.parse(is, null, "").select("meta").first().attr("data-version"));
    }

    @Override
    public int findAssetVersion(Context context, String fPath) throws IOException {
        InputStream is = context.getAssets().open(fPath);
        return getVersionFromHTML(is);
    }

    @Override
    public int findInternalVersion(Context context, String fPath) throws IOException {
        InputStream is = new FileInputStream(fPath);
        return getVersionFromHTML(is);
    }
}


// version of file named 'abcv2.html' is 2
// improve by using image metadata and differentiate b/w the following two overridden methods as in case of html
class ImageVersioning implements VersioningType{

    private int findVersion(String fPath) throws IOException {
        File fd = new File(fPath);
        String name = FilenameUtils.removeExtension(fd.getName());
        int ix = name.lastIndexOf('v');
        return Integer.parseInt(name.substring(ix+1));
    }

    @Override
    public int findAssetVersion(Context context, String fPath) throws IOException {
        return findVersion(fPath);
    }

    @Override
    public int findInternalVersion(Context context, String fPath) throws IOException {
        return findVersion(fPath);
    }
}
