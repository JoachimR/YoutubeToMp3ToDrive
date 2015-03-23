package de.reiss;

import com.ernieyu.feedparser.Feed;
import com.ernieyu.feedparser.Item;
import de.reiss.modules.*;
import de.reiss.modules.downloader.DownloadItem;
import de.reiss.modules.downloader.YoutubeAudioDownloader;
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

        String url = Config.YOUTUBE_RSS_FEED_URL;

        deleteAllFileParts();


        int max = 10;
        int counter = 0;
        try {
            Feed feed = FeedExtractor.getFeed(url);

            List<DownloadItem> allDownloadItems = new ArrayList<DownloadItem>();
            List<Item> allItems = feed.getItemList();
            for (Item item : allItems) {
                counter++;
                // TODO remove
//                if (counter > max) {
//                    break;
//                }
//
                // TODO remove
//                if (  !item.getTitle().contains("Erich")) {
//                    continue;
//                }


                // TODO remove
//                if (!item.getTitle().contains("Waffenlieferungen") ) {
//                    continue;
//                }

                DownloadItem downloadItem = new DownloadItem();
                downloadItem.rssItem = item;

                String filename = YoutubeAudioDownloader.downloadItemAsMp3(item);

                if (filename.length() < 1) {
                    continue;
                }

                downloadItem.filename = filename;

                String md5 = MD5Creator.getMD5Checksum(filename);
                if (md5.length() > 0) {
                    downloadItem.md5 = md5;

                    com.google.api.services.drive.model.File file = uploadAndGetFile(downloadItem);

                    if (file != null) {
                        downloadItem.driveLink = file.getWebContentLink();
                        allDownloadItems.add(downloadItem);
                    }

                }

            }

            JsonBuilder.buildPublishJsonFromDownloadedItems(allDownloadItems);

            DriveApi.updateFile(DriveService.getDriveService(), Config.DRIVE_PUBLISH_JSON_FILE_ID, Config.DRIVE_PUBLISH_JSON_FILE_NAME);

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Program finished");
    }

    private static com.google.api.services.drive.model.File uploadAndGetFile(DownloadItem downloadItem) throws Exception {
        com.google.api.services.drive.model.File result = null;
        try {
            List<com.google.api.services.drive.model.File> allFilesOnline = DriveApi.retrieveAllFiles(DriveService.getDriveService());
            for (com.google.api.services.drive.model.File onlineFile : allFilesOnline) {
                if (onlineFile != null) {
                    String onlineMd5 = onlineFile.getMd5Checksum();
                    if (onlineMd5 != null && onlineMd5.equals(downloadItem.md5)) {
                        return onlineFile;
                    }
                }
            }

            // file not found online, thus upload it now
            return FilesUploader.uploadNow(DriveService.getDriveService(), new File(downloadItem.filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void deleteAllFileParts() {
        File f = new File("."); // current directory
        File[] files = f.listFiles();
        for (File file : files) {
            if (!file.isDirectory()) {

                try {
                    String filename = file.getCanonicalPath();
                    if (filename.endsWith(".part")) {
                        System.out.println("Deleting file '" + filename + "'");
                        file.delete();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
