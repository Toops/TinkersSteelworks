package tsteelworks.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import tconstruct.items.Pattern;
import tsteelworks.lib.TSteelworksRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TSMetalPattern extends Pattern
{

    public TSMetalPattern (int id, String patternType, String folder)
    {
        super(id, patternName, getPatternNames(patternType), folder);
        this.setCreativeTab(TSteelworksRegistry.Steelforge);
    }

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		this.icons = new Icon[textureNames.length];

		for (int i = 0; i < this.icons.length; ++i) {
			if (!(textureNames[i].equals("")))
				this.icons[i] = iconRegister.registerIcon("tsteelworks:"
						+ folder + textureNames[i]);
		}
	}
    
    protected static String[] getPatternNames (String partType)
    {
        String[] names = new String[patternName.length];
        for (int i = 0; i < patternName.length; i++)
            if (!(patternName[i].equals("")))
                names[i] = partType + patternName[i];
            else
                names[i] = "";
        return names;
    }

    private static final String[] patternName = new String[] { "chainlink" };

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubItems (int id, CreativeTabs tab, List list)
    {
        for (int i = 0; i < patternName.length; i++)
            if (!(patternName[i].equals("")))
                list.add(new ItemStack(id, 1, i));
    }
    
    //2 for full material, 1 for half.
    @Override
    public int getPatternCost (ItemStack pattern)
    {
        switch (pattern.getItemDamage())
        {
        case 0:
            return 1;
        default:
            return 0;
        }
    }
}
