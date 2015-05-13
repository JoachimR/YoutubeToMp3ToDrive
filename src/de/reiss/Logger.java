package de.reiss;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public static void log(Object msg, boolean hideInLog) {
        if (!hideInLog) {
            log(msg);
        }
    }

    public static void log(Object msg) {
        System.out.println(simpleDateFormat.format(new Date()) + " - " + msg);

        // TODO store msg in file?
        // TODO So far done by shell script already so no need
    }
}