package firemerald.api.mcms.client.modelwrapper;

import firemerald.api.mcms.model.Bone;
import firemerald.api.mcms.model.IModel;
import net.minecraft.client.model.ModelRenderer;

public interface IModelWrapper
{
	public String getName(ModelRenderer renderer);

    public ModelRenderer[] getComponents();

	public default void ensureVisibility(IModel<?, ?> model)
	{
		for (ModelRenderer component : getComponents()) ensureVisibility(component, model);
	}

	public default void ensureVisibility(ModelRenderer component, IModel<?, ?> model)
	{
		String name = getName(component);
		if (name != null)
		{
			Bone<?> bone = model.getBone(name);
			if (bone != null) bone.childrenVisible = bone.visible = component.showModel && !component.isHidden;
		}
		if (component.childModels != null) component.childModels.forEach(child -> ensureVisibility(child, model));
	}
}