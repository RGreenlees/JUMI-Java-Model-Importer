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
 * FBXShapeDefinition
 * 
 * Container for holding information on a morph target. Not currently in use.
 * 
 * @author RGreenlees
 */
public class FBXShapeDefinition extends FBXObjectDefinition {

    public FBXShapeDefinition(long inUID, String inName) {
        super(inUID, inName);
    }
    
    public FBXShapeDefinition(long inUID) {
        super(inUID);
    }
    
    @Override
    public void readNestedObject(String nestedName, FBXProperty[] properties) {
        
    }

    @Override
    public void readEmbeddedProperty(FBXProperty[] properties) {
        
    }

    
}
