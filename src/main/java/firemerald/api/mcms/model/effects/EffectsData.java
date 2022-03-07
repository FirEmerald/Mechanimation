package firemerald.api.mcms.model.effects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import firemerald.api.mcms.data.AbstractElement;
import firemerald.api.mcms.model.IModel;
import firemerald.api.mcms.model.RenderBone;
import firemerald.api.mcms.util.ISaveable;

public class EffectsData implements ISaveable
{
	public final Map<String, List<BoneEffect>> effects = new HashMap<>();

	public EffectsData() {}

	public EffectsData(AbstractElement el)
	{
		load(el);
	}

	public EffectsData(IModel<?, ? extends RenderBone<?>> model)
	{
		model.getAllBones().stream().filter(bone -> !bone.getEffects().isEmpty()).forEach(bone -> effects.put(bone.getName(), bone.getEffects().stream().map(effect -> effect.cloneObject(null)).collect(Collectors.toList())));
	}

	public void apply(IModel<?, ? extends RenderBone<?>> model)
	{
		model.getAllBones().stream().filter(bone -> effects.containsKey(bone.getName())).forEach(bone -> effects.get(bone.getName()).forEach(effect -> effect.cloneObject(bone)));
	}

	@Override
	public void load(AbstractElement root)
	{
		this.effects.clear();
		root.getChildren().forEach(el ->
		effects.put(el.getString("name", ""), el.getChildren().stream().map(
				child -> BoneEffect.constructIfRegistered(child.getName(), null, child)).filter(effect -> effect != null).collect(Collectors.toList())));
	}

	@Override
	public void save(AbstractElement root)
	{
		effects.forEach((name, effects) -> {
			AbstractElement el = root.addChild("bone");
			el.setString("name", name);
			effects.forEach(effect -> effect.addToXML(el));
		});
	}

	@Override
	public String getElementName()
	{
		return "effects";
	}
}