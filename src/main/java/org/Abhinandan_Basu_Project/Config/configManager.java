package org.Abhinandan_Basu_Project.Config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class configManager {

    private static final Properties properties = new Properties();
    private static String Path;
    static {
        Path = "config/config.properties";
        InputStream input = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(Path);
        if(input == null)
        {
            throw new RuntimeException("Cannot find the path :" +Path);
        }
        try{
            properties.load(input);
        }catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public static String getProperty(String key)
    {
        return properties.getProperty(key);
    }
}
