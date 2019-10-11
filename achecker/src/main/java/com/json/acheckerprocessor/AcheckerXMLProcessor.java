package com.json.acheckerprocessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by vaibhavvashishtha on 06/09/16.
 */
public class AcheckerXMLProcessor {

	private String resultFileName = "";
	private String slash = "";
	private String resultDirectory = "";

	/**
	 * 
	 * @param outputDirectoryForSplunkJsons
	 * @param sourceXML
	 * @param resultFileName
	 * @param resultDirectory
	 */
	public void processXML(String outputDirectoryForSplunkJsons, String sourceXML, String resultFileName,
			String resultDirectory) {
		try {
			Map<String, Object> resultMap = new HashMap<>();

			this.resultFileName = resultFileName;
			this.resultDirectory = resultDirectory;

			if (System.getProperty("os.name").startsWith("Windows")) {
				slash = "\\";
			} else {
				slash = "/";
			}

			evaluateNodeListFromPreparedDoc(resultMap,
					prepareDocFromXML(outputDirectoryForSplunkJsons, sourceXML).getElementsByTagName("summary"));

			writeUpdatedJson(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param resultMap
	 * @param nList
	 */
	private void evaluateNodeListFromPreparedDoc(Map<String, Object> resultMap, NodeList nList) {
		if (nList.getLength() == 1) {
			Node node = nList.item(0);
			NodeList nodeList = node.getChildNodes();
			for (int nodeCount = 0; nodeCount < nodeList.getLength(); nodeCount++) {
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
					for (int count = 0; count < nodeList1.getLength(); count++) {
						if (nodeList1.item(count).getNodeName().equalsIgnoreCase("guideline"))
							resultMap.put("guideline", nodeList1.item(count).getFirstChild().getNodeValue());
					}

					break;
				default:
					break;
				}
			}
		}
	}

	/**
	 * 
	 * @param outputDirectoryForSplunkJsons
	 * @param sourceXML
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private Document prepareDocFromXML(String outputDirectoryForSplunkJsons, String sourceXML)
			throws ParserConfigurationException, SAXException, IOException {
		File fXmlFile = new File(outputDirectoryForSplunkJsons + slash + sourceXML);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		return doc;
	}

	/**
	 * @param jsonData
	 * @throws IOException 
	 */
	public void writeUpdatedJson(Map<String, Object> jsonData) throws IOException
			  {
		ObjectMapper mapper = new ObjectMapper();
		FileOutputStream stream = new FileOutputStream(createFileIfNotPresent(), false);
		addDateToJson(jsonData);
		stream.write(mapper.writeValueAsString(jsonData).getBytes());
		stream.close();
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	private File createFileIfNotPresent() {
		File file = new File(resultDirectory + slash + resultFileName);
		file.getParentFile().mkdirs();
		return file;
	}

	/**
	 * 
	 * @param targetJsonMap
	 */
	private void addDateToJson(Map<String, Object> targetJsonMap) {
		Calendar cal = Calendar.getInstance();
		targetJsonMap.put("date", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(cal.getTime()));
	}
}
