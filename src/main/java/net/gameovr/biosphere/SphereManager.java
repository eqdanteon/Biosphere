package net.gameovr.biosphere;

import net.gameovr.biosphere.config.ModConfig;
import net.gameovr.biosphere.helpers.BioLogger;
import net.gameovr.biosphere.helpers.ChunkCalculator;
import net.gameovr.biosphere.helpers.ChunkCoordinate;
import net.minecraft.util.math.BlockPos;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class SphereManager {

    Random rand;
    Sphere nearestOrigin = null;


    public static Sphere getNearestSphere(BlockPos pos) {

        Sphere closestSphere = null;
        int minDistance = Integer.MAX_VALUE;

        for (Sphere sphere : BiosphereWorldType.spheres) {
            if (sphere.getDistanceFromOrigin(pos) < minDistance) {
                minDistance = sphere.getDistanceFromOrigin(pos);
                closestSphere = sphere;

            }
        }
        return closestSphere;
    }

    public void GenerateSpheres(long seed) {

        Biosphere.logger.info("Starting Generate Spheres...");

        rand = new Random(seed);
        // bounds of world
        int left = -12;
        int right = 12;
        int top = -12;
        int bottom = 12;

        for (int x = left; x < right+1; x++){
            for (int z = top; z < bottom+1; z++){
               genSphereByChunk(x, z);
            }

        }

        BiosphereWorldType.spawnPoint = nearestOrigin.origin;
        BioLogger.writeSphereListToDisk();

        Biosphere.logger.info("Sphere list created: " + BiosphereWorldType.spheres.size() + " spheres added.");

    }

    private Sphere genSphereByChunk(int chunkX, int chunkZ){

        int radius = rand.nextInt((ModConfig.maxSphereRadius-16)+1)+16;
        int yPosMin = 2 + radius;
        int yPosMax = 256 - radius;

        int blockX = (chunkX * 16) + rand.nextInt(16);
        int blockZ = (chunkZ * 16) + rand.nextInt(16);
        int blockY = yPosMin + (int)(rand.nextFloat() * (yPosMax - yPosMin));

        BlockPos randomPos = new BlockPos(blockX, blockY, blockZ);
        Sphere nearestSphere;
        BlockPos worldOrigin = new BlockPos(0, 128, 0);

        int subChunkY = ChunkCalculator.getSubChunkYfromBlockY(randomPos.getY());
        Sphere genSphere = new Sphere(randomPos, radius, new ChunkCoordinate(chunkX, subChunkY, chunkZ));

        if (!BiosphereWorldType.spheres.isEmpty()) {
            nearestSphere = getNearestSphere(randomPos);
            if (nearestSphere.getDistanceFromOrigin(randomPos) > ModConfig.minDistanceApart + radius) {

                BiosphereWorldType.spheres.add(genSphere);

                if (nearestOrigin != null){

                    if(genSphere.getDistanceFromOrigin(worldOrigin) < nearestOrigin.getDistanceFromOrigin(worldOrigin)){
                        nearestOrigin = genSphere;
                    }

                }else{

                    nearestOrigin = genSphere;
                }



            }
        } else {
            //genSphere.originChunk = new ChunkCoordinate(chunkX, subChunkY, chunkZ);
            BiosphereWorldType.spheres.add(genSphere);

        }


        return genSphere;

    }



}
