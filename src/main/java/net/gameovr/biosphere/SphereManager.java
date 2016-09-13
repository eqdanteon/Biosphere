package net.gameovr.biosphere;

import net.gameovr.biosphere.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;

import java.io.*;
import java.util.*;

public class SphereManager {

    Random rand;


    public Sphere getNearestSphere(BlockPos pos) {

        Sphere closestSphere = null;
        int minDistance = Integer.MAX_VALUE;

        for (Sphere sphere : Biosphere.spheres) {
            if (sphere.getDistanceFromOrigin(pos) < minDistance) {
                minDistance = sphere.getDistanceFromOrigin(pos);
                closestSphere = sphere;

            }
        }
        return closestSphere;
    }

    public void GenerateSpheres(long seed) {

        System.out.println("Starting Generate Spheres...");

        rand = new Random(seed);
        // bounds of world
        int left = -100;
        int right = 100;
        int top = -100;
        int bottom = 100;

        for (int x = left; x < right+1; x++){
            rand.nextInt();
            for (int z = top; z < bottom+1; z++){
                rand.nextInt();
                genSphereByChunk(x, z);
            }

        }

        System.out.println("Sphere list created.");

    }

    private void genSphereByChunk(int chunkX, int chunkZ){

        int radius = rand.nextInt((ModConfig.maxSphereRadius-16)+1)+16;
        int yPosMin = 2 + radius;
        int yPosMax = 256 - radius;

        int blockX = (chunkX * 16) + rand.nextInt(16);
        int blockZ = (chunkZ * 16) + rand.nextInt(16);
        rand.nextInt();
        int blockY = yPosMin + (int)(rand.nextFloat() * (yPosMax - yPosMin));

        BlockPos randomPos = new BlockPos(blockX, blockY, blockZ);
        Sphere nearestSphere;

        if (!Biosphere.spheres.isEmpty()) {
            nearestSphere = getNearestSphere(randomPos);
            if (nearestSphere.getDistanceFromOrigin(randomPos) > ModConfig.minDistanceApart) {
                Biosphere.spheres.add(new Sphere(randomPos, radius));
                System.out.println("Sphere: " + randomPos.getX() + "," + randomPos.getY() + "," + randomPos.getZ());
            }
        } else {
            Sphere sphere = new Sphere(randomPos, radius);
            Biosphere.spheres.add(sphere);
            System.out.println("Sphere: " + randomPos.getX() + "," + randomPos.getY() + "," + randomPos.getZ());
        }

    }

    public static BlockPos GetCenterBlockFromChunk(int chunkX, int chunkZ, int playerY){

        int blockXMax = calcBlockMax(chunkX);
        int blockZMax = calcBlockMax(chunkZ);

        int blockXMin = calcBlockMin(chunkX);
        int blockZMin = calcBlockMin(chunkZ);

        int centerX = blockXMax - 8;
        int centerZ = blockZMax - 8;
        return (new BlockPos(centerX, playerY, centerZ));

    }


    public static int calcBlockMin(int chunkCoordinateValue){
        return (chunkCoordinateValue<<4);
    }

    public static int calcBlockMax(int chunkCoordinateValue){
        return (chunkCoordinateValue + 1<<4)-1;
    }


}
