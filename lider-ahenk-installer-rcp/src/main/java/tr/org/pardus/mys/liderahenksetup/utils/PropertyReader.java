package tr.org.pardus.mys.liderahenksetup.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertyReader {
	
	private static final Logger logger = Logger.getLogger(PropertyReader.class.getName());

	private static HashMap<String, String> propertyMap = null;
	private static final String propertiesFile = "lider-config.properties";

	public static void readProperties() {

		propertyMap = new HashMap<String, String>();

		try {

			Properties properties = new Properties();
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesFile);
			
			if (inputStream == null) {
				logger.log(Level.WARNING, "Could not find properties file: {0}", propertiesFile);
				return;
			}

			properties.load(inputStream);

			Enumeration<Object> keySet = properties.keys();
			while (keySet.hasMoreElements()) {
				Object keyObject = keySet.nextElement();
				propertyMap.put(keyObject.toString(), properties.getProperty(keyObject.toString()));
				logger.log(Level.INFO, keyObject.toString() + "=" + properties.getProperty(keyObject.toString()));
			}

		} catch (IOException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			e.printStackTrace();
		}

	}

	public static String property(String key) {
		if (propertyMap == null) {
			readProperties();
		}
		return propertyMap.get(key);
	}
	
}
