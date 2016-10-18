package net.gameovr.biosphere.config;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModConfig {

    //Config file variables
    public static Configuration config;

    // Mod Configuration Variables

    // World
    public static boolean worldFloor = true;
    public static boolean worldFloorBuffer = true;
    public static String bufferLiquid = Blocks.WATER.getRegistryName().toString();
    public static IBlockState bufferBlock;
    public static int bufferThickness = 1;

    // Sphere Generation
    public static int maxSphereRadius = 32;
    public static int minDistanceApart = 64;

    public static void processConfig(Configuration config) {

        // World
        worldFloor = config.get(config.CATEGORY_GENERAL, "worldFloor", worldFloor, "World floor").getBoolean(worldFloor);
        worldFloorBuffer = config.get(config.CATEGORY_GENERAL, "worldFloorBuffer", worldFloorBuffer, "World floor has a liquid buffer").getBoolean(worldFloorBuffer);
        bufferLiquid = config.get(config.CATEGORY_GENERAL, "bufferLiquid", bufferLiquid, "Liquid for world floor buffer").getString();
        String[] bufferLiquidParts = bufferLiquid.split(":");
        if (GameRegistry.findBlock(bufferLiquidParts[0], bufferLiquidParts[1]).getRegistryName().toString().equals(bufferLiquid)) {
            bufferBlock = GameRegistry.findBlock(bufferLiquidParts[0], bufferLiquidParts[1]).getDefaultState();
        }
        else {
            System.out.println("World floor buffer liquid in configuration file is invalid.  Using water as default.");
            bufferLiquid = Blocks.WATER.getRegistryName().toString();
            bufferLiquidParts = bufferLiquid.split(":");
            bufferBlock = GameRegistry.findBlock(bufferLiquidParts[0], bufferLiquidParts[1]).getDefaultState();
        }
        bufferThickness = config.get(config.CATEGORY_GENERAL, "bufferThickness", bufferThickness, "Thickness of world floor buffer").getInt(bufferThickness);


        // make sure to validate user input; especially for values used in algorithms
        configValueValidation();
    }

    private static void configValueValidation() {
        // min value for maxSphereRadius
        maxSphereRadius = (maxSphereRadius < 16) ? 16 : maxSphereRadius;
        // max value for maxSphereRadius
        maxSphereRadius = (maxSphereRadius > 126) ? 126 : maxSphereRadius;

    }

}
