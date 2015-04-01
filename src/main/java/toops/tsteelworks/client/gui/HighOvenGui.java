package toops.tsteelworks.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;
import nf.fr.ephys.cookiecore.util.MultiFluidTank;
import org.lwjgl.opengl.GL11;
import toops.tsteelworks.common.blocks.logic.HighOvenLogic;
import toops.tsteelworks.common.container.HighOvenContainer;
import toops.tsteelworks.common.network.PacketMoveFluidHandler;

import java.util.List;

public class HighOvenGui extends GuiContainer {

	public static final ResourceLocation BACKGROUND = new ResourceLocation("tsteelworks", "textures/gui/highoven.png");
	public static final ResourceLocation ICONS = new ResourceLocation("tsteelworks", "textures/gui/icons.png");
	private static final int TANK_YPOS = 16;

	private final ScalableTankGUI tankGui;
	private final HighOvenLogic highoven;

	public HighOvenGui(EntityPlayer player, HighOvenLogic highoven) {
		super(new HighOvenContainer(player.inventory, highoven));
		this.highoven = highoven;

		xSize = 248;

		tankGui = new ScalableTankGUI(this, 0, 0, 35, 52, BACKGROUND, 176, 76);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void initGui() {
		super.initGui();

		int TANK_XPOS = 179;
		tankGui.setLocation(guiLeft + TANK_XPOS, guiTop + TANK_YPOS);

		buttonList.add(new GuiButton(0, guiLeft - 5, guiTop + 5, 20, 20, "+"));
		buttonList.add(new GuiButton(1, guiLeft - 5, guiTop + 25, 20, 20, "-"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(BACKGROUND);

		drawTexturedModalRect(guiLeft + 46, guiTop, 0, 0, 176, ySize);

		// Liquids - molten metal
		MultiFluidTank tank = highoven.getFluidTank();
		tankGui.renderTank(tank, zLevel);

		// Burn progress
		if (highoven.isBurning()) {
			// icon is 42 pixels high
			int scale;
			if (highoven.getFuelBurnTimeTotal() == 0) {
				scale = 42;
			} else {
				scale = highoven.getFuelBurnTime() / highoven.getFuelBurnTimeTotal() * 42;
			}

			drawTexturedModalRect(guiLeft + 127, (guiTop + 36 + 12) - scale, 176, 12 - scale, 14, scale + 2);
		}

		// Side inventory
		int nbSmeltSlots = highoven.getSmeltableInventory().getSizeInventory();

		if (nbSmeltSlots > 0) {
			// Draw Top
			drawTexturedModalRect(guiLeft + 16, guiTop, 176, 14, 36, 6);
			// Iterate one slot at a time and draw it. Each slot is 18 px high. (background)
			for (int iter = 0; iter < nbSmeltSlots; iter++) {
				drawTexturedModalRect(guiLeft + 16, (guiTop + 6) + (iter * 18), 176, 21, 36, 18);
			}

			final int dy = nbSmeltSlots > 1 ? nbSmeltSlots * 18 : 18;
			// Draw Bottom
			drawTexturedModalRect(guiLeft + 16, guiTop + 6 + dy, 176, 39, 36, 7);

			// Temperatures & icons
			for (int i = 0; i < nbSmeltSlots; i++) {
				int slotTemperature = highoven.getTempForSlot(i) - 20;
				int maxTemperature = highoven.getMeltingPointForSlot(i) - 20;

				if (slotTemperature > 0 && maxTemperature > 0) {
					final int size = (16 * slotTemperature / maxTemperature) + 1;
					drawTexturedModalRect(guiLeft + 24, (guiTop + 7 + (i * 18) + 16) - size, 212, (14 + (15 + 16)) - size, 5, size);
				}
			}
		}

		final String temp = highoven.getInternalTemperature() + "°c";
		fontRendererObj.drawString(temp, (guiLeft - (fontRendererObj.getStringWidth(temp) / 2)) + 135, guiTop + 20, getTempColor());

		// draw slot icons
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(ICONS);

		final int slotX = guiLeft + 54;
		final int slotY = guiTop + TANK_YPOS;

		for (int i = 0; i < 3; i++) {
			if (!inventorySlots.getSlot(i).getHasStack())
				drawTexturedModalRect(slotX, slotY + (i * 18), i * 18, 234, 18, 18);
		}

		if (!inventorySlots.getSlot(HighOvenLogic.SLOT_FUEL).getHasStack())
			drawTexturedModalRect(slotX + 71, slotY + (2 * 18), 3 * 18, 234, 18, 18);

		for (int i = 0; i < nbSmeltSlots; i++) {
			if (highoven.getSmeltableInventory().getStackInSlot(i) == null) {
				drawTexturedModalRect(guiLeft + 27, (guiTop + 7) + (i * 18), 4 * 18, 234, 18, 18);
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		final String title = StatCollector.translateToLocal("crafters.HighOven");

		fontRendererObj.drawString(title, ((xSize / 2) - (fontRendererObj.getStringWidth(title) / 2)) + 10, 5, 0x404040);

		// Player Inventory Caption
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 56, (ySize - 96) + 2, 0x404040);

		MultiFluidTank fluidTank = highoven.getFluidTank();
		List<String> tooltips = tankGui.getTooltips(fluidTank, mouseX, mouseY);
		if (tooltips != null) {
			drawHoveringText(tooltips, mouseX - guiLeft, mouseY - guiTop, fontRendererObj);
		}
	}

	protected int getTempColor() {
		int tempHex = highoven.getInternalTemperature();
		if (tempHex > 2000) return 0xFF0000;

		// shift the temperature to have a gradient from 0 -> 1980 (which will visually give 20 -> 2000)
		float percent = (tempHex - 20) / 1980F;

		// 0xFF0000 <- 0x404040
		int r = (int) ((0xFF - 0x40) * percent) + 0x40;
		int g = (int) ((0x00 - 0x40) * percent) + 0x40;
		int b = (int) ((0x00 - 0x40) * percent) + 0x40;

		return r << 16 | g << 8 | b;
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		tankGui.mouseClicked(mouseX, mouseY, mouseButton);

		FluidStack fluid = tankGui.getFluidAtPos(highoven.getFluidTank(), mouseX, mouseY);

		if (fluid != null) {
			PacketMoveFluidHandler.moveFluidGUI(highoven, fluid);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);

		switch (button.id) {
			case 0:
				tankGui.zoomIn();
				break;
			case 1:
				tankGui.zoomOut();
		}
	}

	@Override
	protected void mouseMovedOrUp(int x, int y, int type) {
		super.mouseMovedOrUp(x, y, type);

		tankGui.mouseMovedOrUp(x, y, type);
	}

	@Override
	protected void mouseClickMove(int x, int y, int mouseButton, long lastClickTime) {
		super.mouseClickMove(x, y, mouseButton, lastClickTime);

		tankGui.mouseClickMove(x, y, mouseButton, lastClickTime);
	}

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();

		tankGui.handleMouseInput();
	}

	@Override
	protected void keyTyped(char key, int p_73869_2_) {
		super.keyTyped(key, p_73869_2_);

		tankGui.sshhhdonttellanyoneaboutthis(key);
	}
}
