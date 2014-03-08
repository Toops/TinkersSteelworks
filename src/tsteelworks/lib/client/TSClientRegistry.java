package tsteelworks.lib.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tconstruct.library.crafting.ToolBuilder;
import net.minecraft.item.Item; 
import net.minecraft.item.ItemStack;

public class TSClientRegistry
{
    public static Map<String, ItemStack> manualIcons = new HashMap<String, ItemStack>();
    public static Map<String, ItemStack[]> recipeIcons = new HashMap<String, ItemStack[]>();
    public static ItemStack defaultStack = new ItemStack(Item.ingotIron);

    public static void registerManualIcon (String name, ItemStack stack)
    {
        manualIcons.put(name, stack);
    }

    public static ItemStack getManualIcon (String textContent)
    {
        ItemStack stack = manualIcons.get(textContent);
        if (stack != null)
            return stack;
        return defaultStack;
    }

    public static void registerManualSmallRecipe (String name, ItemStack output, ItemStack... stacks)
    {
        ItemStack[] recipe = new ItemStack[5];
        recipe[0] = output;
        System.arraycopy(stacks, 0, recipe, 1, 4);
        recipeIcons.put(name, recipe);
    }

    public static void registerManualLargeRecipe (String name, ItemStack output, ItemStack... stacks)
    {
        ItemStack[] recipe = new ItemStack[10];
        recipe[0] = output;
        System.arraycopy(stacks, 0, recipe, 1, 9);
        recipeIcons.put(name, recipe);
    }

    public static void registerManualFurnaceRecipe (String name, ItemStack output, ItemStack input)
    {
        ItemStack[] recipe = new ItemStack[2];
        recipe[0] = output;
        recipe[1] = input;
        recipeIcons.put(name, recipe);
    }

    public static void registerManualModifier (String name, ItemStack output, ItemStack topinput)
    {
        registerManualModifier(name, output, topinput, null);
    }

    public static void registerManualModifier (String name, ItemStack output, ItemStack topinput, ItemStack bottominput)
    {
        ItemStack[] recipe = new ItemStack[3];
        recipe[0] = ToolBuilder.instance.buildTool(output, topinput, bottominput, "");
        recipe[1] = topinput;
        recipe[2] = bottominput;
        recipeIcons.put(name, recipe);
    }

    public static void registerManualSmeltery (String name, ItemStack output, ItemStack liquid, ItemStack cast)
    {
        ItemStack[] recipe = new ItemStack[3];
        recipe[0] = output;
        recipe[1] = liquid;
        recipe[2] = cast;
        recipeIcons.put(name, recipe);
    }

    public static ItemStack[] getRecipeIcons (String recipeName)
    {
        return recipeIcons.get(recipeName);
    }
}
