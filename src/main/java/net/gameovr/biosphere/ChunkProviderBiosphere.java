package net.gameovr.biosphere;

import net.gameovr.biosphere.config.ModConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ChunkProviderBiosphere implements IChunkGenerator {
    private Random rand;
    private World world;
    private int WORLD_MAX_Y;
    private IBlockState bufferBlockState;

    public ChunkProviderBiosphere(World world, long seed) {
        this.world = world;
        rand = new Random(seed);
        WORLD_MAX_Y = world.getHeight();

        bufferBlockState = ModConfig.bufferBlock;
        System.out.println("Spawn point:" + world.getSpawnPoint().toString());
    }

    @Override
    public Chunk provideChunk(int chunkX, int chunkZ) {
        ChunkPrimer chunkprimer = new ChunkPrimer();

        if (ModConfig.worldFloor) {
            setWorldFloor(chunkprimer);
            if (ModConfig.worldFloorBuffer) {
                setWorldFloorBuffer(chunkprimer);
            }
        }

        Chunk chunk = new Chunk(world, chunkprimer, chunkX, chunkZ);

        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public void populate(int chunkX, int chunkZ) {

        generateSphere(chunkX, chunkZ);
    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int chunkX, int chunkZ) {
        return false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return null;
    }

    @Nullable
    @Override
    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
        return null;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int chunkX, int chunkZ) {

    }

    public void setWorldFloor(ChunkPrimer chunkprimer) {
        for (int blockX = 0; blockX < 16; ++blockX) {
            for (int blockZ = 0; blockZ < 16; ++blockZ) {
                chunkprimer.setBlockState(blockX,0,blockZ,Blocks.BEDROCK.getDefaultState());
            }
        }
    }

    public void setWorldFloorBuffer(ChunkPrimer chunkprimer) {
        for (int blockX = 0; blockX < 16; ++blockX) {
            for (int blockZ = 0; blockZ < 16; ++blockZ) {
                for (int blockY = 1; blockY < ModConfig.bufferThickness + 1; ++blockY) {
                    chunkprimer.setBlockState(blockX, blockY, blockZ, bufferBlockState);
                }
            }
        }
    }

    public void generateSphere(int chunkX, int chunkZ) {
        for (int blockX = 0; blockX < 16; ++blockX) {
            for (int blockZ = 0; blockZ < 16; ++blockZ) {
                if (chunkX == 0 && chunkZ == 0) {
                    world.setBlockState(new BlockPos(blockX, WORLD_MAX_Y/4 , blockZ), Blocks.STONE.getDefaultState());
                    world.setSpawnPoint(new BlockPos(0,(WORLD_MAX_Y/4) + 1,0));
                }
            }
        }
    }
}
