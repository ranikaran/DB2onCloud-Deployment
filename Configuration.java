import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class retrieves the value for the key passed 
 * and will return the corresponding value from the config file
 */
public class Configuration   {

	static String value = "";
	static InputStream inputStream;
	
		public static String getPropValue(String key)  {
			try {
				Properties prop = new Properties();
				String propFileName = "config.properties";
				inputStream = Configuration.class.getResourceAsStream("config.properties");
	 
				if (inputStream != null) {
					prop.load(inputStream);
				} else {
					throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
				}
				value = prop.getProperty(key);	 	 
			} catch (Exception e) {
				System.out.println("Exception: " + e);
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return value;
		}
	
		
	}

