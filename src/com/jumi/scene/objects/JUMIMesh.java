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
package com.jumi.scene.objects;

import static java.lang.Math.abs;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author RGreenlees
 */
public class JUMIMesh {

    public static enum FaceType {

        NONE, TRIANGLES, QUADS
    };
    public FaceType faceType = FaceType.NONE;

    public String name;

    public float[] vertices = new float[0];
    public float[] normals = new float[0];
    public float[] binormals = new float[0];
    public float[] tangents = new float[0];
    public float[] uvs = new float[0];

    public int[] indices = new int[0];
    public int[] edges = new int[0];

    public JUMITexture[] textures = new JUMITexture[0];
    public JUMIMaterial[] materials = new JUMIMaterial[0];

    public JUMIBone rootBone;

    public JUMIMesh(String inputName) {
        name = inputName;
    }

    public int numTextures() {
        return textures.length;
    }

    /** Does this mesh have skeletal information? */
    public boolean hasSkeleton() {
        return rootBone != null;
    }

    /** Searches for the supplied bone name within the hierarchy, returning null if not found */
    public JUMIBone getBoneByName(String boneName) {
        return findBoneInHierarchy(rootBone, boneName);
    }

    private JUMIBone findBoneInHierarchy(JUMIBone root, String boneName) {
        JUMIBone result = null;
        if (root.getName().equals(boneName)) {
            return root;
        } else {
            for (JUMIBone child : root.getChildren()) {
                result = findBoneInHierarchy(child, boneName) ;
                if (result != null) {
                    return result;
                }
            }
            return result;
        }
    }
    
    /** Returns the texture at the supplied index, or null if it does not exist. Only for textures without a parent material */
    public JUMITexture getTextureByIndex(int index) {
        if (index >= textures.length) {
            return null;
        }
        return textures[index];
    }

    /** Searches for the supplied texture name and returns any matches, or null if none found */
    public JUMITexture getTextureByName(String inName) {
        for (JUMITexture a : textures) {
            if (a.name.equals(inName)) {
                return a;
            }
        }
        return null;
    }

    /** Returns the material at the supplied index, or null if it does not exist */
    public JUMIMaterial getMaterialByIndex(int index) {
        if (index >= materials.length) {
            return null;
        }
        return materials[index];
    }

    /** Searches for the supplied material name and returns any matches, or null if none found */
    public JUMIMaterial getMaterialByName(String inName) {
        for (JUMIMaterial a : materials) {
            if (a.name.equals(inName)) {
                return a;
            }
        }
        return null;
    }

    /** Return an array of all bones for this mesh */
    public JUMIBone[] getAllBones() {
        return rootBone.getFullSkeleton();
    }

    /** Returns the distance of the furthest point from the model's origin */
    public float getMaxExtent() {
        float result = 0.0f;
        for (int i = 0; i < vertices.length; i++) {
            if (abs(vertices[i]) > result) {
                result = abs(vertices[i]);
            }
        }
        return result;
    }

    /** Scales the model so that the furthest vertex from the model's origin matches the supplied extent */
    public void setMaxExtent(float newExtent) {
        float scale = newExtent / getMaxExtent();

        setScale(scale);

    }

    /** Scales the mesh by the supplied scalar */
    public void setScale(float newScale) {
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] *= newScale;
        }
    }

    /** Triangulates the mesh (Currently only works for QUADS) */
    public void triangulate() {
        if (faceType == FaceType.QUADS) {
            int[] newIndices = new int[(int) (indices.length * 1.5f)];
            int newIndexCounter = 0;

            for (int i = 0; i < indices.length - 4; i += 4) {
                newIndices[newIndexCounter++] = indices[i];
                newIndices[newIndexCounter++] = indices[i + 1];
                newIndices[newIndexCounter++] = indices[i + 2];
                newIndices[newIndexCounter++] = indices[i + 2];
                newIndices[newIndexCounter++] = indices[i + 3];
                newIndices[newIndexCounter++] = indices[i];
            }

            indices = newIndices;
            faceType = FaceType.TRIANGLES;
        }
    }

    public String toString() {
        String result = "JUMIMesh: " + name + "\n\tVertices: " + ((vertices != null) ? (vertices.length / 3) : 0)
                + "\n\tNormals: " + ((normals != null) ? (normals.length / 3) : 0)
                + "\n\tUVs: " + ((uvs != null) ? (uvs.length / 2) : 0)
                + "\n\tTextures: " + ((textures != null) ? textures.length : 0);
        for (int i = 0; i < textures.length; i++) {
            result = result + "\n\t\t" + textures[i].name;
        }
        result = result + "\n\tMaterials: " + ((materials != null) ? materials.length : 0);
        for (int i = 0; i < materials.length; i++) {
            result = result + "\n\t\t" + materials[i].name;
        }

        result = result + "\n\tFace Type: " + faceType.toString()
                + "\n\tMax Extent: " + this.getMaxExtent();
        result = result + "\n\tRoot Bone: " + ((rootBone != null) ? rootBone.getName() : "None");
        return result;
    }

    /** Returns the vertices for this mesh in float[] form */
    public float[] getVertices() {
        return vertices;
    }

    /** Returns the normals for this mesh in float[] form */
    public float[] getNormals() {
        return normals;
    }

    /** Returns the UVs for this mesh in float[] form */
    public float[] getUVs() {
        return uvs;
    }

    /** Returns the indices for this mesh in int[] form */
    public int[] getVertexIndices() {
        return indices;
    }

    /** Returns the edges for this mesh in int[] form (NOT IMPLEMENTED YET!) */
    public int[] getEdges() {
        return edges;
    }

    /** Horizontally flips the UV coordinates */
    public void flipUVX() {
        for (int i = 0; i < uvs.length; i += 2) {
            uvs[i] = 1.0f - uvs[i];
        }
    }

    /** Vertically flips the UV coordinates */
    public void flipUVY() {
        for (int i = 1; i < uvs.length; i += 2) {
            uvs[i] = 1.0f - uvs[i];
        }
    }
}
