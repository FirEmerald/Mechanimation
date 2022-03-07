package firemerald.mechanimation.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import firemerald.api.betterscreens.EnumTextAlignment;
import firemerald.api.betterscreens.GuiBetterScreen;
import firemerald.api.betterscreens.IGuiElement;
import firemerald.api.betterscreens.components.Button;
import firemerald.api.betterscreens.components.decoration.FloatingText;
import firemerald.api.betterscreens.components.scrolling.ScrollBar;
import firemerald.api.betterscreens.components.scrolling.ScrollableComponentPane;
import firemerald.api.betterscreens.components.text.BetterTextField;
import firemerald.api.config.ConfigValueBoolean;
import firemerald.api.config.ConfigValueFloat;
import firemerald.api.config.ConfigValueInt;
import firemerald.api.core.client.Translator;
import firemerald.mechanimation.Main;
import firemerald.mechanimation.config.ClientConfig;
import firemerald.mechanimation.config.CommonConfig;
import firemerald.mechanimation.networking.client.ServerSettingsSyncPacket;
import firemerald.mechanimation.util.BoolReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

@SuppressWarnings("unused")
public class GuiModOptions extends GuiBetterScreen
{
	public static interface IOption
	{
		public boolean isChanged();

		public void reset();
	}

	public static class BooleanOption extends Button implements IOption
	{
		private final boolean original;
		public final ConfigValueBoolean option;

		public BooleanOption(int x, int y, int w, int h, ConfigValueBoolean option, String translationKey)
		{
			super(x, y, w, h, Translator.format(translationKey, option.val), null);
			this.onClick = () -> this.displayString = Translator.format(translationKey, option.val = !option.val);
			this.option = option;
			this.original = option.val;
		}

		@Override
		public boolean isChanged()
		{
			return option.val != original;
		}

		@Override
		public void reset()
		{
			option.val = original;
		}
	}

	public static class IntegerOption extends BetterTextField implements IOption
	{
		private final int original;
		public final ConfigValueInt option;

		public IntegerOption(int x, int y, int w, int h, ConfigValueInt option)
		{
			super(-1, Minecraft.getMinecraft().fontRenderer, x, y, w, h, Integer.toString(option.val), (Predicate<String>) null);
			this.onChanged = str -> {
				try
				{
					int val = Integer.parseInt(str);
					if (val >= option.min && val <= option.max)
					{
						option.val = val;
						return true;
					}
					else return false;
				}
				catch (NumberFormatException e)
				{
					return false;
				}
			};
			this.option = option;
			this.original = option.val;
		}

		@Override
		public boolean isChanged()
		{
			return option.val != original;
		}

		@Override
		public void reset()
		{
			option.val = original;
		}
	}

	public static class FloatOption extends BetterTextField implements IOption
	{
		private final float original;
		public final ConfigValueFloat option;

		public FloatOption(int x, int y, int w, int h, ConfigValueFloat option)
		{
			super(-1, Minecraft.getMinecraft().fontRenderer, x, y, w, h, Float.toString(option.val), (Predicate<String>) null);
			this.onChanged = str -> {
				try
				{
					float val = Float.parseFloat(str);
					if (val >= option.min && val <= option.max)
					{
						option.val = val;
						return true;
					}
					else return false;
				}
				catch (NumberFormatException e)
				{
					return false;
				}
			};
			this.option = option;
			this.original = option.val;
		}

		@Override
		public boolean isChanged()
		{
			return option.val != original;
		}

		@Override
		public void reset()
		{
			option.val = original;
		}
	}

	private final Minecraft mc;
	private final GuiScreen parentGuiScreen;

	private final ScrollableComponentPane pane;
	private final ScrollBar scrollBar;
	private final FloatingText optionsLabel;

	private final FloatingText clientOptionsLabel;
	private final BooleanOption showItems;

	private final FloatingText serverOptionsLabel;

	private final Button okay, cancel;
	private final List<IOption> clientOptions = new ArrayList<>();
	private final List<IOption> commonOptions = new ArrayList<>();

