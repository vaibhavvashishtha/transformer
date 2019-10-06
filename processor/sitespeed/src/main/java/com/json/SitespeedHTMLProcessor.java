package com.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by vaibhavvashishtha on 06/09/16.
 */
public class SitespeedHTMLProcessor {
	private String outputDirectoryForSplunkJsons = "";
	private String sourceHTML = "";
	private String propertiesFilePath = "";
	private String propertiesFileName = "";
	private String resultFileName = "";
	private String resultDirectory = "";
	private String slash = "";

	public void processHTML(String resultFileName, String outputDirectoryForSplunkJsons, String propertiesFilePath,
			String propertiesFileName, String sourceHTML, String resultDirectory) {
		try {
			this.outputDirectoryForSplunkJsons = outputDirectoryForSplunkJsons;
			this.propertiesFilePath = propertiesFilePath;
			this.propertiesFileName = propertiesFileName;
			this.sourceHTML = sourceHTML;
			this.resultDirectory = resultDirectory;
			this.resultFileName = resultFileName;
			if (System.getProperty("os.name").startsWith("Windows")) {
				slash = "\\";
			} else {
				slash = "/";
			}

			File input = new File(outputDirectoryForSplunkJsons+slash+sourceHTML);
			Document doc = Jsoup.parse(input, "UTF-8", "http://google.com/");

			Elements links = doc.select("a[href*=#]"); // a with href

			Map titleMap = new HashMap();
			for (int counter = 0; counter < links.size(); counter++) {
				Element element = links.get(counter);
				titleMap.put(element.attr("title"), element.ownText());
			}

			writeUpdatedJson(getStringObjectMap(titleMap, (Map) ReadProperties
					.loadPropertiesFromFile(propertiesFilePath + slash + propertiesFileName, true)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param sourceJsonMap
	 * @param keyMap
	 * @return
	 */
	private Map<String, Object> getStringObjectMap(Map<String, Object> sourceJsonMap, Map<String, String> keyMap) {
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
	public void writeUpdatedJson(Map<String, Object> jsonData) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		FileOutputStream stream = new FileOutputStream(createFileIfNotPresent(), false);
		addDateToJson(jsonData);
		stream.write(mapper.writeValueAsString((jsonData)).getBytes());

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
}
