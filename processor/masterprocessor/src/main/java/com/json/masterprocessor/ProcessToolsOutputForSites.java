package com.json.masterprocessor;

import java.util.HashMap;
import java.util.Map;

import com.json.zapprocessor.ZapXMLProcessor;
import org.apache.commons.configuration2.Configuration;

import com.json.GPSIJsonProcessor;
import com.json.SitespeedHTMLProcessor;
import com.json.SonarJsonProcessor;
import com.json.WPTJsonProcessor;
import com.json.constants.Constants;
import com.json.service.ReadPropertiesService;
import com.json.service.impl.ReadPropertiesServiceImpl;
import com.json.acheckerprocessor.AcheckerXMLProcessor;

import processor.Pa11yProcessor;

// TODO: Auto-generated Javadoc
/**
 * The Class ProcessToolsOutputForSites.
 */
public class ProcessToolsOutputForSites {

	/** The read properties service. */
	private static ReadPropertiesService readPropertiesService = new ReadPropertiesServiceImpl();

	/** The master processor config. */
	private static Configuration masterProcessorConfig;

	/** The sites. */
	private static String[] sites;

	/** The tools to test. */
	private static String toolsToTest[];

	/** The env. */
	private static String env;

	private static Map<String, String> mapOfCLIParams;

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 * @throws Exception
	 *             the exception
	 */
	public static void main(String[] args) throws Exception {
		getConfigurations(args);
		processDesiredTools();
	}

	/**
	 * Process desired tools.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private static void processDesiredTools() throws Exception {
		for (int tool = 0; tool < toolsToTest.length; tool++) {
			String toolToTest = toolsToTest[tool];
			switch (toolToTest) {
			case "wpt":
				processWPTJson(toolToTest);
				break;
			case "gpsi":
				processGPSIJson();
				break;
//			case "sonar":
//				processSonarJson();
//				break;
			case "zap":
				processZapXML();
				break;
			case "sitespeed":
				processSitespeedHTML();
				break;
			case "pa11y":
				processPa11y();
				break;
			case "achecker":
				processAchecker();
				break;
			default:
				processWPTJson(toolToTest);
				processGPSIJson();
				processSonarJson();
				processZapXML();
				processSitespeedHTML();
				processPa11y();
				break;
			}
		}
	}

	/**
	 * Gets the configurations.
	 *
	 * @param args
	 *            the args
	 * @return the configurations
	 * @throws Exception
	 *             the exception
	 */
	private static void getConfigurations(String[] args) throws Exception {
		mapOfCLIParams = getAllCLIParameters(args);
		if (mapOfCLIParams.get("sites") != null) {
			sites = mapOfCLIParams.get("sites").split(",");
		}
		if (mapOfCLIParams.get("toolsToTest") != null) {
			toolsToTest = mapOfCLIParams.get("toolsToTest").toString().split(",");
		}

		masterProcessorConfig = readPropertiesService.buildConfiguration(mapOfCLIParams.get("masterProperties"));
		env = mapOfCLIParams.get("env");
	}

	/**
	 * Gets the all cli parameters.
	 *
	 * @param args
	 *            the args
	 * @return the all cli parameters
	 */
	private static Map<String, String> getAllCLIParameters(String[] args) {
		Map<String, String> mapOfCLIParams = new HashMap<String, String>();
		for (int count = 0; count < args.length; count++) {
			String argument = args[count];
			String[] keyValue = argument.split("=");
			mapOfCLIParams.put(keyValue[0], keyValue[1]);
		}
		return mapOfCLIParams;
	}

	/**
	 * Process wpt json.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private static void processWPTJson(String toolToTest) throws Exception {
		WPTJsonProcessor wptJsonProcessor = (WPTJsonProcessor) ProcessorFactory.getFileProcessor(toolToTest);

		if (sites == null) {
			for (Object site : masterProcessorConfig.getList(Constants.SITES)) {
				processWPTJson(wptJsonProcessor, site, toolToTest);
			}
		} else {
			for (Object site : sites) {
				processWPTJson(wptJsonProcessor, site, toolToTest);
			}
		}
	}

	/**
	 * Process wpt json.
	 *
	 * @param wptJsonProcessor
	 *            the wpt json processor
	 * @param site
	 *            the site
	 */
	private static void processWPTJson(WPTJsonProcessor wptJsonProcessor, Object site, String toolToTest) {
		String siteName = getSiteName(site);
		String slash = getCorrectSlash();
		wptJsonProcessor.processJson(getResultFileName(toolToTest, site),
				masterProcessorConfig.getString(Constants.TOOLS_OUTPUT_MASTER_DIRECTORY) + slash + siteName,
				masterProcessorConfig.getString(Constants.TOOLS_TRANSFORMER_PROPERTIES_DIRECTORY),
				masterProcessorConfig.getString(toolToTest + Constants.DOT + Constants.PROPERTY_FILE_NAME),
				masterProcessorConfig.getString(site + Constants.DOT + toolToTest),
				masterProcessorConfig.getString(Constants.TOOLS_RESULT_JSON_DIRECTORY) + slash + siteName);
	}