	public GuiModOptions(Minecraft minecraft, GuiScreen screen)
	{
		this.mc = minecraft;
		this.parentGuiScreen = screen;
		ClientConfig clientConfig = ClientConfig.INSTANCE;
		CommonConfig commonConfig = CommonConfig.INSTANCE;
		this.addElement(optionsLabel = new FloatingText(0, 0, 336, 20, mc.fontRenderer, Translator.translate("options.mechanimation.options"), EnumTextAlignment.CENTER));
		this.addElement(pane = new ScrollableComponentPane(10, 24, 320, 336));
		this.addElement(scrollBar = new ScrollBar(320, 24, 336, 336, pane));
		int y = 0;
		pane.addElement(clientOptionsLabel = new FloatingText(0, y, 310, y + 20, mc.fontRenderer, Translator.translate("options.mechanimation.client"), EnumTextAlignment.CENTER));
		y += 24;
		this.addClient(showItems = new BooleanOption(0, y, 150, 20, clientConfig.showItems, "options.mechanimation.showitems"));
		y += 24;
		pane.addElement(serverOptionsLabel = new FloatingText(0, y, 310, y + 20, mc.fontRenderer, Translator.translate("options.mechanimation.server"), EnumTextAlignment.CENTER));
		y += 24;
		//Button(int x, int y, int widthIn, int heightIn, String buttonText, Runnable onClick)
		this.addElement(okay = new Button(0, 340, 163, 20, Translator.format("okay"), () -> {
			BoolReference save = new BoolReference(false);
			clientOptions.forEach(o -> save.or(o.isChanged()));
			if (save.val) clientConfig.saveConfig();
			save.val = false;
			commonOptions.forEach(o -> save.or(o.isChanged()));
			if (save.val) commonConfig.saveConfig();
			if (FMLCommonHandler.instance().getSide().isClient()) //resync settings in a singleplayer/LAN world
			{
				IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();
				if (server != null)
				{
					ServerSettingsSyncPacket packet = new ServerSettingsSyncPacket(commonConfig);
					server.getPlayerList().getPlayers().forEach(player -> Main.network().sendTo(packet, player));
				}
			}
			this.mc.displayGuiScreen(parentGuiScreen);
		})); //TODO format
		this.addElement(cancel = new Button(173, 340, 336, 20, Translator.format("cancel"), () -> {
			clientOptions.forEach(IOption::reset);
			commonOptions.forEach(IOption::reset);
			this.mc.displayGuiScreen(parentGuiScreen);
		})); //TODO format
		pane.updateComponentSize();
		pane.updateScrollSize();
	}

	private <T extends IGuiElement & IOption> void addClient(T element)
	{
		pane.addElement(element);
		clientOptions.add(element);
	}

	private <T extends IGuiElement & IOption> void addCommon(T element)
	{
		pane.addElement(element);
		commonOptions.add(element);
	}

	@Override
    public void initGui()
    {
		int offX, offY;
		int maxH = 48 + pane.height;
		int paneY2;
		if (this.height > maxH)
		{
			offY = (this.height - maxH) / 2;
			paneY2 = offY + 24 + pane.height;
		}
		else
		{
			offY = 0;
			paneY2 = this.height - 24;
		}
		offX = (this.width - 336) / 2;
		optionsLabel.setSize(offX, offY, offX + 336, offY + 20);
		pane.setSize(offX + 10, offY + 24, offX + 320, paneY2);
		scrollBar.setSize(offX + 320, offY + 24, offX + 336, paneY2);
		okay.setSize(offX, paneY2 + 4, offX + 163, paneY2 + 24);
		cancel.setSize(offX + 173, paneY2 + 4, offX + 336, paneY2 + 24);
		pane.updateScrollSize();
    }

	@Override
	public void render(Minecraft mc, int mx, int my, float partialTicks, boolean canHover)
	{
		this.drawDefaultBackground();
		super.render(mc, mx, my, partialTicks, canHover);
	}
}