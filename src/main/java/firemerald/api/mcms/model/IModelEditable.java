package firemerald.api.mcms.model;

import firemerald.api.mcms.animation.Transformation;

public interface IModelEditable extends IEditableParent
{
	public String getName();

	public void movedTo(IEditableParent oldParent, IEditableParent newParent);

	public boolean isVisible();

	public void setVisible(boolean visible);

	public IModelEditable copy(IEditableParent newParent, IRigged<?, ?> iRigged);

	public Transformation getDefaultTransformation();
}