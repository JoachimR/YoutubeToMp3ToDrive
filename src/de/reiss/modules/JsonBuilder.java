package de.reiss.modules;

import com.google.gson.*;
import de.reiss.Config;
import de.reiss.Constants;
import de.reiss.Utils;
import de.reiss.modules.downloader.DownloadItem;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class JsonBuilder {


    public static void buildPublishJsonFromDownloadedItems(List<DownloadItem> itemList) throws IOException {
        JsonObject pj = new JsonObject();
        JsonArray completeJsonArray = new JsonArray();

        for (DownloadItem downloadItem : itemList) {

            File f = new File(downloadItem.filename);
            if (f.exists()) {
                JsonObject o = getJsonObjectFromItem(downloadItem);
                if (o != null) {
                    completeJsonArray.add(o);
                }
            }
        }

        pj.add("files", completeJsonArray);

        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        Gson gson = builder.setPrettyPrinting().create();
        String json = gson.toJson(pj);

        Utils.saveTextFile(json, new File(Config.DRIVE_PUBLISH_JSON_FILE_NAME));
    }


    private static JsonObject getJsonObjectFromItem(DownloadItem downloadItem) {
        try {
            JsonObject fileJsonObject = new JsonObject();
            fileJsonObject.add("title", new JsonPrimitive(downloadItem.rssItem.getTitle()));
            fileJsonObject.add("youtubelink", new JsonPrimitive(downloadItem.rssItem.getLink()));
            fileJsonObject.add("drivelink", new JsonPrimitive(downloadItem.driveLink));

            DateFormat pubdateDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            Date pubDate = pubdateDateFormat.parse(String.valueOf(downloadItem.rssItem.getPubDate()));
            fileJsonObject.add("published", new JsonPrimitive(pubDate.getTime()));

            fileJsonObject.add("filename", new JsonPrimitive(downloadItem.filename));

            fileJsonObject.add("md5", new JsonPrimitive(downloadItem.md5));

            String ffProbeInfoString = Shell.executeCommand(Constants.CMD_FFPROBE + downloadItem.filename, true);

            JsonParser jsonParser = new JsonParser();
            JsonObject ffProbe = (JsonObject) jsonParser.parse(ffProbeInfoString);

            JsonObject ffprobeFormat = ffProbe.getAsJsonObject("format");

            double dur = Double.parseDouble(ffprobeFormat.getAsJsonPrimitive("duration").getAsString());
            fileJsonObject.add("duration", new JsonPrimitive((int) dur));


            long bytes = ffprobeFormat.getAsJsonPrimitive("size").getAsLong();
            fileJsonObject.add("filesize", new JsonPrimitive(Utils.humanReadableByteCount(bytes, true)));

            JsonObject ffprobeFormatTags = ffprobeFormat.getAsJsonObject("tags");

            JsonPrimitive createTime = ffprobeFormatTags.getAsJsonPrimitive("creation_time");
            SimpleDateFormat createTimeDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long timestamp = createTimeDateFormat.parse(createTime.getAsString()).getTime();
            fileJsonObject.add("created", new JsonPrimitive(timestamp));

            return fileJsonObject;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
