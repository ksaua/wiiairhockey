package engine.modelloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;


import engine.Model;
import engine.Texture;
import engine.TextureLoader;

/**
 * Class for loading .obj models
 * @author Knut Saua Mathiesen
 *
 */
public class ObjLoader {

	private static class ObjModel {
		ArrayList<Face> faces = new ArrayList<Face>();
		HashMap<String, Material> materials = new HashMap<String, Material>();
	}

	private static class Material {
		String name;
		Texture texture;
	}

	private static class Face {
		Vertex[] vertices;
		Material material;
	}

	private static class Vertex {
		Vector3f vertex;
		Vector2f texturecoord;
		Vector3f normal;
	}

	/**
	 * Loads a .obj model
	 * @param file 
	 * @return	Model object which renders the .obj model
	 * @throws FileNotFoundException
	 */

	public static Model load(File file) throws FileNotFoundException {

		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
		ArrayList<Vector2f> texturecoords = new ArrayList<Vector2f>();
		ArrayList<Vector3f> normals = new ArrayList<Vector3f>();

		ObjModel obj = new ObjModel();


		BufferedReader br = new BufferedReader(new FileReader(file));
		try {

			String line = br.readLine();
			
			Material currentMat = null;

			while (line != null) {
				String[] parameters = line.split(" ");

				// Material file
				if (parameters[0].equals("mtllib")) {
					String f = file.getParent() + File.separator + parameters[1];
					loadMaterials(obj, new File(f));
				}
				
				else if (parameters[0].equals("usemtl")) {
					currentMat = obj.materials.get(parameters[1]);
				}

				// Vertex
				else if (parameters[0].equals("v")) {
					vertices.add(new Vector3f(Float.valueOf(parameters[1]),
							Float.valueOf(parameters[2]),
							Float.valueOf(parameters[3])));
				}

				// Vertex' Texture coordinate 
				else if (parameters[0].equals("vt")) {
					texturecoords.add(new Vector2f(Float.valueOf(parameters[1]),
							Float.valueOf(parameters[2])));
				}

				// Vertex' normal vector
				else if (parameters[0].equals("vn")) {
					normals.add(new Vector3f(Float.valueOf(parameters[1]),
							Float.valueOf(parameters[2]),
							Float.valueOf(parameters[3])));
				}

				// Face
				else if (parameters[0].equals("f")) {
					Vertex[] faceVertices = new Vertex[parameters.length - 1];

					for (int i = 1; i < parameters.length; i++) {
						Vertex v = new Vertex();

						String[] slash = parameters[i].split("/");

						v.vertex = vertices.get(Integer.valueOf(slash[0]) -1);
						if (slash[1].length() > 0)
							v.texturecoord = texturecoords.get(Integer.valueOf(slash[1]) -1);
						if (slash[2].length() > 0)
							v.normal = normals.get(Integer.valueOf(slash[2]) -1);

						faceVertices[i - 1] = v;
					}

					Face f = new Face();
					f.vertices = faceVertices;
					f.material = currentMat;
					obj.faces.add(f);

				}

				line = br.readLine();
			}


		} catch (IOException e) {
			e.printStackTrace();
		}		
		return new Model(buildGlList(obj));
	}
	
	/**
	 * Reads a .mtl file
	 * @param obj 
	 * @param file
	 * @return
	 */
	private static void loadMaterials(ObjModel obj, File file) {

		try {

			BufferedReader br = new BufferedReader(new FileReader(file));

			Material m = null;

			String line = br.readLine();

			while (line != null) {

				String[] parameters = line.split(" ");

				// Name
				if (parameters[0].equals("newmtl")) {
					if (m != null) 	obj.materials.put(m.name, m);
					
					m = new Material();
					m.name = parameters[1];
				} 

				// Texture
				else if (parameters[0].equals("map_Kd")) {
					m.texture = TextureLoader.loadTexture(parameters[1]);

				}

				line = br.readLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Builds a Display List according to the ObjModel.
	 * @param obj
	 * @return List id
	 */
	private static int buildGlList(ObjModel obj) {

		int id = GL11.glGenLists(1);

		GL11.glNewList(id, GL11.GL_COMPILE);

		Material lastMat = null;
		
		for (Face face: obj.faces) {
			
			if (face.material != lastMat) {
				lastMat = face.material;
				if (face.material.texture != null) 
					face.material.texture.bind();
			}

			if (face.vertices.length == 3)
				GL11.glBegin(GL11.GL_TRIANGLES);
			else 
				GL11.glBegin(GL11.GL_QUADS);


			for (Vertex v: face.vertices) {
				if (v.texturecoord != null)
					GL11.glTexCoord2f(v.texturecoord.x, v.texturecoord.y);
				if (v.normal != null)
					GL11.glNormal3f(v.normal.x, v.normal.y, v.normal.z);
				GL11.glVertex3f(v.vertex.x, v.vertex.y, v.vertex.z);
			}

			GL11.glEnd();

		}

		GL11.glEndList();

		return id;
	}
}
