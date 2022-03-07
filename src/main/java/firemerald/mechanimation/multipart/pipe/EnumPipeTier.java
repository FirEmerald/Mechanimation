package firemerald.mechanimation.multipart.pipe;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.api.MechanimationAPI;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum EnumPipeTier //TODO item pipe speed
{
	//items is about equal to e^(-ln(2.5)/3+2ln(2))(x+(4ln(2.5)/3-2ln(2))/(-ln(2.5)/3+2ln(2)))/20 where x is the tier (0 for stone) - about triple each time
	STONE("stone", .04f, 120, 20, 600, 100, "frame_stone"), //0.8 item/sec, 400 mb/sec, 2000 RF/sec
	IRON("iron", .125f, 240, 40, 1200, 200, "frame_iron"), //2.5 item/sec, 800 mb/sec, 4000 RF/sec (3.125 increase)
	STEEL("steel", .375f, 480, 80, 2400, 400, "frame_steel"), //7.5 item/sec, 1600 mb/sec, 8000 RF/sec (3 increase)
	TITANIUM("titanium", 1.2f, 960, 160, 4800, 800, "frame_titanium"); //24 item/sec, 3200 mb/sec, 16000 RF/sec (3.2 increase)
	//MALDIUM("maldium", 3.2f, 1920, 320, 9600, 1600, "frame_maldium"); //64 item/sec, 6400 mb/sec, 32000 RF/sec (2.6667 increase)

	public final String unlocalizedName;
	public final float maxItemExtract;
	public final int maxFluid, maxFluidTransfer, maxPower, maxPowerTransfer;
	public final ResourceLocation iconRL;
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite icon;

	EnumPipeTier(String name, float maxItemExtract, int maxFluid, int maxFluidTransfer, int maxPower, int maxPowerTransfer, String iconRL)
	{
		this(name, maxItemExtract, maxFluid, maxFluidTransfer, maxPower, maxPowerTransfer, new ResourceLocation(MechanimationAPI.MOD_ID, "blocks/pipe/" + iconRL));
	}

	EnumPipeTier(String name, float maxItemExtract, int maxFluid, int maxFluidTransfer, int maxPower, int maxPowerTransfer, ResourceLocation iconRL)
	{
		this.unlocalizedName = name;
		this.maxItemExtract = maxItemExtract;
		this.maxFluid = maxFluid;
		this.maxFluidTransfer = maxFluidTransfer;
		this.maxPower = maxPower;
		this.maxPowerTransfer = maxPowerTransfer;
		this.iconRL = iconRL;
	}

	public String getLocalizedName()
	{
		return Translator.translate("pipe.tier." + unlocalizedName);
	}
}