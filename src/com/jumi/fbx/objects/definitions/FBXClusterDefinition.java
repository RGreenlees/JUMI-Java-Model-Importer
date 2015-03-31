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

import com.jumi.scene.objects.JUMISubDeformer;
import com.jumi.fbx.objects.FBXProperty;

/**
 * FBXClusterDefinition
 * 
 * A container for holding information on the weights and indices for a bone.
 * 
 * @author Richard Greenlees
 */
public class FBXClusterDefinition extends FBXObjectDefinition {
    
    public int[] indexes = new int[0];
    public float[] weights = new float[0];
    public float[] transforms = new float[0];
    public float[] transformLinks = new float[0];
    
    public FBXLimbNodeDefinition limbNode;
    
    public FBXClusterDefinition(long UID, String name) {
        super(UID, name);
    }
    
    public FBXClusterDefinition(long inUID) {
        super(inUID);
    }

    @Override
    public void readNestedObject(String nestedName, FBXProperty[] properties) {
        if (nestedName.equals("Indexes") && properties.length > 0) {
            if (properties[0].typeCode == 'i' || properties[0].typeCode == 'f') {
                indexes = properties[0].asIntArray();
            } else if (properties[0].typeCode == 'I' || properties[0].typeCode == 'F') {
                indexes = new int[properties.length];
                for (int i = 0; i < properties.length; i++) {
                    indexes[i] = properties[i].asInteger();
                }
            } else {
                System.err.println("Invalid indices data type! Expected: integer or array of integers, actual: " + properties[0].dataType);
            }
        } else if (nestedName.equals("Weights") && properties.length > 0) {
            if (properties[0].typeCode == 'd') {
                weights = properties[0].asFloatArray();
            } else if (properties[0].typeCode == 'D') {
                weights = new float[properties.length];
                for (int i = 0; i < properties.length; i++) {
                    weights[i] = (float) properties[i].asDouble();
                }
            } else {
                System.err.println("Invalid UV data type! Expected: double or array of doubles, actual: " + properties[0].dataType);
            }
        } else if (nestedName.equals("Transform") && properties.length > 0) {
            if (properties[0].typeCode == 'd') {
                transforms = properties[0].asFloatArray();
            } else if (properties[0].typeCode == 'D') {
                transforms = new float[properties.length];
                for (int i = 0; i < properties.length; i++) {
                    transforms[i] = (float) properties[i].asDouble();
                }
            } else {
                System.err.println("Invalid UV data type! Expected: double or array of doubles, actual: " + properties[0].dataType);
            }
        } else if (nestedName.equals("TransformLink") && properties.length > 0) {
            if (properties[0].typeCode == 'd') {
                transformLinks = properties[0].asFloatArray();
            } else if (properties[0].typeCode == 'D') {
                transformLinks = new float[properties.length];
                for (int i = 0; i < properties.length; i++) {
                    transformLinks[i] = (float) properties[i].asDouble();
                }
            } else {
                System.err.println("Invalid UV data type! Expected: double or array of doubles, actual: " + properties[0].dataType);
            }
        }
    }
    
    public JUMISubDeformer createSubDeformer() {
        JUMISubDeformer result = new JUMISubDeformer(name);
        
        result.indexes = indexes;
        result.weights = weights;
        result.transforms = transforms;
        result.transformLinks = transformLinks;
        
        return result;
    }

    @Override
    public void readEmbeddedProperty(FBXProperty[] properties) {
        
    }

    @Override
    public void connect(FBXLimbNodeDefinition inLimbNode) {
        limbNode = inLimbNode;
        limbNode.cluster = this;
    }
    
    @Override
    public void connect(FBXSkinDeformerDefinition inSkinDeformer) {
        inSkinDeformer.clusters.add(this);
    }
    
}
