package com.json.masterprocessor;

import com.json.GPSIJsonProcessor;
import com.json.WPTJsonProcessor;
import com.json.service.ReadPropertiesService;
import com.json.service.impl.ReadPropertiesServiceImpl;
import com.json.zapprocessor.ZapXMLProcessor;
import org.apache.commons.configuration2.Configuration;
import com.json.SonarJsonProcessor;

import com.json.constants.Constants;

import java.util.List;

public class ProcessToolsOutputForSites {

	private static ReadPropertiesService readPropertiesService = new ReadPropertiesServiceImpl();
	private static Configuration masterProcessorConfig;
	private static String[] sites;

	public static void main(String[] args) throws Exception {
		masterProcessorConfig = readPropertiesService.buildConfiguration(args[0]);
		if (args.length > 1 && args[1] != null) {
			sites = args[1].toString().split(",");
		}
		processWPTJson();
		processGPSIJson();
		processSonarJson();
		processZapXML();
	}

	@SuppressWarnings("unused")
	private static void processWPTJson() throws Exception {
		WPTJsonProcessor wptJsonProcessor = new WPTJsonProcessor();

		if (sites == null) {
			for (Object site : masterProcessorConfig.getList(Constants.SITES)) {
				processWPTJson(wptJsonProcessor, site);
			}
		} else {
			for (Object site : sites) {
				processWPTJson(wptJsonProcessor, site);
			}
		}
	}

	@SuppressWarnings("unused")
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
	private static void processWPTJson(WPTJsonProcessor wptJsonProcessor, Object site) {
		String siteName = getSiteName(site);
		String slash = getCorrectSlash();
		wptJsonProcessor.processJson(Constants.WPT + Constants.RUNS_DATA_JSON,
				masterProcessorConfig.getString(Constants.TOOLS_OUTPUT_MASTER_DIRECTORY) + slash + siteName,
				masterProcessorConfig.getString(Constants.TOOLS_TRANSFORMER_PROPERTIES_DIRECTORY),
				Constants.WPT + Constants.PROPERTIES,
				masterProcessorConfig.getString(site + Constants.DOT + Constants.WPT),
				masterProcessorConfig.getString(Constants.TOOLS_RESULT_JSON_DIRECTORY) + slash + siteName);
	}

	private static void processGPSIJson(GPSIJsonProcessor gpsiJsonProcessor, Object site) {
		String siteName = getSiteName(site);
		String slash = getCorrectSlash();
		gpsiJsonProcessor.processJson(Constants.GPSI + Constants.RUNS_DATA_JSON,
				masterProcessorConfig.getString(Constants.TOOLS_OUTPUT_MASTER_DIRECTORY) + slash + siteName,
				masterProcessorConfig.getString(Constants.TOOLS_TRANSFORMER_PROPERTIES_DIRECTORY),
				Constants.WPT + Constants.PROPERTIES,
				masterProcessorConfig.getString(site + Constants.DOT + Constants.GSPI_DESKTOP),
				masterProcessorConfig.getString(site + Constants.DOT + Constants.GSPI_MOBILE),
				masterProcessorConfig.getString(Constants.TOOLS_RESULT_JSON_DIRECTORY) + slash + siteName);
	}
	private static void processSonarJson(SonarJsonProcessor sonarJsonProcessor, Object site) {
		String siteName = getSiteName(site);
		String slash = getCorrectSlash();
		sonarJsonProcessor.processJson(Constants.SONAR + Constants.RUNS_DATA_JSON,
				masterProcessorConfig.getString(Constants.TOOLS_OUTPUT_MASTER_DIRECTORY) + slash + siteName,
				masterProcessorConfig.getString(Constants.TOOLS_TRANSFORMER_PROPERTIES_DIRECTORY),
				Constants.SONAR + Constants.PROPERTIES,
				masterProcessorConfig.getString(site + Constants.DOT + Constants.SONAR),
				masterProcessorConfig.getString(Constants.TOOLS_RESULT_JSON_DIRECTORY) + slash + siteName);
	}
	private static void processZapXML(ZapXMLProcessor zapXMLProcessor, Object site) {
		String siteName = getSiteName(site);
		String slash = getCorrectSlash();
		zapXMLProcessor.processXML(Constants.ZAP + Constants.RUNS_DATA_JSON,
				masterProcessorConfig.getString(Constants.TOOLS_OUTPUT_MASTER_DIRECTORY) + slash + siteName,
				masterProcessorConfig.getString(Constants.TOOLS_TRANSFORMER_PROPERTIES_DIRECTORY),
				Constants.ZAP + Constants.PROPERTIES,
				masterProcessorConfig.getString(site + Constants.DOT + Constants.ZAP),
				masterProcessorConfig.getString(Constants.TOOLS_RESULT_JSON_DIRECTORY) + slash + siteName);
	}

	private static String getCorrectSlash() {
		String slash = "";
		if (System.getProperty("os.name").startsWith("Windows")) {
			slash = "\\";
		} else {
			slash = "/";
		}
		return slash;
	}

	private static String getSiteName(Object site) {
		String siteName = Constants.WWW + site + Constants.COM;
		return siteName;
	}
}
