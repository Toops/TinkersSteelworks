package tsteelworks.client.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;
import tsteelworks.TSteelworks;
import tsteelworks.blocks.logic.HighOvenDuctLogic;
import tsteelworks.inventory.TSActiveContainer;
import tsteelworks.lib.Repo;

public class HighOvenDuctGui extends TSContainerGui
{
    public HighOvenDuctLogic logic;
    String                   username;

    public HighOvenDuctGui (InventoryPlayer inventoryplayer, HighOvenDuctLogic duct, World world, int x, int y, int z)
    {
        super((TSActiveContainer) duct.getGuiContainer(inventoryplayer, world, x, y, z));
        logic = duct;
        username = inventoryplayer.player.username;
        xSize = 248;
    }
    
    public void initGui ()
    {
        super.initGui();
        final int cornerX = (this.width - this.xSize) / 2;
        final int cornerY = (this.height - this.ySize) / 2;
        
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, cornerX + 114, cornerY + 40, 8, 20, (StatCollector.translateToLocal("<"))));
        this.buttonList.add(new GuiButton(1, cornerX + 148, cornerY + 40, 8, 20, (StatCollector.translateToLocal(">"))));
    }
    
    protected void actionPerformed (GuiButton button)
    {
        super.actionPerformed(button);
        if (!logic.hasValidMaster()) return;
        int mode = logic.getMode();
        if (button.id == 1 && mode < 5) 
            mode++;       
        if (button.id == 0 && mode > 0) 
            mode--;  
        //logic.setMode(mode);
        final Packet250CustomPayload packet = new Packet250CustomPayload();
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(bos);
        try
        {
            dos.write(2);
            dos.writeInt(logic.worldObj.provider.dimensionId);
            dos.writeInt(logic.xCoord);
            dos.writeInt(logic.yCoord);
            dos.writeInt(logic.zCoord);
            dos.writeInt(mode);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
        packet.channel = Repo.modChan;
        packet.data = bos.toByteArray();
        packet.length = bos.size();
        PacketDispatcher.sendPacketToServer(packet);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer (int mouseX, int mouseY)
    {
        String title = StatCollector.translateToLocal("container.HighOvenDuct");
        fontRenderer.drawString(title, this.xSize / 2 - this.fontRenderer.getStringWidth(title) / 2 + 10, 5, 0x404040);
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 56, (ySize - 96) + 2, 0x404040);

        int base = 0;
        final int cornerX = ((width - xSize) / 2);
        final int cornerY = (height - ySize) / 2;
    }

    private static final ResourceLocation background = new ResourceLocation("tsteelworks", "textures/gui/duct.png");
    private static final ResourceLocation icons      = new ResourceLocation("tsteelworks", "textures/gui/icons.png");
    
    @Override
    protected void drawGuiContainerBackgroundLayer (float f, int mouseX, int mouseY)
    {
        // Draw Background
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(background);
        final int cornerX = ((width - xSize) / 2);
        final int cornerY = (height - ySize) / 2;
        drawTexturedModalRect(cornerX + 46, cornerY, 0, 0, 176, ySize);
        // Draw Icons
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(icons);
        int slotX = cornerX + 126;
        int slotY = cornerY + 42;
        this.drawTexturedModalRect(slotX, slotY, logic.getMode() * 18, 234, 18, 18);
    }
    
}
