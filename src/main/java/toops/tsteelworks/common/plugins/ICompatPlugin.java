package toops.tsteelworks.common.plugins;

public interface ICompatPlugin {
	/** Mod name of the plugin */
	public abstract String getPluginName();

	/** Whether or not this plugin should be loaded or not */
	public abstract boolean mayLoad();

	/** Called during TS PreInit */
	public abstract void preInit();

	/** Called during TS Init */
	public abstract void init();

	/** Called during TS PostInit */
	public abstract void postInit();
}