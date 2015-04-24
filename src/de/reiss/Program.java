package de.reiss;

import com.ernieyu.feedparser.Feed;
import com.ernieyu.feedparser.FeedException;
import com.ernieyu.feedparser.Item;
import de.reiss.modules.*;
import de.reiss.modules.drive.DriveApi;
import de.reiss.modules.drive.DriveService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * https://www.websequencediagrams.com/
 *
 * Program->FeedExtractor: getFeed
 * note left of Program:
 * http://gdata.youtube.com/feeds/api/
 * users/wwwKenFMde/uploads
 * end note
 * FeedExtractor->Program: List<RssItem>
 * loop for all items
 * Program->YoutubeAudioDownloader: downloadItemAsMp3(item)
 * YoutubeAudioDownloader->Program: file
 * Program->MD5Checksum: getMD5Checksum(file)
 * MD5Checksum->Program: md5
 * Program->DriveApi: checkIfAlreadyUploaded(md5)
 * Program->DriveApi: upload(file)
 * end
 * Program->JsonBuilder: createJson(allFiles)
 * JsonBuilder->Program: json
 * Program->DriveApi: update(json)
 */
public class Program {

    public static void main(String[] args) {
        try {
            final List<com.google.api.services.drive.model.File> allFilesAlreadyUploaded =
                    DriveApi.retrieveAllUploadedFiles(DriveService.getDriveService());

            List<DownloadItem> allDownloadItems = new ArrayList<DownloadItem>();

            // printAllVideosFromChannel();

            for (int index = 1; index < 5000; index += 50) {


                String url = Config.YOUTUBE_RSS_FEED_URL;

                url += "?&max-results=50&start-index=" + index;

                deleteAllFileParts();


                int max = 10;
                int counter = 0;

                Feed feed = FeedExtractor.getFeed(url);


                List<Item> allItems = feed.getItemList();
                for (Item item : allItems) {
                    counter++;
//                if (counter > max) {
//                    break;
//                }

//                    if (!item.getTitle().contains("Erich")) {
//                        continue;
//                    }



                    Logger.log("==============================================================================");
                    Logger.log("TITLE: " + item.getTitle());
                    Logger.log("LINK: " + item.getLink());
                    Logger.log("PUB DATE: " + item.getPubDate());
                    Logger.log("");


                    String filename = Utils.replaceUmlaut(item.getTitle()) + "." + Config.AUDIO_FILE_FORMAT;


                    if (isFileAlreadyDownloaded(filename)) {
                        Logger.log("File '" + filename + "' already downloaded , not doing it again.");
                    } else {
                        Logger.log("File '" + filename + "' not yet downloaded , downloading now.");

                        filename = YoutubeAudioDownloader.downloadItemAsMp3(item);
                    }

                    if (filename.length() < 1) {
                        continue;
                    }


                    DownloadItem downloadItem = new DownloadItem();
                    downloadItem.rssItem = item;


                    downloadItem.filename = filename;

                    String md5 = MD5Creator.getMD5Checksum(Constants.FOLDER_NAME_FILES + "/" +  filename);
                    if (md5.length() > 0) {
                        downloadItem.md5 = md5;

                        com.google.api.services.drive.model.File file = uploadAndGetFile(downloadItem, allFilesAlreadyUploaded);

                        if (file != null) {
                            downloadItem.driveLink = file.getWebContentLink();
                            allDownloadItems.add(downloadItem);
                        }

                    }

                    Logger.log("==============================================================================");

                }
            }

            boolean test = false;

            JsonBuilder.buildPublishJsonFromDownloadedItems(allDownloadItems, test);


            java.io.File fileContent = new java.io.File(
                    test ? Config.DRIVE_PUBLISH_JSON_TEST_FILE_NAME : Config.DRIVE_PUBLISH_JSON_FILE_NAME);
            if (fileContent == null || !fileContent.exists()) {
                Logger.log("Did not find file '" + fileContent + "'");
            }
            DriveApi.updateFile(DriveService.getDriveService(),
                    (test ? Config.DRIVE_PUBLISH_JSON_TEST_FILE_ID : Config.DRIVE_PUBLISH_JSON_FILE_ID),
                    (test ? Config.DRIVE_PUBLISH_JSON_TEST_FILE_NAME : Config.DRIVE_PUBLISH_JSON_FILE_NAME));

        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.log("Program finished");
    }

    /**
     * upload an item. Does not upload if the same file can already be found in a list of files that are already uploaded
     *
     * @param downloadItem
     * @param allFilesUploaded
     * @return
     */
    private static com.google.api.services.drive.model.File uploadAndGetFile(DownloadItem downloadItem,
                                                                             List<com.google.api.services.drive.model.File> allFilesUploaded) {
        try {
            if (allFilesUploaded != null && !allFilesUploaded.isEmpty()) {
                for (com.google.api.services.drive.model.File onlineFile : allFilesUploaded) {
                    if (onlineFile != null) {
                        String onlineMd5 = onlineFile.getMd5Checksum();
                        if (onlineMd5 != null && onlineMd5.equals(downloadItem.md5)) {
                            // Logger.log("File '" + downloadItem.filename + "' was already uploaded, not doing it again");
                            return onlineFile;
                        }
                    }
                }
            }

            // file not found online, thus upload it now
            Logger.log("File '" + downloadItem.filename + "' is not already uploaded, doing it now");
            return FilesUploader.uploadNow(DriveService.getDriveService(), new File(Constants.FOLDER_NAME_FILES + "/" + downloadItem.filename));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isFileAlreadyUploaded(String md5) throws IOException {
        List<com.google.api.services.drive.model.File> allFilesOnline = DriveApi.retrieveAllUploadedFiles(DriveService.getDriveService());
        for (com.google.api.services.drive.model.File onlineFile : allFilesOnline) {
            if (onlineFile != null) {
                String onlineMd5 = onlineFile.getMd5Checksum();
                if (onlineMd5 != null && onlineMd5.equals(md5)) {
                    return true;
                }
            }
        }
        return false;
    }


    public static void deleteAllFileParts() {
        File f = Utils.getDownloadedFilesDirectory();
        File[] files = f.listFiles();
        for (File file : files) {
            if (!file.isDirectory()) {

                try {
                    String filename = file.getCanonicalPath();
                    if (filename.endsWith(".part")) {
                        Logger.log("Deleting file '" + filename + "'");
                        file.delete();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static boolean isFileAlreadyDownloaded(String filenameWithFormat) {
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

    private static void printAllVideosFromChannel() throws IOException, FeedException {
        int counter = 0;
        Logger.log("Starting to print all items of youtube channel '" + Config.YOUTUBE_RSS_FEED_URL + "' ");
        Logger.log("==============================================================================");
        String url;
        Feed feed;
        List<Item> allItems;
        for (int index = 1; index < Integer.MAX_VALUE; index += 50) {

            url = Config.YOUTUBE_RSS_FEED_URL + "?&max-results=50&start-index=" + index;
            feed = FeedExtractor.getFeed(url);

            if (feed == null || feed.getItemList() == null || feed.getItemList().isEmpty()) {
                Logger.log("No more items available when using index " + index);
                break;
            }

            allItems = feed.getItemList();
            for (Item item : allItems) {
                counter++;
                Logger.log("--------------------------------------");
                Logger.log("TITLE: " + item.getTitle());
                Logger.log("LINK: " + item.getLink());
                Logger.log("PUB DATE: " + item.getPubDate());
                Logger.log("--------------------------------------");
            }

        }
        Logger.log("==============================================================================");
        Logger.log("End of listing items");
        Logger.log("Amount of videos in youtube channel '" + Config.YOUTUBE_RSS_FEED_URL + "' :" + counter);
    }
}
