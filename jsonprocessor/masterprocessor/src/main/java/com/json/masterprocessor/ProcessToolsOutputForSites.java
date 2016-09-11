package com.json.masterprocessor;

import com.json.WPTJsonProcessor;
import com.json.service.ReadPropertiesService;
import com.json.service.impl.ReadPropertiesServiceImpl;
import org.apache.commons.configuration2.Configuration;

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
		processGDPSIJson(wptJsonProcessor, site);
	}

	@SuppressWarnings("unused")
	private static void processWPTJson() throws Exception {
		WPTJsonProcessor wptJsonProcessor = new WPTJsonProcessor();

		if (sites != null) {
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
		GPSIJsonProcessor wptJsonProcessor = new GPSIJsonProcessor();

		if (sites != null) {
			for (Object site : masterProcessorConfig.getList(Constants.SITES)) {
				processWPTJson(wptJsonProcessor, site);
			}
		} else {
			for (Object site : sites) {
				processWPTJson(wptJsonProcessor, site);
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
	
	private static void processGDPSIJson(GPSIJsonProcessor gpsiJsonProcessor, Object site) {
		String siteName = getSiteName(site);
		String slash = getCorrectSlash();
		gpsiJsonProcessor.processJson(Constants.WPT + Constants.RUNS_DATA_JSON,
				masterProcessorConfig.getString(Constants.TOOLS_OUTPUT_MASTER_DIRECTORY) + slash + siteName,
				masterProcessorConfig.getString(Constants.TOOLS_TRANSFORMER_PROPERTIES_DIRECTORY),
				Constants.WPT + Constants.PROPERTIES,
				masterProcessorConfig.getString(site + Constants.DOT + Constants.GSPI_DESKTOP),
				masterProcessorConfig.getString(site + Constants.DOT + Constants.GSPI_MOBILE),
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
