package org.maple.ch10;

import java.io.File;

public class PathUtil {

    public static String getFileBasePath() {
        String os = System.getProperty("os.name");
        String basePath;
        if (os.toLowerCase().startsWith("win")) {
            basePath = "D:/data/";
        } else {
            basePath = "/root/upload_source";
        }
        basePath = basePath.replace("/", File.separator);
        return basePath;
    }

}
