package toops.tsteelworks.lib;

public class TSRepo {
	public static final String MOD_ID = "TSteelworks";
	public static final String MOD_NAME = "Tinkers' Steelworks";
	public static final String MOD_VER = "1.7.10-1.1.2";
	public static final String MOD_SERV_PROXY = "toops.tsteelworks.common.core.TSCommonProxy";
	public static final String MOD_CLIENT_PROXY = "toops.tsteelworks.client.core.TSClientProxy";
	public static final String MOD_REQUIRE = "required-after:Forge@[10.13.4.1448,);required-after:cookiecore@[1.4.0,);required-after:TConstruct;after:CoFHCore;after:NotEnoughItems;after:ExtraTic;after:MineTweaker3;after:ThermalExpansion";
	public static final String NAMESPACE = "tsteelworks:";

	public class NBTNames {
		// Shared
		public static final String DIRECTION = "Direction";

		// High Oven
		public static final String INTERNAL_TEMP = "InternalTemp";
		public static final String IN_USE = "InUse";
		public static final String USE_TIME = "UseTime";
		public static final String USE_TIME_TOTAL = "UseTimeTotal";
		public static final String FUEL_HEAT_RATE = "FuelHeatRate";
		public static final String MELTING_TEMPS = "MeltingTemps";
		public static final String ACTIVE_TEMPS = "ActiveTemps";
		public static final String REDSTONE = "Redstone";
	}
}