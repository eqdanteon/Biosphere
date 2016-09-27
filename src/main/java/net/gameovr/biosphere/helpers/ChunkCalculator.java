package net.gameovr.biosphere.helpers;

import net.gameovr.biosphere.BiosphereWorldType;
import net.gameovr.biosphere.Sphere;
import net.gameovr.biosphere.SphereManager;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class ChunkCalculator {

    public static final int numberofSubChunksY = 16;
    public static final int numberofSubChunksX = 1;
    public static final int numberofSubChunksZ = 1;


    public static BlockPos getBlockPosFromChunkCoordiantes(int chunkX, int index_X, int index_Y_SubChunk, int index_Y, int chunkZ, int index_Z){

        int x = (chunkX * 16) + index_X;
        int y = (index_Y_SubChunk * 16) + index_Y;
        int z = (chunkZ * 16) + index_Z;

        return new BlockPos(x, y, z);
    }

    public static BlockPos getSubChunkCenterPos(int chunkX, int subChunkY, int chunkZ){

        BlockPos chunkEdge = ChunkCalculator.getBlockPosFromChunkCoordiantes(chunkX, 0, subChunkY, 0, chunkZ, 0 );

        return chunkEdge.add(8, 8, 8);
    }

    public static ArrayList<BlockPos> getBlocksFromSubChunk(int chunkX, int subChunkY, int chunkZ){

        ArrayList<BlockPos> blockList= new ArrayList<BlockPos>();

            // subchunk blocks in y direction
            for (int blockY = 0; blockY < 256/numberofSubChunksY; ++blockY) {

                // subchunk blocks in x direction
                for (int blockX = 0; blockX < 16/numberofSubChunksX; ++blockX) {

                    // subchunk blocks in z direction
                    for (int blockZ = 0; blockZ < 16/numberofSubChunksZ; ++blockZ) {

                        BlockPos pos = ChunkCalculator.getBlockPosFromChunkCoordiantes(chunkX, blockX, subChunkY, blockY, chunkZ, blockZ);
                        blockList.add(pos);

                    }
                }
            }


        return blockList;

    }

    public static ArrayList<BlockPos> getBlocksFromSubChunk(ChunkCoordinate chunkCoordinate){
        return getBlocksFromSubChunk(chunkCoordinate.x, chunkCoordinate.y, chunkCoordinate.z);
    }

    // Method determines if a sphere is in this subchunk
    public static SphereChunk getSphereFromChunk(int chunkX, int chunkZ){

        SphereChunk sphereChunk = new SphereChunk();

        // divide chunk sub-chunks in y direction
        for (int subChunkY = 0; subChunkY < numberofSubChunksY; ++subChunkY) {

            BlockPos subChunkCenter = ChunkCalculator.getSubChunkCenterPos(chunkX, subChunkY, chunkZ);
            // get the nearest sphere
            Sphere nearestSphere = SphereManager.getNearestSphere(subChunkCenter);

            if(nearestSphere.getDistanceFromOrigin(subChunkCenter) < 16){
             // sphere is in this subChunk

                    sphereChunk.isSphereInChunk = true;
                    sphereChunk.nearestSphere = nearestSphere;
                    sphereChunk.blocks = ChunkCalculator.getBlocksFromSubChunk(chunkX, subChunkY, chunkZ);

            }
        }

        return sphereChunk;

    }

    public static int getSubChunkYfromBlockY(int blockY){

        return (int)Math.ceil(blockY/numberofSubChunksY);

    }

    public static int getLoopIndexfromBlockXZ(int chunkXZ, int blockXZ){

        // formula: blockXZ = (chunkXZ * 16) + unknown
        return Math.abs(chunkXZ * 16 - blockXZ);

    }

    public static ArrayList<Sphere> getSpheresFromChunk(int chunkX, int chunkZ){

        ArrayList<Sphere> spheresInChunk = new ArrayList<Sphere>();

        if(!BiosphereWorldType.spheres.isEmpty()) {
            for (Sphere s : BiosphereWorldType.spheres) {

                if(s.originChunk.x == chunkX && s.originChunk.z == chunkZ){
                    spheresInChunk.add(s);
                }

            }
        }

        return spheresInChunk;
    }

}
