package net.gameovr.biosphere;

import net.gameovr.biosphere.config.ModConfig;
import net.minecraft.util.math.BlockPos;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class SphereManager {

    Random rand;


    public Sphere getNearestSphere(BlockPos pos) {

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
        int left = -10;
        int right = 10;
        int top = -10;
        int bottom = 10;

        for (int x = left; x < right+1; x++){
            rand.nextInt();
            for (int z = top; z < bottom+1; z++){
                rand.nextInt();
                genSphereByChunk(x, z);
            }

        }

        writeSphereListToDisk();
        Biosphere.logger.info("Sphere list created: " + BiosphereWorldType.spheres.size() + " spheres added.");

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

        if (!BiosphereWorldType.spheres.isEmpty()) {
            nearestSphere = getNearestSphere(randomPos);
            if (nearestSphere.getDistanceFromOrigin(randomPos) > ModConfig.minDistanceApart + radius) {
                BiosphereWorldType.spheres.add(new Sphere(randomPos, radius));
            }
        } else {
            Sphere sphere = new Sphere(randomPos, radius);
            BiosphereWorldType.spheres.add(sphere);
        }

    }

    private void writeSphereListToDisk(){

        try {
            File file = new File("sphereList.txt");
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            for(Sphere s : BiosphereWorldType.spheres){
                bw.write("Sphere: " + s.getOrigin().getX() + "," + s.getOrigin().getY() + "," + s.getOrigin().getZ());
                bw.newLine();
            }
            bw.close();

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }



}
