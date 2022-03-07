package buildcraft.api.transport;

import buildcraft.api.transport.pipe.IPipeHolder;
import net.minecraft.item.EnumDyeColor;

public interface IWireManager {

    IPipeHolder getHolder();

    void updateBetweens(boolean recursive);

    EnumDyeColor getColorOfPart(EnumWirePart part);

    EnumDyeColor removePart(EnumWirePart part);

    boolean addPart(EnumWirePart part, EnumDyeColor colour);

    boolean hasPartOfColor(EnumDyeColor color);

    boolean isPowered(EnumWirePart part);

    boolean isAnyPowered(EnumDyeColor color);
}
