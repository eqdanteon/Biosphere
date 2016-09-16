package net.gameovr.biosphere.proxy;

import net.gameovr.biosphere.Biosphere;
import net.gameovr.biosphere.BiosphereWorldProvider;
import net.gameovr.biosphere.BiosphereWorldType;
import net.gameovr.biosphere.SphereManager;
import net.gameovr.biosphere.config.ModConfig;
import net.gameovr.biosphere.helpers.ModHelper;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        ModConfig.config = new Configuration(event.getSuggestedConfigurationFile());
        ModConfig.config.load();
        ModConfig.processConfig(ModConfig.config);
        ModConfig.config.save();


    }
    public void init(FMLInitializationEvent event) {
        Biosphere.biosphereWorldType = new BiosphereWorldType("biosphere");

        DimensionManager.unregisterDimension(0);
        DimensionType bioOverworld = DimensionType.register("Overworld", "", 0, BiosphereWorldProvider.class, false );
        DimensionManager.registerDimension(0, bioOverworld);
        ModHelper.registerEventListeners();


    }
    public void postInit(FMLPostInitializationEvent event) {

    }
}