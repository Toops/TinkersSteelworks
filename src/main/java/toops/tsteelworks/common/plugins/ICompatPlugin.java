package toops.tsteelworks.common.plugins;

public interface ICompatPlugin {
	/**
	 * @return The mod name of this plugin.
	 */
	String getPluginName();

	/**
	 * @return Whether this plugin should be loaded or not.
	 */
	boolean mayLoad();

	/**
	 * Called during TS PreInit
	 */
	void preInit();

	/**
	 * Called during TS Init
	 */
	void init();

	/**
	 * Called during TS PostInit
	 */
	void postInit();
}