package com.json.service.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;

import com.json.service.ReadPropertiesService;

public class ReadPropertiesServiceImpl implements ReadPropertiesService {

    @Override
    public Map<String, Object> readProperties(String fileLocation, String filename) throws FileNotFoundException, IOException {
        if ("".equalsIgnoreCase(fileLocation)) {
        	return readProperties(new FileInputStream(filename));
        } else {
            return readProperties(new FileInputStream(fileLocation + "/" + filename));
        }
    }

    @Override
    public Map<String, Object> readProperties(String filename) throws IOException {
        return readProperties(ClassLoader.class.getResourceAsStream("/config/"+filename));
    }

    @Override
    public Configuration buildConfiguration(String file) {
        Configurations configs = new Configurations();
        
        try {
            return configs.properties(file);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
       
    }

    private Map<String, Object> readProperties(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);
        final Map<String, Object> keyMap = new HashMap<>();
        properties.stringPropertyNames().forEach(property
                -> keyMap.put(property, new Properties().getProperty(property)));
        return keyMap;
    }
}
