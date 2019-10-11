package com.json.service;

import org.apache.commons.configuration2.Configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public interface ReadPropertiesService {

    /**
     * Read properties from a file
     *
     * @param fileLocation location of the file in physical storage; specify as "" if fileName is Fully Qualified Name
     * @param filename name of the properties file
     * @return
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public Map<String, Object> readProperties(String fileLocation, String filename) throws FileNotFoundException, IOException;

    /**
     * Read properties from a file in the classpath
     *
     * @param filename
     * @return
     * @throws IOException 
     */
    public Map<String, Object> readProperties(String filename) throws IOException ;

    /**
     *
     * @return
     * @throws Exception
     */
    public Configuration buildConfiguration(String file);

}
