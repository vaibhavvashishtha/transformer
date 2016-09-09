package com.json.service;

import org.apache.commons.configuration2.Configuration;

import java.util.Map;

public interface ReadPropertiesService {

    /**
     * Read properties from a file
     *
     * @param fileLocation location of the file in physical storage; specify as "" if fileName is Fully Qualified Name
     * @param filename name of the properties file
     * @return
     */
    public Map<String, Object> readProperties(String fileLocation, String filename) throws Exception;

    /**
     * Read properties from a file in the classpath
     *
     * @param filename
     * @return
     */
    public Map<String, Object> readProperties(String filename) throws Exception;

    /**
     *
     * @return
     * @throws Exception
     */
    public Configuration buildConfiguration(String file) throws Exception;

}
