package airhockey;

import java.io.File;
import java.io.FileNotFoundException;

import engine.Renderable;
import engine.Texture;
import engine.TextureLoader;
import engine.modelloader.ObjLoader;

public class MediaLoader {
    public static Renderable loadObj(String name) {
        try {
            return ObjLoader.load(new File("data/models/" + name));
        } catch (FileNotFoundException e) {
            System.err.println("Error loading model: " + name);
            System.exit(0);
        }
        return null;
        
    }
}
