package toops.tsteelworks.common.plugins.nei;

import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.util.StatCollector;

public class RecipeHandlerHighOven extends TemplateRecipeHandler {
	@Override
	public String getGuiTexture() {
		return null;
	}

	@Override
	public String getRecipeName() {
		return StatCollector.translateToLocal("crafters.HighOven");
	}
}
