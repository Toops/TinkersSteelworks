/**
 * 
 */
package tsteelworks.items;

import net.minecraft.item.ItemStack;
import tconstruct.library.util.IToolPart;

/**
 * @author Toops
 *
 */
public class Ring extends TSCraftingItem implements IToolPart
{
    public Ring(int id)
    {
        super(id, toolMaterialNames, buildTextureNames("_ring"), "parts/");
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    private static String[] buildTextureNames (String textureType)
    {
        String[] names = new String[toolMaterialNames.length];
        for (int i = 0; i < toolMaterialNames.length; i++)
        {
            if (toolTextureNames[i].equals(""))
                names[i] = "";
            else
                names[i] = toolTextureNames[i] + textureType;
        }
        return names;
    }

    public static final String[] toolMaterialNames = new String[] { "wood", "iron" };

    public static final String[] toolTextureNames = new String[] { "wood", "iron" };

    @Override
    public int getMaterialID (ItemStack stack)
    {
        return stack.getItemDamage();
    }
}
