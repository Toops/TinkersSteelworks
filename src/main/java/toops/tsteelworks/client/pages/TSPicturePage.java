package toops.tsteelworks.client.pages;

import mantle.client.pages.BookPage;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TSPicturePage extends BookPage {
	private String text;
	private ResourceLocation background;

	@Override
	public void readPageFromXML(Element element) {
		NodeList nodes = element.getElementsByTagName("text");
		if (nodes != null)
			text = nodes.item(0).getTextContent();

		nodes = element.getElementsByTagName("location");

		if (nodes != null) {
			background = new ResourceLocation(nodes.item(0).getTextContent());
		}
	}

	@Override
	public void renderContentLayer(int localWidth, int localHeight, boolean isTranslatable) {
		if (isTranslatable) {
			text = StatCollector.translateToLocal(text);
		}

		manual.fonts.drawSplitString(text, localWidth + 8, localHeight, 178, 0);
	}

	public void renderBackgroundLayer(int localWidth, int localHeight) {
		if (background != null) {
			manual.getMC().getTextureManager().bindTexture(background);
		}

		manual.drawTexturedModalRect(localWidth, localHeight + 12, 0, 0, 170, 144);
	}
}