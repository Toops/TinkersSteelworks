package tsteelworks.lib;

import org.apache.logging.log4j.Logger;
import tconstruct.TConstruct;
import tsteelworks.TSteelworks;

public class TSLogger {
	public static Logger logger;
	public boolean debugMode;

	public TSLogger(Logger logger, boolean debugMode) {
		this.debugMode = debugMode;
		TSLogger.logger = logger;
	}

	public void introMessage() {
		TConstruct.logger.info("TSteelworks, are you pondering what I'm pondering?");
		logger.info("I think so, TConstruct, but where are we going to find a duck and a hose at this hour?");
	}

	public static void info(String desc) {
		if (!TSteelworks.DEBUG_MODE)
			return;

		logger.info(desc);
	}

	public static void info(String desc, int value) {
		if (!TSteelworks.DEBUG_MODE)
			return;

		logger.info(desc + ": " + value);
	}

	public static void info(String desc, float value) {
		if (!TSteelworks.DEBUG_MODE)
			return;

		logger.info(desc + ": " + value);
	}

	public static void info(String desc, String text) {
		if (!TSteelworks.DEBUG_MODE)
			return;

		logger.info(desc + ": " + text);
	}

	public static void info(String desc, boolean flag) {
		if (!TSteelworks.DEBUG_MODE)
			return;

		logger.info(desc + ": " + flag);
	}

	public static void warning(String desc) {
		logger.warn(desc);
	}

	public static void error(String msg, Throwable thrown) {
		logger.error(msg, thrown);
	}
}
