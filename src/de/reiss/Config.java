package de.reiss;

public class Config {

    public static final String YOUTUBE_CHANNEL = "xxxxxx";
    public static final String YOUTUBE_RSS_FEED_URL =
            "https://www.youtube.com/feeds/videos.xml?user="+
                    YOUTUBE_CHANNEL;

    public static final String DRIVE_PUBLISH_JSON_FILE_NAME = "json/publish.json";
    public static final String DRIVE_PUBLISH_JSON_FILE_ID = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

    public static final String DRIVE_PUBLISH_JSON_TEST_FILE_NAME = "json/testpublish.json";
    public static final String DRIVE_PUBLISH_JSON_TEST_FILE_ID = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

    public static String DRIVE_CLIENT_ID = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.apps.googleusercontent.com";
    public static String DRIVE_CLIENT_SECRET = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

    public static String DRIVE_ACCESS_TOKEN = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    public static String DRIVE_REFRESH_TOKEN = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

    public static String DRIVE_REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

    public static final String AUDIO_FILE_FORMAT = "mp3";
}
