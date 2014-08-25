package tsteelworks.lib.client;

import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class TSClientRegistry {
	public static Map<String, ItemStack[]> recipeIcons = new HashMap<>();

	public static ItemStack[] getRecipeIcons(String name) {
		return recipeIcons.get(name);
	}

	public static void registerManualHighOvenRecipe(String name, ItemStack output, ItemStack input, ItemStack oxyder, ItemStack reducer, ItemStack purifier) {
		final ItemStack[] recipe = new ItemStack[5];
		recipe[0] = output;
		recipe[1] = input;
		recipe[2] = oxyder;
		recipe[3] = reducer;
		recipe[4] = purifier;

		recipeIcons.put(name, recipe);
	}
}
