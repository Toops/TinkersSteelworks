package tsteelworks.blocks.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import tconstruct.TConstruct;
import tconstruct.blocks.logic.MultiServantLogic;
import tconstruct.blocks.logic.SmelteryDrainLogic;
import tconstruct.common.TContent;
import tconstruct.library.util.CoordTuple;
import tconstruct.library.util.IFacingLogic;
import tconstruct.library.util.IMasterLogic;
import tconstruct.library.util.IServantLogic;
import tsteelworks.TSteelworks;
import tsteelworks.common.TSContent;
import tsteelworks.inventory.DeepTankContainer;
import tsteelworks.lib.ConfigCore;
import tsteelworks.lib.crafting.AlloyInfo;

public class DeepTankLogic extends TileEntity implements IFacingLogic, IFluidTank, IMasterLogic
{
	public ArrayList<FluidStack> fluidlist = new ArrayList<FluidStack>();
	boolean structureHasBottom;
	boolean structureHasTop;
	boolean needsUpdate;
	boolean containsAlloy;
	boolean activeTurbineAttached;
	public boolean validStructure;
	byte direction;
	public CoordTuple centerPos;
	int tick;
	int maxLiquid;
	int currentLiquid;
	int numBricks;
	public int innerMaxX;
	public int innerMaxZ;
	public int layers;
	public final int innerMaxSpace = 9;
	Random rand = new Random();

	public DeepTankLogic() 
	{ 
		super(); 
		innerMaxX = 0;
		innerMaxZ = 0;
		containsAlloy = false;
		activeTurbineAttached = false;
	}

	public int xDistanceToRim () { return (innerMaxX / 2) + 1; }
	public int zDistanceToRim () { return (innerMaxZ / 2) + 1; }
	public int innerSpaceTotal () { return innerMaxX * innerMaxZ; }
	public int layerFluidCapacity() { return (FluidContainerRegistry.BUCKET_VOLUME * ConfigCore.deeptankCapacityMultiplier) * innerSpaceTotal(); }

	void adjustLayers (int lay, boolean forceAdjust)
	{
		if (lay != layers || forceAdjust)
		{
			needsUpdate = true;
			layers = lay;
			maxLiquid = layerFluidCapacity() * lay;
		}
	}

	/* Misc */
	public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
	{
		return new DeepTankContainer(inventoryplayer, this);
	}

