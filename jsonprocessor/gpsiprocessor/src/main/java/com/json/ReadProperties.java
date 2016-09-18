package com.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map;
import java.util.Set;

/**
 * Created by vaibhavvashishtha on 03/09/16.
 */
public class ReadProperties {

    /**
     *
     * @param filename
     * @param loadTypeIsFile
     * @return
     * @throws Exception
     */
    public static Map<String, String> loadPropertiesFromFile(String filename, boolean loadTypeIsFile)
            throws Exception{
        final Properties properties = new Properties();
        InputStream is = null;
        if (!loadTypeIsFile)
            is = ClassLoader.class.getResourceAsStream("/config/"+filename);
        else
            is = new FileInputStream(new File(filename));
        properties.load(is);
        final Map<String, String> keyMap = new HashMap<String, String>();
        properties.stringPropertyNames().forEach((property)
                -> {keyMap.put(property, properties.getProperty(property));});
        return keyMap;
    }
}
