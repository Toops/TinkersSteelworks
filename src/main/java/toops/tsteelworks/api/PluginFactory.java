package toops.tsteelworks.api;

import sun.plugin.dom.exception.InvalidStateException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
		InputStream stream = null;
		try {
			stream = ClassLoader.getSystemResourceAsStream(apiFile);
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
			throw new RuntimeException("Could not fetch class for interface " + iClassName + ": Missing from properties file.");
		}

		try {
			Class clazz = Class.forName(className);

			@SuppressWarnings("unchecked")
			Constructor constructor = clazz.getDeclaredConstructor();

			constructor.setAccessible(true);

			return constructor.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			throw new RuntimeException("Could not fetch class for interface " + iClassName, e);
		}
	}
}
