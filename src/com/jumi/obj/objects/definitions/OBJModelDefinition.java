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
package com.jumi.obj.objects.definitions;

import com.jumi.data.Vector2;
import com.jumi.data.Vector3;
import com.jumi.scene.objects.JUMIMaterial;
import com.jumi.scene.objects.JUMIMesh;
import com.jumi.scene.objects.JUMIMesh.FaceType;
import com.jumi.scene.objects.JUMITexture;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author RGreenlees
 */
public class OBJModelDefinition {

    public String name = "";
    public ArrayList<OBJMaterialDefinition> materialDefinitions = new ArrayList();

    float[] vertices = new float[0];
    float[] normals = new float[0];
    float[] uvs = new float[0];
    int[] indices = new int[0];

    public HashSet<String> uniqueIndexDefinitions = new HashSet();
    public ArrayList<String> allIndexDefinitions = new ArrayList();

    public OBJModelDefinition(String inName) {
        super();
        name = inName;
    }

    /** Generates a JUMIMesh from this OBJModelDefinition
     * 
     * @param vertexPool All the defined vertices for this OBJ
     * @param normalPool All the defined normals for this OBJ
     * @param uvPool All the defined UVs for this OBJ
     * @return 
     */
    public JUMIMesh createMesh(ArrayList<Vector3> vertexPool, ArrayList<Vector3> normalPool, ArrayList<Vector2> uvPool) {
        JUMIMesh result = new JUMIMesh(name);

        // We're going to take the total vertices/normals/UVs and divide them up among each JUMIMesh in the scene
        ArrayList<Vector3> modelVertices = new ArrayList();
        ArrayList<Vector3> modelNormals = new ArrayList();
        ArrayList<Vector2> modelUVs = new ArrayList();

        // First create the set of vertices, UVs and normals relevant to this model. Do this using the unique indices associated with it
        for (String a : uniqueIndexDefinitions) {
            String[] indexDef = a.split("/");
            int vindex = Integer.valueOf(indexDef[0]);

            // Retrieve from the wider vertex pool, the vertices for this model. -1 as OBJ goes from 1...n
            modelVertices.add(vertexPool.get(vindex - 1));

            // If UVs are defined, retrieve from the wider UV pool the UVs for this model. -1 as OBJ goes from 1...n
            if (indexDef.length > 1 && !indexDef[1].equals("")) {
                int uvIndex = Integer.valueOf(indexDef[1]);
                modelUVs.add(uvPool.get(uvIndex - 1));
            }

            // If normals are defined, retrieve from the wider normal pool the normals for this model. -1 as OBJ goes from 1...n
            if (indexDef.length > 2 && !indexDef[2].equals("")) {
                int nIndex = Integer.valueOf(indexDef[2]);
                modelNormals.add(normalPool.get(nIndex - 1));
            }
        }

        // Create the arrays ready to hold the raw float data
        result.vertices = new float[modelVertices.size() * 3];
        result.normals = new float[modelNormals.size() * 3];
        result.uvs = new float[modelUVs.size() * 2];
        result.indices = new int[allIndexDefinitions.size()];

        int arrayCounter = 0;

        // Create a hashmap with each unique face mapped against the order in which it appears. Lookup is MUCH faster than ArrayList for large models
        HashMap<String, Integer> uniqueIndices = new HashMap();

        for (String a : uniqueIndexDefinitions) {
            uniqueIndices.put(a, arrayCounter++);
        }

        arrayCounter = 0;

        // For every index defined, retrieve the index within the hashmap to get our final index!
        for (String a : allIndexDefinitions) {
            result.indices[arrayCounter++] = uniqueIndices.get(a);
        }

        arrayCounter = 0;

        // Now populate the raw float data for vertices, UVs and normals (if applicable)
        for (Vector3 a : modelVertices) {
            result.vertices[arrayCounter++] = a.x;
            result.vertices[arrayCounter++] = a.y;
            result.vertices[arrayCounter++] = a.z;
        }

        arrayCounter = 0;

        for (Vector3 a : modelNormals) {
            result.normals[arrayCounter++] = a.x;
            result.normals[arrayCounter++] = a.y;
            result.normals[arrayCounter++] = a.z;
        }

        arrayCounter = 0;

        for (Vector2 a : modelUVs) {
            result.uvs[arrayCounter++] = a.x;
            result.uvs[arrayCounter++] = a.y;
        }
       
        // Now create all the materials for the mesh
        result.materials = new JUMIMaterial[materialDefinitions.size()];

        int materialCounter = 0;

        // TODO: What happens if a .obj references a material by its diffuse map rather than name? (see Ax.obj)
        for (OBJMaterialDefinition a : materialDefinitions) {
            if (a == null) {
                continue;
            }
            JUMIMaterial newMat = new JUMIMaterial();
            newMat.name = a.name;
            newMat.ambientColor = a.ambientColour;
            newMat.diffuseColor = a.diffuseColour;
            newMat.specularColor = a.specularColour;
            newMat.specularFactor = a.specularWeight;
            newMat.opacity = a.alpha;

            // If a diffuse/specular/normal map is defined, make sure it's included in the JUMIMaterial!
            if (!a.diffuseMap.equals("")) {
                JUMITexture newTexture = new JUMITexture();
                newTexture.fileName = a.diffuseMap;
                newTexture.name = a.diffuseMap;
                newMat.diffuseTexture = newTexture;
            }

            if (!a.specularMap.equals("")) {
                JUMITexture newTexture = new JUMITexture();
                newTexture.fileName = a.specularMap;
                newTexture.name = a.specularMap;
                newMat.specularTexture = newTexture;
            }
            
            if (!a.bumpMap.equals("")) {
                JUMITexture newTexture = new JUMITexture();
                newTexture.fileName = a.bumpMap;
                newTexture.name = a.bumpMap;
                newMat.normalTexture = newTexture;
            }

            result.materials[materialCounter++] = newMat;

        }
        
        result.faceType = FaceType.TRIANGLES;

        return result;
    }

}
