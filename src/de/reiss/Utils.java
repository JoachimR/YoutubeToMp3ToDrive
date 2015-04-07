package de.reiss;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Utils {
    public static String replaceUmlaut(String input) {

        //replace all lower Umlauts
        String o_strResult =
                input
                        .replaceAll(" ", "_")

                        .replaceAll("ü", "ue")
                        .replaceAll("ö", "oe")
                        .replaceAll("ä", "ae")
                        .replaceAll("ß", "ss");

        //first replace all capital umlaute in a non-capitalized context (e.g. Übung)
        o_strResult =
                o_strResult
                        .replaceAll("Ü(?=[a-zäöüß ])", "Ue")
                        .replaceAll("Ö(?=[a-zäöüß ])", "Oe")
                        .replaceAll("Ä(?=[a-zäöüß ])", "Ae");

        //now replace all the other capital umlaute
        o_strResult =
                o_strResult
                        .replaceAll("Ü", "UE")
                        .replaceAll("Ö", "OE")
                        .replaceAll("Ä", "AE");


        o_strResult = o_strResult.replaceAll("[^a-zA-Z0-9.-]", "_");

        return o_strResult;
    }

    public static void saveTextFile(String contents, File file) throws IOException {
        FileUtils.writeStringToFile(file, contents, "UTF-8");
    }

    /**
     * http://stackoverflow.com/a/3758880/883083
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static File getDownloadedFilesDirectory() {
        return new File(Constants.FOLDER_NAME_FILES);
    }
}
