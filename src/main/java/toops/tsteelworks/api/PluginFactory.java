package toops.tsteelworks.api;

import sun.plugin.dom.exception.InvalidStateException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Handles fetching the instances for TSteelworks api interfaces
 */
public class PluginFactory {
	public static final String apiVersion = "1";

	private static final Properties props = new Properties();
	private static final String apiFile = "assets/tsteelworks/api.properties";
	private static boolean isLoaded = false;

	static {
		FileInputStream stream = null;

		try {
			stream = new FileInputStream(apiFile);
			props.load(stream);

			isLoaded = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException ignore) {}
			}
		}
	}

	public static Object getInstance(Class iClazz) {
		if (!isLoaded) throw new InvalidStateException("Properties not loaded - might mean TSteelworks is missing. Report this a bug otherwise");

		String iClassName = iClazz.getCanonicalName();

		String className = props.getProperty(iClassName);

		if (className == null) {
			props.setProperty(iClassName, "<TO_SET>");

			try {
				FileOutputStream stream = new FileOutputStream(apiFile);
				props.store(stream, "TSteelworks API instances");
				stream.close();
			} catch (IOException ignore) {}

			throw new RuntimeException("Could not fetch class for interface " + iClassName + ": Invalid properties file.");
		}

		try {
			Class clazz = Class.forName(className);

			return clazz.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
