package firemerald.mechanimation.items;

import firemerald.mechanimation.api.MechanimationAPI;
import firemerald.mechanimation.init.MechanimationTabs;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCarbonFiberArmor extends ItemArmor
{
	@SideOnly(Side.CLIENT)
	public static ModelBiped modelLeggings, modelArmor;
	public static final ArmorMaterial MATERIAL = EnumHelper.addArmorMaterial("CARBON_FIBER", MechanimationAPI.MOD_ID + ":carbon_fiber", 10, new int[]{3, 6, 8, 3}, 10, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0F);

	static
	{
		if (FMLCommonHandler.instance().getSide().isClient())
		{
	        modelLeggings = new ModelBiped(0.125F);
	        modelArmor = new ModelBiped(0.5F);
		}
	}

	public ItemCarbonFiberArmor(EntityEquipmentSlot equipmentSlotIn)
	{
		super(MATERIAL, 0, equipmentSlotIn);
		this.setCreativeTab(MechanimationTabs.COMBAT);
	}

	@Override
	@SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default)
	{
		return setArmorModel(armorSlot == EntityEquipmentSlot.LEGS ? modelLeggings : modelArmor, entityLiving, armorSlot, _default);
	}

	@SideOnly(Side.CLIENT)
	public ModelBiped setArmorModel(ModelBiped armorModel, EntityLivingBase entityLiving, EntityEquipmentSlot armorSlot, ModelBiped _default)
	{
		if (armorModel == null) return null;
		armorModel.bipedHead.showModel = armorSlot == EntityEquipmentSlot.HEAD;
		armorModel.bipedHeadwear.showModel = armorSlot == EntityEquipmentSlot.HEAD;
		armorModel.bipedBody.showModel = armorSlot == EntityEquipmentSlot.CHEST;
		armorModel.bipedRightArm.showModel = armorSlot == EntityEquipmentSlot.CHEST;
		armorModel.bipedLeftArm.showModel = armorSlot == EntityEquipmentSlot.CHEST;
		armorModel.bipedRightLeg.showModel = armorSlot == EntityEquipmentSlot.LEGS || armorSlot == EntityEquipmentSlot.FEET;
		armorModel.bipedLeftLeg.showModel = armorSlot == EntityEquipmentSlot.LEGS || armorSlot == EntityEquipmentSlot.FEET;
		setAnglesFrom(_default, armorModel);
		return armorModel;
	}

	public void setAnglesFrom(ModelBiped from, ModelBiped to)
	{
		to.isChild = from.isChild;
		setAnglesFrom(to.bipedBody, from.bipedBody);
		setAnglesFrom(to.bipedHead, from.bipedHead);
		setAnglesFrom(to.bipedHeadwear, from.bipedHeadwear);
		setAnglesFrom(to.bipedLeftArm, from.bipedLeftArm);
		setAnglesFrom(to.bipedLeftLeg, from.bipedLeftLeg);
		setAnglesFrom(to.bipedRightArm, from.bipedRightArm);
		setAnglesFrom(to.bipedRightLeg, from.bipedRightLeg);
	}

	public void setAnglesFrom(ModelRenderer to, ModelRenderer from)
	{
		to.rotateAngleX = from.rotateAngleX;
		to.rotateAngleY = from.rotateAngleY;
		to.rotateAngleZ = from.rotateAngleZ;
		to.rotationPointX = from.rotationPointX;
		to.rotationPointY = from.rotationPointY;
		to.rotationPointZ = from.rotationPointZ;
		to.offsetX = from.offsetX;
		to.offsetY = from.offsetY;
		to.offsetZ = from.offsetZ;
	}
}