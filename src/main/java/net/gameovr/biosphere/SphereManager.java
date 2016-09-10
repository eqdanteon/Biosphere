package net.gameovr.biosphere;

import net.minecraft.util.math.BlockPos;

import java.util.*;

public class SphereManager {


    public ArrayList<Sphere> spheres = new ArrayList<Sphere>();

    public Sphere getNearestSphere(BlockPos pos) {

        Sphere closestSphere = null;
        int minDistance = Integer.MAX_VALUE;

        for (Sphere sphere : spheres) {
            if (sphere.getDistanceFromOrigin(pos) < minDistance) {
                minDistance = sphere.getDistanceFromOrigin(pos);
                closestSphere = sphere;

            }
        }
        return closestSphere;
    }


}
