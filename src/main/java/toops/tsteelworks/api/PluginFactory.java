package toops.tsteelworks.api;

import cpw.mods.fml.common.Loader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Handles fetching the instances for TSteelworks api interfaces
 */
public class PluginFactory {
	public static final String apiVersion = "1.2";
	private static final String apiFile = "assets/tsteelworks/api.properties";

	private static Properties props = new Properties();
	private static Map<String, Object> instances = new HashMap<>();
	private static Exception error = null;

	static {
		InputStream stream = null;
		try {
			ClassLoader loader = Loader.instance().getIndexedModList().get("TSteelworks").getMetadata().getClass().getClassLoader();
			stream = loader.getResourceAsStream(apiFile);
			props.load(stream);
		} catch (Exception e) {
			error = e;
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

	public static Object getInstance(Class iClazz) {
		if (error != null) {
			throw new RuntimeException("Properties not loaded - might mean TSteelworks is missing. Report this a bug otherwise", error);
		}

		String iClassName = iClazz.getCanonicalName();
		String className = props.getProperty(iClassName);

		if (className == null) {
			throw new RuntimeException("Could not fetch class for interface " + iClassName + ": Missing from properties file.");
		}

		// Reuse existing instances
		Object instance = instances.get(className);
		if (instance != null) return instance;

		try {
			Class clazz = Class.forName(className);

			@SuppressWarnings("unchecked")
			Constructor constructor = clazz.getDeclaredConstructor();

			constructor.setAccessible(true);

			instance = constructor.newInstance();
			instances.put(className, instance);

			return instance;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			throw new RuntimeException("Could not fetch class for interface " + iClassName, e);
		}
	}
}
