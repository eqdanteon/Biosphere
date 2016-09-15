package net.gameovr.biosphere;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkGenerator;

import java.util.ArrayList;
import java.util.Random;

public class BiosphereWorldType extends WorldType {

    public BiosphereWorldType(String name) {
        super(name);
    }

    public static ArrayList<Sphere> spheres = new ArrayList<Sphere>();
    public static Random rand;

    @Override
    public IChunkGenerator getChunkGenerator(World world, String generatorOptions)
    {
        return new ChunkProviderBiosphere(world, world.getSeed());
    }

    @Override
    public void onGUICreateWorldPress() {


        super.onGUICreateWorldPress();

    }
}