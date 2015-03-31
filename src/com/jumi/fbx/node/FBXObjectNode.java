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
package com.jumi.fbx.node;

import com.jumi.fbx.objects.definitions.FBXAnimCurveDefinition;
import com.jumi.fbx.objects.definitions.FBXAnimCurveNodeDefinition;
import com.jumi.fbx.objects.definitions.FBXAnimLayerDefinition;
import com.jumi.fbx.objects.definitions.FBXAnimStackDefinition;
import com.jumi.fbx.objects.definitions.FBXCameraDefinition;
import com.jumi.fbx.objects.definitions.FBXClusterDefinition;
import com.jumi.fbx.objects.definitions.FBXLimbNodeDefinition;
import com.jumi.fbx.objects.definitions.FBXMaterialDefinition;
import com.jumi.fbx.objects.definitions.FBXMediaDefinition;
import com.jumi.fbx.objects.definitions.FBXModelDefinition;
import com.jumi.fbx.objects.definitions.FBXObjectDefinition;
import com.jumi.fbx.objects.FBXProperty;
import com.jumi.fbx.objects.definitions.FBXShapeDefinition;
import com.jumi.fbx.objects.definitions.FBXSkinDeformerDefinition;
import com.jumi.fbx.objects.definitions.FBXTextureDefinition;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * FBXObjectNode
 * 
 * Parses data stored in the Objects node.
 * 
 * @author Richard Greenlees
 */
public class FBXObjectNode extends FBXNode {

    public HashMap<String, FBXObjectDefinition> connectableObjects = new HashMap();

    public FBXObjectNode(byte[] inputData, int propertyOffset) {
        super(inputData, propertyOffset);
    }

    @Override
    public void parseData(byte[] inputData, int propertyOffset) {

        long objectUID;
        FBXObjectDefinition newObject;

        while (sizeInBytes - cursorPosition > 13) {

            objectUID = 0;
            newObject = null;

            int nestedNameLength = inputData[cursorPosition + 12];

            byte[] nestedNameData = FBXNode.retrieveBytesFrom(inputData, nestedNameLength, cursorPosition + 13);
            String nestedName = new String(nestedNameData);

            byte[] numPropertiesData = FBXNode.retrieveBytesFrom(inputData, 4, cursorPosition + 4);
            int numNestedProperties = ByteBuffer.wrap(numPropertiesData).order(ByteOrder.LITTLE_ENDIAN).getInt();

            FBXProperty[] nestedProperties = new FBXProperty[numNestedProperties];

            cursorPosition += nestedNameLength + 13;

            for (int i = 0; i < nestedProperties.length; i++) {
                FBXProperty newProp = new FBXProperty(inputData, cursorPosition);
                cursorPosition += newProp.dataLength;
                nestedProperties[i] = newProp;
            }

            if (nestedProperties.length > 0 && nestedProperties[0].dataType.equals("Long")) {
                objectUID = nestedProperties[0].asLong();
            }

            // What kind of nested item is this?
            switch (nestedName) {
                case "Model":
                case "Geometry":
                    String modelType = nestedProperties[nestedProperties.length - 1].asString();
                    
                    switch (modelType) {
                        // Bone definition. Name and local translation
                        case "LimbNode":
                            newObject = new FBXLimbNodeDefinition(objectUID);
                            break;
                        // Morph target
                        case "Shape":
                            newObject = new FBXShapeDefinition(objectUID);
                            break;
                        // Mesh geometry or model attributes (local translation etc...)
                        case "Mesh":
                            newObject = new FBXModelDefinition(objectUID);
                            break;
                        // TODO: Look at adding support for cameras
                        default: break;
                    }
                    break;
                // Material definition. Doesn't contain texture info
                case "Material":
                    newObject = new FBXMaterialDefinition(objectUID);
                    break;
                // A texture definition, filename, format etc.
                case "Texture":
                    newObject = new FBXTextureDefinition(objectUID);
                    break;
                // Seems to do the same job as a texture object, not sure why it's needed but it's used so keep it
                case "Video":
                    newObject = new FBXMediaDefinition(objectUID);
                    break;
                // Bone definition. Affected vertices and weights
                case "Deformer":
                    String deformerType = nestedProperties[nestedProperties.length - 1].asString();
                    if (deformerType.equals("Skin")) {
                        newObject = new FBXSkinDeformerDefinition(objectUID);
                    } else if (deformerType.equals("Cluster")) {
                        newObject = new FBXClusterDefinition(objectUID);
                    }   break;
                // A container for animations, allowing for blending
                case "AnimationStack":
                    newObject = new FBXAnimStackDefinition(objectUID);
                    break;
                // A single animation
                case "AnimationLayer":
                    newObject = new FBXAnimLayerDefinition(objectUID);
                    break;
                // Contains the actual animation data
                case "AnimationCurveNode":
                    newObject = new FBXAnimCurveNodeDefinition(objectUID);
                    break;
                // Data for a single keyframe
                case "AnimationCurve":
                    newObject = new FBXAnimCurveDefinition(objectUID);
                    break;
            }

            // If our new object is one of the above then add it to our connectable objects ready to hook up
            if (newObject != null) {
                
                String newObjectName = nestedProperties[nestedProperties.length - 2].asString();
                newObjectName = newObjectName.substring(0, newObjectName.indexOf('\0'));
                
                newObject.setName(newObjectName);
                newObject.parseData(inputData, cursorPosition);
                // Some older FBX files use String names as IDs instead of longs. Performance isn't affected too badly so let's allow it
                connectableObjects.put((objectUID > 0) ? String.valueOf(objectUID) : newObjectName, newObject);
                cursorPosition = newObject.endOffset;
            }

        }
    }

    /** Returns a specific connectable object using the supplied key */
    public FBXObjectDefinition getConnectable(String key) {
        return connectableObjects.get(key);
    }

    /** Retrieves all the individual mesh objects in the scene. Only returns parent models that have geometry assigned */
    public ArrayList<FBXModelDefinition> getMeshDefinitions() {
        ArrayList<FBXModelDefinition> result = new ArrayList();
        for (Entry<String, FBXObjectDefinition> entry : connectableObjects.entrySet()) {
            if (entry.getValue() instanceof FBXModelDefinition) {
                FBXModelDefinition test = (FBXModelDefinition) entry.getValue();
                if (test.isRoot() && test.containsGeometryDefinition()) {
                    result.add(test);
                }
            }
        }
        return result;
    }

    /** Retrieves all "orphan" textures which aren't assigned to a mesh for some reason */
    public ArrayList<FBXTextureDefinition> getTextureDefinitions() {
        ArrayList<FBXTextureDefinition> result = new ArrayList();
        for (Entry<String, FBXObjectDefinition> entry : connectableObjects.entrySet()) {
            if (entry.getValue() instanceof FBXTextureDefinition) {
                FBXTextureDefinition test = (FBXTextureDefinition) entry.getValue();
                if (test.parent == null) {
                    result.add(test);
                }
            }
        }
        return result;
    }

    /** Get the root node for the mesh */
    public FBXLimbNodeDefinition getRootNode() {
        for (Entry<String, FBXObjectDefinition> entry : connectableObjects.entrySet()) {
            if (entry.getValue() instanceof FBXLimbNodeDefinition) {
                FBXLimbNodeDefinition test = (FBXLimbNodeDefinition) entry.getValue();
                if (test.isRoot) {
                    return test;
                }
            }
        }
        return null;
    }
}
