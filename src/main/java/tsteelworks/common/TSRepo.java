package tsteelworks.common;


public class TSRepo {
	public static final String modId = "TSteelworks";
	public static final String modName = "Tinkers' Steelworks";
	public static final String modVer = "1.7.10-1.0.0";
	public static final String modServProxy = "tsteelworks.common.TSCommonProxy";
	public static final String modClientProxy = "tsteelworks.client.TSClientProxy";
	public static final String modRequire = "required-after:TConstruct;required-after:Forge@[10.13.0.1199,);required-after:cookiecore@[1.1.0,)";
	public static final String textureDir = "tsteelworks:";

	public static final Integer ovenPacketID = 0;
	public static final Integer ductPacketID = 1;
	public static final Integer tankPacketID = 2;

	public class NBTNames {
		// Shared
		public static final String layers = "Layers";
		public static final String centerPos = "CenterPos";
		public static final String direction = "Direction";

		public static final String currentLiquid = "CurrentLiquid";
		public static final String maxLiquid = "MaxLiquid";
		public static final String liquids = "Liquids";
		public static final String redstoneOn = "RedstoneActivated";

		// High Oven
		public static final String maxTemp = "MaxTemp";
		public static final String internalTemp = "InternalTemp";
		public static final String outputDuct = "OutputDuct";
		public static final String inUse = "InUse";
		public static final String useTime = "UseTime";
		public static final String fuelHeatRate = "FuelHeatRate";
		public static final String meltingTemps = "MeltingTemps";
		public static final String activeTemps = "ActiveTemps";

		// Deep Tank
		public static final String innerMaxX = "InnerMaxX";
		public static final String innerMaxZ = "InnerMaxZ";
		public static final String containsAlloy = "ContainsAlloy";
	}
}