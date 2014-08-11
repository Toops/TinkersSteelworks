package tsteelworks.client.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import tsteelworks.TSteelworks;
import tsteelworks.common.blocks.logic.DeepTankLogic;
import tsteelworks.common.core.TSRepo;
import tsteelworks.common.core.TSRecipes;
import tsteelworks.common.container.TSActiveContainer;
import cpw.mods.fml.common.network.PacketDispatcher;

public class DeepTankGui extends TSContainerGui
{
    public DeepTankLogic logic;
    String username;
    boolean wasClicking;

    public DeepTankGui(InventoryPlayer inventoryplayer, DeepTankLogic tank) {
        super((TSActiveContainer) tank.getGuiContainer(inventoryplayer));
        logic = tank;
        username = inventoryplayer.player.username;
        xSize = 248;
    }

    @Override
    public void drawScreen (int mouseX, int mouseY, float par3)
    {
        super.drawScreen(mouseX, mouseY, par3);
    }

    @SuppressWarnings ("unused")
    @Override
    protected void drawGuiContainerForegroundLayer (int mouseX, int mouseY)
    {
        final String title = StatCollector.translateToLocal("tank.DeepTank");
        fontRenderer.drawString(title, ((xSize / 2) - (fontRenderer.getStringWidth(title) / 2)), 17, 0x404040);

        int base = 0;
        int cornerX = (width - xSize) / 2 + 20;
        int cornerY = (height - ySize) / 2 + 12;

        for (FluidStack liquid : logic.getFluidList())
        {
            int basePos = 54;
            int initialLiquidSize = 0;
            int liquidSize = 0;//liquid.amount * 104 / liquidLayers;
            if (logic.getCapacity() > 0)
            {
                int total = logic.getTotalLiquid();
                int liquidLayers = (total / logic.layerFluidCapacity() + 1) * logic.layerFluidCapacity();
                if (liquidLayers > 0)
                {
                    liquidSize = liquid.amount * 104 / liquidLayers;
                    if (liquidSize == 0)
                        liquidSize = 1;
                    base += liquidSize;
                }
            }

            int leftX = cornerX + basePos;
            int topY = (cornerY + 120) - base;
            int sizeX = 104;
            int sizeY = liquidSize;
            if (mouseX >= leftX && mouseX <= leftX + sizeX && mouseY >= topY && mouseY < topY + sizeY)
            {
                drawFluidStackTooltip(liquid, mouseX - cornerX + 36, mouseY - cornerY);

            }
        }
    }

    private static final ResourceLocation background = new ResourceLocation("tsteelworks", "textures/gui/deeptank.png");

    @Override
    protected void drawGuiContainerBackgroundLayer (float f, int mouseX, int mouseY)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        int cornerX = (width - xSize) / 2 + 20;
        int cornerY = (height - ySize) / 2 + 12;
        drawTexturedModalRect(cornerX + 46, cornerY, 0, 0, 120, ySize);

        //Liquids - molten metal
        mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        int base = 0;
        for (FluidStack liquid : logic.getFluidList())
        {
            Icon renderIndex = liquid.getFluid().getStillIcon();
            int basePos = 54;
            if (logic.getCapacity() > 0)
            {
                int total = logic.getTotalLiquid();
                int liquidLayers = (total / logic.layerFluidCapacity() + 1) * logic.layerFluidCapacity();
                if (liquidLayers > 0)
                {
                    int liquidSize = liquid.amount * 104 / liquidLayers;
                    if (liquidSize == 0)
                        liquidSize = 1;
                    while (liquidSize > 0)
                    {
                        int size = liquidSize >= 16 ? 16 : liquidSize;
                        if (renderIndex != null)
                        {
                            drawLiquidRect(cornerX + basePos, (cornerY + 120) - size - base, renderIndex, 16, size);
                            drawLiquidRect(cornerX + basePos + 16, (cornerY + 120) - size - base, renderIndex, 16, size);
                            drawLiquidRect(cornerX + basePos + 32, (cornerY + 120) - size - base, renderIndex, 16, size);
                            drawLiquidRect(cornerX + basePos + 48, (cornerY + 120) - size - base, renderIndex, 16, size);
                            drawLiquidRect(cornerX + basePos + 64, (cornerY + 120) - size - base, renderIndex, 16, size);
                            drawLiquidRect(cornerX + basePos + 80, (cornerY + 120) - size - base, renderIndex, 16, size);
                            drawLiquidRect(cornerX + basePos + 96, (cornerY + 120) - size - base, renderIndex, 8, size);
                        }
                        liquidSize -= size;
                        base += size;
                    }
                }
            }
        }

