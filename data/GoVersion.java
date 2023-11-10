package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class GoVersion {
    public static int version = -1;
    public static final String file = "VERSION.log";
    public static Path jarFile;
    static {
        try {
            jarFile = new File(GoVersion.class.getProtectionDomain().getCodeSource().getLocation()
    .toURI()).toPath();

            InputStream is = GoVersion.class.getClassLoader().getResourceAsStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            try {
                version = Integer.parseInt(line);
            }
            catch(NumberFormatException ex) { System.out.println("ERROR: Could not read Version '"+line+"'."); }
            reader.close();
        }
        catch (URISyntaxException ex) { ex.printStackTrace(); }
        catch (FileNotFoundException ex) { ex.printStackTrace(); }
        catch (IOException ex) { ex.printStackTrace(); }
    }
}
