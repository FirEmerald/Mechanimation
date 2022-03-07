package firemerald.mechanimation.compat.jei.recipe;

import java.util.Collections;
import java.util.List;

import firemerald.api.core.client.Translator;
import firemerald.mechanimation.api.util.Rectangle;

public abstract class RecipeWrapperFluxReciever extends RecipeWrapperBase
{
	public final int fluxDisplayX1, fluxDisplayY1, fluxDisplayX2, fluxDisplayY2;

	public RecipeWrapperFluxReciever(String uId, int fluxDisplayX, int fluxDisplayY, Rectangle fluxDisplay)
	{
		this(uId, fluxDisplayX, fluxDisplayY, fluxDisplayX + fluxDisplay.w, fluxDisplayY + fluxDisplay.h);
	}

	public RecipeWrapperFluxReciever(String uId, int fluxDisplayX1, int fluxDisplayY1, int fluxDisplayX2, int fluxDisplayY2)
	{
		super(uId);
		this.fluxDisplayX1 = fluxDisplayX1;
		this.fluxDisplayY1 = fluxDisplayY1;
		this.fluxDisplayX2 = fluxDisplayX2;
		this.fluxDisplayY2 = fluxDisplayY2;
	}

	public abstract int getRFUsed();

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY)
	{
		if (mouseX >= fluxDisplayX1 && mouseY >= fluxDisplayY1 && mouseX < fluxDisplayX2 && mouseY < fluxDisplayY2) return Collections.singletonList(Translator.format("mechanimation.jei.tooltip.rf", getRFUsed()));
		else return Collections.emptyList();
	}
}