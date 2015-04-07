package de.reiss.modules;

import com.ernieyu.feedparser.Item;
import de.reiss.Config;
import de.reiss.Constants;
import de.reiss.Utils;

import java.io.IOException;

public class YoutubeAudioDownloader {

    public static String downloadItemAsMp3(Item item) throws IOException {

        final String filename =   Utils.replaceUmlaut(item.getTitle());
        final String filenameWithFormat = filename + "." + Config.AUDIO_FILE_FORMAT;

        // put subfolder into filename
        final String subFolderWithFilename = Constants.FOLDER_NAME_FILES + "/" + filename;

        Shell.executeCommand(ytdl(subFolderWithFilename) + " " + item.getLink(), false);

        return filenameWithFormat;
    }

    /**
     * need to use --audio-format and %(ext)s
     *
     * @param filename
     * @return
     */
    private static String ytdl(String filename) {
        return "youtube-dl " +
                "--continue --ignore-errors --no-overwrites --restrict-filenames " +
                "--extract-audio --audio-format " + Config.AUDIO_FILE_FORMAT + " " +
                "--output " + filename + "." + "%(ext)s";
    }

}
