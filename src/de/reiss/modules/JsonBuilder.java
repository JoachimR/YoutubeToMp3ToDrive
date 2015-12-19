package de.reiss.modules;

import com.google.gson.*;
import de.reiss.Config;
import de.reiss.Constants;
import de.reiss.DownloadItem;
import de.reiss.Utils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class JsonBuilder {


    public static void buildPublishJsonFromDownloadedItems(List<DownloadItem> itemList, boolean test) throws IOException {
        JsonObject pj = new JsonObject();
        JsonArray completeJsonArray = new JsonArray();

        for (DownloadItem downloadItem : itemList) {

            File f = new File(Constants.FOLDER_NAME_FILES + "/" + downloadItem.filename);
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

        Utils.saveTextFile(json, new File((test? Config.DRIVE_PUBLISH_JSON_TEST_FILE_NAME : Config.DRIVE_PUBLISH_JSON_FILE_NAME)));
    }


    private static JsonObject getJsonObjectFromItem(DownloadItem downloadItem) {
        try {
            JsonObject fileJsonObject = new JsonObject();

            // take care of special char problems with Normalizer
            String titleSave = Normalizer.normalize(downloadItem.rssItem.getTitle(), Normalizer.Form.NFC);
            fileJsonObject.add("title", new JsonPrimitive(titleSave));
            fileJsonObject.add("youtubelink", new JsonPrimitive(downloadItem.rssItem.getLink()));
            fileJsonObject.add("drivelink", new JsonPrimitive(downloadItem.driveLink));

            DateFormat pubdateDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            Date pubDate = pubdateDateFormat.parse(String.valueOf(downloadItem.rssItem.getPubDate()));
            fileJsonObject.add("published", new JsonPrimitive(pubDate.getTime()));

            fileJsonObject.add("filename", new JsonPrimitive(downloadItem.filename));

            fileJsonObject.add("md5", new JsonPrimitive(downloadItem.md5));

            String ffProbeInfoString = Shell.executeCommand(Constants.CMD_FFPROBE +
                    Constants.FOLDER_NAME_FILES + "/" + downloadItem.filename, false);

            JsonParser jsonParser = new JsonParser();
            JsonObject ffProbe = (JsonObject) jsonParser.parse(ffProbeInfoString);

            JsonObject ffprobeFormat = ffProbe.getAsJsonObject("format");

            double dur = Double.parseDouble(ffprobeFormat.getAsJsonPrimitive("duration").getAsString());
            fileJsonObject.add("duration", new JsonPrimitive((int) dur));

            long bytes = (long) ffprobeFormat.getAsJsonPrimitive("size").getAsDouble();
            fileJsonObject.add("filesize", new JsonPrimitive(Utils.humanReadableByteCount(bytes, true)));

            JsonObject ffprobeFormatTags = ffprobeFormat.getAsJsonObject("tags");

            JsonPrimitive createTime = ffprobeFormatTags.getAsJsonPrimitive("creation_time");
            SimpleDateFormat createTimeDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long timestamp;
            if (createTime != null) {
                timestamp = createTimeDateFormat.parse(createTime.getAsString()).getTime();
            } else {
                timestamp = pubDate.getTime();
            }
            fileJsonObject.add("created", new JsonPrimitive(timestamp));

            return fileJsonObject;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
