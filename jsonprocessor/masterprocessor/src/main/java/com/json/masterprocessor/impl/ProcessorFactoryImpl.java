package com.json.masterprocessor.impl;

import com.json.WPTJsonProcessor;
import com.json.FileProcessor;
import com.json.GPSIJsonProcessor;
import com.json.masterprocessor.ProcessorFactory;

public class ProcessorFactoryImpl {

	public FileProcessor getFileProcessor(String toolToTest) {
		switch (toolToTest) {
		case "wpt":
			return new WPTJsonProcessor();
		case "gpsi":
			return new GPSIJsonProcessor();
		case "sonar":
			return new WPTJsonProcessor();
		case "zap":
			return new WPTJsonProcessor();
		case "sitespeed":
			return new WPTJsonProcessor();
		case "pa11y":
			return new WPTJsonProcessor();
		default:
			return null;
		}
	}

}
