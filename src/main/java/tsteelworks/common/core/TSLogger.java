package tsteelworks.common.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tconstruct.TConstruct;
import tsteelworks.TSteelworks;
import tsteelworks.lib.TSRepo;

public class TSLogger {
	public static Logger logger = LogManager.getLogger(TSRepo.MOD_ID);

	public static void introMessage() {
		TConstruct.logger.info("TSteelworks, are you pondering what I'm pondering?");
		logger.info("I think so, TConstruct, but where are we going to find a duck and a hose at this hour?");
	}

	public static void info(String desc) {
		logger.info(desc);
	}

	public static void info(String desc, int value) {
		logger.info(desc + ": " + value);
	}

	public static void info(String desc, float value) {
		logger.info(desc + ": " + value);
	}

	public static void info(String desc, String text) {
		logger.info(desc + ": " + text);
	}

	public static void info(String desc, boolean flag) {
		logger.info(desc + ": " + flag);
	}

	public static void warning(String desc) {
		logger.warn(desc);
	}

	public static void error(String msg, Throwable thrown) {
		logger.error(msg, thrown);
	}

	public static void debug(String msg) {
		if (TSteelworks.DEBUG_MODE)
			logger.debug(msg);
	}
}
