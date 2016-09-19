package com.json.zapprocessor;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

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
            Map keyMap = ReadProperties.loadPropertiesFromFile(propertiesFilePath
                    +slash+propertiesFileName, true);
            Map resultMap = new HashMap();
            //Node node2 = doc.getDocumentElement().getFirstChild();

            //XPath xPath = XPathFactory.newInstance().newXPath();
            //Node node = (Node) xPath.evaluate("/status", doc, XPathConstants.NODE);
            //System.out.println(node.getNodeValue());
            NodeList nList = doc.getElementsByTagName("summary");
            if (nList.getLength()==1) {
                Node node = nList.item(0);
                NodeList nodeList = node.getChildNodes();
                for (int nodeCount=0; nodeCount<nodeList.getLength(); nodeCount++) {
                    switch (nodeList.item(nodeCount).getNodeName()) {
                        case "status":
                            resultMap.put("status", nodeList.item(nodeCount).getFirstChild().getNodeValue());
                            break;
                        case "NumOfErrors":
                            resultMap.put("numberOfErrors", nodeList.item(nodeCount).getFirstChild().getNodeValue());
                            break;
                        case "NumOfLikelyProblems":
                            resultMap.put("noOfLikelyProblems", nodeList.item(nodeCount).getFirstChild().getNodeValue());
                            break;
                        case "NumOfPotentialProblems":
                            resultMap.put("noOfPotentialProblems", nodeList.item(nodeCount).getFirstChild().getNodeValue());
                            break;
                        case "guidelines":
                            NodeList nodeList1 = nodeList.item(nodeCount).getChildNodes();
                            for (int count=0; count<nodeList1.getLength(); count++) {
                                if (nodeList1.item(count).getNodeName().equalsIgnoreCase("guideline"))
                                    resultMap.put("guideline", nodeList1.item(count).getFirstChild().getNodeValue());
                            }

                            break;
                        default:
                            break;
                    }
                }
            }

            writeUpdatedJson(resultMap, "achecker", outputDirectoryForSplunkJsons);
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
