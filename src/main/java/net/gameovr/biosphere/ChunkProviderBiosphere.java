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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChunkProviderBiosphere implements IChunkGenerator {
    private Random rand;
    private World world;
    private int WORLD_MAX_Y;
    private IBlockState bufferBlockState;
    private int minX;
    private int maxX;
    private int minZ;
    private int maxZ;

    public ChunkProviderBiosphere(World world, long seed) {
        this.world = world;
        rand = new Random(seed);
        WORLD_MAX_Y = world.getHeight();

        bufferBlockState = ModConfig.bufferBlock;
    }

    @Override
    public Chunk provideChunk(int chunkX, int chunkZ) {

        if (chunkX < minX) minX = chunkX;
        if (chunkX > maxX) maxX = chunkX;
        if (chunkZ < minZ) minZ = chunkZ;
        if (chunkZ > maxZ) maxZ = chunkZ;

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
        if (!(world.getSpawnPoint().toLong() == new BlockPos(0,0,0).toLong())) {
            if (world.getChunkFromChunkCoords(chunkX,chunkZ) == world.getChunkFromBlockCoords(world.getSpawnPoint())) {
                generateSphere(chunkX, chunkZ);
                System.out.println(minX + ", " + minZ + ", " + maxX + ", " + maxZ);
                System.out.println(world.getSpawnPoint());
            }
        }
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
        System.out.println(world.getSpawnPoint());
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
        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        Sphere sphere = new Sphere(world.getSpawnPoint(),15);

        for (int blockX = 0; blockX < 16; ++blockX) {
            for (int blockZ = 0; blockZ < 16; ++blockZ) {
                for (int blockY = 0; blockY < WORLD_MAX_Y; ++blockY) {
                    if (sphere.getDistanceFromOrigin((chunkX * 16) + blockX, blockY, (chunkZ * 16) +blockZ) == 0) chunk.setBlockState(new BlockPos(blockX,blockY,blockZ),Blocks.GRASS.getDefaultState());
                    if (sphere.getDistanceFromOrigin((chunkX * 16) + blockX, blockY, (chunkZ * 16) +blockZ) == sphere.getRadius()){
                        chunk.setBlockState(new BlockPos(blockX,blockY,blockZ),Blocks.GLASS.getDefaultState());
                    }
                }
            }
        }
    }
}
