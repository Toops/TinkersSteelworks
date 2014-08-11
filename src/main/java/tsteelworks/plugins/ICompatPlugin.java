package tsteelworks.plugins;

public interface ICompatPlugin {

	/** Mod ID the plugin handles */
	public abstract String getModId();

	/** Called during TS Init */
	public abstract void init();

	/** Called during TS PostInit */
	public abstract void postInit();

	/** Called during TS PreInit */
	public abstract void preInit();
}