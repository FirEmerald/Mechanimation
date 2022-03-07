package firemerald.mechanimation.compat.tconstruct;

import firemerald.api.config.Category;
import firemerald.api.config.ConfigValueBoolean;
import firemerald.api.config.ConfigValueFloat;
import firemerald.api.config.ConfigValueInt;

public class ConfigValueMaterial extends ConfigValueBoolean
{
	public final ConfigValueInt headDurability, handleDurability, extraDurability;
	public final ConfigValueFloat miningSpeed, damage;
	public final ConfigValueInt miningLevel;
	public final ConfigValueFloat modifier;

	public ConfigValueMaterial(Category category, boolean addMaterial, int headDurability, int handleDurability, int extraDurability,
			float miningSpeed, float damage, int miningLevel, float modifier)
	{
		super(category, "enable", addMaterial, "Enable tool material");
		this.headDurability = new ConfigValueInt(category, "head_durability", headDurability, "head part durability");
		this.handleDurability = new ConfigValueInt(category, "handle_durability", handleDurability, "handle part durability");
		this.extraDurability = new ConfigValueInt(category, "extra_durability", extraDurability, "extra part durability");
		this.miningSpeed = new ConfigValueFloat(category, "mining_speed", miningSpeed, "mining speed");
		this.damage = new ConfigValueFloat(category, "damage", damage, "attack damage");
		this.miningLevel = new ConfigValueInt(category, "mining_level", miningLevel, "mining level");
		this.modifier = new ConfigValueFloat(category, "modifier", modifier, "modifier");
	}
}