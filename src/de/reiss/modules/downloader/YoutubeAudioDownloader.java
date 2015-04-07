package de.reiss.modules.downloader;

import com.ernieyu.feedparser.Item;
import de.reiss.Config;
import de.reiss.Utils;
import de.reiss.modules.Shell;

import java.io.File;
import java.io.IOException;

public class YoutubeAudioDownloader {

    public static String downloadItemAsMp3(Item item) throws IOException {
        System.out.println("==============================================================================");
        System.out.println("TITLE: " + item.getTitle());
        System.out.println("LINK: " + item.getLink());
        System.out.println("PUB DATE: " + item.getPubDate());
        System.out.println("");

        final String filename = Utils.replaceUmlaut(item.getTitle());

        final String filenameWithFormat = filename + "." + Config.AUDIO_FILE_FORMAT;

        if (isFileAlreadyDownloaded(filenameWithFormat)) {
            System.out.println("File '" + filenameWithFormat +
                    "' already downloaded , not doing it again.");
        } else {
            System.out.println("File '" + filenameWithFormat +
                    "' not yet downloaded , downloading now.");

            Shell.executeCommand(getYoutubeDLShellCmd(filename) + " " + item.getLink(), false);
        }


        System.out.println("==============================================================================");

        return filenameWithFormat;
    }


    private static String getYoutubeDLShellCmd(String filename) {
        return "youtube-dl " +
                "--continue --ignore-errors --no-overwrites --restrict-filenames " +
                "--extract-audio --audio-format " + Config.AUDIO_FILE_FORMAT + " " +
                "--output " + filename + "." + "%(ext)s";
    }

    private static boolean isFileAlreadyDownloaded(String filenameWithFormat) {
        File f = Utils.getDownloadedFilesDirectory();
        File[] files = f.listFiles();
        if (files == null || files.length < 1) {
            return false;
        }
        for (File file : files) {
            if (!file.isDirectory()) {

                try {
                    String filename = file.getName();
                    if (filename.equals(filenameWithFormat)) {
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

}
