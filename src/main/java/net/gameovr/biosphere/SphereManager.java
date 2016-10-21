package net.gameovr.biosphere;

import net.gameovr.biosphere.config.ModConfig;
import net.gameovr.biosphere.helpers.BioLogger;
import net.gameovr.biosphere.helpers.ChunkCalculator;
import net.gameovr.biosphere.helpers.ChunkCoordinate;

import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.*;
import scala.tools.nsc.doc.base.comment.Block;


import java.util.ArrayList;
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
            int distance = (int)sphere.getDistanceFromOrigin(pos);

            if (distance < minDistance && distance > 0) {
                minDistance = distance;
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

        connectSpheres();

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
        //int blockY = yPosMin + (int)(rand.nextFloat() * (yPosMax - yPosMin));
        int blockY = 58;

        BlockPos randomPos = new BlockPos(blockX, blockY, blockZ);
        Sphere nearestSphere;
        BlockPos worldOrigin = new BlockPos(0, 128, 0);

        int subChunkY = ChunkCalculator.getSubChunkYfromBlockY(randomPos.getY());
        Sphere genSphere = new Sphere(randomPos, radius, new ChunkCoordinate(chunkX, subChunkY, chunkZ));

        if (!BiosphereWorldType.spheres.isEmpty()) {
            nearestSphere = getNearestSphere(randomPos);
            if (nearestSphere.getDistanceFromOrigin(randomPos) > ModConfig.minDistanceApart + radius) {

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

    private void connectSpheres(){

        for(int i = 0; i < BiosphereWorldType.spheres.size(); i++){

            // current sphere
            Sphere cs = BiosphereWorldType.spheres.get(i);
            Sphere cs1 = getNearestSphere(cs.getOrigin());
            BlockPos startGroundLevel = null;
            BlockPos endGroundLevel = null;
            double minDistance = 100000;
            for (int s = 0; s < cs.Connections.length; s++) {
                for (int e = 0; e < cs1.Connections.length; e++) {
                    double distance = cs.Connections[s].getDistance(cs1.Connections[e].getX(),cs1.Connections[e].getY(),cs1.Connections[e].getZ());
                    if (distance < minDistance){
                        minDistance = distance;
                        startGroundLevel = cs.Connections[s];
                        endGroundLevel = cs1.Connections[e];
                    }
                }
            }

            // get vector between origins at ground level
            Vec3i line = endGroundLevel.subtract(startGroundLevel);
            Vec3d vector = new Vec3d(line);
            Vec3d normal = vector.normalize();

            cs.startBridgeConnection = startGroundLevel;
            cs.endBridgeConnection = endGroundLevel;

            // create new vector representing the connection points with correct length
            Vec3i connectLine = cs.endBridgeConnection.subtract(cs.startBridgeConnection);

            // store this vector in sphere for future reference
            cs.bridgeConnection = new Vec3d(connectLine);

            // find all the blocks that make up the bridge
            cs.bridgeBlocks = getBridgeBlocks(cs.startBridgeConnection, cs.endBridgeConnection, cs.bridgeConnection);

        }

    }

    private ArrayList<BlockPos> getBridgeBlocks(BlockPos startPoint, BlockPos endPoint, Vec3d pathIn){

        int bridgeWidth = 1;

        Vec3d normal = pathIn.normalize();
        //get all the points on the line which is the bridge
        int dt = (int)pathIn.lengthVector();

        ArrayList<BlockPos> blocks = new ArrayList<BlockPos>();
        for (int i = 0; i < dt; i++){
            BlockPos pos = startPoint.add(normal.xCoord * i, 0, normal.zCoord * i);

            blocks.add(pos);
            for(int b = 0; b < bridgeWidth; b++){
                if(Math.abs(pos.getX()+b - startPoint.getX()) < bridgeWidth){
                    blocks.add(pos.add(b,0,0));

                }
                if( Math.abs(pos.getZ()+b - startPoint.getZ()) < bridgeWidth){
                    blocks.add(pos.add(0,0,b));
                }
            }

        }

        return blocks;
    }
}
