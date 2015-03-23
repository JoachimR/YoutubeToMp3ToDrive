package de.reiss.modules;

import com.ernieyu.feedparser.*;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FeedExtractor {


    public static Feed getFeed(String url) throws IOException, FeedException {
        String s = download(url);
        InputStream stream = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
        FeedParser parser = FeedParserFactory.newParser();
        return parser.parse(stream);
    }

    public static String download(String url) throws IOException {
        InputStream s = null;
        InputStreamReader r = null;
        StringBuilder content = new StringBuilder();
        try {
            s = (InputStream) new URL(url).getContent();

            r = new InputStreamReader(s, "UTF-8");

            char[] buffer = new char[4 * 1024];
            int n = 0;
            while (n >= 0) {
                n = r.read(buffer, 0, buffer.length);
                if (n > 0) {
                    content.append(buffer, 0, n);
                }
            }
        } finally {
            if (r != null) r.close();
            if (s != null) s.close();
        }
        return content.toString();
    }

}
