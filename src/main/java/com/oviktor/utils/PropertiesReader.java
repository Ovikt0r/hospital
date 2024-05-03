package com.oviktor.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    private PropertiesReader() {
    }

    public static Properties readPropertiesFile(String fileName) throws IOException {
        Properties prop = new Properties();

        try (InputStream is = PropertiesReader.class.getClassLoader().getResourceAsStream(fileName)) {
            prop.load(is);
        }
        return prop;
    }
}
