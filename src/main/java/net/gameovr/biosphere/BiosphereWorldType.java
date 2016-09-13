package net.gameovr.biosphere;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkGenerator;

public class BiosphereWorldType extends WorldType {

    public BiosphereWorldType(String name) {
        super(name);
    }

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