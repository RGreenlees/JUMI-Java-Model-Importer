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
package com.jumi.fbx.objects.definitions;

import com.jumi.scene.objects.JUMIMaterial;
import com.jumi.scene.objects.JUMIMesh;
import com.jumi.scene.objects.JUMIMesh.FaceType;
import com.jumi.scene.objects.JUMITexture;
import com.jumi.fbx.objects.FBXProperty;
import java.util.ArrayList;

/**
 * FBXModelDefinition
 * 
 * Container for model information. A model can either be a geometry definition, or contain other data like
 * local translation/rotation etc. Models can be joined in hierarchies, but the final JUMIMesh will flatten
 * it into a single JUMIMesh object, merging the various models together.
 * 
 * @author Richard Greenlees
 */
public class FBXModelDefinition extends FBXObjectDefinition {

    public FaceType faceType = FaceType.NONE;

    public float[] vertices = new float[0];
    public float[] normals = new float[0];
    public float[] uvs = new float[0];
    public int[] UVIndices = new int[0];
    public int[] indices = new int[0];
    public int[] edges = new int[0];
    public float[] binormals = new float[0];

    public boolean bDiscard;

    public ArrayList<FBXTextureDefinition> textures = new ArrayList();
    public ArrayList<FBXMaterialDefinition> materials = new ArrayList();
    public ArrayList<FBXModelDefinition> models = new ArrayList();
    public FBXSkinDeformerDefinition deformer = null;
    
    public FBXLimbNodeDefinition rootNode = null;
    
    public FBXModelDefinition parent = null;

    public FBXModelDefinition(long inUID, String inName) {
        super(inUID, inName);
    }
    
    public FBXModelDefinition(long inUID) {
        super(inUID);
    }
    
    public boolean isRoot() {
        return (parent == null);
    }

    public void readNestedObject(String nestedName, FBXProperty[] properties) {
        switch (nestedName) {
            // Vertex information
            case "Vertices":
                // Just to make life difficult, some models express vertices as a single array of doubles (correct),
                // and some express them as a load of individual double primitives (BAD!). Should provide support for both I guess
                if (properties[0].typeCode == 'd') {
                    vertices = properties[0].asFloatArray();
                } else if (properties[0].typeCode == 'D') {
                    vertices = new float[properties.length];
                    for (int i = 0; i < properties.length; i++) {
                        vertices[i] = (float) properties[i].asDouble();
                    }
                } else {
                    // Vertices should be either a tonne of individual double values or an array of doubles. Anything else just ain't right
                    System.err.println("Invalid vertex data type! Expected: double or array of doubles, actual: " + properties[0].dataType);
                }   break;
            case "Normals":
                // Same as vertices, normals can either be an array or lots of single doubles
                if (properties[0].typeCode == 'd') {
                    normals = properties[0].asFloatArray();
                } else if (properties[0].typeCode == 'D') {
                    normals = new float[properties.length];
                    for (int i = 0; i < properties.length; i++) {
                        normals[i] = (float) properties[i].asDouble();
                    }
                } else {
                    System.err.println("Invalid normal data type! Expected: double or array of doubles, actual: " + properties[0].dataType);
                }   break;
            // Indices
            case "PolygonVertexIndex":
                // Same as vertices, indices can either be an array or lots of single integers
                if (properties[0].typeCode == 'i' || properties[0].typeCode == 'f') {
                    indices = properties[0].asIntArray();
                } else if (properties[0].typeCode == 'I' || properties[0].typeCode == 'F') {
                    indices = new int[properties.length];
                    for (int i = 0; i < properties.length; i++) {
                        indices[i] = properties[i].asInteger();
                    }
                } else {
                    System.err.println("Invalid indices data type! Expected: integer or array of integers, actual: " + properties[0].dataType);
                }   int faceSize = 0;
                for (int i = 0; i < indices.length; i++) {
                    if (indices[i] < 0) {
                        // FBX denotes the end of a single face with a negated index, so we need to restore it
                        indices[i] = ~indices[i];
                        // If we don't know whether we're working with QUADS or TRIANGLES yet, we do now!
                        if (faceSize == 0) {
                            faceSize = i + 1;
                        }
                    }
                }   if (faceSize == 3) {
                    faceType = JUMIMesh.FaceType.TRIANGLES;
                } else if (faceSize == 4) {
                    faceType = JUMIMesh.FaceType.QUADS;
                }   break;
                // TODO: Add something in case we're dealing with polygons or some other rubbish
            case "UV":
                // See my comments for vertices/normals
                if (properties[0].typeCode == 'd') {
                    uvs = properties[0].asFloatArray();
                } else if (properties[0].typeCode == 'D') {
                    uvs = new float[properties.length];
                    for (int i = 0; i < properties.length; i++) {
                        uvs[i] = (float) properties[i].asDouble();
                    }
                } else {
                    System.err.println("Invalid UV data type! Expected: double or array of doubles, actual: " + properties[0].dataType);
                }   break;
            case "UVIndex":
                // See my comments for vertices/normals
                if (properties[0].typeCode == 'i' || properties[0].typeCode == 'f') {
                    UVIndices = properties[0].asIntArray();
                } else if (properties[0].typeCode == 'I' || properties[0].typeCode == 'F') {
                    UVIndices = new int[properties.length];
                    for (int i = 0; i < properties.length; i++) {
                        UVIndices[i] = properties[i].asInteger();
                }
            } else {
                System.err.println("Invalid UV indices data type! Expected: integer or array of integers, actual: " + properties[0].dataType);
            }   break;
        }
    }
    
