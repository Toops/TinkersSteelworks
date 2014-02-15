package tsteelworks.client.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import tconstruct.TConstruct;
import tconstruct.client.gui.NewContainerGui;
import tconstruct.inventory.ActiveContainer;
import tsteelworks.blocks.logic.HighOvenLogic;
import tsteelworks.lib.Repo;
import cpw.mods.fml.common.network.PacketDispatcher;

public class HighOvenGui extends NewContainerGui
{
    public HighOvenLogic logic;
    String               username;
    boolean              wasClicking;
    float                currentScroll = 0.0F;
    int                  slotPos       = 0;
    int                  prevSlotPos   = 0;

    public HighOvenGui (InventoryPlayer inventoryplayer, HighOvenLogic highoven, World world, int x, int y, int z)
    {
        super((ActiveContainer) highoven.getGuiContainer(inventoryplayer, world, x, y, z));
        logic = highoven;
        username = inventoryplayer.player.username;
        xSize = 248;
        highoven.updateFuelDisplay();
    }

    @Override
    public void drawScreen (int mouseX, int mouseY, float par3)
    {
        super.drawScreen(mouseX, mouseY, par3);
    }

    @Override
    protected void drawGuiContainerForegroundLayer (int mouseX, int mouseY)
    {
        // High Oven Caption
        String hoCaption = StatCollector.translateToLocal("crafters.HighOven");
        String oxiCaption = StatCollector.translateToLocal("gui.highoven.oxidizer");
        String redCaption = StatCollector.translateToLocal("gui.highoven.reducer");
        String purCaption = StatCollector.translateToLocal("gui.highoven.purifier");
        
        fontRenderer.drawString(hoCaption, this.xSize / 2 - this.fontRenderer.getStringWidth(hoCaption) / 2, 5, 0x404040);
        
        fontRenderer.drawString(oxiCaption, this.xSize / 2 - this.fontRenderer.getStringWidth(oxiCaption) / 2 + 48, 20, 0x404040);
        fontRenderer.drawString(redCaption, this.xSize / 2 - this.fontRenderer.getStringWidth(redCaption) / 2 + 48, 38, 0x404040);
        fontRenderer.drawString(purCaption, this.xSize / 2 - this.fontRenderer.getStringWidth(purCaption) / 2 + 48, 56, 0x404040);
        
        // Player Inventory Caption
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 56, (ySize - 96) + 2, 0x404040);
        
