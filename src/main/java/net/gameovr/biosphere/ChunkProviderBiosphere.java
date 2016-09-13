package net.gameovr.biosphere;

import net.gameovr.biosphere.config.ModConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
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
    private SphereManager spheremanager = new SphereManager();

    public ChunkProviderBiosphere(World world, long seed) {
        this.world = world;
        rand = new Random(seed);
        WORLD_MAX_Y = world.getHeight();

        bufferBlockState = ModConfig.bufferBlock;

        //Generate list of spheres
        SphereManager sphereManager = new SphereManager();
        sphereManager.GenerateSpheres(seed);

    }

    public Chunk provideChunk(int chunkX, int chunkZ) {

        ChunkPrimer chunkprimer = new ChunkPrimer();

        // start off by adding basic terrain: air, water, bedrock
        setWorldFloorOptions(chunkprimer, ModConfig.worldFloor, ModConfig.worldFloorBuffer);


        // Add spheres to world
        drawSpheres(chunkX, chunkZ, chunkprimer);


        Chunk chunk = new Chunk(world, chunkprimer, chunkX, chunkZ);

        chunk.generateSkylightMap();
        return chunk;
    }

    public void populate(int chunkX, int chunkZ) {


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

    public void drawSpheres(int chunkX, int chunkZ, ChunkPrimer chunkPrimer) {


        // divide chunk into 16 subchunks in y direction, index as iy
        for (int iy = 0; iy < 16; ++iy) {

            BlockPos referenceBlock = new BlockPos((chunkX * 16) + 8, (iy * 16) + 8, (chunkZ * 16) + 8);
            Sphere nearestSphere = spheremanager.getNearestSphere(referenceBlock);

            // subchunk is 16 blocks high in y direction, index as jy
            for (int jy = 0; jy < 16; ++jy) {

                // subchunk is 16 blocks long in x direction, index as jx
                for (int jx = 0; jx < 16; ++jx) {

                    // subchunk is 16 blocks long in z direction, index as jz
                    for (int jz = 0; jz < 16; ++jz) {

                        int currentBlockX = (chunkX * 16) + jx;
                        int currentBlockY = (iy * 16) + jy;
                        int currentBlockZ = (chunkZ * 16) + jz;

                        //TODO: figure out Exception in server tick loop sometimes
                        if (nearestSphere.getDistanceFromOrigin(currentBlockX, currentBlockY, currentBlockZ) == nearestSphere.getRadius()) {
                            chunkPrimer.setBlockState(jx, currentBlockY, jz, Blocks.GLASS.getDefaultState());
                           // chunk.setBlockState(new BlockPos(jx, currentBlockY, jz), Blocks.GLASS.getDefaultState());
                        }
                        if (nearestSphere.getDistanceFromOrigin(currentBlockX, currentBlockY, currentBlockZ) < nearestSphere.getRadius() && currentBlockY < nearestSphere.getOrigin().getY() - 2) {
                           //chunk.setBlockState(new BlockPos(jx, currentBlockY, jz), Blocks.STONE.getDefaultState());
                            chunkPrimer.setBlockState(jx, currentBlockY, jz, Blocks.STONE.getDefaultState());
                        }
                    }
                }
            }
        }
    }

/*    public void drawSpheres(int chunkX, int chunkZ, ChunkPrimer chunkPrimer) {

        //Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        BlockPos chunkCenter;
        Sphere nearestSphere = null;

        // Divide chunk into 16 subchunks
        for (int y = 0; y < 16; ++y) {
            int yMin = y * 16;
            int yMax = yMin + 16;

            // this is the Y block range of this section
            // get the nearest sphere from the y section center
            chunkCenter = spheremanager.GetCenterBlockFromChunk(chunkX, chunkZ, yMin + 8);
            nearestSphere = spheremanager.getNearestSphere(chunkCenter);

            // subchunk is 16 blocks high in y direction
            for (int yPlane = 0; yPlane < 16; ++yPlane) {

                // scan chunk x and z based on block location
                for (int blockX = 0; blockX < 16; ++blockX) {

                    for (int blockZ = 0; blockZ < 16; ++blockZ) {

                            // current BlockPos based on world block position
                            BlockPos currentBlockPos = new BlockPos(SphereManager.calcBlockMin(chunkX) + blockX, yPlane, SphereManager.calcBlockMin(chunkZ) + blockZ);

                            //TODO: figure out Exception in server tick loop sometimes
                            // setBlockState position based on position within chunk
                            if (nearestSphere.getDistanceFromOrigin(currentBlockPos.getX(), currentBlockPos.getY(), currentBlockPos.getZ()) == nearestSphere.getRadius()) {
                                //chunk.setBlockState(new BlockPos(currentBlockPos.getX(), currentBlockPos.getY(), currentBlockPos.getZ()), Blocks.GLASS.getDefaultState());
                                chunkPrimer.setBlockState(blockX, y*16+yPlane, blockZ, Blocks.GLASS.getDefaultState());

                            }
                            if (nearestSphere.getDistanceFromOrigin(currentBlockPos.getX(), currentBlockPos.getY(), currentBlockPos.getZ()) < nearestSphere.getRadius() && currentBlockPos.getY() < nearestSphere.getOrigin().getY() - 2) {
                                //chunk.setBlockState(new BlockPos(currentBlockPos.getX(), currentBlockPos.getY(), currentBlockPos.getZ()), Blocks.STONE.getDefaultState());
                                chunkPrimer.setBlockState(blockX, y*16+yPlane, blockZ, Blocks.STONE.getDefaultState());
                            }
                    }
                }

            }
        }

    }*/


} //End Class
