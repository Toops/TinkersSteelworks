package tsteelworks.client.pages;

import mantle.client.pages.BookPage;
import mantle.lib.client.MantleClientRegistry;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import tsteelworks.lib.registry.TSClientRegistry;

public class TSHighOvenPage extends BookPage {
	private static final int[] ICONS_OFFSET_X = new int[] { 120, 52, 6, 6, 6 };
	private static final int[] ICONS_OFFSET_Y = new int[] { 72, 36, 36, 74, 112 };

	private String text;
	private ItemStack[] icons;

	private static final ResourceLocation background = new ResourceLocation("tsteelworks", "textures/gui/bookhighoven.png");

	@Override
	public void readPageFromXML(Element element) {
		NodeList nodes = element.getElementsByTagName("text");
		if (nodes != null)
			text = nodes.item(0).getTextContent();

		nodes = element.getElementsByTagName("recipe");

		if (nodes != null)
			icons = TSClientRegistry.getRecipeIcons(nodes.item(0).getTextContent());
	}

	@Override
	public void renderBackgroundLayer(int localWidth, int localHeight) {
		manual.getMC().getTextureManager().bindTexture(background);
		manual.drawTexturedModalRect(localWidth, localHeight + 32, 0, 0, 156, 116);
	}

	@Override
	public void renderContentLayer(int localWidth, int localHeight, boolean b) {
		if (text != null)
			manual.fonts.drawString("\u00a7n" + text, localWidth + 50, localHeight + 4, 0);

		GL11.glScalef(2f, 2f, 2f);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.enableGUIStandardItemLighting();
		manual.renderitem.zLevel = 100;
		manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, MantleClientRegistry.getManualIcon("charcoal"), (localWidth + 50) / 2, (localHeight + 110) / 2);

		if (icons != null) {
			for (int i = 0; i < icons.length; i++) {
				if (icons[i] == null)
					continue;

				manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[i], (localWidth + ICONS_OFFSET_X[i]) / 2, (localHeight + ICONS_OFFSET_Y[i]) / 2);

				if (icons[i].stackSize > 1)
					manual.renderitem.renderItemOverlayIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[i], (localWidth + ICONS_OFFSET_X[i]) / 2, (localHeight + ICONS_OFFSET_Y[i]) / 2, String.valueOf(icons[i].stackSize));
			}
		}

		manual.renderitem.zLevel = 0;

		GL11.glScalef(0.5F, 0.5F, 0.5F);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
	}
}