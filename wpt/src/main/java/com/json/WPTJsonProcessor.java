package com.json;

import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.json.service.ReadPropertiesService;
import com.json.service.impl.ReadPropertiesServiceImpl;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import java.util.Set;

/**
 * Created by vaibhavvashishtha on 03/09/16.
 */
public class WPTJsonProcessor implements FileProcessor{
	
	/** The read properties service. */
	private static ReadPropertiesService readPropertiesService = new ReadPropertiesServiceImpl();


	//private String splunkJsonFileName = "";
	private String outputDirectoryForSplunkJsons = "";
	private String propertiesFilePath = "";
	private String propertiesFileName = "";
	private String sourceJson = "";
	private String resultDirectory = "";
	private String resultFileName = "";
	private String slash = "";

	public void processJson( String resultFileName, String outputDirectoryForSplunkJsons, String propertiesFilePath,
			String propertiesFileName, String sourceJson, String resultDirectory) {
		try {
			//this.splunkJsonFileName = splunkJsonFileName;
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
	public Map getMapFromJSON() throws Exception {
		Map<String, Object> jsonMap = JsonUtils
				.jsonToMap(new FileInputStream(new File(outputDirectoryForSplunkJsons + slash + sourceJson)));
		return jsonMap;
	}

	/**
	 * @param jsonData
	 * @throws Exception
	 */
	public void writeUpdatedJson(Map<String, Object> jsonData) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		FileOutputStream stream = new FileOutputStream(createFileIfNotPresent(), false);
		addDateToJson(jsonData);
		stream.write(mapper.writeValueAsString(preprepareJsonDataToWrite(jsonData)).getBytes());
		stream.close();
	}
	
	private File createFileIfNotPresent() throws IOException {
		File file = new File(resultDirectory + slash + resultFileName);
		file.getParentFile().mkdirs();
		return file;
	}
	
	private void addDateToJson(Map targetJsonMap) {
		Calendar cal = Calendar.getInstance();
		targetJsonMap.put("date", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(cal.getTime()));
	}

	/**
	 * @param sourceJsonMap
	 * @return
	 */
	public Map<String, Object> preprepareJsonDataToWrite(Map<String, Object> sourceJsonMap) throws Exception {
		Map targetJsonMap = new HashMap();
		Map<String, Object> keyMap = readPropertiesService
				.readProperties(propertiesFilePath + slash + propertiesFileName);

		Set<String> keys = keyMap.keySet();
		for (String key : keys) {
			targetJsonMap.put(keyMap.get(key), sourceJsonMap.get(key));
		}
		return targetJsonMap;
	}
}
