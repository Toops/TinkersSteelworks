package toops.tsteelworks.common.plugins.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import nf.fr.ephys.cookiecore.helpers.ChatHelper;
import nf.fr.ephys.cookiecore.util.MultiFluidTank;
import toops.tsteelworks.common.blocks.logic.DeepTankLogic;
import toops.tsteelworks.common.blocks.logic.HighOvenLogic;
import toops.tsteelworks.common.core.TSLogger;
import toops.tsteelworks.lib.logic.IFluidTankHolder;
import toops.tsteelworks.lib.logic.IMasterLogic;

import java.util.List;

public class HighOvenTankDataProvider implements IWailaDataProvider {
	public static void register(IWailaRegistrar registrar) {
		TSLogger.info("[Waila-Compat] Got registrar: " + registrar);

		// Tanks
		final HighOvenTankDataProvider provider = new HighOvenTankDataProvider();
		registrar.registerBodyProvider(provider, DeepTankLogic.class);
		registrar.registerBodyProvider(provider, HighOvenLogic.class);

		//config
		registrar.addConfig("TinkersSteelworks", "tseelworks.showTotal");
		registrar.addConfig("TinkersSteelworks", "tseelworks.autoUnit");
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		if (accessor.getTileEntity() instanceof IMasterLogic) {
			if (!((IMasterLogic) accessor.getTileEntity()).isValid())
				currenttip.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("tconstruct.waila.invalidstructure"));
		}

		if (accessor.getTileEntity() instanceof IFluidTankHolder) {
			MultiFluidTank tank = ((IFluidTankHolder) accessor.getTileEntity()).getFluidTank();

			listFluids(currenttip, tank, config.getConfig("tseelworks.autoUnit"), config.getConfig("tseelworks.showTotal"));
		}

		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		return null;
	}

	public static void listFluids(List<String> currenttip, MultiFluidTank tank, boolean autoUnit, boolean showTotal) {
		if (tank.getCapacity() == 0) return;

		if (tank.getFluidAmount() == 0) {
			currenttip.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("tconstruct.waila.empty"));
			return;
		}

		for (int i = 0; i < tank.getNbFluids(); i++) {
			FluidStack stack = tank.getFluid(i);

			String textValue = ChatHelper.formatFluidValue(autoUnit, stack.amount);
			currenttip.add(fluidNameHelper(stack) + " (" + textValue + ")");
		}

		if (showTotal) {
			currenttip.add("-----");
			currenttip.add(ChatHelper.formatFluidValue(autoUnit, tank.getFluidAmount()) + " / " + ChatHelper.formatFluidValue(autoUnit, tank.getCapacity()) + " " + StatCollector.translateToLocal("tconstruct.waila.total"));
		}
	}

	private static String fluidNameHelper(FluidStack f) {
		return StatCollector.translateToLocal(FluidRegistry.getFluidName(f));
	}
}
