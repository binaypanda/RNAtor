package in.ganitlabs.rnator.Helpers;


import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public abstract class FileHelper{

    private String filePath;

    private int fileVersion;

    private String fileAssetDirectory;

    private String fileName;

    private final Context myContext;

    private VersioningType versioningType;

    FileHelper(Context context, String name, VersioningType vType, String fileAssetDir) throws IOException {
        myContext = context;
        fileName = name;
        versioningType = vType;
        fileAssetDirectory = fileAssetDir;
        filePath = context.getDatabasePath(fileName).getPath();
        fileVersion = versioningType.findAssetVersion(myContext, fileAssetDirectory + fileName);
        setStraight();
    }

    public void installFile(InputStream from) throws IOException {
        Helper.copyData(from, new FileOutputStream(filePath));
        setStraight();
    }

    public int getVersion() {
        return fileVersion;
    }

    public String getUrl(){
        return filePath;
    }

    private void setStraight()throws IOException{
        boolean isCreate = false, isUpgrade = false;
        File htmlFile = new File(filePath);
        if (htmlFile.exists()){
            int version = versioningType.findInternalVersion(myContext, filePath);
            if (version > fileVersion){
                fileVersion = version;
            }else if(version < fileVersion){
                isUpgrade = true;
            }
        }else {
            isCreate = true;
        }
        if (isCreate || isUpgrade) {
            installFile(myContext.getAssets().open(fileAssetDirectory + fileName));
        }
    }
}
