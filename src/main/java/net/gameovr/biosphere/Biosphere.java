package net.gameovr.biosphere;

import net.gameovr.biosphere.helpers.ModHelper;
import net.gameovr.biosphere.proxy.CommonProxy;
import net.minecraft.world.WorldType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = ModHelper.MOD_ID, name = ModHelper.NAME, version = ModHelper.VERSION, acceptedMinecraftVersions = ModHelper.ACCEPTED_VERSIONS)
public class Biosphere {

    @Instance
    public static Biosphere instance;

    public static Logger logger;

    @SidedProxy(clientSide = ModHelper.CLIENT_PROXY_CLASS, serverSide = ModHelper.SERVER_PROXY_CLASS)
    public static CommonProxy proxy;

    public static WorldType biosphereWorldType;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
