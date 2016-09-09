package com.json;

import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import java.util.Set;

/**
 * Created by vaibhavvashishtha on 03/09/16.
 */
public class WPTJsonProcessor {

    private String splunkJsonFileName = "";
    private String outputDirectoryForSplunkJsons = "";
    private String propertiesFilePath = "";
    private String propertiesFileName = "";
    private String sourceJson = "";

    public void processJson(String splunkJsonFileName, String outputDirectoryForSplunkJsons, String propertiesFilePath, String propertiesFileName, String sourceJson) {
        try {
            this.splunkJsonFileName = splunkJsonFileName;
            this.outputDirectoryForSplunkJsons = outputDirectoryForSplunkJsons;
            this.propertiesFilePath = propertiesFilePath;
            this.propertiesFileName = propertiesFileName;
            this.sourceJson = sourceJson;
            processWPTJSON();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @throws Exception
     */
    public void processWPTJSON() throws Exception {
        Map jsonMap = getMapFromJSON();
        writeUpdatedJson(jsonMap);
    }

    /**
     * @return
     */
    public Map getMapFromJSON() throws Exception{
        Map<String, Object> jsonMap = JsonUtils.jsonToMap(new FileInputStream(new File(sourceJson)));
        return jsonMap;
    }

    /**
     * @param jsonData
     * @throws Exception
     */
    public void writeUpdatedJson(Map<String, Object> jsonData) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        FileOutputStream stream = new FileOutputStream(new File(outputDirectoryForSplunkJsons
                + "/" + splunkJsonFileName));
        stream.write(mapper.writeValueAsString(preprepareJsonDataToWrite(jsonData)).getBytes());
        stream.close();
    }

    /**
     * @param sourceJsonMap
     * @return
     */
    public Map<String, Object> preprepareJsonDataToWrite(Map<String, Object> sourceJsonMap)
            throws Exception {
        Map targetJsonMap = new HashMap();
        Map<String, String> keyMap = ReadProperties.loadPropertiesFromFile(propertiesFilePath
                +"/"+propertiesFileName, true);

        Set<String> keys = keyMap.keySet();
        for (String key : keys) {
            targetJsonMap.put(keyMap.get(key), sourceJsonMap.get(key));
        }
        return targetJsonMap;
    }
}
