package firemerald.mechanimation.compat.forgemultipart;

import java.util.Arrays;

import codechicken.multipart.MultiPartRegistry;
import firemerald.api.core.InitFunctions;
import firemerald.mechanimation.compat.forgemultipart.pipe.ItemPartEnergyPipe;
import firemerald.mechanimation.compat.forgemultipart.pipe.ItemPartFluidPipe;
import firemerald.mechanimation.compat.forgemultipart.pipe.ItemPartItemPipe;
import firemerald.mechanimation.compat.forgemultipart.pipe.ItemPartPipe;
import firemerald.mechanimation.compat.forgemultipart.pipe.PartEnergyPipe;
import firemerald.mechanimation.compat.forgemultipart.pipe.PartFluidPipe;
import firemerald.mechanimation.compat.forgemultipart.pipe.PartItemPipe;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class MultipartItems
{
	public static final ItemExtractor EXTRACTOR = new ItemExtractor();
	public static final ItemPartItemPipe ITEM_PIPE = new ItemPartItemPipe();
	public static final ItemPartEnergyPipe ENERGY_PIPE = new ItemPartEnergyPipe();
	public static final ItemPartFluidPipe FLUID_PIPE = new ItemPartFluidPipe();

	public static void init(IForgeRegistry<Item> registry)
	{
		InitFunctions.addItem(EXTRACTOR, "extractor", registry);
		InitFunctions.addItem(ITEM_PIPE, "item_pipe", registry);
		InitFunctions.addItem(ENERGY_PIPE, "energy_pipe", registry);
		InitFunctions.addItem(FLUID_PIPE, "fluid_pipe", registry);
	}

    @SideOnly(Side.CLIENT)
    public static void registerColors(ItemColors colors)
    {
    	colors.registerItemColorHandler(new IItemColor() {
			@Override
			public int colorMultiplier(ItemStack stack, int tintIndex)
			{
				if (tintIndex > 0) return -1;
				else
				{
					ItemPartPipe pipe = (ItemPartPipe) stack.getItem();
					EnumDyeColor color = pipe.getColor(stack);
					return color == null ? pipe.getDefaultColor() : color.getColorValue();
				}
			}

    	}, ITEM_PIPE, ENERGY_PIPE, FLUID_PIPE);
    }

	@SideOnly(Side.CLIENT)
	public static void registerModels()
	{
		InitFunctions.registerItemModels(EXTRACTOR);
		InitFunctions.registerItemModels(ITEM_PIPE);
		InitFunctions.registerItemModels(ENERGY_PIPE);
		InitFunctions.registerItemModels(FLUID_PIPE);
	}

	public static void initMultipart()
	{
		MultiPartRegistry.registerParts(new MechanimationPartFactory(), Arrays.asList(PartItemPipe.TYPE, PartEnergyPipe.TYPE, PartFluidPipe.TYPE));
	}
}
