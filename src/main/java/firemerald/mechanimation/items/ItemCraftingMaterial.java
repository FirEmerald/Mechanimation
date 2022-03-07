package firemerald.mechanimation.items;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import firemerald.api.core.items.IItemSubtyped;
import firemerald.mechanimation.init.MechanimationTabs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCraftingMaterial extends Item implements IItemSubtyped
{
	public static final int
	COAL_DUST 			= 0,
	IRON_DUST 			= 1,
	GOLD_DUST 			= 2,
	NICKEL_DUST 		= 3,
	COPPER_DUST 		= 4,
	ALUMINUM_DUST 		= 5,
	TIN_DUST 			= 6,
	SILVER_DUST 		= 7,
	TUNGSTEN_DUST 		= 8,
	TITANIUM_DUST 		= 9,
	STEEL_DUST 			= 10,
	NICKEL_NUGGET 		= 11,
	COPPER_NUGGET 		= 12,
	ALUMINUM_NUGGET 	= 13,
	TIN_NUGGET 			= 14,
	SILVER_NUGGET 		= 15,
	TUNGSTEN_NUGGET		= 16,
	TITANIUM_NUGGET 	= 17,
	STEEL_NUGGET 		= 18,
	NICKEL_INGOT 		= 19,
	COPPER_INGOT 		= 20,
	ALUMINUM_INGOT 		= 21,
	TIN_INGOT 			= 22,
	SILVER_INGOT 		= 23,
	TUNGSTEN_INGOT 		= 24,
	TITANIUM_INGOT 		= 25,
	STEEL_INGOT 		= 26,
	IRON_PLATE 			= 27,
	GOLD_PLATE 			= 28,
	NICKEL_PLATE 		= 29,
	COPPER_PLATE 		= 30,
	ALUMINUM_PLATE 		= 31,
	TIN_PLATE 			= 32,
	SILVER_PLATE 		= 33,
	TUNGSTEN_PLATE 		= 34,
	TITANIUM_PLATE 		= 35,
	STEEL_PLATE 		= 36,
	CARBON_FIBER 		= 37,
	COPPER_WIRE 		= 38,
	MOTOR 				= 39,
	MIRROR 				= 40,
	LENS 				= 41,
	CRYSTAL_FOCUS		= 42,
	REDSTONE_CAPACITOR	= 43,
	CIRCUIT_BOARD		= 44,
	POWER_CORE 			= 45,
	SULFUR 				= 46,
	SOAP 				= 47,
	GUNCOTTON 			= 48;

	private final Map<Integer, String> materials = new LinkedHashMap<>();
	private final Map<String, Integer> materialsInverse = new LinkedHashMap<>();
	private final HashSet<Integer> hasGlint = new HashSet<>();

	public ItemCraftingMaterial()
	{
		setHasSubtypes(true);
		setCreativeTab(MechanimationTabs.MATERIALS);
		setMaterial(COAL_DUST, "coal_dust");
		setMaterial(IRON_DUST, "iron_dust");
		setMaterial(GOLD_DUST, "gold_dust");
		setMaterial(NICKEL_DUST, "nickel_dust");
		setMaterial(COPPER_DUST, "copper_dust");
		setMaterial(ALUMINUM_DUST, "aluminum_dust");
		setMaterial(TIN_DUST, "tin_dust");
		setMaterial(SILVER_DUST, "silver_dust");
		setMaterial(TUNGSTEN_DUST, "tungsten_dust");
		setMaterial(TITANIUM_DUST, "titanium_dust");
		setMaterial(STEEL_DUST, "steel_dust");
		setMaterial(NICKEL_NUGGET, "nickel_nugget");
		setMaterial(COPPER_NUGGET, "copper_nugget");
		setMaterial(ALUMINUM_NUGGET, "aluminum_nugget");
		setMaterial(TIN_NUGGET, "tin_nugget");
		setMaterial(SILVER_NUGGET, "silver_nugget");
		setMaterial(TUNGSTEN_NUGGET, "tungsten_nugget");
		setMaterial(TITANIUM_NUGGET, "titanium_nugget");
		setMaterial(STEEL_NUGGET, "steel_nugget");
		setMaterial(NICKEL_INGOT, "nickel_ingot");
		setMaterial(COPPER_INGOT, "copper_ingot");
		setMaterial(ALUMINUM_INGOT, "aluminum_ingot");
		setMaterial(TIN_INGOT, "tin_ingot");
		setMaterial(SILVER_INGOT, "silver_ingot");
		setMaterial(TUNGSTEN_INGOT, "tungsten_ingot");
		setMaterial(TITANIUM_INGOT, "titanium_ingot");
		setMaterial(STEEL_INGOT, "steel_ingot");
		setMaterial(IRON_PLATE, "iron_plate");
		setMaterial(GOLD_PLATE, "gold_plate");
		setMaterial(NICKEL_PLATE, "nickel_plate");
		setMaterial(COPPER_PLATE, "copper_plate");
		setMaterial(ALUMINUM_PLATE, "aluminum_plate");
		setMaterial(TIN_PLATE, "tin_plate");
		setMaterial(SILVER_PLATE, "silver_plate");
		setMaterial(TUNGSTEN_PLATE, "tungsten_plate");
		setMaterial(TITANIUM_PLATE, "titanium_plate");
		setMaterial(STEEL_PLATE, "steel_plate");
		setMaterial(CARBON_FIBER, "carbon_fiber");
		setMaterial(COPPER_WIRE, "copper_wire");
		setMaterial(MOTOR, "motor");
		setMaterial(MIRROR, "mirror");
		setMaterial(LENS, "lens");
		setMaterial(CRYSTAL_FOCUS, "crystal_focus");
		setMaterial(REDSTONE_CAPACITOR, "redstone_capacitor");
		setMaterial(CIRCUIT_BOARD, "circuit_board");
		setMaterial(POWER_CORE, "power_core");
		setMaterial(GUNCOTTON, "guncotton");
		setMaterial(SULFUR, "sulfur");
		setMaterial(SOAP, "soap");
	}

	protected void setMaterial(Integer id, String material)
	{
		setMaterial(id, material, false);
	}

	protected void setMaterial(Integer id, String material, boolean hasGlint)
	{
		materials.put(id, material);
		materialsInverse.put(material, id);
		if (hasGlint) this.hasGlint.add(id);
	}

	@Override
    public String getUnlocalizedName(ItemStack stack)
    {
		String material = materials.get(stack.getItemDamage());
		if (material != null) return super.getUnlocalizedName() + "." + material;
		else return super.getUnlocalizedName() + ".unknown";
    }

	@Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab)) materials.keySet().forEach(id -> items.add(new ItemStack(this, 1, id)));
    }

	@Override
	public String getSubtype(int meta)
	{
		String material = materials.get(meta);
		if (material != null) return material;
		else return "unknown";
	}

	@Override
	public int getMeta(String subtype)
	{
		Integer meta = materialsInverse.get(subtype);
		if (meta != null) return meta;
		else return -1;
	}

	@Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack)
    {
        return hasGlint.contains(stack.getItemDamage()) || super.hasEffect(stack);
    }
}