        //Liquid gague
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(cornerX + 54, cornerY + 16, 120, 0, 104, 104);
    }

    @SuppressWarnings ({ "unchecked", "rawtypes" })
    protected void drawFluidStackTooltip (FluidStack par1ItemStack, int par2, int par3)
    {
        this.zLevel = 100;
        List list = getLiquidTooltip(par1ItemStack, this.mc.gameSettings.advancedItemTooltips);

        for (int k = 0; k < list.size(); ++k)
        {
            list.set(k, EnumChatFormatting.GRAY + (String) list.get(k));
        }

        this.drawToolTip(list, par2, par3);
        this.zLevel = 0;
    }

    @SuppressWarnings ({ "rawtypes", "unchecked" })
    public List getLiquidTooltip (FluidStack liquid, boolean par2)
    {
        ArrayList list = new ArrayList();
        if (liquid.fluidID == -37)
        {
            list.add("\u00A7f" + StatCollector.translateToLocal("gui.smeltery1"));
            list.add("mB: " + liquid.amount);
        }
        else
        {
            String name = StatCollector.translateToLocal("fluid." + FluidRegistry.getFluidName(liquid));
            list.add("\u00A7f" + name);
            if (name.equals("Liquified Emerald"))
            {
                list.add("Emeralds: " + liquid.amount / 640f);
            }
            else if (name.equals("Molten Glass"))
            {
                int blocks = liquid.amount / 1000;
                if (blocks > 0)
                    list.add("Blocks: " + blocks);
                int panels = (liquid.amount % 1000) / 250;
                if (panels > 0)
                    list.add("Panels: " + panels);
                int mB = (liquid.amount % 1000) % 250;
                if (mB > 0)
                    list.add("mB: " + mB);
            }
            else if (name.contains("Molten"))
            {
                int ingots = liquid.amount / TSRecipes.ingotLiquidValue;
                if (ingots > 0)
                    list.add("Ingots: " + ingots);
                int mB = liquid.amount % TSRecipes.ingotLiquidValue;
                if (mB > 0)
                {
                    int nuggets = mB / TSRecipes.nuggetLiquidValue;
                    int junk = (mB % TSRecipes.nuggetLiquidValue);
                    if (nuggets > 0)
                        list.add("Nuggets: " + nuggets);
                    if (junk > 0)
                        list.add("mB: " + junk);
                }
            }
            else if (name.equals("Seared Stone"))
            {
                int ingots = liquid.amount / TSRecipes.ingotLiquidValue;
                if (ingots > 0)
                    list.add("Blocks: " + ingots);
                int mB = liquid.amount % TSRecipes.ingotLiquidValue;
                if (mB > 0)
                    list.add("mB: " + mB);
            }
            else
            {
                list.add("mB: " + liquid.amount);
            }
        }
        return list;
    }

    @SuppressWarnings ("rawtypes")
    protected void drawToolTip (List par1List, int par2, int par3)
    {
        if (!par1List.isEmpty())
        {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int k = 0;
            Iterator iterator = par1List.iterator();

            while (iterator.hasNext())
            {
                String s = (String) iterator.next();
                int l = this.fontRenderer.getStringWidth(s);

                if (l > k)
                {
                    k = l;
                }
            }

            int i1 = par2 + 12;
            int j1 = par3 - 12;
            int k1 = 8;

            if (par1List.size() > 1)
            {
                k1 += 2 + (par1List.size() - 1) * 10;
            }

            if (i1 + k > this.width)
            {
                i1 -= 28 + k;
            }

            if (j1 + k1 + 6 > this.height)
            {
                j1 = this.height - k1 - 6;
            }

            this.zLevel = 300.0F;
            itemRenderer.zLevel = 300.0F;
            int l1 = -267386864;
            this.drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1);
            this.drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1);
            this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1);
            this.drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1);
            this.drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1);
            int i2 = 1347420415;
            int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
            this.drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, i2, j2);
            this.drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, i2, j2);
            this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2);
            this.drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);

            for (int k2 = 0; k2 < par1List.size(); ++k2)
            {
                String s1 = (String) par1List.get(k2);
                this.fontRenderer.drawStringWithShadow(s1, i1, j1, -1);

                if (k2 == 0)
                {
                    j1 += 2;
                }

                j1 += 10;
            }

            this.zLevel = 0.0F;
            itemRenderer.zLevel = 0.0F;
        }
    }

    public void drawLiquidRect (int startU, int startV, Icon par3Icon, int endU, int endV)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(startU + 0, startV + endV, this.zLevel, par3Icon.getMinU(), par3Icon.getMaxV());//Bottom left
        tessellator.addVertexWithUV(startU + endU, startV + endV, this.zLevel, par3Icon.getMaxU(), par3Icon.getMaxV());//Bottom right
        tessellator.addVertexWithUV(startU + endU, startV + 0, this.zLevel, par3Icon.getMaxU(), par3Icon.getMinV());//Top right
        tessellator.addVertexWithUV(startU + 0, startV + 0, this.zLevel, par3Icon.getMinU(), par3Icon.getMinV()); //Top left
        tessellator.draw();
    }

    @SuppressWarnings ({ "unused", "static-access" })
    @Override
    public void mouseClicked (int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        int base = 0;
        int cornerX = (width - xSize) / 2 + 20;
        int cornerY = (height - ySize) / 2 + 12;
        int fluidToBeBroughtUp = -1;

        for (FluidStack liquid : logic.getFluidList())
        {
            int basePos = 54;
            int initialLiquidSize = 0;
            int liquidSize = 0;//liquid.amount * 104 / liquidLayers;
            if (logic.getCapacity() > 0)
            {
                int total = logic.getTotalLiquid();
                int liquidLayers = (total / logic.layerFluidCapacity() + 1) * logic.layerFluidCapacity();
                if (liquidLayers > 0)
                {
                    liquidSize = liquid.amount * 104 / liquidLayers;
                    if (liquidSize == 0)
                        liquidSize = 1;
                    base += liquidSize;
                }
            }
            int leftX = cornerX + basePos;
            int topY = (cornerY + 120) - base;
            int sizeX = 104;
            int sizeY = liquidSize;
            if (mouseX >= leftX && mouseX <= leftX + sizeX && mouseY >= topY && mouseY < topY + sizeY)
            {
                fluidToBeBroughtUp = liquid.fluidID;

                Packet250CustomPayload packet = new Packet250CustomPayload();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bos);

                try
                {
                    dos.write(TSRepo.tankPacketID);

                    dos.writeInt(logic.worldObj.provider.dimensionId);
                    dos.writeInt(logic.xCoord);
                    dos.writeInt(logic.yCoord);
                    dos.writeInt(logic.zCoord);

                    dos.writeBoolean(this.isShiftKeyDown());

                    dos.writeInt(fluidToBeBroughtUp);
                }
                catch (Exception e)
                {
                   TSteelworks.logError("an error occured", e);
                }

                packet.channel = TSRepo.modChan;
                packet.data = bos.toByteArray();
                packet.length = bos.size();

                PacketDispatcher.sendPacketToServer(packet);
            }
        }
    }
}