	/**
	 * Process gpsi json.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private static void processGPSIJson() throws Exception {
		GPSIJsonProcessor gpsiJsonProcessor = new GPSIJsonProcessor();

		if (sites == null) {
			for (Object site : masterProcessorConfig.getList(Constants.SITES)) {
				processGPSIJson(gpsiJsonProcessor, site);
			}
		} else {
			for (Object site : sites) {
				processGPSIJson(gpsiJsonProcessor, site);
			}
		}
	}

	/**
	 * Process sonar json.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private static void processSonarJson() throws Exception {
		SonarJsonProcessor sonarJsonProcessor = new SonarJsonProcessor();

		if (sites == null) {
			for (Object site : masterProcessorConfig.getList(Constants.SITES)) {
				processSonarJson(sonarJsonProcessor, site);
			}
		} else {
			for (Object site : sites) {
				processSonarJson(sonarJsonProcessor, site);
			}
		}
	}

	/**
	 * Process zap xml.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private static void processZapXML() throws Exception {
		ZapXMLProcessor zapXMLProcessor = new ZapXMLProcessor();

		if (sites == null) {
			for (Object site : masterProcessorConfig.getList(Constants.SITES)) {
				processZapXML(zapXMLProcessor, site);
			}
		} else {
			for (Object site : sites) {
				processZapXML(zapXMLProcessor, site);
			}
		}
	}

	private static void processAchecker() {
		AcheckerXMLProcessor acheckerXMLProcessor = new AcheckerXMLProcessor();

		if (sites == null) {
			for (Object site : masterProcessorConfig.getList(Constants.SITES)) {
				processAcheckerXML(acheckerXMLProcessor, site);
			}
		} else {
			for (Object site : sites) {
				processAcheckerXML(acheckerXMLProcessor, site);
			}
		}
	}

	/**
	 * Process sitespeed html.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private static void processSitespeedHTML() throws Exception {
		SitespeedHTMLProcessor sitespeedHTMLProcessor = new SitespeedHTMLProcessor();

		if (sites == null) {
			for (Object site : masterProcessorConfig.getList(Constants.SITES)) {
				processSitespeedHTML(sitespeedHTMLProcessor, site);
			}
		} else {
			for (Object site : sites) {
				processSitespeedHTML(sitespeedHTMLProcessor, site);
			}
		}
	}

	/**
	 * Process pa11y.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private static void processPa11y() throws Exception {
		Pa11yProcessor pa11yProcessor = new Pa11yProcessor();

		if (sites == null) {
			for (Object site : masterProcessorConfig.getList(Constants.SITES)) {
				processPa11yCSV(pa11yProcessor, site);
			}
		} else {
			for (Object site : sites) {
				processPa11yCSV(pa11yProcessor, site);
			}
		}
	}

	/**
	 * Gets the result file name.
	 *
	 * @param toolName
	 *            the tool name
	 * @param site
	 *            the site
	 * @return the result file name
	 */
	private static String getResultFileName(String toolName, Object site) {
		return toolName + Constants.UNDER_SCORE + site + Constants.RUNS_DATA_JSON;
	}

	/**
	 * Process gpsi json.
	 *
	 * @param gpsiJsonProcessor
	 *            the gpsi json processor
	 * @param site
	 *            the site
	 */
	private static void processGPSIJson(GPSIJsonProcessor gpsiJsonProcessor, Object site) {
		String siteName = getSiteName(site);
		String slash = getCorrectSlash();
		gpsiJsonProcessor.processJson(getResultFileName(Constants.GPSI, site),
				masterProcessorConfig.getString(Constants.TOOLS_OUTPUT_MASTER_DIRECTORY) + slash + siteName,
				masterProcessorConfig.getString(Constants.TOOLS_TRANSFORMER_PROPERTIES_DIRECTORY),
				masterProcessorConfig.getString(Constants.GPSI + Constants.DOT + Constants.PROPERTY_FILE_NAME),
				masterProcessorConfig.getString(site + Constants.DOT + Constants.GSPI_DESKTOP),
				masterProcessorConfig.getString(site + Constants.DOT + Constants.GSPI_MOBILE),
				masterProcessorConfig.getString(Constants.TOOLS_RESULT_JSON_DIRECTORY) + slash + siteName);
	}

	/**
	 * Process sonar json.
	 *
	 * @param sonarJsonProcessor
	 *            the sonar json processor
	 * @param site
	 *            the site
	 */
	private static void processSonarJson(SonarJsonProcessor sonarJsonProcessor, Object site) {
		String siteName = getSiteName(site);
		String slash = getCorrectSlash();
		sonarJsonProcessor.processJson(getResultFileName(Constants.SONAR, site),
				masterProcessorConfig.getString(Constants.TOOLS_OUTPUT_MASTER_DIRECTORY) + slash + siteName,
				masterProcessorConfig.getString(Constants.TOOLS_TRANSFORMER_PROPERTIES_DIRECTORY),
				masterProcessorConfig.getString(Constants.SONAR + Constants.DOT + Constants.PROPERTY_FILE_NAME),
				masterProcessorConfig.getString(site + Constants.DOT + Constants.SONAR),
				masterProcessorConfig.getString(Constants.TOOLS_RESULT_JSON_DIRECTORY) + slash + siteName);
	}

