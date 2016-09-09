package com.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by vaibhavvashishtha on 06/09/16.
 */
public class SiteSpeedProcessor {
    private static String outputDirectoryForSplunkJsons = "";
    private static String sourceHTML = "";
    private static String tool = "";
    private static String propertyFileLocation = "";
    private static String propertyFileName = "";

    public static void main(String[] args) throws Exception {
        outputDirectoryForSplunkJsons = args[0];
        tool = args[1];
        sourceHTML = args[2];
        propertyFileLocation = args[3];
        propertyFileName = args[4];

        File input = new File(sourceHTML);
        Document doc = Jsoup.parse(input, "UTF-8", "http://google.com/");

        Elements links = doc.select("a[href*=#]"); // a with href

        Map titleMap = new HashMap();
        for (int counter = 0; counter < links.size(); counter++) {
            Element element = links.get(counter);
            titleMap.put(element.attr("title"), element.ownText());
        }

        writeUpdatedJson(getStringObjectMap(titleMap, (Map) ReadProperties.loadPropertiesFromFile(propertyFileLocation
                + "/" + propertyFileName, true)));
    }

    /**
     * @param sourceJsonMap
     * @param keyMap
     * @return
     */
    private static Map<String, Object> getStringObjectMap(Map<String, Object> sourceJsonMap,
                                                          Map<String, String> keyMap) {
        Map targetJsonMap = new HashMap();
        Set<String> keys = keyMap.keySet();
        for (String key : keys) {
            if (sourceJsonMap.get(key) != null)
                targetJsonMap.put(keyMap.get(key), sourceJsonMap.get(key));
        }
        return targetJsonMap;
    }

    /**
     * @param jsonData
     * @throws Exception
     */
    public static void writeUpdatedJson(Map<String, Object> jsonData) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        FileOutputStream stream = new FileOutputStream(new File(outputDirectoryForSplunkJsons
                + "/" + tool + "_runs_data.json"));

        stream.write(mapper.writeValueAsString((jsonData)).getBytes());

        stream.close();
    }
}
