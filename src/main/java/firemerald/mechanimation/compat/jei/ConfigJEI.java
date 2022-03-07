package firemerald.mechanimation.compat.jei;

import firemerald.api.config.Category;
import firemerald.api.config.Config;
import firemerald.api.config.ConfigValueBoolean;
import firemerald.mechanimation.api.MechanimationAPI;
import net.minecraft.util.ResourceLocation;

public class ConfigJEI extends Config
{
	public static final ConfigJEI INSTANCE = new ConfigJEI();

	public final ConfigValueBoolean
	arcFurnaceRecipes,
	assemblyTerminalRecipes,
	castingRecipes,
	catalyticReformerRecipes,
	clausPlantRecipes,
	desalterRecipes,
	distillationRecipes,
	electrolyzerRecipes,
	fluidReactorRecipes,
	combustionGeneratorRecipes,
	stirlingGeneratorRecipes,
	hydrotreaterRecipes,
	meroxTreaterRecipes,
	pressRecipes,
	pulverizerRecipes;

	public ConfigJEI()
	{
		super(new ResourceLocation(MechanimationAPI.MOD_ID, "compat/jaopca.cfg"));
		Category recipes = new Category(this, "recipes", "Recipe options");
		arcFurnaceRecipes = new ConfigValueBoolean(recipes, "arc_furnace_recipes", true, "Show arc furnace recipes in JEI");
		assemblyTerminalRecipes = new ConfigValueBoolean(recipes, "assembly_terminal_recipes", true, "Show assembly terminal recipes in JEI");
		castingRecipes = new ConfigValueBoolean(recipes, "casting_recipes", true, "Show casting recipes in JEI");
		catalyticReformerRecipes = new ConfigValueBoolean(recipes, "catalytic_reformer_recipes", true, "Show catalytic reformer recipes in JEI");
		clausPlantRecipes = new ConfigValueBoolean(recipes, "claus_plant_recipes", true, "Show claus plant recipes in JEI");
		desalterRecipes = new ConfigValueBoolean(recipes, "desalter_recipes", true, "Show desalter recipes in JEI");
		distillationRecipes = new ConfigValueBoolean(recipes, "distillation_recipes", true, "Show distillation recipes in JEI");
		electrolyzerRecipes = new ConfigValueBoolean(recipes, "electrolyzer_recipes", true, "Show electrolyzer recipes in JEI");
		fluidReactorRecipes = new ConfigValueBoolean(recipes, "fluid_reactor_recipes", true, "Show fluid reactor recipes in JEI");
		combustionGeneratorRecipes = new ConfigValueBoolean(recipes, "combustion_generator_recipes", true, "Show combustion generator recipes in JEI");
		stirlingGeneratorRecipes = new ConfigValueBoolean(recipes, "stirling_generator_recipes", true, "Show stirling generator recipes in JEI");
		hydrotreaterRecipes = new ConfigValueBoolean(recipes, "hydrotreater_recipes", true, "Show hydrotreater recipes in JEI");
		meroxTreaterRecipes = new ConfigValueBoolean(recipes, "merox_treater_recipes", true, "Show merox treater recipes in JEI");
		pressRecipes = new ConfigValueBoolean(recipes, "press_recipes", true, "Show press recipes in JEI");
		pulverizerRecipes = new ConfigValueBoolean(recipes, "pulverizer_recipes", true, "Show pulverizer recipes in JEI");
	}
}