package tsteelworks.modifiers.tools;


//TODO: During migration into TConstruct, actually use a separate class like this.
// Sadly, this is acting up - possibly due to ModInteger having tooltipName, increase, ect private?
// For now, we're registering Vacuous through ModInteger, which actually works.
// -- Had to comment everything out due to build errors
public class ModHopper //extends ModInteger
{
    /*
    int max = 3;
    String color;
    String tooltipName;
    int initialIncrease;
    int secondaryIncrease;
    
    public ModHopper(ItemStack[] items, int effect, int increase)
    {
        super(items, effect, "Hopper", increase, "\u00a77", "Vacuous");//StatCollector.translateToLocal("modifier.tool.vacuous"));
    }
    
//    @Override
//    protected boolean canModify (ItemStack tool, ItemStack[] input)
//    {
//        ToolCore toolItem = (ToolCore) tool.getItem();
//        if (!validType(toolItem))
//            return false;
//
//        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
//        if (!tags.hasKey(key))
//            return tags.getInteger("Modifiers") > 0 && matchingAmount(input) <= max;
//
//        int keyPair[] = tags.getIntArray(key);
//        if (keyPair[0] + matchingAmount(input) <= keyPair[1])
//            return true;
//
//        else if (keyPair[0] == keyPair[1])
//            return tags.getInteger("Modifiers") > 0;
//
//        else
//            return false;
//    }

//    @Override
//    public void modify (ItemStack[] input, ItemStack tool)
//    {
//        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
//        int increase = matchingAmount(input);
//        if (tags.hasKey(key))
//        {
//            int[] keyPair = tags.getIntArray(key);
//            if (keyPair[0] % max == 0)
//            {
//                keyPair[0] += increase;
//                keyPair[1] += max;
//                tags.setIntArray(key, keyPair);
//
//                int modifiers = tags.getInteger("Modifiers");
//                modifiers -= 1;
//                tags.setInteger("Modifiers", modifiers);
//            }
//            else
//            {
//                keyPair[0] += increase;
//                tags.setIntArray(key, keyPair);
//            }
//            updateModTag(tool, keyPair);
//        }
//        else
//        {
//            int modifiers = tags.getInteger("Modifiers");
//            modifiers -= 1;
//            tags.setInteger("Modifiers", modifiers);
//            String modName = "\u00a74" + StatCollector.translateToLocal("modifier.tool.vacuous") + "(" + increase + "/" + max + ")";
//            int tooltipIndex = addToolTip(tool, tooltipName, modName);
//            int[] keyPair = new int[] { increase, max, tooltipIndex };
//            tags.setIntArray(key, keyPair);
//        }
//
//        int vacuum = tags.getInteger("Vacuous");
//
//        vacuum += 1 * increase;
//        tags.setInteger("Vacuous", vacuum);
//    }

    @Override
    public void modify (ItemStack[] input, ItemStack tool)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        if (tags.hasKey(key))
        {
            int increase = tags.getInteger(key);
            increase += secondaryIncrease;
            tags.setInteger(key, increase);
        }
        else
        {
            tags.setInteger(key, initialIncrease);
        }

        int modifiers = tags.getInteger("Modifiers");
        modifiers -= 1;
        tags.setInteger("Modifiers", modifiers);

        int vacuous = tags.getInteger("Hopper");
        vacuous += 1;
        tags.setInteger("Hopper", vacuous);

        addToolTip(tool, color + tooltipName, color + key);
    }
    
    protected int addToolTip (ItemStack tool, String tooltip, String modifierTip)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        int tipNum = 0;
        while (true)
        {
            tipNum++;
            String tip = "Tooltip" + tipNum;
            if (!tags.hasKey(tip))
            {
                //tags.setString(tip, tooltip);
                String modTip = "ModifierTip" + tipNum;
                String tag = tags.getString(modTip);
                tags.setString(modTip, getProperName(modifierTip, tag));
                return tipNum;
            }
            else
            {
                String modTip = "ModifierTip" + tipNum;
                String tag = tags.getString(modTip);
                if (tag.contains(modifierTip))
                {
                    //tags.setString(tip, getProperName(tooltip, tag));
                    tag = tags.getString(modTip);
                    tags.setString(modTip, getProperName(modifierTip, tag));
                    return tipNum;
                }
            }
        }
    }
//    void updateModTag (ItemStack tool, int[] keys)
//    {
//        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
//        String tip = "ModifierTip" + keys[2];
//        String modName = "\u00a77" + StatCollector.translateToLocal("modifier.tool.vacuous") + "(" + keys[0] + "/" + keys[1] + ")";
//        tags.setString(tip, modName);
//    }
//
//    public boolean validType (ToolCore tool)
//    {
//        List list = Arrays.asList(tool.toolCategories());
//        return list.contains("weapon") || list.contains("harvest");
//    }
 * 
 */
}
