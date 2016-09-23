package net.gameovr.biosphere.helpers;

import net.gameovr.biosphere.BiosphereWorldType;
import net.gameovr.biosphere.Sphere;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class BioLogger {

    public static void writeSphereListToDisk(){

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

    public static void writeStringToDisk(String fileName, String whatToWrite){

        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);

            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(whatToWrite);
            bw.newLine();

            bw.close();



        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
