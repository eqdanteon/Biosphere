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
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChunkProviderBiosphere implements IChunkGenerator {

    private Random rand;
    private World world;
    private int WORLD_MAX_Y;
    private IBlockState bufferBlockState;
    private SphereManager spheremanager = new SphereManager();

    public ChunkProviderBiosphere(World world, long seed) {
        this.world = world;
        rand = new Random(seed);
        WORLD_MAX_Y = world.getHeight();

        bufferBlockState = ModConfig.bufferBlock;

    }

    public Chunk provideChunk(int chunkX, int chunkZ) {

        ChunkPrimer chunkprimer = new ChunkPrimer();

        setWorldFloorOptions(chunkprimer, ModConfig.worldFloor, ModConfig.worldFloorBuffer);

        generateSpheres(chunkX, chunkZ);

        Chunk chunk = new Chunk(world, chunkprimer, chunkX, chunkZ);

        chunk.generateSkylightMap();
        return chunk;
    }

    public void populate(int chunkX, int chunkZ) {

        drawSpheres(chunkX, chunkZ);
    }

    public boolean generateStructures(Chunk chunkIn, int chunkX, int chunkZ) {
        return false;
    }

    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return null;
    }

    @Nullable
    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
        return null;
    }

    public void recreateStructures(Chunk chunkIn, int chunkX, int chunkZ) {

    }

    public void setWorldFloorOptions(ChunkPrimer chunkprimer, boolean floor, boolean buffer){
        for (int blockX = 0; blockX < 16; ++blockX) {
            for (int blockZ = 0; blockZ < 16; ++blockZ) {
                if(floor){chunkprimer.setBlockState(blockX,0,blockZ,Blocks.BEDROCK.getDefaultState());}
                if(buffer){
                    for (int blockY = 1; blockY < ModConfig.bufferThickness + 1; ++blockY) {
                        chunkprimer.setBlockState(blockX, blockY, blockZ, bufferBlockState);
                    }
                }
            }
        }
    }

    public void generateSpheres(int chunkX, int chunkZ) {


        int radius = rand.nextInt((ModConfig.maxSphereRadius-16)+1)+16;
        int yPosMin = 2 + radius;
        int yPosMax = 254 - radius;

        int blockX = (chunkX * 16) + rand.nextInt(16);
        int blockZ = (chunkZ * 16) + rand.nextInt(16);
        int blockY = rand.nextInt((yPosMax-yPosMin)+1)+ yPosMin;

        BlockPos randomPos = new BlockPos(blockX, blockY, blockZ);
        Sphere nearestSphere;

        if (!spheremanager.spheres.isEmpty()) {
            nearestSphere = spheremanager.getNearestSphere(randomPos);
            if (nearestSphere.getDistanceFromOrigin(randomPos) > ModConfig.minDistanceApart) {
                spheremanager.spheres.add(new Sphere(randomPos, radius));
            }
        } else {
            Sphere sphere = new Sphere(randomPos, radius);
            spheremanager.spheres.add(sphere);
        }
    }

    public void drawSpheres(int chunkX, int chunkZ) {
        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        for (int blockX = 0; blockX < 16; ++blockX) {
            for (int blockZ = 0; blockZ < 16; ++blockZ) {
                for (int blockY = 0; blockY < WORLD_MAX_Y; ++blockY) {
                    BlockPos currentBlockPos = new BlockPos(chunkX * 16 + blockX, blockY, chunkZ * 16 + blockZ);
                    Sphere nearestSphere = spheremanager.getNearestSphere(currentBlockPos);

                    //TODO: figure out Exception in server tick loop sometimes
                    if (nearestSphere.getDistanceFromOrigin(currentBlockPos.getX(), currentBlockPos.getY(), currentBlockPos.getZ()) == nearestSphere.getRadius()){
                        chunk.setBlockState(new BlockPos(blockX, blockY, blockZ),Blocks.GLASS.getDefaultState());
                    }
                    if (nearestSphere.getDistanceFromOrigin(currentBlockPos.getX(), currentBlockPos.getY(), currentBlockPos.getZ()) < nearestSphere.getRadius() && blockY < nearestSphere.getOrigin().getY() - 2) {
                        chunk.setBlockState(new BlockPos(blockX, blockY, blockZ),Blocks.STONE.getDefaultState());
                    }
                }
            }
        }
    }


} //End Class
