package tsteelworks.common.plugins.nei;

import codechicken.nei.api.API;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class NeiRegistrar {
	public static void register() {
		//registerHandler(new RecipeHandlerHighOven());
	}

	private static void registerHandler(TemplateRecipeHandler handler) {
		API.registerRecipeHandler(handler);
		API.registerUsageHandler(handler);
	}
}