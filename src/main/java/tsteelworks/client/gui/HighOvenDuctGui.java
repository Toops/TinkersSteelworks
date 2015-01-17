package tsteelworks.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import tsteelworks.common.blocks.logic.HighOvenDuctLogic;
import tsteelworks.common.container.HighOvenDuctContainer;
import tsteelworks.common.network.PacketSetDuctModeHandler;

public class HighOvenDuctGui extends GuiContainer {
	public static final ResourceLocation BACKGROUND = new ResourceLocation("tsteelworks", "textures/gui/duct.png");
	public static final ResourceLocation ICONS = new ResourceLocation("tsteelworks", "textures/gui/icons.png");

	public HighOvenDuctGui(InventoryPlayer inventoryplayer, HighOvenDuctLogic duct) {
		super(new HighOvenDuctContainer(inventoryplayer, duct));

		xSize = 248;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void initGui() {
		super.initGui();

		final int cornerX = (width - xSize) / 2;
		final int cornerY = (height - ySize) / 2;

		buttonList.clear();
		buttonList.add(new GuiButton(0, cornerX + 114, cornerY + 40, 8, 20, (StatCollector.translateToLocal("<"))));
		buttonList.add(new GuiButton(1, cornerX + 148, cornerY + 40, 8, 20, (StatCollector.translateToLocal(">"))));
	}

	private HighOvenDuctLogic getLogic() {
		return ((HighOvenDuctContainer) inventorySlots).getLogic();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		HighOvenDuctLogic logic = getLogic();

		int mode = logic.getMode();

		switch (button.id) {
			case 0:
				if (mode > 0)
					mode--;
				break;

			case 1:
				if (mode < 5)
					mode++;
				break;
		}

		PacketSetDuctModeHandler.changeDuctMode(logic, (byte) mode);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		// Draw Background
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(BACKGROUND);
		final int cornerX = ((width - xSize) / 2);
		final int cornerY = (height - ySize) / 2;
		drawTexturedModalRect(cornerX + 46, cornerY, 0, 0, 176, ySize);

		// Draw Icons
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(ICONS);
		final int slotX = cornerX + 126;
		final int slotY = cornerY + 42;
		drawTexturedModalRect(slotX, slotY, getLogic().getMode() * 18, 234, 18, 18);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		final String title = StatCollector.translateToLocal("container.HighOvenDuct");

		fontRendererObj.drawString(title, ((xSize / 2) - (fontRendererObj.getStringWidth(title) / 2)) + 10, 5, 0x404040);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 56, (ySize - 96) + 2, 0x404040);
	}
}
