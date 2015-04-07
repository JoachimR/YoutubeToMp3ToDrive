package de.reiss;

import com.ernieyu.feedparser.Item;

public class DownloadItem {

    public Item rssItem;
    public String filename;
    public String md5;
    public String driveLink;


    public DownloadItem() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DownloadItem that = (DownloadItem) o;

        if (driveLink != null ? !driveLink.equals(that.driveLink) : that.driveLink != null) return false;
        if (filename != null ? !filename.equals(that.filename) : that.filename != null) return false;
        if (md5 != null ? !md5.equals(that.md5) : that.md5 != null) return false;
        if (rssItem != null ? !rssItem.equals(that.rssItem) : that.rssItem != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = rssItem != null ? rssItem.hashCode() : 0;
        result = 31 * result + (filename != null ? filename.hashCode() : 0);
        result = 31 * result + (md5 != null ? md5.hashCode() : 0);
        result = 31 * result + (driveLink != null ? driveLink.hashCode() : 0);
        return result;
    }
}
