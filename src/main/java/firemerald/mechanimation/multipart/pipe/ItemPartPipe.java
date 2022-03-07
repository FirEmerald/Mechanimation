package firemerald.mechanimation.multipart.pipe;

import codechicken.lib.vec.Vector3;
import codechicken.multipart.TMultiPart;
import firemerald.api.core.client.Translator;
import firemerald.api.core.items.ICustomSubtypes;
import firemerald.mechanimation.init.MechanimationTabs;
import firemerald.mechanimation.multipart.ItemPart;
import firemerald.mechanimation.util.Utils;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ItemPartPipe extends ItemPart implements ICustomSubtypes
{
	public ItemPartPipe()
	{
		super();
		this.setCreativeTab(MechanimationTabs.PIPES);
		this.setHasSubtypes(true);
	}

	@Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab))
        {
            for (EnumPipeTier tier : EnumPipeTier.values()) for (int i = 0; i <= EnumDyeColor.values().length; i++) items.add(setTier(new ItemStack(this, 1, i), tier));
        }
    }

	public EnumDyeColor getColor(ItemStack stack)
	{
		if (stack.getMetadata() < 1 || stack.getMetadata() > EnumDyeColor.values().length) return null;
		else return EnumDyeColor.values()[stack.getMetadata() - 1];
	}

	public EnumPipeTier getTier(ItemStack stack)
	{
		if (stack.hasTagCompound())
		{
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey("tier", 8)) return Utils.getEnum(tag.getString("tier"), EnumPipeTier.values(), EnumPipeTier.STONE);
			else return EnumPipeTier.STONE;
		}
		else return EnumPipeTier.STONE;
	}

	public ItemStack setTier(ItemStack stack, EnumPipeTier tier)
	{
		NBTTagCompound tag;
		if (stack.hasTagCompound()) tag = stack.getTagCompound();
		else stack.setTagCompound(tag = new NBTTagCompound());
		tag.setString("tier", tier.name());
		return stack;
	}

	public int getDefaultColor()
	{
		return 0xCBCBCB;
	}

	@Override
	public TMultiPart newPart(ItemStack stack, EntityPlayer player, World world, BlockPos pos, int side, Vector3 vHit)
	{
		PartPipe pipe = newPipe(stack, player, world, pos, side, vHit);
		pipe.color = getColor(stack);
		pipe.setTierNoWorldUpdate(getTier(stack));
		return pipe;
	}

	public abstract PartPipe newPipe(ItemStack stack, EntityPlayer player, World world, BlockPos pos, int side, Vector3 vHit);

	@Override
    public String getUnlocalizedName(ItemStack stack)
    {
		EnumDyeColor color = this.getColor(stack);
		return color == null ? super.getUnlocalizedName(stack) : (super.getUnlocalizedName(stack) + ".colored");
    }

	@Override
    public String getItemStackDisplayName(ItemStack stack)
    {
		EnumDyeColor color = this.getColor(stack);
		return color == null ? String.format(super.getItemStackDisplayName(stack), getTier(stack).getLocalizedName()) : String.format(super.getItemStackDisplayName(stack), getTier(stack).getLocalizedName(), color.chatColor + Translator.translate("item.fireworksCharge." + color.getUnlocalizedName()));
    }

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels()
	{
		final ModelResourceLocation[] models = new ModelResourceLocation[EnumPipeTier.values().length];
		for (int i = 0; i < EnumPipeTier.values().length; i++) models[i] = new ModelResourceLocation(this.getRegistryName(), "tier=" + EnumPipeTier.values()[i].unlocalizedName);
		ModelBakery.registerItemVariants(this, models);
		ModelLoader.setCustomMeshDefinition(this, (stack) -> models[this.getTier(stack).ordinal()]);
	}
}