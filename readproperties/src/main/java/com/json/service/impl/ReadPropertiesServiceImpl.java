package com.json.service.impl;

import com.json.service.ReadPropertiesService;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.builder.fluent.Parameters;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ReadPropertiesServiceImpl implements ReadPropertiesService {

    @Override
    public Map<String, Object> readProperties(String fileLocation, String filename) throws Exception{
        switch (fileLocation) {
            case "":
                return readProperties(new FileInputStream(filename));
            default:
                return readProperties(new FileInputStream(fileLocation + "/" + filename));
        }
    }

    @Override
    public Map<String, Object> readProperties(String filename) throws Exception{
        return readProperties(ClassLoader.class.getResourceAsStream("/config/"+filename));
    }

    @Override
    public Configuration buildConfiguration(String file) throws Exception {
        Configurations configs = new Configurations();
        
        try {
            return configs.properties(file);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
       
    }

    private Map<String, Object> readProperties(InputStream inputStream) throws Exception{
        Properties properties = new Properties();
        properties.load(inputStream);
        final Map<String, Object> keyMap = new HashMap<String, Object>();
        properties.stringPropertyNames().forEach((property)
                -> {keyMap.put(property, new Properties().getProperty(property));});
        return keyMap;
    }
}