	/**
	 * Process zap xml.
	 *
	 * @param zapXMLProcessor
	 *            the zap xml processor
	 * @param site
	 *            the site
	 */
	private static void processZapXML(ZapXMLProcessor zapXMLProcessor, Object site) {
		String siteName = getSiteName(site);
		String slash = getCorrectSlash();
		zapXMLProcessor.processXML(
				masterProcessorConfig.getString(Constants.TOOLS_OUTPUT_MASTER_DIRECTORY) + slash + siteName,
				masterProcessorConfig.getString(Constants.TOOLS_TRANSFORMER_PROPERTIES_DIRECTORY),
				masterProcessorConfig.getString(Constants.ZAP + Constants.DOT + Constants.PROPERTY_FILE_NAME),
				masterProcessorConfig.getString(site + Constants.DOT + Constants.ZAP),
				getResultFileName(Constants.ZAP, site),
				masterProcessorConfig.getString(Constants.TOOLS_RESULT_JSON_DIRECTORY) + slash + siteName);
	}

	private static void processAcheckerXML(AcheckerXMLProcessor acheckerXMLProcessor, Object site) {
		String siteName = getSiteName(site);
		String slash = getCorrectSlash();
		acheckerXMLProcessor
				.processXML(masterProcessorConfig.getString(Constants.TOOLS_OUTPUT_MASTER_DIRECTORY) + slash + siteName,
						masterProcessorConfig.getString(Constants.TOOLS_TRANSFORMER_PROPERTIES_DIRECTORY),
						masterProcessorConfig
								.getString(Constants.ACHECKER + Constants.DOT + Constants.PROPERTY_FILE_NAME),
						masterProcessorConfig.getString(site + Constants.DOT + Constants.ACHECKER),
						getResultFileName(Constants.ACHECKER, site),
						masterProcessorConfig.getString(Constants.TOOLS_RESULT_JSON_DIRECTORY) + slash + siteName);
	}

	/**
	 * Process sitespeed html.
	 *
	 * @param sitespeedHTMLProcessor
	 *            the sitespeed html processor
	 * @param site
	 *            the site
	 */
	private static void processSitespeedHTML(SitespeedHTMLProcessor sitespeedHTMLProcessor, Object site) {
		String siteName = getSiteName(site);
		String slash = getCorrectSlash();
		sitespeedHTMLProcessor.processHTML(getResultFileName(Constants.SITESPEED, site),
				masterProcessorConfig.getString(Constants.TOOLS_OUTPUT_MASTER_DIRECTORY) + slash + siteName,
				masterProcessorConfig.getString(Constants.TOOLS_TRANSFORMER_PROPERTIES_DIRECTORY),
				masterProcessorConfig.getString(Constants.SITESPEED + Constants.DOT + Constants.PROPERTY_FILE_NAME),
				masterProcessorConfig.getString(site + Constants.DOT + Constants.SITESPEED),
				masterProcessorConfig.getString(Constants.TOOLS_RESULT_JSON_DIRECTORY) + slash + siteName);
	}

	/**
	 * Process pa11y csv.
	 *
	 * @param pa11yProcessor
	 *            the pa11y processor
	 * @param site
	 *            the site
	 */
	private static void processPa11yCSV(Pa11yProcessor pa11yProcessor, Object site) {
		String siteName = getSiteName(site);
		String slash = getCorrectSlash();
		pa11yProcessor.processCSV(getResultFileName(Constants.Pa11y, site),
				masterProcessorConfig.getString(Constants.TOOLS_OUTPUT_MASTER_DIRECTORY) + slash + siteName,
				masterProcessorConfig.getString(Constants.TOOLS_TRANSFORMER_PROPERTIES_DIRECTORY),
				masterProcessorConfig.getString(Constants.Pa11y + Constants.DOT + Constants.PROPERTY_FILE_NAME),
				masterProcessorConfig.getString(site + Constants.DOT + Constants.Pa11y),
				masterProcessorConfig.getString(Constants.TOOLS_RESULT_JSON_DIRECTORY) + slash + siteName);
	}

	/**
	 * Gets the correct slash.
	 *
	 * @return the correct slash
	 */
	private static String getCorrectSlash() {
		String slash = "";
		if (System.getProperty("os.name").startsWith("Windows")) {
			slash = "\\";
		} else {
			slash = "/";
		}
		return slash;
	}

	/**
	 * Gets the site name.
	 *
	 * @param site
	 *            the site
	 * @return the site name
	 */
	private static String getSiteName(Object site) {
		String siteName = "";
		if (env != null && env.equalsIgnoreCase("prod")) {
			siteName = Constants.WWW + site + Constants.COM;
		} else {
			siteName = site + Constants.COM;
		}
		return siteName;
	}
}