	public boolean isUseableByPlayer (EntityPlayer entityplayer)
	{
		if (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this)
			return false;
		else
			return entityplayer.getDistance(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
	}

	public String getDefaultName () { return "tank.DeepTank"; }

	/*
	 * (non-Javadoc)
	 * @see tconstruct.library.util.IFacingLogic#getRenderDirection()
	 */
	@Override
	public byte getRenderDirection () { return direction; }

	/*
	 * (non-Javadoc)
	 * @see tconstruct.library.util.IFacingLogic#getForgeDirection()
	 */
	@Override
	public ForgeDirection getForgeDirection () { return ForgeDirection.VALID_DIRECTIONS[direction]; }

	/*
	 * (non-Javadoc)
	 * @see tconstruct.library.util.IFacingLogic#setDirection(int)
	 */
	@Override
	public void setDirection (int side) {}

	/*
	 * (non-Javadoc)
	 * @see tconstruct.library.util.IFacingLogic#setDirection(float, float, net.minecraft.entity.EntityLivingBase)
	 */
	@Override
	public void setDirection (float yaw, float pitch, EntityLivingBase player)
	{
		int facing = MathHelper.floor_double((double) (yaw / 360) + 0.5D) & 3;
		switch (facing)
		{
		case 0:
			direction = 2;
			break;
		case 1:
			direction = 5;
			break;
		case 2:
			direction = 3;
			break;
		case 3:
			direction = 4;
			break;
		}
	}


	/*
	 * (non-Javadoc)
	 * @see net.minecraft.tileentity.TileEntity#updateEntity()
	 */
	@Override
	public void updateEntity ()
	{
		tick++;
		if (tick % 20 == 0)
		{
			if (!validStructure)
				checkValidPlacement();
			if (needsUpdate)
			{
				needsUpdate = false;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
		if (tick == 40)
			if (activeTurbineAttached)
				dealloyFluids();
		if (tick == 60)
			tick = 0;
	}

	boolean addFluidToTank (FluidStack liquid, boolean first)
	{
		needsUpdate = true;
		if (fluidlist.size() == 0)
		{
			fluidlist.add(liquid.copy());
			currentLiquid += liquid.amount;
			containsAlloy = containsAlloy();
			return true;
		}
		else
		{
			if (liquid.amount + currentLiquid > maxLiquid)
				return false;
			currentLiquid += liquid.amount;
			boolean added = false;
			for (int i = 0; i < fluidlist.size(); i++)
			{
				FluidStack l = fluidlist.get(i);
				if (l.isFluidEqual(liquid))
				{
					l.amount += liquid.amount;
					added = true;
				}
				if (l.amount <= 0)
				{
					fluidlist.remove(l);
					i--;
				}
			}
			if (!added)
			{
				if (first)
					fluidlist.add(0, liquid.copy());
				else
					fluidlist.add(liquid.copy());
			}
			containsAlloy = containsAlloy();
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see net.minecraft.tileentity.TileEntity#onInventoryChanged()
	 */
	 @Override
	 public void onInventoryChanged ()
	{
		updateEntity();
		super.onInventoryChanged();
		needsUpdate = true;
	}



	/* Multiblock */


	/*
	 * (non-Javadoc)
	 * @see tconstruct.library.util.IMasterLogic#notifyChange(tconstruct.library.util.IServantLogic, int, int, int)
	 */
	 @Override
	 public void notifyChange (IServantLogic servant, int x, int y, int z)
	 {
		 checkValidPlacement();
	 }

	 public void checkValidPlacement ()
	 {
		 int[] center = scanGlassLayerCenter();
		 alignControllerLayer(center[0], yCoord, center[1]);
	 }

	 void alignControllerLayer (int x, int y, int z)
	 {
		 // If new centralized coords didn't change, INVALID!
		 if (x == xCoord && z == zCoord) return;
		 // Let's get those central points again...
		 int innerCenterX = xDistanceToRim();
		 int innerCenterZ = zDistanceToRim();
		 // Just a little counter to pass along...
		 int brickCounter = 0;
		 int glassCounter = 0;
		 // Set up a new block for scanning purposes
		 Block block;
		 // Scan inner for glass blocks by adjusted X/Z coordinates
		 for (int xPos = x - innerCenterX; xPos <= x + innerCenterX; xPos++)
		 {
			 for (int zPos = z - innerCenterZ; zPos <= z + innerCenterZ; zPos++)
			 {
				 block = Block.blocksList[worldObj.getBlockId(xPos, y, zPos)];
				 if (block != null && validGlassID(block.blockID))
					 glassCounter += checkBricks(xPos, y, zPos, true);
			 }
		 }
		 // Scan outter for brick/drain blocks by adjusted X/Z coordinates
		 for (int xPos = x - innerCenterX; xPos <= x + innerCenterX; xPos++)
		 {
			 brickCounter += checkBricks(xPos, y, z - (innerCenterZ), false);
			 brickCounter += checkBricks(xPos, y, z + (innerCenterZ), false);
		 }
		 for (int zPos = z - (innerCenterZ-1); zPos <= z + (innerCenterZ-1); zPos++)
		 {
			 brickCounter += checkBricks(x - innerCenterX, y, zPos, false);
			 brickCounter += checkBricks(x + innerCenterX, y, zPos, false);
		 }
		 if (!validateRimmedLayer(glassCounter, brickCounter)) return;
		 checkValidStructure(x, y, z, glassCounter + brickCounter);
	 }

	 public void checkValidStructure (int x, int y, int z, int compareBricks)
	 {
		 int checkLayers = 0;
		 if (checkSameLevel(x, y, z, compareBricks))
		 {
			 checkLayers++;
			 int checkUp = recurseStructureUp(x, y + 1, z, 0, compareBricks);
			 int checkDown = recurseStructureDown(x, y - 1, z, 0, compareBricks);

			 checkLayers += checkUp;
			 checkLayers += checkDown;

			 if (checkUp > 1 && !structureHasBottom)
				 validateBottom (x, y, z, 0, compareBricks);
			 if (checkDown > 1 && !structureHasTop)
				 validateTop (x, y, z, 0, compareBricks);
		 }

		 if (structureHasTop != structureHasBottom != validStructure || checkLayers != this.layers)
		 {
			 if (structureHasBottom && structureHasTop)
			 {
				 adjustLayers(checkLayers, false);
				 worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				 validStructure = true;
			 }
			 else
				 validStructure = false;
		 }
	 }




	 // trying to refactor the following methods to use only one

	 public boolean checkSameLevel2(int x, int y, int z, int compareBricks)
	 {
		 return checkStructureGlobal(x, y, z, compareBricks, -1, 0, 0, 0)==0;
	 }

	 public int recurseStructureUp2(int x, int y, int z, int count, int compareBricks)
	 {
		 return checkStructureGlobal(x, y, z, compareBricks, count, -1, 1, -1); // add count somewhere
	 }
	 
	 public int recurseStructureDown2(int x, int y, int z, int count, int compareBricks)
	 {
		 return checkStructureGlobal(x, y, z, compareBricks, count, -1, -1, -1); // add count somewhere
	 }

	 public int checkStructureGlobal(int x, int y, int z, int compareBricks, int count, int modX, int modY, int modZ)
	 {
		 numBricks = 0;
		 Block block = null;
		 int innerCenterX = xDistanceToRim();
		 int innerCenterZ = zDistanceToRim();
		 
		 boolean isMiddleLayer = (count > -1);

		 for(int xPos = x - (innerCenterX + modX); xPos <= x + (innerCenterX + modX); xPos++)
		 {
			 for(int zPos = z - (innerCenterZ + modZ); zPos <= z + (innerCenterZ + modZ); zPos++)
			 {
				 block = Block.blocksList[worldObj.getBlockId(xPos, y, zPos)];
				 if (block != null && validGlassID(block.blockID))
				 {
					 if(! isMiddleLayer)
					 {
						 numBricks += checkBricks(xPos, y, zPos, true);
					 }else
					 {
						 if ((block != null) && !block.isAirBlock(worldObj, xPos, y, zPos)) 
							 return (validGlassID(block.blockID)) ? validateTop(x, y, z, count, compareBricks) : count;
					 }
				 }
			 }
		 }
		 for (int xPos = x - innerCenterX; xPos <= x + innerCenterX; xPos++)
		 {
			 numBricks += checkBricks(xPos, y, z - (innerCenterZ), isMiddleLayer);
			 numBricks += checkBricks(xPos, y, z + (innerCenterZ), isMiddleLayer);
		 }
		 for (int zPos = z - (innerCenterZ-1); zPos <= z + (innerCenterZ-1); zPos++)
		 {
			 numBricks += checkBricks(x - innerCenterX, y, zPos, isMiddleLayer);
			 numBricks += checkBricks(x + innerCenterX, y, zPos, isMiddleLayer);
		 }
		 if(!isMiddleLayer)
		 {
			 return (numBricks == compareBricks)?0:1;
		 }else{
			 if (numBricks != compareBricks - innerSpaceTotal())
				 return count;

			 count++;
			 return checkStructureGlobal(x, y + modY, z, compareBricks, count, modX, modY, modZ);
		 }
	 }

	 // Redundancy at its finest.
	 public boolean checkSameLevel (int x, int y, int z, int compareBricks)
	 {
		 numBricks = 0;
		 Block block;
		 int innerCenterX = xDistanceToRim();
		 int innerCenterZ = zDistanceToRim();
		 for (int xPos = x - innerCenterX; xPos <= x + innerCenterX; xPos++)
		 {
			 for (int zPos = z - innerCenterZ; zPos <= z + innerCenterZ; zPos++)
			 {
				 block = Block.blocksList[worldObj.getBlockId(xPos, y, zPos)];
				 if (block != null && validGlassID(block.blockID))
					 numBricks += checkBricks(xPos, y, zPos, true);
			 }
		 }
		 for (int xPos = x - innerCenterX; xPos <= x + innerCenterX; xPos++)
		 {
			 numBricks += checkBricks(xPos, y, z - (innerCenterZ), false);
			 numBricks += checkBricks(xPos, y, z + (innerCenterZ), false);
		 }
		 for (int zPos = z - (innerCenterZ-1); zPos <= z + (innerCenterZ-1); zPos++)
		 {
			 numBricks += checkBricks(x - innerCenterX, y, zPos, false);
			 numBricks += checkBricks(x + innerCenterX, y, zPos, false);
		 }
		 return (numBricks == compareBricks);
	 }

	 public int recurseStructureUp (int x, int y, int z, int count, int compareBricks)
	 {
		 numBricks = 0;
		 //Check inside
		 int innerCenterX = xDistanceToRim();
		 int innerCenterZ = zDistanceToRim();

		 Block block;
		 for (int xPos = x - (innerCenterX-1); xPos <= x + (innerCenterX-1); xPos++)
		 {
			 for (int zPos = z - (innerCenterZ-1); zPos <= z + (innerCenterZ-1); zPos++)
			 {
				 block = Block.blocksList[worldObj.getBlockId(xPos, y, zPos)];
				 if ((block != null) && validGlassID(block.blockID)) 
					 if ((block != null) && !block.isAirBlock(worldObj, xPos, y, zPos)) 
						 return (validGlassID(block.blockID)) ? validateTop(x, y, z, count, compareBricks) : count;
			 }
		 }
		 //Check outer layer
		 for (int xPos = x - innerCenterX; xPos <= x + innerCenterX; xPos++)
		 {
			 numBricks += checkBricks(xPos, y, z - (innerCenterZ), true);
			 numBricks += checkBricks(xPos, y, z + (innerCenterZ), true);
		 }
		 for (int zPos = z - (innerCenterZ-1); zPos <= z + (innerCenterZ-1); zPos++)
		 {
			 numBricks += checkBricks(x - innerCenterX, y, zPos, true);
			 numBricks += checkBricks(x + innerCenterX, y, zPos, true);
		 }

		 if (numBricks != compareBricks - innerSpaceTotal())
			 return count;

		 count++;
		 return recurseStructureUp(x, y + 1, z, count, compareBricks);
	 }

	 public int recurseStructureDown (int x, int y, int z, int count, int compareBricks)
	 {
		 numBricks = 0;
		 //Check inside
		 int innerCenterX = xDistanceToRim();
		 int innerCenterZ = zDistanceToRim();
		 Block block;
		 for (int xPos = x - (innerCenterX-1); xPos <= x + (innerCenterX-1); xPos++)
		 {
			 for (int zPos = z - (innerCenterZ-1); zPos <= z + (innerCenterZ-1); zPos++)
			 {
				 block = Block.blocksList[worldObj.getBlockId(xPos, y, zPos)];
				 if ((block != null) && validGlassID(block.blockID)) 
					 if ((block != null) && !block.isAirBlock(worldObj, xPos, y, zPos)) 
						 return (validGlassID(block.blockID)) ? validateBottom(x, y, z, count, compareBricks) : count;
			 }
		 }
		 //Check outer layer
		 for (int xPos = x - innerCenterX; xPos <= x + innerCenterX; xPos++)
		 {
			 numBricks += checkBricks(xPos, y, z - (innerCenterZ), true);
			 numBricks += checkBricks(xPos, y, z + (innerCenterZ), true);
		 }
		 for (int zPos = z - (innerCenterZ-1); zPos <= z + (innerCenterZ-1); zPos++)
		 {
			 numBricks += checkBricks(x - innerCenterX, y, zPos, true);
			 numBricks += checkBricks(x + innerCenterX, y, zPos, true);
		 }
		 if (numBricks != compareBricks - innerSpaceTotal())
			 return count;

		 count++;
		 return recurseStructureDown(x, y - 1, z, count, compareBricks);
	 }

	 public int validateTop (int x, int y, int z, int count, int compareBricks)
	 {
		 int topBricks = 0;

		 int innerCenterX = xDistanceToRim();
		 int innerCenterZ = zDistanceToRim();

		 for (int xPos = x - innerCenterX; xPos <= x + innerCenterX; xPos++)
			 for (int zPos = z - innerCenterZ; zPos <= z + innerCenterZ; zPos++)
				 if (validGlassID(worldObj.getBlockId(xPos, y, zPos)))
					 topBricks += checkBricks(xPos, y, zPos, true);

		 //Check outer rim
		 for (int xPos = x - innerCenterX; xPos <= x + innerCenterX; xPos++)
		 {
			 topBricks += checkBricks(xPos, y, z - (innerCenterZ), false);
			 topBricks += checkBricks(xPos, y, z + (innerCenterZ), false);
		 }
		 for (int zPos = z - (innerCenterZ-1); zPos <= z + (innerCenterZ-1); zPos++)
		 {
			 topBricks += checkBricks(x - innerCenterX, y, zPos, false);
			 topBricks += checkBricks(x + innerCenterX, y, zPos, false);
		 }
		 structureHasTop = (topBricks == compareBricks);
		 return count;
	 }

	 public int validateBottom (int x, int y, int z, int count, int compareBricks)
	 {
		 int bottomBricks = 0;
		 int innerCenterX = xDistanceToRim();
		 int innerCenterZ = zDistanceToRim();
		 for (int xPos = x - innerCenterX; xPos <= x + innerCenterX; xPos++)
			 for (int zPos = z - innerCenterZ; zPos <= z + innerCenterZ; zPos++)
				 if (validGlassID(worldObj.getBlockId(xPos, y, zPos)))
					 bottomBricks += checkBricks(xPos, y, zPos, true);
		 //Check outer layer
		 for (int xPos = x - innerCenterX; xPos <= x + innerCenterX; xPos++)
		 {
			 bottomBricks += checkBricks(xPos, y, z - (innerCenterZ), false);
			 bottomBricks += checkBricks(xPos, y, z + (innerCenterZ), false);
		 }
		 for (int zPos = z - (innerCenterZ-1); zPos <= z + (innerCenterZ-1); zPos++)
		 {
			 bottomBricks += checkBricks(x - innerCenterX, y, zPos, false);
			 bottomBricks += checkBricks(x + innerCenterX, y, zPos, false);
		 }
		 structureHasBottom = (bottomBricks == compareBricks);
		 if (structureHasBottom)
			 centerPos = new CoordTuple(x, y + 1, z);
		 return count;
	 }

	 /* Returns whether the brick is a lava tank or not.
	  * Increments bricks, sets them as part of the structure, and adds tanks to the list.
	  */
	 int checkBricks (int x, int y, int z, boolean glassOnly)
	 {
		 int tempBricks = 0;
		 int blockID = worldObj.getBlockId(x, y, z);
		 if (glassOnly && (validGlassID(blockID) || validTankID(blockID)))
			 tempBricks++;
		 if (!glassOnly && validBlockID(blockID))
		 {
			 TileEntity te = worldObj.getBlockTileEntity(x, y, z);
			 if (te == this)
				 tempBricks++;
			 else if (te instanceof TSMultiServantLogic)
			 {
				 TSMultiServantLogic servant = (TSMultiServantLogic) te;
				 if (servant.hasValidMaster())
				 {
					 if (servant.verifyMaster(this, this.xCoord, this.yCoord, this.zCoord))
						 tempBricks++;
				 }
				 else if (servant.setMaster(this.xCoord, this.yCoord, this.zCoord))
					 tempBricks++;
			 }
			 else if (te instanceof MultiServantLogic)
			 {
				 if (!(te instanceof SmelteryDrainLogic))
				 {
					 MultiServantLogic servant = (MultiServantLogic) te;
					 if (servant.hasValidMaster())
					 {
						 if (servant.verifyMaster(this, this.xCoord, this.yCoord, this.zCoord))
							 tempBricks++;
					 }
					 else if (servant.setMaster(this.xCoord, this.yCoord, this.zCoord))
						 tempBricks++;
				 }
			 }
		 }
		 return tempBricks;
	 }

	 // TODO: Clean this mess up.
	 private int[] scanGlassLayerCenter ()
	 {
		 int centerX = 0;
		 int centerZ = 0;
		 Block block;
		 switch (getRenderDirection())
		 {
		 case 2: // +z
		 // Scan to last block
			 for (int z = zCoord + 1; z < zCoord + (innerMaxSpace + 1); z++)
			 {
				 block = Block.blocksList[worldObj.getBlockId(xCoord, yCoord, z)];
				 if (block != null && validGlassID(block.blockID))
					 centerZ += checkBricks(xCoord, yCoord, z, true);
				 else break;
			 }
			 // Adjust depth scan to center
			 if ((centerZ != 1) && (centerZ % 2 == 0))
				 break;
			 else 
			 {
				 innerMaxZ = centerZ;
				 centerZ = (centerZ / 2 + 1); 
			 }
			 if (centerZ == 0) break;
			 // Scan width from center
			 for (int x = xCoord; x >= xCoord - 4; x--)
			 {
				 block = Block.blocksList[worldObj.getBlockId(x, yCoord, zCoord + centerZ)];
				 if (block != null && validGlassID(block.blockID))
					 centerX += checkBricks(x, yCoord, zCoord + centerZ, true);
				 else break;
			 }
			 for (int x = xCoord + 1; x <= xCoord + 4; x++)
			 {
				 block = Block.blocksList[worldObj.getBlockId(x, yCoord, zCoord + centerZ)];
				 if (block != null && validGlassID(block.blockID))
					 centerX += checkBricks(x, yCoord, zCoord + centerZ, true);
				 else break;
			 }
			 // Adjust width to center
			 if ((centerX != 1) && (centerX % 2 == 0))
				 break;
			 else 
			 {
				 innerMaxX = centerX;
				 centerX = (centerX / 2 + 1); 
			 }
			 if (centerX == 0) break;
			 return new int[] {xCoord, zCoord + centerZ};
		 case 3: // -z
			 for (int z = zCoord - 1; z > zCoord - (innerMaxSpace + 1); z--)
			 {
				 block = Block.blocksList[worldObj.getBlockId(xCoord, yCoord, z)];
				 if (block != null && validGlassID(block.blockID))
					 centerZ += checkBricks(xCoord, yCoord, z, true);
				 else break;
			 }
			 // Adjust depth scan to center
			 if ((centerZ != 1) && (centerZ % 2 == 0))
				 break;
			 else 
			 {
				 innerMaxZ = centerZ;
				 centerZ = (centerZ / 2 + 1); 
			 }
			 if (centerZ == 0) break;
			 // Scan width from center
			 for (int x = xCoord; x >= xCoord - 4; x--)
			 {
				 block = Block.blocksList[worldObj.getBlockId(x, yCoord, zCoord - centerZ)];
				 if (block != null && validGlassID(block.blockID))
					 centerX += checkBricks(x, yCoord, zCoord - centerZ, true);
				 else break;
			 }
			 for (int x = xCoord + 1; x <= xCoord + 4; x++)
			 {
				 block = Block.blocksList[worldObj.getBlockId(x, yCoord, zCoord - centerZ)];
				 if (block != null && validGlassID(block.blockID))
					 centerX += checkBricks(x, yCoord, zCoord - centerZ, true);
				 else break;
			 }
			 // Adjust width to center
			 if ((centerX != 1) && (centerX % 2 == 0))
				 break;
			 else 
			 {
				 innerMaxX = centerX;
				 centerX = (centerX / 2 + 1); 
			 }
			 if (centerX == 0) break;
			 return new int[] {xCoord, zCoord - centerZ};
		 case 4: // +x
			 for (int x = xCoord + 1; x < xCoord + (innerMaxSpace + 1); x++)
			 {
				 block = Block.blocksList[worldObj.getBlockId(x, yCoord, zCoord)];
				 if (block != null && validGlassID(block.blockID))
					 centerX += checkBricks(x, yCoord, zCoord, true);
				 else break;
			 }
			 // Adjust depth scan to center
			 if ((centerX != 1) && (centerX % 2 == 0))
				 break;
			 else 
			 {
				 innerMaxX = centerX;
				 centerX = (centerX / 2 + 1); 
			 }
			 if (centerX == 0) break;
			 // Scan length from center
			 for (int z = zCoord; z >= zCoord - 4; z--)
			 {
				 block = Block.blocksList[worldObj.getBlockId(xCoord + centerX, yCoord, z)];
				 if (block != null && validGlassID(block.blockID))
					 centerZ += checkBricks(xCoord + centerX, yCoord, z, true);
				 else break;
			 }
			 for (int z = zCoord + 1; z <= zCoord + 4; z++)
			 {
				 block = Block.blocksList[worldObj.getBlockId(xCoord + centerX, yCoord, z)];
				 if (block != null && validGlassID(block.blockID))
					 centerZ += checkBricks(xCoord + centerX, yCoord, z, true);
				 else break;
			 }
			 // Adjust length to center
			 if ((centerZ != 1) && (centerZ % 2 == 0))
				 break;
			 else 
			 {
				 innerMaxZ = centerZ;
				 centerZ = (centerZ / 2 + 1); 
			 }
			 if (centerZ == 0) break;
			 return new int[] {xCoord + centerX, zCoord};
		 case 5: // -x
			 for (int x = xCoord - 1; x > xCoord - (innerMaxSpace + 1); x--)
			 {
				 block = Block.blocksList[worldObj.getBlockId(x, yCoord, zCoord)];
				 if (block != null && validGlassID(block.blockID))
					 centerX += checkBricks(x, yCoord, zCoord, true);
				 else break;
			 }
			 // Adjust depth scan to center
			 if ((centerX != 1) && (centerX % 2 == 0))
				 break;
			 else 
			 {
				 innerMaxX = centerX;
				 centerX = (centerX / 2 + 1); 
			 }
			 if (centerX == 0) break;
			 // Scan length from center
			 for (int z = zCoord; z >= zCoord - 4; z--)
			 {
				 block = Block.blocksList[worldObj.getBlockId(xCoord - centerX, yCoord, z)];
				 if (block != null && validGlassID(block.blockID))
					 centerZ += checkBricks(xCoord - centerX, yCoord, z, true);
				 else break;
			 }
			 for (int z = zCoord + 1; z <= zCoord + 4; z++)
			 {
				 block = Block.blocksList[worldObj.getBlockId(xCoord - centerX, yCoord, z)];
				 if (block != null && validGlassID(block.blockID))
					 centerZ += checkBricks(xCoord - centerX, yCoord, z, true);
				 else break;
			 }
			 // Adjust length to center
			 if ((centerZ != 1) && (centerZ % 2 == 0))
				 break;
			 else 
			 {
				 innerMaxZ = centerZ;
				 centerZ = (centerZ / 2 + 1); 
			 }
			 if (centerZ == 0) break;
			 return new int[] {xCoord - centerX, zCoord};
		 }
		 return new int[] {xCoord, yCoord};
	 }

	 // TODO: Algorithms, man. 
	 public boolean validateRimmedLayer(int innerbricks, int outerbricks)
	 {
		 final int total = innerbricks + outerbricks;
		 if (innerMaxX == 1 || innerMaxZ == 1)
			 return Arrays.asList(9, 15, 21, 27, 33).contains(total);
		 else if (innerMaxX == 3 || innerMaxZ == 3)
			 return Arrays.asList(25, 35, 45, 55).contains(total);
		 else if (innerMaxX == 5 || innerMaxZ == 5)
			 return Arrays.asList(49, 63, 77).contains(total);
		 else if (innerMaxX == 7 || innerMaxZ == 7)
			 return Arrays.asList(81, 99).contains(total);
		 else if (innerMaxX == 9 || innerMaxZ == 9)
			 return (total == 121);
		 return false;
	 }

	 boolean validBlockID(int blockID)
	 {
		 return (blockID == TSContent.highoven.blockID);
	 }

	 boolean validTankID(int blockID)
	 {
		 return (blockID == TContent.lavaTank.blockID || blockID == TContent.lavaTankNether.blockID);
	 }

	 boolean validGlassID(int blockID)
	 {
		 if (blockID == Block.glass.blockID || blockID == TContent.stainedGlassClear.blockID || blockID == TContent.clearGlass.blockID)
			 return true;
		 else
			 return validModGlassID(blockID);
	 }

	 boolean validModGlassID(int blockID)
	 {
		 if (ConfigCore.modTankGlassBlocks.length < 1) return false;

		 for (int id : ConfigCore.modTankGlassBlocks)
		 {
			 if (id == blockID)
				 return true;
		 }
		 return false;
	 }

	 public int getCapacity () { return maxLiquid; }

	 public int getTotalLiquid () { return currentLiquid; }

	 /*
	  * (non-Javadoc)
	  * @see net.minecraftforge.fluids.IFluidTank#drain(int, boolean)
	  */
	 @Override
	 public FluidStack drain (int maxDrain, boolean doDrain)
	 {
		 if (fluidlist.size() == 0)
			 return null;

		 FluidStack liquid = fluidlist.get(0);
		 if (liquid != null)
		 {
			 if (liquid.amount - maxDrain <= 0)
			 {
				 FluidStack liq = liquid.copy();
				 if (doDrain)
				 {
					 //liquid = null;
					 fluidlist.remove(liquid);
					 worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
					 currentLiquid = 0;
					 needsUpdate = true;
				 }
				 containsAlloy = containsAlloy();
				 return liq;
			 }
			 else
			 {
				 if (doDrain && maxDrain > 0)
				 {
					 liquid.amount -= maxDrain;
					 worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
					 currentLiquid -= maxDrain;
					 needsUpdate = true;
				 }
				 containsAlloy = containsAlloy();
				 return new FluidStack(liquid.fluidID, maxDrain, liquid.tag);
			 }
		 }
		 else
		 {
			 containsAlloy = containsAlloy();
			 return new FluidStack(0, 0);
		 }
	 }

	 /*
	  * (non-Javadoc)
	  * @see net.minecraftforge.fluids.IFluidTank#fill(net.minecraftforge.fluids.FluidStack, boolean)
	  */
	 @Override
	 public int fill (FluidStack resource, boolean doFill)
	 {
		 if (resource == null) return 0;
		 int amount = resource.amount;
		 if (amount + currentLiquid < maxLiquid)
		 {
			 if (amount + currentLiquid > maxLiquid)
				 amount = maxLiquid - currentLiquid;
			 resource.amount = amount;

			 if (amount > 0 && doFill)
			 {
				 addFluidToTank(resource, false);
				 needsUpdate = true;
				 worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
			 }
			 containsAlloy = containsAlloy();
			 return amount;
		 }
		 else
			 return 0;
	 }

	 /*
	  * (non-Javadoc)
	  * @see net.minecraftforge.fluids.IFluidTank#getFluid()
	  */
	 @Override
	 public FluidStack getFluid ()
	 {
		 if (fluidlist.size() == 0)
			 return null;
		 return fluidlist.get(0);
	 }

	 public List<FluidStack> getAllFluids () { return fluidlist; }

	 /*
	  * (non-Javadoc)
	  * @see net.minecraftforge.fluids.IFluidTank#getFluidAmount()
	  */
	 @Override
	 public int getFluidAmount () { return currentLiquid; }

	 /*
	  * (non-Javadoc)
	  * @see net.minecraftforge.fluids.IFluidTank#getInfo()
	  */
	 @Override
	 public FluidTankInfo getInfo () { return new FluidTankInfo(this); }

	 public FluidTankInfo[] getMultiTankInfo ()
	 {
		 FluidTankInfo[] info = new FluidTankInfo[fluidlist.size() + 1];
		 for (int i = 0; i < fluidlist.size(); i++)
		 {
			 FluidStack fluid = fluidlist.get(i);
			 info[i] = new FluidTankInfo(fluid.copy(), fluid.amount);
		 }
		 info[fluidlist.size()] = new FluidTankInfo(null, maxLiquid - currentLiquid);
		 return info;
	 }

	 public void setTurbineAttached (boolean flag)
	 {
		 activeTurbineAttached = flag;
	 }

	 public boolean isTurbineAttached ()
	 {
		 return activeTurbineAttached;
	 }

	 boolean containsAlloy ()
	 {
		 for (FluidStack fluid : fluidlist) if (fluidIsAlloy(fluid)) return true;
		 return false;
	 }

	 boolean fluidIsAlloy (FluidStack fluidstack)
	 {
		 for (int i = 0; i < AlloyInfo.alloys.size(); ++i)
			 if (fluidstack.getFluid() == AlloyInfo.alloys.get(i).result.copy().getFluid())
				 return true;
		 return false;
	 }

	 void dealloyFluids ()
	 {
		 if (!containsAlloy) return;
		 for (int i = 0; i < fluidlist.size(); ++i)
		 {
			 FluidStack alloy = fluidlist.get(i).copy();
			 if (!fluidIsAlloy(alloy)) continue;

			 ArrayList fluids = AlloyInfo.deAlloy(alloy);
			 fluidlist.remove(i);

			 for (int j = 0; j < fluids.size(); j++)
			 {
				 FluidStack liquid = (FluidStack) fluids.get(j);
				 addFluidToTank(liquid, true);
			 }
		 }
	 }

	 /* NBT */

	 /*
	  * (non-Javadoc)
	  * @see net.minecraft.tileentity.TileEntity#readFromNBT(net.minecraft.nbt.NBTTagCompound)
	  */
	 @Override
	 public void readFromNBT (NBTTagCompound tags)
	 {
		 layers = tags.getInteger("Layers");
		 innerMaxX = tags.getInteger("InnerMaxX");
		 innerMaxZ = tags.getInteger("InnerMaxZ");
		 super.readFromNBT(tags);
		 validStructure = tags.getBoolean("ValidStructure");
		 containsAlloy = tags.getBoolean("ContainsAlloy");
		 int[] center = tags.getIntArray("CenterPos");
		 if (center.length > 2)
			 centerPos = new CoordTuple(center[0], center[1], center[2]);
		 else
			 centerPos = new CoordTuple(xCoord, yCoord, zCoord);
		 direction = tags.getByte("Direction");
		 currentLiquid = tags.getInteger("CurrentLiquid");
		 maxLiquid = tags.getInteger("MaxLiquid");
		 NBTTagList liquidTag = tags.getTagList("Liquids");
		 fluidlist.clear();

		 for (int iter = 0; iter < liquidTag.tagCount(); iter++)
		 {
			 NBTTagCompound nbt = (NBTTagCompound) liquidTag.tagAt(iter);
			 FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
			 if (fluid != null)
				 fluidlist.add(fluid);
		 }
	 }

	 /*
	  * (non-Javadoc)
	  * @see net.minecraft.tileentity.TileEntity#writeToNBT(net.minecraft.nbt.NBTTagCompound)
	  */
	 @Override
	 public void writeToNBT (NBTTagCompound tags)
	 {
		 super.writeToNBT(tags);
		 tags.setBoolean("ValidStructure", validStructure);
		 tags.setBoolean("ContainsAlloy", containsAlloy);
		 int[] center = new int[3];// { centerPos.x, centerPos.y, centerPos.z };
		 if (centerPos == null)
			 center = new int[] { xCoord, yCoord, zCoord };
		 else
			 center = new int[] { centerPos.x, centerPos.y, centerPos.z };
		 tags.setIntArray("CenterPos", center);
		 tags.setByte("Direction", direction);
		 tags.setInteger("CurrentLiquid", currentLiquid);
		 tags.setInteger("MaxLiquid", maxLiquid);
		 tags.setInteger("InnerMaxZ", innerMaxZ);
		 tags.setInteger("InnerMaxX", innerMaxX);
		 tags.setInteger("Layers", layers);

		 NBTTagList taglist = new NBTTagList();
		 for (FluidStack liquid : fluidlist)
		 {
			 NBTTagCompound nbt = new NBTTagCompound();
			 liquid.writeToNBT(nbt);
			 taglist.appendTag(nbt);
		 }

		 tags.setTag("Liquids", taglist);
	 }

	 /* Packets */
	 
	 /*
	  * (non-Javadoc)
	  * @see net.minecraft.tileentity.TileEntity#getDescriptionPacket()
	  */
	 @Override
	 public Packet getDescriptionPacket ()
	 {
		 NBTTagCompound tag = new NBTTagCompound();
		 writeToNBT(tag);
		 return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
	 }

	 /*
	  * (non-Javadoc)
	  * @see net.minecraft.tileentity.TileEntity#onDataPacket(net.minecraft.network.INetworkManager, net.minecraft.network.packet.Packet132TileEntityData)
	  */
	 @Override
	 public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
	 {
		 readFromNBT(packet.data);
		 onInventoryChanged();
		 worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
		 this.needsUpdate = true;
	 }
}
