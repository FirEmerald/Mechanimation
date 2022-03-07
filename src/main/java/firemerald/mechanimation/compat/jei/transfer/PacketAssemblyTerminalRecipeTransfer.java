package firemerald.mechanimation.compat.jei.transfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import firemerald.api.core.networking.ServerPacket;
import io.netty.buffer.ByteBuf;
import mezz.jei.transfer.BasicRecipeTransferHandlerServer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketAssemblyTerminalRecipeTransfer extends ServerPacket
{
	public int blueprintMapping, blueprintSlot;
	public Map<Integer, Integer> recipeMap;
	public List<Integer> craftingSlots;
	public List<Integer> inventorySlots;
	private boolean maxTransfer;
	private boolean requireCompleteSets;

	public PacketAssemblyTerminalRecipeTransfer() {}

	public PacketAssemblyTerminalRecipeTransfer(int blueprintMapping, int blueprintSlot, Map<Integer, Integer> recipeMap, List<Integer> craftingSlots, List<Integer> inventorySlots, boolean maxTransfer, boolean requireCompleteSets)
	{
		this.blueprintMapping = blueprintMapping;
		this.blueprintSlot = blueprintSlot;
		this.recipeMap = recipeMap;
		this.craftingSlots = craftingSlots;
		this.inventorySlots = inventorySlots;
		this.maxTransfer = maxTransfer;
		this.requireCompleteSets = requireCompleteSets;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		blueprintMapping = ByteBufUtils.readVarInt(buf, 5);
		blueprintSlot = ByteBufUtils.readVarInt(buf, 5);
		int recipeMapSize = ByteBufUtils.readVarInt(buf, 5);
		recipeMap = new HashMap<>(recipeMapSize);
		for (int i = 0; i < recipeMapSize; i++) recipeMap.put(ByteBufUtils.readVarInt(buf, 5), ByteBufUtils.readVarInt(buf, 5));
		int craftingSlotsSize = ByteBufUtils.readVarInt(buf, 5);
		craftingSlots = new ArrayList<>(craftingSlotsSize);
		for (int i = 0; i < craftingSlotsSize; i++) craftingSlots.add(ByteBufUtils.readVarInt(buf, 5));
		int inventorySlotsSize = ByteBufUtils.readVarInt(buf, 5);
		inventorySlots = new ArrayList<>(inventorySlotsSize);
		for (int i = 0; i < inventorySlotsSize; i++) inventorySlots.add(ByteBufUtils.readVarInt(buf, 5));
		maxTransfer = buf.readBoolean();
		requireCompleteSets = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarInt(buf, blueprintMapping, 5);
		ByteBufUtils.writeVarInt(buf, blueprintSlot, 5);
		ByteBufUtils.writeVarInt(buf, recipeMap.size(), 5);
		for (Map.Entry<Integer, Integer> recipeMapEntry : recipeMap.entrySet())
		{
			ByteBufUtils.writeVarInt(buf, recipeMapEntry.getKey(), 5);
			ByteBufUtils.writeVarInt(buf, recipeMapEntry.getValue(), 5);
		}
		ByteBufUtils.writeVarInt(buf, craftingSlots.size(), 5);
		for (Integer craftingSlot : craftingSlots) ByteBufUtils.writeVarInt(buf, craftingSlot, 5);
		ByteBufUtils.writeVarInt(buf, inventorySlots.size(), 5);
		for (Integer inventorySlot : inventorySlots) ByteBufUtils.writeVarInt(buf, inventorySlot, 5);
		buf.writeBoolean(maxTransfer);
		buf.writeBoolean(requireCompleteSets);
	}

	@Override
	public void handleServerSide(EntityPlayerMP player)
	{
		BasicRecipeTransferHandlerServer.setItems(player, Collections.singletonMap(0, blueprintMapping), Collections.singletonList(blueprintSlot), inventorySlots, maxTransfer, requireCompleteSets); //first set blueprint
		BasicRecipeTransferHandlerServer.setItems(player, recipeMap, craftingSlots, inventorySlots, maxTransfer, requireCompleteSets); //then set crafting
	}

	public static class Handler extends ServerPacket.Handler<PacketAssemblyTerminalRecipeTransfer> {}
}
