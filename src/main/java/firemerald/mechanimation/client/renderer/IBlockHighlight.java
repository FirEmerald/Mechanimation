package firemerald.mechanimation.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

@FunctionalInterface
public interface IBlockHighlight
{
	public static final IBlockHighlight
	SLAB = (player, result, partial) -> {
		GlStateManager.glBegin(GL11.GL_LINES);
		GlStateManager.glVertex3f(-.5f, -.5f, -.5f);
		GlStateManager.glVertex3f(.5f, -.5f, -.5f);
		GlStateManager.glVertex3f(.5f, -.5f, -.5f);
		GlStateManager.glVertex3f(.5f, .5f, -.5f);
		GlStateManager.glVertex3f(.5f, .5f, -.5f);
		GlStateManager.glVertex3f(-.5f, .5f, -.5f);
		GlStateManager.glVertex3f(-.5f, .5f, -.5f);
		GlStateManager.glVertex3f(-.5f, -.5f, -.5f);
		GlStateManager.glVertex3f(-.25f, -.25f, -.5f);
		GlStateManager.glVertex3f(.25f, -.25f, -.5f);
		GlStateManager.glVertex3f(.25f, -.25f, -.5f);
		GlStateManager.glVertex3f(.25f, .25f, -.5f);
		GlStateManager.glVertex3f(.25f, .25f, -.5f);
		GlStateManager.glVertex3f(-.25f, .25f, -.5f);
		GlStateManager.glVertex3f(-.25f, .25f, -.5f);
		GlStateManager.glVertex3f(-.25f, -.25f, -.5f);
		GlStateManager.glVertex3f(-.5f, -.5f, -.5f);
		GlStateManager.glVertex3f(-.25f, -.25f, -.5f);
		GlStateManager.glVertex3f(.5f, -.5f, -.5f);
		GlStateManager.glVertex3f(.25f, -.25f, -.5f);
		GlStateManager.glVertex3f(.5f, .5f, -.5f);
		GlStateManager.glVertex3f(.25f, .25f, -.5f);
		GlStateManager.glVertex3f(-.5f, .5f, -.5f);
		GlStateManager.glVertex3f(-.25f, .25f, -.5f);
		GlStateManager.glEnd();
	},
	STAIRS = (player, result, partial) -> {
		GlStateManager.glBegin(GL11.GL_LINES);
		GlStateManager.glVertex3f(-.5f, -.5f, -.5f);
		GlStateManager.glVertex3f(.5f, -.5f, -.5f);
		GlStateManager.glVertex3f(.5f, -.5f, -.5f);
		GlStateManager.glVertex3f(.5f, .5f, -.5f);
		GlStateManager.glVertex3f(.5f, .5f, -.5f);
		GlStateManager.glVertex3f(-.5f, .5f, -.5f);
		GlStateManager.glVertex3f(-.5f, .5f, -.5f);
		GlStateManager.glVertex3f(-.5f, -.5f, -.5f);
		GlStateManager.glVertex3f(-.25f, -.25f, -.5f);
		GlStateManager.glVertex3f(.25f, .25f, -.5f);
		GlStateManager.glVertex3f(-.25f, .25f, -.5f);
		GlStateManager.glVertex3f(.25f, -.25f, -.5f);
		GlStateManager.glVertex3f(-.5f, -.25f, -.5f);
		GlStateManager.glVertex3f(-.25f, -.25f, -.5f);
		GlStateManager.glVertex3f(-.25f, -.25f, -.5f);
		GlStateManager.glVertex3f(-.25f, -.5f, -.5f);
		GlStateManager.glVertex3f(.5f, -.25f, -.5f);
		GlStateManager.glVertex3f(.25f, -.25f, -.5f);
		GlStateManager.glVertex3f(.25f, -.25f, -.5f);
		GlStateManager.glVertex3f(.25f, -.5f, -.5f);
		GlStateManager.glVertex3f(-.5f, .25f, -.5f);
		GlStateManager.glVertex3f(-.25f, .25f, -.5f);
		GlStateManager.glVertex3f(-.25f, .25f, -.5f);
		GlStateManager.glVertex3f(-.25f, .5f, -.5f);
		GlStateManager.glVertex3f(.5f, .25f, -.5f);
		GlStateManager.glVertex3f(.25f, .25f, -.5f);
		GlStateManager.glVertex3f(.25f, .25f, -.5f);
		GlStateManager.glVertex3f(.25f, .5f, -.5f);
		GlStateManager.glEnd();
	},
	MACHINE = (player, result, partial) -> {
		GlStateManager.glBegin(GL11.GL_LINES);
		GlStateManager.glVertex3f(-.5f, -.5f, -.5f);
		GlStateManager.glVertex3f(.5f, .5f, -.5f);
		GlStateManager.glVertex3f(.5f, -.5f, -.5f);
		GlStateManager.glVertex3f(-.5f, .5f, -.5f);
		GlStateManager.glEnd();
	},
	MACHINE_VERTICAL = (player, result, partial) -> {
		if (result.sideHit.getAxis() == Axis.Y)
		{
			GlStateManager.glBegin(GL11.GL_LINES);
			GlStateManager.glVertex3f(-.5f, -.5f, -.5f);
			GlStateManager.glVertex3f(.5f, .5f, -.5f);
			GlStateManager.glVertex3f(.5f, -.5f, -.5f);
			GlStateManager.glVertex3f(-.5f, .5f, -.5f);
			GlStateManager.glEnd();
		}
	};

	public static void transform(EnumFacing sideHit)
	{
		switch (sideHit)
		{
		case WEST:
			GlStateManager.rotate(-90, 0, 1, 0);
			break;
		case EAST:
			GlStateManager.rotate(90, 0, 1, 0);
			break;
		case UP:
			GlStateManager.rotate(-90, 1, 0, 0);
			break;
		case DOWN:
			GlStateManager.rotate(90, 1, 0, 0);
			break;
		case NORTH:
			GlStateManager.rotate(180, 0, 1, 0);
			break;
		default:
		}
	}

	public default void render(EntityPlayer player, RayTraceResult result, float partial)
	{
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(1.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GlStateManager.color(0f, 0f, 0f, 1f);
		GlStateManager.pushMatrix();
		BlockPos hit = result.getBlockPos();
		double hitX = hit.getX();
		double hitY = hit.getY();
		double hitZ = hit.getZ();
		switch (result.sideHit)
		{
		case WEST:
			hitX = result.hitVec.x - 1.005;
			break;
		case EAST:
			hitX = result.hitVec.x + .005;
			break;
		case DOWN:
			hitY = result.hitVec.y - 1.005;
			break;
		case UP:
			hitY = result.hitVec.y + .005;
			break;
		case NORTH:
			hitZ = result.hitVec.z - 1.005;
			break;
		case SOUTH:
			hitZ = result.hitVec.z + .005;
			break;
		default:
		}
		double posX = player.prevPosX + (player.posX - player.prevPosX) * partial;
		double posY = player.prevPosY + (player.posY - player.prevPosY) * partial;
		double posZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partial;
		GlStateManager.translate(hitX - posX + .5, hitY - posY + .5, hitZ - posZ + .5);
		transform(result.sideHit);
		draw(player, result, partial);
		GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
	}

	public void draw(EntityPlayer player, RayTraceResult result, float partial);
}