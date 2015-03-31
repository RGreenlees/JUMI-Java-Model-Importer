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

import com.jumi.fbx.objects.FBXProperty;
import com.jumi.fbx.node.FBXNode;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * FBXObjectDefinition
 * 
 * Base class for all object definitions. Contains hooks for connecting them to each other and for parsing data.
 * Please note this is still in a rough shape, but it seems to work which is always useful.
 * 
 * @author Richard Greenlees
 */
public abstract class FBXObjectDefinition {
    public long UID;
    public String name;
    public int endOffset;
    
    public FBXObjectDefinition(long inUID, String inName) {       
        UID = inUID;
        if (inName.equals("")) {
            name = "NULL";
        } else {
            name = inName;
        }
    }
    
    public FBXObjectDefinition(long inUID) {
        UID = inUID;
        name = "NULL";
    }
    
    public void setName(String newName) {
        name = newName;
    }
    
    public final void parseData(byte[] inputData, int startPosition) {
        endOffset = startPosition;
        
        byte scope = 1;
        
        while (scope > 0) {
            int nestedNameLength = inputData[endOffset + 12];
            byte[] nestedNameData = FBXNode.retrieveBytesFrom(inputData, nestedNameLength, endOffset + 13);
            String nestedName = new String(nestedNameData);
            
            byte[] numPropertiesData = FBXNode.retrieveBytesFrom(inputData, 4, endOffset + 4);

            int numProperties = ByteBuffer.wrap(numPropertiesData).order(ByteOrder.LITTLE_ENDIAN).getInt();

            endOffset += nestedNameLength + 13;
            
            if (nestedName.equals("")) {
                scope--;
                if (scope <= 0) {
                    break;
                }
            } else {
                
                // TODO: Figure out a better way of deciding which nested nodes contain MORE nested nodes.
                // Tracking the size of each nested element vs. its properties doesn't seem to be reliable
                if (nestedName.equals("Properties60")
                        || nestedName.equals("Properties70")
                        || nestedName.equals("LayerElementUV")
                        || nestedName.equals("LayerElementMaterial")
                        || nestedName.equals("LayerElementTexture")
                        || nestedName.equals("Layer")
                        || nestedName.equals("LayerElement")
                        || nestedName.equals("LayerElementBinormal")
                        || nestedName.equals("LayerElementNormal")
                        || nestedName.equals("LayerElementTangent")
                        || nestedName.equals("LayerElementSmoothing")
                        || nestedName.equals("LayerElementColor")
                        || nestedName.equals("LayerElementVisibility")) {
                    scope++;
                }

            }
            
            FBXProperty[] properties = new FBXProperty[numProperties];
            
            // Get all the properties this nested item contains
            for (int i = 0; i < properties.length; i++) {
                FBXProperty newProp = new FBXProperty(inputData, endOffset);
                endOffset += newProp.dataLength;
                properties[i] = newProp;
            }
            
            // "P" or "Property" means we're inside a Properties60 or Properties70 node, so it's an embedded property
            if (nestedName.equals("P") || nestedName.equals("Property")) {
                readEmbeddedProperty(properties);
            } else {
                readNestedObject(nestedName, properties);
            }
            
        }
    }
    
    /** Called every time a new nested node is parsed */
    protected abstract void readNestedObject(String nestedName, FBXProperty[] properties);
    
    /** Called whenever an embedded property (via Properties60 or Properties70) is encountered */
    protected abstract void readEmbeddedProperty(FBXProperty[] properties);

    public void connect(FBXModelDefinition inModel) {
        
    }

    public void connect(FBXTextureDefinition inTexture) {
        
    }

    public void connect(FBXMaterialDefinition inMaterial) {
        
    }

    public void connect(FBXMediaDefinition inMedia) {
        
    }

    public void connect(FBXLimbNodeDefinition inLimbNode) {
        
    }

    public void connect(FBXSkinDeformerDefinition inSkinDeformer) {
        
    }

    public void connect(FBXCameraDefinition inCamera) {
       
    }

    public void connect(FBXShapeDefinition inShape) {
       
    }

    public void connect(FBXClusterDefinition inCluster) {
        
    }

    public void connect(FBXAnimCurveDefinition inAnimCurve) {
        
    }

    public void connect(FBXAnimCurveNodeDefinition inAnimCurveNode) {
        
    }

    public void connect(FBXAnimLayerDefinition inAnimLayer) {
        
    }

    public void connect(FBXAnimStackDefinition inAnimStack) {
        
    }
    
    // TODO: Sort this mess out. Quick hack until I'm happy with how everything connects up and put a proper process together
    public void connect(FBXObjectDefinition inObject) {
        if (inObject instanceof FBXModelDefinition) {
            connect((FBXModelDefinition) inObject);
        } else if (inObject instanceof FBXTextureDefinition) {
            connect((FBXTextureDefinition) inObject);
        } else if (inObject instanceof FBXMediaDefinition) {
            connect((FBXMediaDefinition) inObject);
        } else if (inObject instanceof FBXMaterialDefinition) {
            connect((FBXMaterialDefinition) inObject);
        } else if (inObject instanceof FBXLimbNodeDefinition) {
            connect((FBXLimbNodeDefinition) inObject);
        } else if (inObject instanceof FBXSkinDeformerDefinition) {
            connect((FBXSkinDeformerDefinition) inObject);
        } else if (inObject instanceof FBXCameraDefinition) {
            connect((FBXCameraDefinition) inObject);
        } else if (inObject instanceof FBXShapeDefinition) {
            connect((FBXShapeDefinition) inObject);
        } else if (inObject instanceof FBXClusterDefinition) {
            connect((FBXClusterDefinition) inObject);
        } else if (inObject instanceof FBXAnimCurveDefinition) {
            connect((FBXAnimCurveDefinition) inObject);
        } else if (inObject instanceof FBXAnimCurveNodeDefinition) {
            connect((FBXAnimCurveNodeDefinition) inObject);
        } else if (inObject instanceof FBXAnimLayerDefinition) {
            connect((FBXAnimLayerDefinition) inObject);
        } else if (inObject instanceof FBXAnimStackDefinition) {
            connect((FBXAnimStackDefinition) inObject);
        }
    }
    
    public final boolean hasUID() {
        return UID > 0;
    }
}
