package data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class GoVersion {
    public static int version = -1;
    public static final String jarFile = "Go3D.jar";
    public static final String file = "VERSION.log";
    static {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            try {
                version = (int)(Math.random()*1000);//Integer.parseInt(line);
            }
            catch(NumberFormatException ex) { System.out.println("ERROR: Could not read Version '"+line+"'."); }
            reader.close();
        }
        catch (FileNotFoundException ex) { System.out.println("ERROR: Version file not found."); }
        catch (IOException ex) { ex.printStackTrace(); }
    }
}
