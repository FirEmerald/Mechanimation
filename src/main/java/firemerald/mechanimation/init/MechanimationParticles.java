package firemerald.mechanimation.init;

import java.util.Map;

import com.google.common.collect.Maps;

import firemerald.api.core.networking.ClientPacket;
import firemerald.mechanimation.Main;
import firemerald.mechanimation.networking.client.MechanimationParticleSpawnPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MechanimationParticles
{
    private static final Map<Integer, IParticleFactory> PARTICLE_TYPES = Maps.<Integer, IParticleFactory>newHashMap();

    public static void registerParticleTypes()
    {
    	//PARTICLE_TYPES.put(ParticleDiffusion.PARTICLE_ID, new ParticleDiffusion.Factory());
    }

    public static void spawnParticleServer(int id, World world, boolean ignoreRange, boolean minParticles, double x, double y, double z, double mX, double mY, double mZ, int... args)
    {
    	MechanimationParticleSpawnPacket packet = new MechanimationParticleSpawnPacket(id, x, y, z, mX, mY, mZ, ignoreRange, minParticles, args);
    	world.playerEntities.forEach(player -> {
    		if (player instanceof EntityPlayerMP) sendPacketWithinDistance((EntityPlayerMP) player, ignoreRange, x, y, z, packet);
    	});
    }

    private static void sendPacketWithinDistance(EntityPlayerMP player, boolean longDistance, double x, double y, double z, ClientPacket packetIn)
    {
        BlockPos blockpos = player.getPosition();
        double disSqr = blockpos.distanceSq(x, y, z);
        if (disSqr <= 1024.0D || longDistance && disSqr <= 262144.0D) Main.network().sendTo(packetIn, player);
    }

    @SideOnly(Side.CLIENT)
    public static Particle spawn(int id, World world, double x, double y, double z, double mX, double mY, double mZ, int... args)
    {
    	IParticleFactory factory = PARTICLE_TYPES.get(id);
    	if (factory != null)
    	{
    		Particle particle = factory.createParticle(id, world, x, y, z, mX, mY, mZ, args);
    		if (particle != null) Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    		return particle;
    	}
    	else return null;
    }

    @SideOnly(Side.CLIENT)
    public static void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        spawnParticle(particleID, ignoreRange, false, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    @SideOnly(Side.CLIENT)
    public static void spawnParticle(int id, boolean ignoreRange, boolean minParticles, final double x, final double y, final double z, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        try
        {
            spawnParticleImp(id, ignoreRange, minParticles, x, y, z, xSpeed, ySpeed, zSpeed, parameters);
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while adding Mechanimation particle");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being added");
            crashreportcategory.addCrashSection("ID", Integer.valueOf(id));
            if (parameters != null) crashreportcategory.addCrashSection("Parameters", parameters);
            crashreportcategory.addDetail("Position", new ICrashReportDetail<String>()
            {
                @Override
				public String call() throws Exception
                {
                    return CrashReportCategory.getCoordinateInfo(x, y, z);
                }
            });
            throw new ReportedException(crashreport);
        }
    }

    @SideOnly(Side.CLIENT)
    private static Particle spawnParticleImp(int particleID, boolean ignoreRange, boolean minParticles, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
    	Minecraft mc = Minecraft.getMinecraft();
        Entity entity = mc.getRenderViewEntity();
        if (mc != null && entity != null && mc.effectRenderer != null)
        {
            int particleLevel = calculateParticleLevel(mc, entity.world, minParticles);
            double dX = entity.posX - xCoord;
            double dY = entity.posY - yCoord;
            double dZ = entity.posZ - zCoord;
            if (ignoreRange) return spawn(particleID, entity.world, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
            else if (dX * dX + dY * dY + dZ * dZ > 1024.0D) return null;
            else return particleLevel > 1 ? null : spawn(particleID, mc.world, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
        }
        else return null;
    }

    @SideOnly(Side.CLIENT)
    private static int calculateParticleLevel(Minecraft mc, World world, boolean minParticles)
    {
        int setting = mc.gameSettings.particleSetting;
        if (minParticles && setting == 2 && world.rand.nextInt(10) == 0) setting = 1;
        if (setting == 1 && world.rand.nextInt(3) == 0) setting = 2;
        return setting;
    }
}