    public void addChildModel(FBXModelDefinition newChild) {
        models.add(newChild);
    }

    @Override
    public void readEmbeddedProperty(FBXProperty[] properties) {

    }
    
    public boolean containsGeometryDefinition() {
        return findMeshData() != null;
    }

    public boolean hasGeometry() {
        return (vertices != null && vertices.length > 0);
    }
    
    public void addMaterialDefinition(FBXMaterialDefinition newMat) {
        materials.add(newMat);
    }

    public void connect(FBXModelDefinition inModel) {
        inModel.addChildModel(this);
        parent = inModel;
    }

    public void connect(FBXTextureDefinition inTexture) {
        textures.add(inTexture);
    }

    @Override
    public void connect(FBXMaterialDefinition inMaterial) {
        materials.add(inMaterial);
    }
    
    public void connect(FBXLimbNodeDefinition inLimbNode) {
        rootNode = inLimbNode;
    }
    
    @Override
    public void connect(FBXSkinDeformerDefinition inSkinDeformer) {
        deformer = inSkinDeformer;
    }

    /* UVs are expressed as indices referencing the doubles we extracted. Why make things simple when they can be needlessly complex? */
    private float[] generateUVs() {
        if (UVIndices == null || UVIndices.length == 0) {
            return uvs;
        } else {
            float[] uvResult = new float[UVIndices.length * 2];

            for (int i = 0; i < indices.length; i++) {
                uvResult[indices[i] * 2] = uvs[UVIndices[i] * 2];
                uvResult[indices[i] * 2 + 1] = uvs[UVIndices[i] * 2 + 1];
            }

            return uvResult;
        }
    }
    
    /** Check this model and its children to find where we're keeping our geometry data */
    public FBXModelDefinition findMeshData() {
        FBXModelDefinition result = null;
        if (this.hasGeometry()) {
            result = this;
        } else {
            for (FBXModelDefinition a : models) {
                result = a.findMeshData();
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }
    
    /** Do we have a root bone and if so, what is it? */
    public FBXLimbNodeDefinition findRootNode() {
        FBXLimbNodeDefinition result = null;
        if (rootNode != null) {
            return rootNode;
        } else {
            for (FBXModelDefinition a : models) {
                if (a.deformer != null) {
                    return a.deformer.getRootNode();
                }
            }
        }
        return result;
    }
    
    public void buildMaterialList(ArrayList<FBXMaterialDefinition> list) {
        for (FBXMaterialDefinition a : materials) {
            list.add(a);
        }
        
        for (FBXModelDefinition a : models) {
            a.buildMaterialList(list);
        }
    }
    
    public void buildTextureList(ArrayList<FBXTextureDefinition> list) {
        for (FBXTextureDefinition a : textures) {
            list.add(a);
        }
        
        for (FBXModelDefinition a : models) {
            a.buildTextureList(list);
        }
    }

    /** Generate a JUMIMesh using this model definition */
    public JUMIMesh createMesh() {
        JUMIMesh result = new JUMIMesh(name);
        
        FBXModelDefinition meshData = findMeshData();
        FBXLimbNodeDefinition rootNode = findRootNode();
        
        result.vertices = meshData.vertices;
        result.normals = meshData.normals;
        result.binormals = meshData.binormals;
        result.indices = meshData.indices;
        result.uvs = meshData.generateUVs();
        result.edges = meshData.edges;
        result.faceType = meshData.faceType;

        ArrayList<FBXTextureDefinition> textureList = new ArrayList();
        buildTextureList(textureList);
        result.textures = new JUMITexture[textureList.size()];

        for (int i = 0; i < textures.size(); i++) {
            result.textures[i] = textureList.get(i).createTexture();
        }
        
        ArrayList<FBXMaterialDefinition> materialList = new ArrayList();
        buildMaterialList(materialList);
        
        result.materials = new JUMIMaterial[materialList.size()];
        
        for (int i = 0; i < materialList.size(); i++) {
            
            result.materials[i] = materialList.get(i).createMaterial();
        }
        
        if (rootNode != null) {
            result.rootBone = rootNode.createSkeleton();
        }


        return result;
    }

}
