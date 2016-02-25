package toops.tsteelworks.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;
import nf.fr.ephys.cookiecore.helpers.ChatHelper;
import nf.fr.ephys.cookiecore.util.MultiFluidTank;
import org.lwjgl.opengl.GL11;
import toops.tsteelworks.common.blocks.logic.DeepTankLogic;
import toops.tsteelworks.common.container.DeepTankContainer;
import toops.tsteelworks.common.network.PacketMoveFluidHandler;

import java.util.List;

public class DeepTankGui extends GuiContainer {
	public static final ResourceLocation BACKGROUND = new ResourceLocation("tsteelworks", "textures/gui/deeptank.png");
	private final ScalableTankGUI tankGui;
	private final DeepTankLogic tank;

	public DeepTankGui(InventoryPlayer inventoryplayer, DeepTankLogic tank) {
		super(new DeepTankContainer(inventoryplayer, tank));
		this.tank = tank;

		xSize = 120;
		ySize = 137;

		final int TANK_WIDTH = 104;
		final int TANK_HEIGHT = 104;
		tankGui = new ScalableTankGUI(this, 0, 0, TANK_WIDTH, TANK_HEIGHT, BACKGROUND, 120, 0);
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
	@SuppressWarnings("unchecked")
	public void initGui() {
		super.initGui();

		final int TANK_YPOS = 16;
		final int TANK_XPOS = 8;
		tankGui.setLocation(guiLeft + TANK_XPOS, guiTop + TANK_YPOS);

		buttonList.add(new GuiButton(0, guiLeft - 20, guiTop + 5, 20, 20, "+"));
		buttonList.add(new GuiButton(1, guiLeft - 20, guiTop + 25, 20, 20, "-"));
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		MultiFluidTank fluidTank = tank.getFluidTank();

		// title
		final String title = StatCollector.translateToLocal("tank.DeepTank");
		fontRendererObj.drawString(title, (xSize / 2) - (fontRendererObj.getStringWidth(title) / 2), 5, 0x404040);

		final String capacity = ChatHelper.formatFluidValue(true, fluidTank.getFluidAmount()) + " / " + ChatHelper.formatFluidValue(true, fluidTank.getCapacity());

		fontRendererObj.drawString(capacity, (xSize / 2) - (fontRendererObj.getStringWidth(capacity) / 2), ySize - 14, 0x404040);

		List<String> tooltips = tankGui.getTooltips(fluidTank, mouseX, mouseY);
		if (tooltips != null)
			drawHoveringText(tooltips, mouseX - guiLeft, mouseY - guiTop, fontRendererObj);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(BACKGROUND);

		drawTexturedModalRect(guiLeft, guiTop, 0, 0, 120, ySize);

		//Liquids
		tankGui.renderTank(tank.getFluidTank(), zLevel);
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
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		tankGui.mouseClicked(mouseX, mouseY, mouseButton);

		FluidStack fluid = tankGui.getFluidAtPos(tank.getFluidTank(), mouseX, mouseY);

		if (fluid == null) return;

		PacketMoveFluidHandler.moveFluidGUI(tank, fluid);
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
