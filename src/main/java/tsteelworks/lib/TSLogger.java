package tsteelworks.lib;

import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.Logger;
import tconstruct.TConstruct;
import tsteelworks.TSteelworks;
import tsteelworks.lib.crafting.AlloyInfo;

import java.util.ArrayList;

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

	void logAlloyList() {
		if (!TSteelworks.DEBUG_MODE)
			return;

		for (int i = 0; i < AlloyInfo.alloys.size(); ++i) {
			FluidStack f = AlloyInfo.alloys.get(i).result.copy();
			f.amount = 1000;
			ArrayList<FluidStack> result = AlloyInfo.deAlloy(f);

			System.out.println("Alloy " + AlloyInfo.alloys.get(i).result.getFluid().getName() + " produces:");
			for (int j = 0; j < result.size(); ++j) {
				System.out.println(result.get(j).amount + " mB of " + result.get(j).getFluid().getName());
			}
		}
	}
}