        int base = 0;
        final int cornerX = ((width - xSize) / 2);// + 36;
        final int cornerY = (height - ySize) / 2;
        for (final FluidStack liquid : logic.moltenMetal)
        {
            final int basePos = 54;
            int liquidSize = 0;// liquid.amount * 52 / liquidLayers;
            if (logic.getCapacity() > 0)
            {
                final int total = logic.getTotalLiquid();
                final int liquidLayers = ((total / 20000) + 1) * 20000;
                if (liquidLayers > 0)
                {
                    liquidSize = (liquid.amount * 52) / liquidLayers;
                    if (liquidSize == 0)
                    {
                        liquidSize = 1;
                    }
                    base += liquidSize;
                }
            }
            final int leftX = cornerX + basePos;
            final int topY = (cornerY + 68) - base;
            final int sizeX = 52;
            final int sizeY = liquidSize;
            if ((mouseX >= leftX) && (mouseX <= (leftX + sizeX)) && (mouseY >= topY) && (mouseY < (topY + sizeY)))
            {
                drawFluidStackTooltip(liquid, (mouseX - cornerX) + 36, mouseY - cornerY);
            }
        }
        if (logic.fuelGague > 0)
        {
            final int leftX = cornerX + 117;
            final int topY = (cornerY + 68) - logic.getScaledFuelGague(52);
            final int sizeX = 12;
            final int sizeY = logic.getScaledFuelGague(52);
            if ((mouseX >= leftX) && (mouseX <= (leftX + sizeX)) && (mouseY >= topY) && (mouseY < (topY + sizeY)))
            {
                drawFluidStackTooltip(new FluidStack(-37, logic.fuelAmount), (mouseX - cornerX) + 36, mouseY - cornerY);
            }
        }
    }

    private static final ResourceLocation background     = new ResourceLocation("tsteelworks", "textures/gui/highoven.png");
    private static final ResourceLocation backgroundSide = new ResourceLocation("tsteelworks", "textures/gui/highovenside.png");

    @Override
    protected void drawGuiContainerBackgroundLayer (float f, int mouseX, int mouseY)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(background);
        final int cornerX = ((width - xSize) / 2);// + 36;
        final int cornerY = (height - ySize) / 2;
        drawTexturedModalRect(cornerX + 46, cornerY, 0, 0, 176, ySize);
        // Fuel - Lava
        mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        if (logic.fuelGague > 0)
        {
            final Icon lavaIcon = Block.lavaStill.getIcon(0, 0);
            int fuel = logic.getScaledFuelGague(52);
            int count = 0;
            while (fuel > 0)
            {
                final int size = fuel >= 16 ? 16 : fuel;
                fuel -= size;
                drawLiquidRect(cornerX + 117, (cornerY + 68) - size - (16 * count), lavaIcon, 12, size);
                count++;
            }
        }
        // Liquids - molten metal
        int base = 0;
        for (final FluidStack liquid : logic.moltenMetal)
        {
            final Icon renderIndex = liquid.getFluid().getStillIcon();
            final int basePos = 54;
            if (logic.getCapacity() > 0)
            {
                final int total = logic.getTotalLiquid();
                final int liquidLayers = ((total / 20000) + 1) * 20000;
                if (liquidLayers > 0)
                {
                    int liquidSize = (liquid.amount * 52) / liquidLayers;
                    if (liquidSize == 0)
                    {
                        liquidSize = 1;
                    }
                    while (liquidSize > 0)
                    {
                        final int size = liquidSize >= 16 ? 16 : liquidSize;
                        if (renderIndex != null)
                        {
                            drawLiquidRect(cornerX + basePos, (cornerY + 68) - size - base, renderIndex, 16, size);
                            drawLiquidRect(cornerX + basePos + 16, (cornerY + 68) - size - base, renderIndex, 16, size);
                            drawLiquidRect(cornerX + basePos + 32, (cornerY + 68) - size - base, renderIndex, 16, size);
                            drawLiquidRect(cornerX + basePos + 48, (cornerY + 68) - size - base, renderIndex, 4, size);
                        }
                        liquidSize -= size;
                        base += size;
                    }
                }
            }
        }
        // Liquid gague
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(cornerX + 54, cornerY + 16, 176, 76, 52, 52);
        // Side inventory
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(backgroundSide);
        final int slotSize = logic.layers;
        if (slotSize > 0)
        {
            // Draw Top
            drawTexturedModalRect(cornerX + 16, cornerY, 0, 0, 36, 6);
            // Draw Slots
            // Here we iterate one slot at a time and draw it. Each slot is 18
            // px high.
            for (int iter = 0; iter < slotSize; iter++)
            {
                drawTexturedModalRect(cornerX + 16, cornerY + 6, 0, 7, 36, (iter * 18) + 18);
            }
            final int dy = slotSize > 1 ? slotSize * 18 : 18;
            // Draw Bottom
            drawTexturedModalRect(cornerX + 16, cornerY + 6 + dy, 0, 115, 36, 7);
        }
        // Temperatures
        for (int iter = 0; iter < slotSize + 4; iter++)
        {
            final int slotTemp = logic.getTempForSlot(iter + slotPos) - 20;
            final int maxTemp = logic.getMeltingPointForSlot(iter + slotPos) - 20;
            if ((slotTemp > 0) && (maxTemp > 0))
            {
                final int size = ((16 * slotTemp) / maxTemp) + 1;
                drawTexturedModalRect(cornerX + 24, (cornerY + 7 + ((iter-4) * 18) + 16) - size, 36, (15 + 16) - size, 5,
                                      size);
            }
        }
    }

    protected void drawFluidStackTooltip (FluidStack par1ItemStack, int par2, int par3)
    {
        zLevel = 100;
        final List list = getLiquidTooltip(par1ItemStack, mc.gameSettings.advancedItemTooltips);
        for (int k = 0; k < list.size(); ++k)
        {
            list.set(k, EnumChatFormatting.GRAY + (String) list.get(k));
        }
        drawToolTip(list, par2, par3);
        zLevel = 0;
    }

    public List getLiquidTooltip (FluidStack liquid, boolean par2)
    {
        final ArrayList list = new ArrayList();
        if (liquid.fluidID == -37)
        {
            list.add("\u00A7fFuel");
            list.add("mB: " + liquid.amount);
        }
        else
        {
            final String name = StatCollector.translateToLocal("fluid." + FluidRegistry.getFluidName(liquid));
            list.add("\u00A7f" + name);
            if (name.contains("Molten"))
            {
                final int ingots = liquid.amount / TConstruct.ingotLiquidValue;
                if (ingots > 0)
                {
                    list.add("Ingots: " + ingots);
                }
                final int mB = liquid.amount % TConstruct.ingotLiquidValue;
                if (mB > 0)
                {
                    final int nuggets = mB / TConstruct.nuggetLiquidValue;
                    final int junk = (mB % TConstruct.nuggetLiquidValue);
                    if (nuggets > 0)
                    {
                        list.add("Nuggets: " + nuggets);
                    }
                    if (junk > 0)
                    {
                        list.add("mB: " + junk);
                    }
                }
            }
            else
            {
                list.add("mB: " + liquid.amount);
            }
        }
        return list;
    }

    protected void drawToolTip (List par1List, int par2, int par3)
    {
        if (!par1List.isEmpty())
        {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int k = 0;
            final Iterator iterator = par1List.iterator();
            while (iterator.hasNext())
            {
                final String s = (String) iterator.next();
                final int l = fontRenderer.getStringWidth(s);
                if (l > k)
                {
                    k = l;
                }
            }
            int i1 = par2 - 22;
            int j1 = par3 - 12;
            int k1 = 8;
            if (par1List.size() > 1)
            {
                k1 += 2 + ((par1List.size() - 1) * 10);
            }
            if ((i1 + k) > width)
            {
                i1 -= 28 + k;
            }
            if ((j1 + k1 + 6) > height)
            {
                j1 = height - k1 - 6;
            }
            zLevel = 300.0F;
            itemRenderer.zLevel = 300.0F;
            final int l1 = -267386864;
            drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1);
            drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1);
            drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1);
            drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1);
            drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1);
            final int i2 = 1347420415;
            final int j2 = ((i2 & 16711422) >> 1) | (i2 & -16777216);
            drawGradientRect(i1 - 3, (j1 - 3) + 1, (i1 - 3) + 1, (j1 + k1 + 3) - 1, i2, j2);
            drawGradientRect(i1 + k + 2, (j1 - 3) + 1, i1 + k + 3, (j1 + k1 + 3) - 1, i2, j2);
            drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, (j1 - 3) + 1, i2, i2);
            drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);
            for (int k2 = 0; k2 < par1List.size(); ++k2)
            {
                final String s1 = (String) par1List.get(k2);
                fontRenderer.drawStringWithShadow(s1, i1, j1, -1);
                if (k2 == 0)
                {
                    j1 += 2;
                }
                j1 += 10;
            }
            zLevel = 0.0F;
            itemRenderer.zLevel = 0.0F;
        }
    }

    public void drawLiquidRect (int startU, int startV, Icon par3Icon, int endU, int endV)
    {
        final Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(startU + 0, startV + endV, zLevel, par3Icon.getMinU(), par3Icon.getMaxV());// Bottom
                                                                                                               // left
        tessellator.addVertexWithUV(startU + endU, startV + endV, zLevel, par3Icon.getMaxU(), par3Icon.getMaxV());// Bottom
                                                                                                                  // right
        tessellator.addVertexWithUV(startU + endU, startV + 0, zLevel, par3Icon.getMaxU(), par3Icon.getMinV());// Top
                                                                                                               // right
        tessellator.addVertexWithUV(startU + 0, startV + 0, zLevel, par3Icon.getMinU(), par3Icon.getMinV()); // Top
                                                                                                             // left
        tessellator.draw();
    }

    @Override
    public void mouseClicked (int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int base = 0;
        final int cornerX = ((width - xSize) / 2) + 36;
        final int cornerY = (height - ySize) / 2;
        int fluidToBeBroughtUp = -1;
        for (final FluidStack liquid : logic.moltenMetal)
        {
            final int basePos = 54;
            int liquidSize = 0;// liquid.amount * 52 / liquidLayers;
            if (logic.getCapacity() > 0)
            {
                final int total = logic.getTotalLiquid();
                final int liquidLayers = ((total / 20000) + 1) * 20000;
                if (liquidLayers > 0)
                {
                    liquidSize = (liquid.amount * 52) / liquidLayers;
                    if (liquidSize == 0)
                    {
                        liquidSize = 1;
                    }
                    base += liquidSize;
                }
            }
            final int leftX = cornerX + basePos;
            final int topY = (cornerY + 68) - base;
            final int sizeX = 52;
            final int sizeY = liquidSize;
            if ((mouseX >= leftX) && (mouseX <= (leftX + sizeX)) && (mouseY >= topY) && (mouseY < (topY + sizeY)))
            {
                fluidToBeBroughtUp = liquid.fluidID;
                final Packet250CustomPayload packet = new Packet250CustomPayload();
                final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                final DataOutputStream dos = new DataOutputStream(bos);
                try
                {
                    dos.write(1);
                    dos.writeInt(logic.worldObj.provider.dimensionId);
                    dos.writeInt(logic.xCoord);
                    dos.writeInt(logic.yCoord);
                    dos.writeInt(logic.zCoord);
                    dos.writeBoolean(isShiftKeyDown());
                    dos.writeInt(fluidToBeBroughtUp);
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
        }
    }
}
