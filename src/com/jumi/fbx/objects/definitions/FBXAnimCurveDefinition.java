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

/**
 * FBXAnimCurveDefinition
 * 
 * Container for FBX data related to an Animation Curve. Not currently used for anything.
 * 
 * @author Richard Greenlees
 */
public class FBXAnimCurveDefinition extends FBXObjectDefinition {
    
    public static final long FBX_TC_MILLIS = 46186158L;
    
    long[] keyTime = new long[0];
    int[] keyValueFloat = new int[0];
    int[] keyAttrFlags = new int[0];
    int[] keyAttrDataFloat = new int[0];
    int[] keyAttrRefCount = new int[0];
    
    public FBXAnimCurveDefinition(long UID, String inName) {
        super(UID, inName);
    }
    
    public FBXAnimCurveDefinition(long inUID) {
        super(inUID);
    }
    
    @Override
    public void readNestedObject(String nestedName, FBXProperty[] properties) {
        if (nestedName.equals("KeyTime")) {
            keyTime = properties[0].asLongArray();            
        } else if (nestedName.equals("KeyValueFloat")) {
            keyValueFloat = properties[0].asIntArray();
        } else if (nestedName.equals("KeyAttrFlags")) {
            keyAttrFlags = properties[0].asIntArray();
        } else if (nestedName.equals("KeyAttrDataFloat")) {
            keyAttrDataFloat = properties[0].asIntArray();
        } else if (nestedName.equals("KeyAttrRefCount")) {
            keyAttrRefCount = properties[0].asIntArray();
        }
    }

    @Override
    public void readEmbeddedProperty(FBXProperty[] properties) {
        
    }


    @Override
    public void connect(FBXAnimCurveNodeDefinition inAnimCurveNode) {
        inAnimCurveNode.animationCurve = this;
    }
    
}
