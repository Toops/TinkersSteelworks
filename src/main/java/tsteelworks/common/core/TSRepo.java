package tsteelworks.common.core;


public class TSRepo {
	public static final String MOD_ID = "TSteelworks";
	public static final String MOD_NAME = "Tinkers' Steelworks";
	public static final String MOD_VER = "1.7.10-1.0.0";
	public static final String MOD_SERV_PROXY = "tsteelworks.common.core.TSCommonProxy";
	public static final String MOD_CLIENT_PROXY = "tsteelworks.client.TSClientProxy";
	public static final String MOD_REQUIRE = "required-after:TConstruct;required-after:Forge@[10.13.0.1199,);required-after:cookiecore@[1.1.0,);after:CoFHCore";
	public static final String TEXTURE_DIR = "tsteelworks:";

	public static final Integer OVEN_PACKET_ID = 0;
	public static final Integer DUCT_PACKET_ID = 1;
	public static final Integer TANK_PACKET_ID = 2;

	public class NBTNames {
		// Shared
		public static final String DIRECTION = "Direction";

		// High Oven
		public static final String INTERNAL_TEMP = "InternalTemp";
		public static final String IN_USE = "InUse";
		public static final String USE_TIME = "UseTime";
		public static final String FUEL_HEAT_RATE = "FuelHeatRate";
		public static final String MELTING_TEMPS = "MeltingTemps";
		public static final String ACTIVE_TEMPS = "ActiveTemps";
	}
}