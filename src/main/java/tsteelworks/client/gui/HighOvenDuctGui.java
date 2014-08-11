package tsteelworks.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import tsteelworks.TSteelworks;
import tsteelworks.common.blocks.logic.HighOvenDuctLogic;
import tsteelworks.common.core.TSRepo;
import tsteelworks.inventory.TSActiveContainer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class HighOvenDuctGui extends TSContainerGui {
	public HighOvenDuctLogic logic;

	private static final ResourceLocation background = new ResourceLocation("tsteelworks", "textures/gui/duct.png");

	private static final ResourceLocation icons = new ResourceLocation("tsteelworks", "textures/gui/icons.png");

	public HighOvenDuctGui(InventoryPlayer inventoryplayer, HighOvenDuctLogic duct, World world, int x, int y, int z) {
		super((TSActiveContainer) duct.getGuiContainer(inventoryplayer, world, x, y, z));

		logic = duct;
		xSize = 248;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		final int cornerX = (width - xSize) / 2;
		final int cornerY = (height - ySize) / 2;

		buttonList.clear();
		buttonList.add(new GuiButton(0, cornerX + 114, cornerY + 40, 8, 20, (StatCollector.translateToLocal("<"))));
		buttonList.add(new GuiButton(1, cornerX + 148, cornerY + 40, 8, 20, (StatCollector.translateToLocal(">"))));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if (!logic.hasValidMaster())
			return;
		int mode = logic.getMode();
		if ((button.id == 1) && (mode < 5))
			mode++;
		if ((button.id == 0) && (mode > 0))
			mode--;
		final Packet250CustomPayload packet = new Packet250CustomPayload();
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.write(TSRepo.ductPacketID);
			dos.writeInt(logic.worldObj.provider.dimensionId);
			dos.writeInt(logic.xCoord);
			dos.writeInt(logic.yCoord);
			dos.writeInt(logic.zCoord);
			dos.writeInt(mode);
		} catch (final Exception e) {
			TSteelworks.logError("an error occured", e);
		}
		packet.channel = TSRepo.modChan;
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		PacketDispatcher.sendPacketToServer(packet);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		// Draw Background
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(background);
		final int cornerX = ((width - xSize) / 2);
		final int cornerY = (height - ySize) / 2;
		drawTexturedModalRect(cornerX + 46, cornerY, 0, 0, 176, ySize);
		// Draw Icons
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(icons);
		final int slotX = cornerX + 126;
		final int slotY = cornerY + 42;
		drawTexturedModalRect(slotX, slotY, logic.getMode() * 18, 234, 18, 18);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		final String title = StatCollector.translateToLocal("container.HighOvenDuct");
		fontRenderer.drawString(title, ((xSize / 2) - (fontRenderer.getStringWidth(title) / 2)) + 10, 5, 0x404040);
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 56, (ySize - 96) + 2, 0x404040);
	}

}
