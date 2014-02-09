package tsteelworks.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import tconstruct.items.Pattern;
import tsteelworks.lib.Repo;
import tsteelworks.lib.TSteelworksRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TSMetalPattern extends TSWoodPattern
{
    public TSMetalPattern (int id, String patternType, String folder)
    {
        super(id, patternName, getPatternNames(patternType), folder);
    }

    protected static String[] getPatternNames (String partType)
    {
        final String[] names = new String[patternName.length];
        for (int i = 0; i < patternName.length; i++)
            if (!(patternName[i].equals("")))
            {
                names[i] = partType + patternName[i];
            }
            else
            {
                names[i] = "";
            }
        return names;
    }

    private static final String[] patternName = new String[] { "ring" };

    @Override
    public void getSubItems (int id, CreativeTabs tab, List list)
    {
        for (int i = 0; i < patternName.length; i++)
            if (!(patternName[i].equals("")))
            {
                list.add(new ItemStack(id, 1, i));
            }
    }
}
