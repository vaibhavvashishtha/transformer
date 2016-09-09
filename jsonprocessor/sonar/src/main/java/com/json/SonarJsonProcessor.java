package com.json;

import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;

/**
 * Created by vaibhavvashishtha on 03/09/16.
 */
public class SonarJsonProcessor {

    private static String outputDirectoryForSplunkJsons = "";
    private static String propertiesFilePath = "";
    private static String sourceJsonPath = "";
    private static String tool = "";

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            outputDirectoryForSplunkJsons = args[0];
            propertiesFilePath = args[1];
            sourceJsonPath = args[2];
            tool = args[3];
            processSonarJSON();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void processJson() throws Exception {
        switch (tool) {
            case "sonar":
                processSonarJSON();
                break;
            default:
                break;
        }
    }

    /**
     * @throws Exception
     */
    public static void processSonarJSON() throws Exception {
        Map jsonMap = getMapFromJSON();
        List<Map<String, Object>> listOfViolations = new ArrayList<>();
        listOfViolations = (List)jsonMap.get("msr");
        writeUpdatedJson(jsonMap);
    }

    /**
     * @return
     */
    public static Map getMapFromJSON() throws Exception{
        Map<String, Object> jsonMap = JsonUtils.jsonToMap(new FileInputStream(new File(sourceJsonPath+"/"+tool+".json")));
        return jsonMap;
    }

    /**
     * @param jsonData
     * @throws Exception
     */
    public static void writeUpdatedJson(Map<String, Object> jsonData) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        FileOutputStream stream = new FileOutputStream(new File(outputDirectoryForSplunkJsons
                + "/" + tool+"_runs_data.json"));
        switch (tool) {
            case "sonar":
                stream.write(mapper.writeValueAsString(preprepareJsonDataToWrite((List) jsonData.get("msr"))).getBytes());
                break;
            default:
                break;
        }
        stream.close();
    }

    /**
     * @param sourceJsonMap
     * @return
     */
    public static Map<String, Object> preprepareJsonDataToWrite(Map<String, Object> sourceJsonMap,
                                                                String tool)
            throws Exception {
        Map targetJsonMap = new HashMap();
        Map keyMap = ReadProperties.loadPropertiesFromFile(propertiesFilePath
                +"/"+tool+"keysforsplunkjson.properties", true);

        switch (tool) {
            case "sonar":
                getStringObjectMapForSonar(sourceJsonMap, keyMap, targetJsonMap);
                break;
            default:
                getStringObjectMap(sourceJsonMap, keyMap, targetJsonMap);
                break;
        }
        return targetJsonMap;
    }

    /**
     *
     * @param sourceJsonMap
     * @param keyMap
     * @param targetJsonMap
     */
    private static void getStringObjectMapForSonar(Map<String, Object> sourceJsonMap,
                                                   Map<String, String> keyMap, Map targetJsonMap) {
        String violationName = (String)sourceJsonMap.get("key");
        String violationCount = ((Integer)sourceJsonMap.get("val")).toString();
        Map<String, Object> violationMap = new HashMap<>();
        violationMap.put(violationName, violationCount);
        getStringObjectMap(violationMap, keyMap, targetJsonMap);
    }

    /**
     *
     * @param sourceJsonMap
     * @param keyMap
     * @param targetJsonMap
     * @return
     */
    private static Map<String, Object> getStringObjectMap(Map<String, Object> sourceJsonMap,
                                                          Map<String, String> keyMap, Map targetJsonMap) {
        Set<String> keys = keyMap.keySet();
        for (String key : keys) {
            if (sourceJsonMap.get(key) != null)
                targetJsonMap.put(keyMap.get(key), sourceJsonMap.get(key));
        }
        return targetJsonMap;
    }

    /**
     * @return
     */
    public static Map<String, Object> preprepareJsonDataToWrite(List listOfJsonMaps)
            throws Exception {
        Map targetJsonMap = new HashMap();
        for (Object sourceJson : listOfJsonMaps) {
            targetJsonMap.putAll(preprepareJsonDataToWrite((Map)sourceJson, tool));
        }
        return targetJsonMap;
    }
}

