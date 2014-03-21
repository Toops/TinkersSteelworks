package tsteelworks.client.gui;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import tsteelworks.inventory.TSActiveContainer;
import tsteelworks.inventory.TSActiveSlot;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class TSContainerGui extends GuiScreen
{
    /** Stacks renderer. Icons, stack size, health, etc... */
    protected static RenderItem itemRenderer = new RenderItem();

    /** The X/Y size of the inventory window in stretched pixels. */
    protected int xSize = 176;
    protected int ySize = 166;

    /** A list of the players inventory slots. */
    public TSActiveContainer container;

    /**
     * Starting X/Y position for the Gui. Inconsistent use for Gui backgrounds.
     */
    protected int guiLeft;
    protected int guiTop;

    protected Slot mainSlot;

    /** Used when touchscreen is enabled */
    protected Slot clickedSlot = null;
    protected boolean isRightMouseClick = false;
    protected ItemStack draggedStack = null;

    protected int field_85049_r = 0;
    protected int field_85048_s = 0;
    protected Slot returningStackDestSlot = null;
    protected long returningStackTime = 0L;

    /** Used when touchscreen is enabled */
    protected ItemStack returningStack = null;
    protected Slot field_92033_y = null;
    protected long field_92032_z = 0L;
    protected final Set field_94077_p = new HashSet();
    protected boolean field_94076_q;
    protected int field_94071_C = 0;
    protected int field_94067_D = 0;
    protected boolean field_94068_E = false;
    protected int field_94069_F;
    protected long field_94070_G = 0L;
    protected Slot field_94072_H = null;
    protected int field_94073_I = 0;
    protected boolean field_94074_J;
    protected ItemStack field_94075_K = null;

    public TSContainerGui(TSActiveContainer activeContainer)
    {
        container = activeContainer;
        field_94068_E = true;
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    @Override
    public boolean doesGuiPauseGame ()
    {
        return false;
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen (int mouseX, int mouseY, float par3)
    {
        drawDefaultBackground();
        final int gLeft = guiLeft;
        final int gTop = guiTop;
        drawGuiContainerBackgroundLayer(par3, mouseX, mouseY);

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        super.drawScreen(mouseX, mouseY, par3);

        RenderHelper.enableGUIStandardItemLighting();
        GL11.glPushMatrix();
        GL11.glTranslatef(gLeft, gTop, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        mainSlot = null;
        final short short1 = 240;
        final short short2 = 240;

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, short1 / 1.0F, short2 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int slotXPos;
        int slotYPos;

        for (int slotIter = 0; slotIter < container.inventorySlots.size(); ++slotIter)
        {
            final Slot slot = (Slot) container.inventorySlots.get(slotIter);
            if (!(slot instanceof TSActiveSlot) || ((TSActiveSlot) slot).getActive())
            {
                drawSlotInventory(slot);

                if (isMouseOverSlot(slot, mouseX, mouseY))
                {
                    mainSlot = slot;
                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    slotXPos = slot.xDisplayPosition;
                    slotYPos = slot.yDisplayPosition;
                    drawGradientRect(slotXPos, slotYPos, slotXPos + 16, slotYPos + 16, -2130706433, -2130706433);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                }
            }
        }

        /*for (int slotIter = 0; slotIter < this.container.otherInventorySlots.size(); ++slotIter)
        {
            TSActiveSlot slot = (TSActiveSlot) this.container.otherInventorySlots.get(slotIter);
            if (slot.getActive())
            {
                this.drawSlotInventory(slot);

                if (this.isMouseOverSlot(slot, mouseX, mouseY))
                {
                    this.mainSlot = slot;
                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    slotXPos = slot.xDisplayPosition;
                    slotYPos = slot.yDisplayPosition;
                    this.drawGradientRect(slotXPos, slotYPos, slotXPos + 16, slotYPos + 16, -2130706433, -2130706433);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                }
            }
        }*/

        drawGuiContainerForegroundLayer(mouseX, mouseY);
        final InventoryPlayer inventoryplayer = mc.thePlayer.inventory;
        ItemStack itemstack = draggedStack == null ? inventoryplayer.getItemStack() : draggedStack;

        if (itemstack != null)
        {
            final byte b0 = 8;
            slotYPos = draggedStack == null ? 8 : 16;
            String s = null;

            if ((draggedStack != null) && isRightMouseClick)
            {
                itemstack = itemstack.copy();
                itemstack.stackSize = MathHelper.ceiling_float_int(itemstack.stackSize / 2.0F);
            }
            else if (field_94076_q && (field_94077_p.size() > 1))
            {
                itemstack = itemstack.copy();
                itemstack.stackSize = field_94069_F;

                if (itemstack.stackSize == 0)
                    s = "" + EnumChatFormatting.YELLOW + "0";
            }

            drawItemStack(itemstack, mouseX - gLeft - b0, mouseY - gTop - slotYPos, s);
        }

        if (returningStack != null)
        {
            float f1 = (Minecraft.getSystemTime() - returningStackTime) / 100.0F;

            if (f1 >= 1.0F)
            {
                f1 = 1.0F;
                returningStack = null;
            }

            slotXPos = returningStackDestSlot.xDisplayPosition - field_85049_r;
            slotYPos = returningStackDestSlot.yDisplayPosition - field_85048_s;
            final int xPos = field_85049_r + (int) (slotXPos * f1);
            final int yPos = field_85048_s + (int) (slotYPos * f1);
            drawItemStack(returningStack, xPos, yPos, (String) null);
        }

        GL11.glPopMatrix();

        if ((inventoryplayer.getItemStack() == null) && (mainSlot != null) && mainSlot.getHasStack())
        {
            final ItemStack itemstack1 = mainSlot.getStack();
            drawItemStackTooltip(itemstack1, mouseX, mouseY);
        }

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderHelper.enableStandardItemLighting();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui ()
    {
        super.initGui();
        mc.thePlayer.openContainer = container;
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    @Override
    public void onGuiClosed ()
    {
        if (mc.thePlayer != null)
            container.onContainerClosed(mc.thePlayer);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void updateScreen ()
    {
        super.updateScreen();

        if (!mc.thePlayer.isEntityAlive() || mc.thePlayer.isDead)
            mc.thePlayer.closeScreen();
    }

    /**
     * This function is what controls the hotbar shortcut check when you press a number key when hovering a stack.
     */
    protected boolean checkHotbarKeys (int par1)
    {
        if ((mc.thePlayer.inventory.getItemStack() == null) && (mainSlot != null))
            for (int j = 0; j < 9; ++j)
                if (par1 == (2 + j))
                {
                    handleMouseClick(mainSlot, mainSlot.slotNumber, j, 2);
                    return true;
                }

        return false;
    }

    /**
     * Draws the text when mouse is over creative inventory tab. Params: current creative tab to be checked, current
     * mouse x position, current mouse y position.
     */
    protected void drawCreativeTabHoveringText (String par1Str, int par2, int par3)
    {
        func_102021_a(Arrays.asList(new String[] { par1Str }), par2, par3);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected abstract void drawGuiContainerBackgroundLayer (float f, int i, int j);

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
    }

    protected void drawItemStack (ItemStack par1ItemStack, int par2, int par3, String par4Str)
    {
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        zLevel = 200.0F;
        itemRenderer.zLevel = 200.0F;
        itemRenderer.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, par1ItemStack, par2, par3);
        itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, par1ItemStack, par2, par3 - (draggedStack == null ? 0 : 8), par4Str);
        zLevel = 0.0F;
        itemRenderer.zLevel = 0.0F;
    }

    protected void drawItemStackTooltip (ItemStack par1ItemStack, int par2, int par3)
    {
        final List list = par1ItemStack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);

        for (int k = 0; k < list.size(); ++k)
            if (k == 0)
                list.set(k, "\u00a7" + Integer.toHexString(par1ItemStack.getRarity().rarityColor) + (String) list.get(k));
            else
                list.set(k, EnumChatFormatting.GRAY + (String) list.get(k));

        func_102021_a(list, par2, par3);
    }

    /**
     * Draws an inventory slot
     */
    protected void drawSlotInventory (Slot par1Slot)
    {
        final int i = par1Slot.xDisplayPosition;
        final int j = par1Slot.yDisplayPosition;
        ItemStack itemstack = par1Slot.getStack();
        boolean flag = false;
        boolean flag1 = (par1Slot == clickedSlot) && (draggedStack != null) && !isRightMouseClick;
        final ItemStack itemstack1 = mc.thePlayer.inventory.getItemStack();
        String s = null;

        if ((par1Slot == clickedSlot) && (draggedStack != null) && isRightMouseClick && (itemstack != null))
        {
            itemstack = itemstack.copy();
            itemstack.stackSize /= 2;
        }
        else if (field_94076_q && field_94077_p.contains(par1Slot) && (itemstack1 != null))
        {
            if (field_94077_p.size() == 1)
                return;

            if (Container.func_94527_a(par1Slot, itemstack1, true) && container.canDragIntoSlot(par1Slot))
            {
                itemstack = itemstack1.copy();
                flag = true;
                Container.func_94525_a(field_94077_p, field_94071_C, itemstack, par1Slot.getStack() == null ? 0 : par1Slot.getStack().stackSize);

                if (itemstack.stackSize > itemstack.getMaxStackSize())
                {
                    s = EnumChatFormatting.YELLOW + "" + itemstack.getMaxStackSize();
                    itemstack.stackSize = itemstack.getMaxStackSize();
                }

                if (itemstack.stackSize > par1Slot.getSlotStackLimit())
                {
                    s = EnumChatFormatting.YELLOW + "" + par1Slot.getSlotStackLimit();
                    itemstack.stackSize = par1Slot.getSlotStackLimit();
                }
            }
            else
            {
                field_94077_p.remove(par1Slot);
                func_94066_g();
            }
        }

        zLevel = 100.0F;
        itemRenderer.zLevel = 100.0F;

        if (itemstack == null)
        {
            final Icon icon = par1Slot.getBackgroundIconIndex();

            if (icon != null)
            {
                GL11.glDisable(GL11.GL_LIGHTING);
                mc.getTextureManager().bindTexture(TextureMap.locationItemsTexture);
                drawTexturedModelRectFromIcon(i, j, icon, 16, 16);
                GL11.glEnable(GL11.GL_LIGHTING);
                flag1 = true;
            }
        }

        if (!flag1)
        {
            if (flag)
                drawRect(i, j, i + 16, j + 16, -2130706433);

            GL11.glEnable(GL11.GL_DEPTH_TEST);
            itemRenderer.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, itemstack, i, j);
            itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, itemstack, i, j, s);
        }

        itemRenderer.zLevel = 0.0F;
        zLevel = 0.0F;
    }

    protected void func_102021_a (List par1List, int par2, int par3)
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
                    k = l;
            }

            int i1 = par2 + 12;
            int j1 = par3 - 12;
            int k1 = 8;

            if (par1List.size() > 1)
                k1 += 2 + ((par1List.size() - 1) * 10);

            if ((i1 + k) > width)
                i1 -= 28 + k;

            if ((j1 + k1 + 6) > height)
                j1 = height - k1 - 6;

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
                    j1 += 2;

                j1 += 10;
            }

            zLevel = 0.0F;
            itemRenderer.zLevel = 0.0F;
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }

    protected void func_85041_a (int par1, int par2, int par3, long par4)
    {
        final Slot slot = getSlotAtPosition(par1, par2);
        final ItemStack itemstack = mc.thePlayer.inventory.getItemStack();

        if ((clickedSlot != null) && mc.gameSettings.touchscreen)
        {
            if ((par3 == 0) || (par3 == 1))
                if (draggedStack == null)
                {
                    if (slot != clickedSlot)
                        draggedStack = clickedSlot.getStack().copy();
                }
                else if ((draggedStack.stackSize > 1) && (slot != null) && Container.func_94527_a(slot, draggedStack, false))
                {
                    final long i1 = Minecraft.getSystemTime();

                    if (field_92033_y == slot)
                    {
                        if ((i1 - field_92032_z) > 500L)
                        {
                            handleMouseClick(clickedSlot, clickedSlot.slotNumber, 0, 0);
                            handleMouseClick(slot, slot.slotNumber, 1, 0);
                            handleMouseClick(clickedSlot, clickedSlot.slotNumber, 0, 0);
                            field_92032_z = i1 + 750L;
                            --draggedStack.stackSize;
                        }
                    }
                    else
                    {
                        field_92033_y = slot;
                        field_92032_z = i1;
                    }
                }
        }
        else if (field_94076_q && (slot != null) && (itemstack != null) && (itemstack.stackSize > field_94077_p.size()) && Container.func_94527_a(slot, itemstack, true) && slot.isItemValid(itemstack)
                && container.canDragIntoSlot(slot))
        {
            field_94077_p.add(slot);
            func_94066_g();
        }
    }

    protected void func_94066_g ()
    {
        final ItemStack itemstack = mc.thePlayer.inventory.getItemStack();

        if ((itemstack != null) && field_94076_q)
        {
            field_94069_F = itemstack.stackSize;
            ItemStack itemstack1;
            int i;

            for (final Iterator iterator = field_94077_p.iterator(); iterator.hasNext(); field_94069_F -= itemstack1.stackSize - i)
            {
                final Slot slot = (Slot) iterator.next();
                itemstack1 = itemstack.copy();
                i = slot.getStack() == null ? 0 : slot.getStack().stackSize;
                Container.func_94525_a(field_94077_p, field_94071_C, itemstack1, i);

                if (itemstack1.stackSize > itemstack1.getMaxStackSize())
                    itemstack1.stackSize = itemstack1.getMaxStackSize();

                if (itemstack1.stackSize > slot.getSlotStackLimit())
                    itemstack1.stackSize = slot.getSlotStackLimit();
            }
        }
    }

    /**
     * Returns the slot at the given coordinates or null if there is none.
     */
    protected Slot getSlotAtPosition (int mouseX, int mouseY)
    {
        for (int k = 0; k < container.inventorySlots.size(); ++k)
        {
            final Slot slot = (Slot) container.inventorySlots.get(k);

            if (isMouseOverSlot(slot, mouseX, mouseY))
                return slot;
        }

        return null;
    }

    protected void handleMouseClick (Slot par1Slot, int par2, int par3, int par4)
    {
        if (par1Slot != null)
            par2 = par1Slot.slotNumber;

        mc.playerController.windowClick(container.windowId, par2, par3, par4, mc.thePlayer);
    }

    /**
     * Returns if the passed mouse position is over the specified slot.
     */
    protected boolean isMouseOverSlot (Slot slot, int mouseX, int mouseY)
    {
        if (!(slot instanceof TSActiveSlot) || ((TSActiveSlot) slot).getActive())
            return isPointInRegion(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, mouseX, mouseY);
        return false;
    }

    /**
     * Args: left, top, width, height, pointX, pointY. Note: left, top are local to Gui, pointX, pointY are local to
     * screen
     */
    protected boolean isPointInRegion (int par1, int par2, int par3, int par4, int par5, int par6)
    {
        final int k1 = guiLeft;
        final int l1 = guiTop;
        par5 -= k1;
        par6 -= l1;
        return (par5 >= (par1 - 1)) && (par5 < (par1 + par3 + 1)) && (par6 >= (par2 - 1)) && (par6 < (par2 + par4 + 1));
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    @Override
    protected void keyTyped (char par1, int par2)
    {
        if ((par2 == 1) || (par2 == mc.gameSettings.keyBindInventory.keyCode))
            mc.thePlayer.closeScreen();

        checkHotbarKeys(par2);

        if ((mainSlot != null) && mainSlot.getHasStack())
            if (par2 == mc.gameSettings.keyBindPickBlock.keyCode)
                handleMouseClick(mainSlot, mainSlot.slotNumber, 0, 3);
            else if (par2 == mc.gameSettings.keyBindDrop.keyCode)
                handleMouseClick(mainSlot, mainSlot.slotNumber, isCtrlKeyDown() ? 1 : 0, 4);
    }

    /**
     * Called when the mouse is clicked.
     */
    @Override
    protected void mouseClicked (int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final boolean flag = mouseButton == (mc.gameSettings.keyBindPickBlock.keyCode + 100);
        final Slot slot = getSlotAtPosition(mouseX, mouseY);
        final long l = Minecraft.getSystemTime();
        field_94074_J = (field_94072_H == slot) && ((l - field_94070_G) < 250L) && (field_94073_I == mouseButton);
        field_94068_E = false;

        if ((mouseButton == 0) || (mouseButton == 1) || flag)
        {
            final int gLeft = guiLeft;
            final int gTop = guiTop;
            final boolean flag1 = (mouseX < gLeft) || (mouseY < gTop) || (mouseX >= (gLeft + xSize)) || (mouseY >= (gTop + ySize));
            int k1 = -1;

            if (slot != null)
                k1 = slot.slotNumber;

            if (flag1)
                k1 = -999;

            if (mc.gameSettings.touchscreen && flag1 && (mc.thePlayer.inventory.getItemStack() == null))
            {
                mc.displayGuiScreen((GuiScreen) null);
                return;
            }

            if (k1 != -1)
                if (mc.gameSettings.touchscreen)
                {
                    if ((slot != null) && slot.getHasStack())
                    {
                        clickedSlot = slot;
                        draggedStack = null;
                        isRightMouseClick = mouseButton == 1;
                    }
                    else
                        clickedSlot = null;
                }
                else if (!field_94076_q)
                    if (mc.thePlayer.inventory.getItemStack() == null)
                    {
                        if (mouseButton == (mc.gameSettings.keyBindPickBlock.keyCode + 100))
                            handleMouseClick(slot, k1, mouseButton, 3);
                        else
                        {
                            final boolean flag2 = (k1 != -999) && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                            byte b0 = 0;

                            if (flag2)
                            {
                                field_94075_K = (slot != null) && slot.getHasStack() ? slot.getStack() : null;
                                b0 = 1;
                            }
                            else if (k1 == -999)
                                b0 = 4;

                            handleMouseClick(slot, k1, mouseButton, b0);
                        }

                        field_94068_E = true;
                    }
                    else
                    {
                        field_94076_q = true;
                        field_94067_D = mouseButton;
                        field_94077_p.clear();

                        if (mouseButton == 0)
                            field_94071_C = 0;
                        else if (mouseButton == 1)
                            field_94071_C = 1;
                    }
        }

        field_94072_H = slot;
        field_94070_G = l;
        field_94073_I = mouseButton;
    }

    /**
     * Called when the mouse is moved or a mouse button is released.  Signature: (mouseX, mouseY, which) which==-1 is
     * mouseMove, which==0 or which==1 is mouseUp
     */
    @Override
    protected void mouseMovedOrUp (int par1, int par2, int par3)
    {
        final Slot slot = getSlotAtPosition(par1, par2);
        final int l = guiLeft;
        final int i1 = guiTop;
        final boolean flag = (par1 < l) || (par2 < i1) || (par1 >= (l + xSize)) || (par2 >= (i1 + ySize));
        int j1 = -1;

        if (slot != null)
            j1 = slot.slotNumber;

        if (flag)
            j1 = -999;

        Slot slot1;
        Iterator iterator;

        if (field_94074_J && (slot != null) && (par3 == 0) && container.func_94530_a((ItemStack) null, slot))
        {
            if (isShiftKeyDown())
            {
                if ((slot != null) && (slot.inventory != null) && (field_94075_K != null))
                {
                    iterator = container.inventorySlots.iterator();

                    while (iterator.hasNext())
                    {
                        slot1 = (Slot) iterator.next();

                        if ((slot1 != null) && slot1.canTakeStack(mc.thePlayer) && slot1.getHasStack() && (slot1.inventory == slot.inventory) && Container.func_94527_a(slot1, field_94075_K, true))
                            handleMouseClick(slot1, slot1.slotNumber, par3, 1);
                    }
                }
            }
            else
                handleMouseClick(slot, j1, par3, 6);

            field_94074_J = false;
            field_94070_G = 0L;
        }
        else
        {
            if (field_94076_q && (field_94067_D != par3))
            {
                field_94076_q = false;
                field_94077_p.clear();
                field_94068_E = true;
                return;
            }

            if (field_94068_E)
            {
                field_94068_E = false;
                return;
            }

            boolean flag1;

            if ((clickedSlot != null) && mc.gameSettings.touchscreen)
            {
                if ((par3 == 0) || (par3 == 1))
                {
                    if ((draggedStack == null) && (slot != clickedSlot))
                        draggedStack = clickedSlot.getStack();

                    flag1 = Container.func_94527_a(slot, draggedStack, false);

                    if ((j1 != -1) && (draggedStack != null) && flag1)
                    {
                        handleMouseClick(clickedSlot, clickedSlot.slotNumber, par3, 0);
                        handleMouseClick(slot, j1, 0, 0);

                        if (mc.thePlayer.inventory.getItemStack() != null)
                        {
                            handleMouseClick(clickedSlot, clickedSlot.slotNumber, par3, 0);
                            field_85049_r = par1 - l;
                            field_85048_s = par2 - i1;
                            returningStackDestSlot = clickedSlot;
                            returningStack = draggedStack;
                            returningStackTime = Minecraft.getSystemTime();
                        }
                        else
                            returningStack = null;
                    }
                    else if (draggedStack != null)
                    {
                        field_85049_r = par1 - l;
                        field_85048_s = par2 - i1;
                        returningStackDestSlot = clickedSlot;
                        returningStack = draggedStack;
                        returningStackTime = Minecraft.getSystemTime();
                    }

                    draggedStack = null;
                    clickedSlot = null;
                }
            }
            else if (field_94076_q && !field_94077_p.isEmpty())
            {
                handleMouseClick((Slot) null, -999, Container.func_94534_d(0, field_94071_C), 5);
                iterator = field_94077_p.iterator();

                while (iterator.hasNext())
                {
                    slot1 = (Slot) iterator.next();
                    handleMouseClick(slot1, slot1.slotNumber, Container.func_94534_d(1, field_94071_C), 5);
                }

                handleMouseClick((Slot) null, -999, Container.func_94534_d(2, field_94071_C), 5);
            }
            else if (mc.thePlayer.inventory.getItemStack() != null)
                if (par3 == (mc.gameSettings.keyBindPickBlock.keyCode + 100))
                    handleMouseClick(slot, j1, par3, 3);
                else
                {
                    flag1 = (j1 != -999) && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));

                    if (flag1)
                        field_94075_K = (slot != null) && slot.getHasStack() ? slot.getStack() : null;

                    handleMouseClick(slot, j1, par3, flag1 ? 1 : 0);
                }
        }

        if (mc.thePlayer.inventory.getItemStack() == null)
            field_94070_G = 0L;

        field_94076_q = false;
    }
}