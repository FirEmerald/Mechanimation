package firemerald.mechanimation.tileentity.machine.base;

import java.util.List;
import java.util.Map;

import firemerald.mechanimation.client.gui.inventory.GuiMachine;
import firemerald.mechanimation.inventory.container.ContainerMachine;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorldNameable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IGuiMachine<T extends IGuiMachine<T>> extends IMachine, IWorldNameable
{
    public void addItemSlotsToContainer(ContainerMachine<T> container);

    @SideOnly(Side.CLIENT)
    public default void onGuiInit(GuiMachine<T> gui, int startX, int startY) {}

    @SideOnly(Side.CLIENT)
    public default void onGuiDrawnBackground(GuiMachine<T> gui, int startX, int startY, int adjMouseX, int adjMouseY, float partialTicks) {}

    @SideOnly(Side.CLIENT)
    public default void onGuiDrawn(GuiMachine<T> gui, int startX, int startY, int adjMouseX, int adjMouseY, float partialTicks) {}

    @SideOnly(Side.CLIENT)
    public ResourceLocation getGuiTexture();

    @SideOnly(Side.CLIENT)
    public void renderProgressMeters(GuiContainer gui, int offX, int offY, float zLevel);

    @SideOnly(Side.CLIENT)
    public default Object getIngredient(int adjX, int adjY)
    {
    	return null;
    }

    @SideOnly(Side.CLIENT)
    public String[] getRecipeTypes(int adjX, int adjY);

	public default int inventorySizeX()
	{
		return 176;
	}

	public default int inventorySizeY()
	{
		return 166;
	}

	public default int inventoryOffsetX()
	{
		return inventorySizeX() / 2 - 80;
	}

	public default int inventoryOffsetY()
	{
		return inventorySizeY() - 82;
	}

	public default void setPreview(Map<Integer, List<ItemStack>> previewMap) {}

	public default void updatePreview(Map<Integer, List<ItemStack>> previewMap) {}

	public default void onGuiPacket(byte id, ByteBuf data) {}

    int getField(int id);

    void setField(int id, int value);

    int getFieldCount();

    void clear();

    boolean isUsableByPlayer(EntityPlayer player);
}