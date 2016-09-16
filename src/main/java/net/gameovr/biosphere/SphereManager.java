package net.gameovr.biosphere;

import net.gameovr.biosphere.config.ModConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class SphereManager {

    Random rand;
    Sphere nearestOrigin = null;


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
        int left = -16;
        int right = 16;
        int top = -16;
        int bottom = 16;

        for (int x = left; x < right+1; x++){
            for (int z = top; z < bottom+1; z++){
               genSphereByChunk(x, z);
            }

        }

        BiosphereWorldType.spawnPoint = nearestOrigin.origin;
        writeSphereListToDisk();

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


        Sphere genSphere = new Sphere(randomPos, radius);

        if (!BiosphereWorldType.spheres.isEmpty()) {
            nearestSphere = getNearestSphere(randomPos);
            if (nearestSphere.getDistanceFromOrigin(randomPos) > ModConfig.minDistanceApart + radius) {
                genSphere = new Sphere(randomPos, radius);
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
