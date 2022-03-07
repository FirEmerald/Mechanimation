package firemerald.mechanimation.tileentity.machine.base.implementation.actual;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import firemerald.api.mcms.animation.AnimationState;
import firemerald.api.mcms.animation.IAnimation;
import firemerald.api.mcms.util.IModeledEntity;
import firemerald.mechanimation.inventory.container.ContainerMachine;
import firemerald.mechanimation.tileentity.machine.base.IGuiMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityMachineBase<T extends TileEntityMachineBase<T, R>, R> extends TileEntity implements IGuiMachine<T>, IModeledEntity, ITickable, ILockableContainer
{
    protected String machineCustomName;
    public static final AnimationState[] NO_ANIMATION = new AnimationState[0];
    public final AnimationState[] running = new AnimationState[] {new AnimationState(this::getRunningAnimation)};
	protected boolean needsUpdate = false;
	protected boolean isRunning = false;
	protected float runTime = 0;
	protected List<Runnable> operations = Collections.emptyList();

	@Override
	public World getTheWorld()
	{
		return this.getWorld();
	}

	@Override
	public BlockPos getThePos()
	{
		return this.getPos();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return new AxisAlignedBB(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 2, pos.getY() + 1, pos.getZ() + 2);
	}

	public abstract IAnimation getRunningAnimation();

	public abstract R getValidRecipe();

    public abstract String getDefaultName();

    @Override
	public String getName()
    {
        return this.hasCustomName() ? this.machineCustomName : getDefaultName();
    }

    @Override
	public boolean hasCustomName()
    {
        return this.machineCustomName != null && !this.machineCustomName.isEmpty();
    }

	@Override
	public void setNeedsUpdate()
	{
		if (world != null && !world.isRemote)
		{
			needsUpdate = true;
			markDirty();
		}
	}

	public boolean useAnimation()
	{
		return this.isRunning;
	}

	@Override
	public AnimationState[] getAnimationStates(float partial)
	{
		if (useAnimation())
		{
			running[0].time = (shouldTickAnimation() ? (runTime + partial * getAnimationSpeed()) : runTime) * 0.05f;
			return running;
		}
		else return NO_ANIMATION;
	}

    public void setRunning(boolean running)
    {
    	if (running)
    	{
    		if (!this.isRunning)
    		{
        		this.isRunning = true;
    			onStartedRunning();
        		setNeedsUpdate();
    		}
    	}
    	else if (this.isRunning)
    	{
    		this.isRunning = false;
			onStoppedRunning();
    		setNeedsUpdate();
    	}
    }

    public boolean shouldTickAnimation()
    {
    	return isRunning;
    }

    public abstract boolean canProcessRecipeThisTick(R recipe);

    public abstract void onUsageTick(R recipe);

    public void preProcess() {}

    public void postProcess() {}

    public void onStoppedRunning() {}

    public void onStartedRunning()
    {
    	runTime = 0;
    }

    public float getAnimationSpeed()
    {
    	return 1;
    }

	@Override
	public void update()
	{
		if (shouldTickAnimation()) runTime += getAnimationSpeed();
		if (!world.isRemote)
		{
			operations.forEach(Runnable::run);
			preProcess();
			R recipe = getValidRecipe();
			if (recipe == null) setRunning(false);
			else
			{
				setRunning(true);
				onUsageTick(recipe);
				while (canProcessRecipeThisTick(recipe))
				{
					processRecipe(recipe);
					recipe = getValidRecipe();
					if (recipe == null)
					{
						setRunning(false);
						break;
					}
					this.setNeedsUpdate();
				}
			}
			postProcess();
		}
    	if (needsUpdate)
    	{
    		needsUpdate = false;
    		if (!world.isRemote)
    		{
    			SPacketUpdateTileEntity p = this.getUpdatePacket();
    			for (EntityPlayer player : world.playerEntities) if (player instanceof EntityPlayerMP) if (player.getDistanceSq(this.pos) < 4096.0D) ((EntityPlayerMP) player).connection.sendPacket(p);
    		}
    	}
	}

	public abstract void processRecipe(R recipe);

    @Override
	public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.code = LockCode.fromNBT(compound);
        if (compound.hasKey("CustomName", 8)) this.machineCustomName = compound.getString("CustomName");
        readFromDisk(compound);
        readTag(compound);
        readFromDiskPost(compound);
    }

	public void readTag(NBTTagCompound tag)
	{
        readFromShared(tag);
        if (tag.getBoolean("isRunning"))
        {
        	if (!this.isRunning)
        	{
        		this.isRunning = true;
        		onStartedRunning();
        	}
        }
        else
        {
        	this.isRunning = false;
        	onStoppedRunning();
        }
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(this.pos, 3, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		handleUpdateTag(pkt.getNbtCompound());
	}

	@Override
    public NBTTagCompound getUpdateTag()
    {
		NBTTagCompound tag = super.getUpdateTag();
		writeToUpdate(tag);
		writeTag(tag);
        return tag;
    }

	@Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
		super.handleUpdateTag(tag);
		readFromUpdate(tag);
		readTag(tag);
    }

    @Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);
        if (this.code != null) this.code.toNBT(compound);
        writeTag(compound);
        if (this.hasCustomName()) compound.setString("CustomName", this.machineCustomName);
        writeToDisk(compound);
        return compound;
    }

	public void writeTag(NBTTagCompound tag)
	{
		tag.setBoolean("isRunning", isRunning);
		writeToShared(tag);
	}

	public void writeToShared(NBTTagCompound tag) {}

	public void readFromShared(NBTTagCompound tag) {}

	public void readFromDisk(NBTTagCompound tag) {}

	public void writeToDisk(NBTTagCompound tag) {}

	public void readFromDiskPost(NBTTagCompound tag) {}

	public void writeToUpdate(NBTTagCompound tag) {}

	public void readFromUpdate(NBTTagCompound tag) {}

	public abstract StatBase getInteractionStat();

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@SuppressWarnings("unchecked")
	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
	{
        return new ContainerMachine<>(playerInventory, (T) this);
	}

	@Override
	public void clear() {}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
        if (this.world.getTileEntity(this.pos) != this) return false;
        else return player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
	}

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
		if (hasCapabilityLocal(capability, facing)) return true;
		else return super.hasCapability(capability, facing);
    }

    public boolean hasCapabilityLocal(Capability<?> capability, @Nullable EnumFacing facing)
    {
    	return false;
    }

	@Override
    @Nullable
    public <S> S getCapability(Capability<S> capability, @Nullable EnumFacing facing)
    {
		S has = getCapabilityLocal(capability, facing);
		if (has != null) return has;
		else return super.getCapability(capability, facing);
    }

	public <S> S getCapabilityLocal(Capability<S> capability, @Nullable EnumFacing facing)
    {
    	return null;
    }

    private LockCode code = LockCode.EMPTY_CODE;

    @Override
    public boolean isLocked()
    {
        return this.code != null && !this.code.isEmpty();
    }

    @Override
    public LockCode getLockCode()
    {
        return this.code;
    }

    @Override
    public void setLockCode(LockCode code)
    {
        this.code = code;
    }

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    @Override
	public ITextComponent getDisplayName()
    {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
    }

    public double getProgress()
    {
        return getProgress(0);
    }

    public double getProgress(int index)
    {
        return getField(index + 1) == 0 ? 0 : ((double) getField(index)) / getField(index + 1);
    }
}