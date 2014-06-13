package tsteelworks.common;


public class TSRepo
{
    public static final String modId = "TSteelworks";
    public static final String modName = "Tinkers' Steelworks";
    public static final String modVer = "1.6.4-0.0.4.2-fix2";
    public static final String modChan = modId;
    public static final String modServProxy = "tsteelworks.common.TSCommonProxy";
    public static final String modClientProxy = "tsteelworks.client.TSClientProxy";
    public static final String modRequire = "required-after:TConstruct";
    public static final String textureDir = "tsteelworks:";
    
    public static final Integer ovenPacketID = 1;
    public static final Integer ductPacketID = 2;
    public static final Integer tankPacketID = 3;
    
    public class NBTNames
    {
        public static final String layers = "Layers";
        public static final String centerPos = "CenterPos";
        public static final String direction = "Direction";
        
        public static final String currentLiquid = "CurrentLiquid"; 
        public static final String maxLiquid = "MaxLiquid"; 
        public static final String liquids = "Liquids"; 
        
        public static final String innerMaxX = "InnerMaxX";
        public static final String innerMaxZ = "InnerMaxZ";
        public static final String containsAlloy = "ContainsAlloy";
    }
}
