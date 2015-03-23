package com.ernieyu.feedparser.impl;

import com.ernieyu.feedparser.Feed;
import com.ernieyu.feedparser.FeedException;
import com.ernieyu.feedparser.FeedParser;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;

/**
 * Default implementation of FeedParser.  This uses a SAX parser to process
 * the feed.
 */
public class DefaultFeedParser implements FeedParser {

    @Override
    public Feed parse(InputStream inStream) throws FeedException {
        try {
            // Create SAX parser.
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            
            // Turn on namespace support.
            XMLReader reader = parser.getXMLReader();
            reader.setFeature("http://xml.org/sax/features/namespaces", true);
            reader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);

        	// Create SAX handler.
        	FeedHandler handler = new FeedHandler();

            // Parse feed and return data.
        	parser.parse(inStream, handler);
        	return handler.getFeed();
        
        } catch (Exception ex) {
        	throw new FeedException(ex);
        }
    }
}