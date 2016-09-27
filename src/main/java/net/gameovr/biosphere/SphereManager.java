package net.gameovr.biosphere;

import net.gameovr.biosphere.config.ModConfig;
import net.gameovr.biosphere.helpers.BioLogger;
import net.gameovr.biosphere.helpers.ChunkCalculator;
import net.gameovr.biosphere.helpers.ChunkCoordinate;

import net.minecraft.util.math.*;


import java.util.Random;

public class SphereManager {

    Random rand;
    Sphere nearestOrigin = null;


    public static int getDistanceBetweenPoints(BlockPos p1, BlockPos p2) {
        int x1 = p1.getX();
        int y1 = p1.getY();
        int z1 = p1.getZ();
        int x2 = p2.getX();
        int y2 = p2.getY();
        int z2 = p2.getZ();
        return (int)Math.round(Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1) + (z2 - z1) * (z2 - z1)));
    }

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

                //set Bridge Connection Point
                Vec3i line = genSphere.getOrigin().subtract(nearestSphere.getOrigin());
                Vec3d line3d = new Vec3d(line);

                Vec3d normalized =  line3d.normalize();

                BlockPos currentSpherePoint = new BlockPos(genSphere.getOrigin().getX() - normalized.xCoord*genSphere.getRadius(), genSphere.getSphereGroundLevel(), genSphere.getOrigin().getZ() - normalized.zCoord*genSphere.getRadius());
                BlockPos nearestSpherePoint = new BlockPos(nearestSphere.getOrigin().getX() + normalized.xCoord*nearestSphere.getRadius(), nearestSphere.getSphereGroundLevel(), nearestSphere.getOrigin().getZ() + normalized.zCoord*nearestSphere.getRadius());

                // are the points within a 45 degree angle?
                float angle = (float) Math.toDegrees(Math.atan2(nearestSpherePoint.getY() - currentSpherePoint.getY(), nearestSpherePoint.getX() - currentSpherePoint.getX()));
                if(angle < 0){
                    angle += 360;
                }
                if((angle <= 135 && angle >= 45) || (angle <= 315 && angle >= 225 )) {

                    genSphere.startBridgeConnection = currentSpherePoint;
                    genSphere.endBridgeConnection = nearestSpherePoint;

                    nearestSphere.startBridgeConnection = nearestSpherePoint;
                    nearestSphere.endBridgeConnection = currentSpherePoint;

                }

                    // Add Sphere to World List
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
            BiosphereWorldType.spheres.add(genSphere);

        }


        return genSphere;

    }



}
