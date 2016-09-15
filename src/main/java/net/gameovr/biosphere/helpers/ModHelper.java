package net.gameovr.biosphere.helpers;

import net.gameovr.biosphere.BiosphereEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ModHelper {

    public static final String MOD_ID = "biosphere";
    public static final String NAME = "Gameovr biosphere Mod";
    public static final String VERSION = "1.0 ALPHA";
    public static final String ACCEPTED_VERSIONS = "[1.9,)";

    public static final String CLIENT_PROXY_CLASS = "net.gameovr.biosphere.proxy.ClientProxy";
    public static final String SERVER_PROXY_CLASS = "net.gameovr.biosphere.proxy.ServerProxy";

    public static final String DIMENSION_TYPE = "Biosphere";
    public static final int DIMENSION_ID = 99;

    public static void registerEventListeners(){

        MinecraftForge.EVENT_BUS.register(new BiosphereEventHandler());

    }

}


