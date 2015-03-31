/*
 * (C) Copyright 2015 Richard Greenlees
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 *  1) The above copyright notice and this permission notice shall be included
 *     in all copies or substantial portions of the Software.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package com.jumi.obj;

import com.jumi.JUMILoader;
import com.jumi.data.Vector2;
import com.jumi.data.Vector3;
import com.jumi.obj.objects.definitions.OBJMatLibDefinition;
import com.jumi.obj.objects.definitions.OBJModelDefinition;
import com.jumi.scene.JUMIScene;
import com.jumi.scene.objects.JUMIMesh;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author RGreenlees
 */
public class OBJLoader extends JUMILoader {

    public static JUMIScene importModel(String fileName) throws IOException {
        ArrayList<Vector3> vertices = new ArrayList();
        ArrayList<Vector3> normals = new ArrayList();
        ArrayList<Vector2> uvs = new ArrayList();
        ArrayList<OBJModelDefinition> modelContexts = new ArrayList();

        OBJMatLibDefinition materialLibrary = null;

        BufferedReader reader = null;

        OBJModelDefinition defaultModel = new OBJModelDefinition("default");
        modelContexts.add(defaultModel);
        OBJModelDefinition currentModelContext = defaultModel;

        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line;

            while ((line = reader.readLine()) != null) {
                // Split up the string by space or tab
                String[] lineTokens = line.split("\\s+");

                // Obviously ignore blank lines
                if (lineTokens.length == 0) {
                    continue;
                }
                
                // Make sure we remove leading/trailing white space!
                switch (lineTokens[0].trim()) {
                    case "mtllib": // Define our material library
                        // Some OBJ files like to include hard-coded paths, let's get rid of that and get just the name of the file
                        String libName = line.substring(line.indexOf(" ") + 1, line.length()).replace("\\", "/");

                        if (libName.contains("/")) {
                            libName = libName.substring(libName.lastIndexOf("/") + 1, libName.length());
                        }
                        
                        // Look for our MTL file in the same folder as the OBJ file
                        String libLocation = fileName.substring(0, fileName.lastIndexOf("/") + 1) + libName;
                        try {
                            // Create the material library and parse the MTL file separately
                            materialLibrary = new OBJMatLibDefinition();
                            materialLibrary.parseMTL(libLocation);
                        } catch (FileNotFoundException e) {
                            System.err.println("WARNING: Could not find requested material file " + libLocation);
                        } catch (IOException e) {
                            System.out.println("WARNING: Error while parsing requested material file " + libLocation);
                        }
                        break;
                    case "V":
                    case "v":
                        // OBJ indices refer to the vertices/UVs/normals as groups of 3, so let's do the same
                        vertices.add(new Vector3(Float.valueOf(lineTokens[1]), Float.valueOf(lineTokens[2]), Float.valueOf(lineTokens[3])));
                        break;
                    case "VN":
                    case "vn":
                        // OBJ indices refer to the vertices/UVs/normals as groups of 3, so let's do the same
                        normals.add(new Vector3(Float.valueOf(lineTokens[1]), Float.valueOf(lineTokens[2]), Float.valueOf(lineTokens[3])));
                        break;
                    case "VT":
                    case "vt":
                        // OBJ indices refer to the vertices/UVs/normals as groups of 3, so let's do the same
                        uvs.add(new Vector2(Float.valueOf(lineTokens[1]), Float.valueOf(lineTokens[2])));
                        break;
                    case "F":
                    case "f":
                        // Only allow support for Triangles or Quads for now
                        if (lineTokens.length > 5 || lineTokens.length < 4) {
                            System.err.println("Invalid face definition! Expected 3 (Triangles) or 4 (Quads), actual = " + (lineTokens.length - 1));
                        } else {
                            // Some OBJ models use relative positions (negative indices). The aim here is to convert them to regular face definitions
                            // First check if we have a negative index
                            if (lineTokens[1].charAt(0) == '-') {
                                for (int i = 1; i < lineTokens.length; i++) {
                                    // Split the face definition, we're going to redefine each index
                                    String[] lineToken = lineTokens[i].split("/");
                                    
                                    // Check that model has UVs and normals defined before converting them
                                    int vIndex = Integer.valueOf(lineToken[0]);
                                    int vtIndex = (lineToken.length > 1 && !lineToken[1].equals("")) ? Integer.valueOf(lineToken[1]) : 0;
                                    int nIndex = (lineToken.length > 2 && !lineToken[2].equals("")) ? Integer.valueOf(lineToken[2]) : 0;
                                    
                                    // Convert each index as the current number of definitions - index. We use + as the index is negative
                                    // Also add +1 as OBJ definitions go from 1...n while ArrayLists go 0...n
                                    vIndex = vertices.size() + vIndex + 1;
                                    vtIndex = (vtIndex < 0) ? (uvs.size() + vtIndex + 1) : 0;
                                    nIndex = (nIndex < 0) ? (normals.size() + nIndex + 1) : 0;
                                    
                                    // Reconstruct the face definition so it can be processed as normal when constructing the mesh
                                    lineTokens[i] = vIndex + "/" + ((vtIndex > 0) ? vtIndex : "") + "/" + ((nIndex > 0) ? nIndex : "");
                                }
                            }
                            
                            // If we're dealing with a quad, triangulate it. Some models use a mix of triangles and quads, and it's easier
                            // to just triangulate everything rather than try and switch back and forth
                            if (lineTokens.length == 5) {

                                currentModelContext.uniqueIndexDefinitions.add(lineTokens[1]);
                                currentModelContext.uniqueIndexDefinitions.add(lineTokens[2]);
                                currentModelContext.uniqueIndexDefinitions.add(lineTokens[3]);
                                currentModelContext.uniqueIndexDefinitions.add(lineTokens[4]);

                                currentModelContext.allIndexDefinitions.add(lineTokens[1]);
                                currentModelContext.allIndexDefinitions.add(lineTokens[2]);
                                currentModelContext.allIndexDefinitions.add(lineTokens[3]);
                                currentModelContext.allIndexDefinitions.add(lineTokens[3]);
                                currentModelContext.allIndexDefinitions.add(lineTokens[4]);
                                currentModelContext.allIndexDefinitions.add(lineTokens[1]);
                            } else {
                                // Keep a separate list of unique index definitions and the actual defined indices. These will be used later
                                // to create a final indices array
                                for (int i = 1; i < lineTokens.length; i++) {
                                    currentModelContext.uniqueIndexDefinitions.add(lineTokens[i]);
                                    currentModelContext.allIndexDefinitions.add(lineTokens[i]);
                                }
                            }
                        }

                        break;
                    case "usemtl": // Use a material from the defined library
                        // Only do stuff if we've already had a material library defined (via mtllib)
                        if (materialLibrary != null && lineTokens.length > 1) {
                            currentModelContext.materialDefinitions.add(materialLibrary.getMaterialDefinition(lineTokens[1]));
                        }
                        break;
                    case "O":
                    case "o":
                    case "G":
                    case "g":
                        // If the group or object has no name defined, give it a default one
                        String modelContextName = "default";
                        if (lineTokens.length > 1) {
                            modelContextName = lineTokens[1];
                        }

                        // First check if we already have a model with that name defined, and use that instead
                        OBJModelDefinition newContext = null;

                        for (OBJModelDefinition a : modelContexts) {
                            if (a.name.equals(modelContextName)) {
                                newContext = a;
                                break;
                            }
                        }

                        // Otherwise, create a new model and use it
                        if (newContext == null) {
                            newContext = new OBJModelDefinition(modelContextName);
                            modelContexts.add(newContext);
                        }

                        currentModelContext = newContext;

                        break;

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                // Don't forget to close the reader!
                reader.close();
            }
        }

        ArrayList<JUMIMesh> allMeshes = new ArrayList();

        JUMIScene result = new JUMIScene();

        // Gather all the models we have defined and turn them into JUMI Meshes for inclusion in the scene
        for (OBJModelDefinition a : modelContexts) {
            if (a.allIndexDefinitions.size() > 0) {
                allMeshes.add(a.createMesh(vertices, normals, uvs));
            }
        }

        
        result.addMeshes(allMeshes);

        return result;

    }

}
