package com.json;

import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by vaibhavvashishtha on 03/09/16.
 */
public class SonarJsonProcessor {

    private String outputDirectoryForSplunkJsons = "";
    private String propertiesFilePath = "";
    private String propertiesFileName = "";
    private String sourceJson = "";
    private String resultFileName = "";
    private String slash = "";
    private String resultDirectory = "";

    public  void processJson(String resultFileName, String outputDirectoryForSplunkJsons, String propertiesFilePath,
                                   String propertiesFileName, String sourceJson,
                                   String resultDirectory) {
        try {
            this.outputDirectoryForSplunkJsons = outputDirectoryForSplunkJsons;
            this.propertiesFilePath = propertiesFilePath;
            this.propertiesFileName = propertiesFileName;
            this.sourceJson = sourceJson;
            this.resultDirectory = resultDirectory;
            this.resultFileName = resultFileName;
            if (System.getProperty("os.name").startsWith("Windows")) {
                slash = "\\";
            } else {
                slash = "/";
            }
            processSonarJSON();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * @throws Exception
     */
    public  void processSonarJSON() throws Exception {
        Map jsonMap = getMapFromJSON();
        List<Map<String, Object>> listOfViolations = new ArrayList<>();
        listOfViolations = (List)jsonMap.get("msr");
        writeUpdatedJson(jsonMap);
    }

    /**
     * @return
     */
    public  Map getMapFromJSON() throws Exception{
        Map<String, Object> jsonMap = JsonUtils.jsonToMap(new FileInputStream(new File(outputDirectoryForSplunkJsons+slash+sourceJson)));
        return jsonMap;
    }

    /**
     * @param jsonData
     * @throws Exception
     */
    public  void writeUpdatedJson(Map<String, Object> jsonData) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        FileOutputStream stream = new FileOutputStream(createFileIfNotPresent(),false);

                stream.write(mapper.writeValueAsString(preprepareJsonDataToWrite((List) jsonData.get("msr"))).getBytes());

        stream.close();
    }
    
	private File createFileIfNotPresent() throws IOException {
		File file = new File(resultDirectory + slash + resultFileName);
		file.getParentFile().mkdirs();
		return file;
	}

    /**
     * @param sourceJsonMap
     * @return
     */
    public  Map<String, Object> preprepareJsonDataToWrite(Map<String, Object> sourceJsonMap)
            throws Exception {
        Map targetJsonMap = new HashMap();
        Map keyMap = ReadProperties.loadPropertiesFromFile(propertiesFilePath
                +slash + propertiesFileName, true);


                getStringObjectMapForSonar(sourceJsonMap, keyMap, targetJsonMap);

        return targetJsonMap;
    }

    /**
     *
     * @param sourceJsonMap
     * @param keyMap
     * @param targetJsonMap
     */
    private  void getStringObjectMapForSonar(Map<String, Object> sourceJsonMap,
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
    private  Map<String, Object> getStringObjectMap(Map<String, Object> sourceJsonMap,
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
    public  Map<String, Object> preprepareJsonDataToWrite(List listOfJsonMaps)
            throws Exception {
        Map targetJsonMap = new HashMap();
        for (Object sourceJson : listOfJsonMaps) {
            targetJsonMap.putAll(preprepareJsonDataToWrite((Map)sourceJson));
        }
        
        addDateToJson(targetJsonMap);
        return targetJsonMap;
    }
    
    private void addDateToJson(Map targetJsonMap) {
		Calendar cal = Calendar.getInstance();
		targetJsonMap.put("date", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(cal.getTime()));
	}
}

