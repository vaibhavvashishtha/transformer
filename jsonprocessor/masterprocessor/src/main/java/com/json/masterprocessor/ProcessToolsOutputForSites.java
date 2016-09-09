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

    public static void main(String[] args) throws Exception{
        masterProcessorConfig = readPropertiesService.buildConfiguration(args[0]);
        processWPTJson();
    }

    @SuppressWarnings("unused")
	private static void processWPTJson() throws Exception{
        WPTJsonProcessor wptJsonProcessor = new WPTJsonProcessor();

        for (Object site : masterProcessorConfig.getList(Constants.SITES)
             ) {
        	String siteName = Constants.WWW + site + Constants.COM;
        	String slash = "";
        	if (System.getProperty("os.name").startsWith("Windows")) {
        		slash = "\\";
        	} else {
        		slash = "/";
        	}
            wptJsonProcessor.processJson(Constants.WPT + Constants.RUNS_DATA_JSON,
                    masterProcessorConfig.getString(Constants.TOOLS_OUTPUT_MASTER_DIRECTORY) + slash + siteName,
                    masterProcessorConfig.getString(Constants.TOOLS_TRANSFORMER_PROPERTIES_DIRECTORY),
                    Constants.WPT + Constants.PROPERTIES,
                    masterProcessorConfig.getString(site + Constants.DOT + Constants.WPT), 
                    masterProcessorConfig.getString(Constants.TOOLS_RESULT_JSON_DIRECTORY) + slash + siteName);
        }
    }
}
