package com.json.zapprocessor;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vaibhavvashishtha on 06/09/16.
 */
public class AcheckerXMLProcessor {

    private String outputDirectoryForSplunkJsons = "";
    private String propertiesFilePath = "";
    private String propertiesFileName = "";
    private String sourceXML = "";
    private String resultFileName = "";
    private String slash = "";
    private String resultDirectory = "";

    public  void processXML(String outputDirectoryForSplunkJsons,
                            String propertiesFilePath,
                            String propertiesFileName,
                            String sourceXML,
                            String resultFileName,
                            String resultDirectory) {
        try {
            this.outputDirectoryForSplunkJsons = outputDirectoryForSplunkJsons;
            this.sourceXML = sourceXML;
            this.resultFileName = resultFileName;
            this.propertiesFilePath = propertiesFilePath;
            this.propertiesFileName = propertiesFileName;
            this.resultDirectory = resultDirectory;

            if (System.getProperty("os.name").startsWith("Windows")) {
                slash = "\\";
            } else {
                slash = "/";
            }

            File fXmlFile = new File(outputDirectoryForSplunkJsons + slash + sourceXML);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();
            
            System.out.println(doc.getDocumentElement().getNodeName());
            Map keyMap = ReadProperties.loadPropertiesFromFile(propertiesFilePath
                    +slash+propertiesFileName, true);
            NodeList nList = doc.getElementsByTagName("summary");
            int low = 0;
            int medium = 0;
            int high = 0;
            Map<String, Object> errorMap = new HashMap<String, Object>();
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node node = nList.item(temp);
                String nodeContent = node.getTextContent();
                if (nodeContent.matches("Low(.*)")) {
                    low+=1;
                } else if (nodeContent.matches("Medium(.*)")) {
                    medium+=1;
                } else {
                    high+=1;
                }
            }
            errorMap.put("low", low);
            errorMap.put("medium", medium);
            errorMap.put("high", high);
            writeUpdatedJson(errorMap, "zap", outputDirectoryForSplunkJsons);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param jsonData
     * @throws Exception
     */
    public  void writeUpdatedJson(Map<String, Object> jsonData, String tool, String outputDirectoryForSplunkJsons) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        FileOutputStream stream = new FileOutputStream(createFileIfNotPresent(), false);
        addDateToJson(jsonData);
        stream.write(mapper.writeValueAsString(jsonData).getBytes());
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
