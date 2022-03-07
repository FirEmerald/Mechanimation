package firemerald.mechanimation.tileentity.machine.casting_table;

import firemerald.api.mcms.model.IModel;
import firemerald.mechanimation.api.crafting.casting.EnumCastingType;
import firemerald.mechanimation.init.MechanimationStats;
import firemerald.mechanimation.mcms.MCMSModel;
import firemerald.mechanimation.mcms.MCMSTexture;
import firemerald.mechanimation.util.EnumFace;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fluids.Fluid;

public class TileEntityCastingTableBasic extends TileEntityCastingTable<TileEntityCastingTableBasic>
{
	public TileEntityCastingTableBasic()
	{
		super(new EnumCastingType[] {EnumCastingType.INGOT});
	}

	@Override
	public int getMaxFluid()
	{
		return Fluid.BUCKET_VOLUME * 2;
	}

	@Override
	public int getMaxFluidTransfer()
	{
		return 100;
	}

	@Override
	public int getMaxFluidTransferItem()
	{
		return Fluid.BUCKET_VOLUME;
	}

	@Override
	public EnumFace[] getInputSides()
	{
		return new EnumFace[] { EnumFace.RIGHT };
	}

	@Override
	public EnumFace[] getOutputSides()
	{
		return EnumFace.SIDES_AND_BOTTOM;
	}

	@Override
	public IModel<?, ?> getModel(float partial)
	{
		return MCMSModel.CASTING_TABLE_BASIC_INGOT.model.get();
	}

	@Override
	public ResourceLocation getTexture()
	{
		return MCMSTexture.CASTING_TABLE_BASIC_INGOT;
	}

	@Override
	public int getAmbientSurfaceArea()
	{
		return 100; //TODO value
	}

	@Override
	public AxisAlignedBB getChamberBox()
	{
		return new AxisAlignedBB(this.pos);
	}

	@Override
	public StatBase getInteractionStat()
	{
		return MechanimationStats.BASIC_CAST_INTERACTION;
	}
}