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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vaibhavvashishtha on 06/09/16.
 */
public class ZapProcessor {
    public static void main(String[] args) {
        try {
            String sourceFileLocation = args[0];
            String sourceFileName = args[1];
            String outputDirectoryForSplunkJsons = args[2];
            String propertiesFilePath = args[3];
            String propertiesFileName = args[4];
            File fXmlFile = new File(sourceFileLocation + "/" + sourceFileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            Map keyMap = ReadProperties.loadPropertiesFromFile(propertiesFilePath
                    +"/"+propertiesFileName, true);

            NodeList nList = doc.getElementsByTagName((String) keyMap.get("nodename"));
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
    public static void writeUpdatedJson(Map<String, Object> jsonData, String tool, String outputDirectoryForSplunkJsons) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        FileOutputStream stream = new FileOutputStream(new File(outputDirectoryForSplunkJsons
                + "/" + tool+"_runs_data.json"));
        stream.write(mapper.writeValueAsString(jsonData).getBytes());
        stream.close();
    }
}
