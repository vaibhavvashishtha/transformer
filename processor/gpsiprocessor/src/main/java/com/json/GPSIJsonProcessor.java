package com.json;

import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by vaibhavvashishtha on 03/09/16.
 */
public class GPSIJsonProcessor implements FileProcessor{

	private String outputDirectoryForSplunkJsons = "";
	private String propertiesFilePath = "";
	private String propertiesFileName = "";
	private String sourceJsonForDesktop = "";
	private String sourceJsonForMobile = "";
	private String resultDirectory = "";
	private String resultFileName = "";
	private String slash = "";


	public void processJson(String resultFileName, String outputDirectoryForSplunkJsons, String propertiesFilePath,
							String propertiesFileName, String sourceJsonForDesktop, String sourceJsonforMobile,
							String resultDirectory) {
		try {
			// this.splunkJsonFileName = splunkJsonFileName;
			this.outputDirectoryForSplunkJsons = outputDirectoryForSplunkJsons;
			this.propertiesFilePath = propertiesFilePath;
			this.propertiesFileName = propertiesFileName;
			this.sourceJsonForDesktop = sourceJsonForDesktop;
			this.sourceJsonForMobile = sourceJsonforMobile;
			this.resultDirectory = resultDirectory;
			this.resultFileName = resultFileName;
			if (System.getProperty("os.name").startsWith("Windows")) {
				slash = "\\";
			} else {
				slash = "/";
			}
			processJson();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @throws Exception
	 */
	public void processJson() throws Exception {

		Map jsonMap = getConsolidatedGPSIMap(getMapFromJSON(true), getMapFromJSON(false));

		writeUpdatedJson(jsonMap);
	}

	private Map getConsolidatedGPSIMap(Map desktopMap, Map mobileMap) {
		Map jsonMap = new HashMap();
		jsonMap.put("desktop_score", ((Map) ((Map) desktopMap.get("ruleGroups")).get("SPEED")).get("score"));
		jsonMap.put("mobile_score", ((Map) ((Map) mobileMap.get("ruleGroups")).get("SPEED")).get("score"));
		return jsonMap;
	}

	/**
	 * @return
	 */
	public Map getMapFromJSON(boolean isDesktopJSON) throws Exception {
		Map<String, Object> jsonMap = null;
		if (isDesktopJSON)
			jsonMap = JsonUtils.jsonToMap(
					new FileInputStream(new File(outputDirectoryForSplunkJsons + slash + sourceJsonForDesktop)));
		else
			jsonMap = JsonUtils.jsonToMap(
					new FileInputStream(new File(outputDirectoryForSplunkJsons + slash + sourceJsonForMobile)));
		return jsonMap;
	}

	/**
	 * @param jsonData
	 * @throws Exception
	 */
	public void writeUpdatedJson(Map<String, Object> jsonData) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		FileOutputStream stream = new FileOutputStream(createFileIfNotPresent());

		stream.write(mapper.writeValueAsString(preprepareJsonDataToWrite(jsonData)).getBytes());

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
	public Map<String, Object> preprepareJsonDataToWrite(Map<String, Object> sourceJsonMap)
			throws Exception {
		Map targetJsonMap = new HashMap();
		Map keyMap = ReadProperties
				.loadPropertiesFromFile(propertiesFilePath + slash + propertiesFileName, true);
		
			getStringObjectMap(sourceJsonMap, keyMap, targetJsonMap);
			addDateToJson(targetJsonMap);
		return targetJsonMap;
	}

	private void addDateToJson(Map targetJsonMap) {
		Calendar cal = Calendar.getInstance();
		targetJsonMap.put("date", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(cal.getTime()));
	}

	

	/**
	 *
	 * @param sourceJsonMap
	 * @param keyMap
	 * @param targetJsonMap
	 * @return
	 */
	private Map<String, Object> getStringObjectMap(Map<String, Object> sourceJsonMap, Map<String, String> keyMap,
			Map targetJsonMap) {
		Set<String> keys = keyMap.keySet();
		for (String key : keys) {
			if (sourceJsonMap.get(key) != null)
				targetJsonMap.put(keyMap.get(key), sourceJsonMap.get(key));
		}
		return targetJsonMap;
	}

//	/**
//	 * @return
//	 */
//	public Map<String, Object> preprepareJsonDataToWrite(List listOfJsonMaps) throws Exception {
//		Map targetJsonMap = new HashMap();
//		for (Object sourceJson : listOfJsonMaps) {
//			targetJsonMap.putAll(preprepareJsonDataToWrite((Map) sourceJson));
//		}
//		Date date = new Date();
//		targetJsonMap.put("date", date.getDate() + "-" + date.getMonth());
//		return targetJsonMap;
//	}

	
}
