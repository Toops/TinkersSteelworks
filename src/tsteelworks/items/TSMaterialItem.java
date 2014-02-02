package tsteelworks.items;

public class TSMaterialItem extends TSCraftingItem {

	public TSMaterialItem(int id) {
		super(id, materialNames, getTextures(), "materials/");
	}

	private static String[] getTextures() {
		String[] names = new String[craftingTextures.length];
		for (int i = 0; i < craftingTextures.length; i++) {
			if (craftingTextures[i].equals(""))
				names[i] = "";
			else
				names[i] = "material_" + craftingTextures[i];
		}
		return names;
	}

	static String[] materialNames = new String[] { "ScorchedBrick", "MonoatomicGoldIngot", "MonoatomicGoldNugget" };

	static String[] craftingTextures = new String[] { "scorchedbrick", "monoatomicgoldingot", "nugget_monoatomicgold" };
}
