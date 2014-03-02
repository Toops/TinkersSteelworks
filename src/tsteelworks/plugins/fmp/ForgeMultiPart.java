package tsteelworks.plugins.fmp;

import tsteelworks.TSteelworks;
import tsteelworks.common.TSContent;
import tsteelworks.plugins.ICompatPlugin;
import tsteelworks.plugins.fmp.register.RegisterWithFMP;

public class ForgeMultiPart implements ICompatPlugin
{
    @Override
    public String getModId() {
        return "ForgeMultipart";
    }

    @Override
    public void preInit() {
        // Nothing
    }
    
    @Override
    public void init()
    {
        TSteelworks.logger.info("ForgeMultipart detected. Registering TSteelworks decorative blocks with FMP.");
        RegisterWithFMP.registerBlock(TSContent.charcoalBlock);
        RegisterWithFMP.registerBlock(TSContent.dustStorageBlock, 0, 1);
        RegisterWithFMP.registerBlock(TSContent.highoven, 2, 2);
        RegisterWithFMP.registerBlock(TSContent.highoven, 4, 10);
    }

    @Override
    public void postInit() {
        // Nothing
    }
}
