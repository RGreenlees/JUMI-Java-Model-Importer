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
 * FBXMediaDefinition
 * 
 * Container for holding information on a texture file. Currently just used for embedded data.
 * 
 * @author Richard Greenlees
 */
public class FBXMediaDefinition extends FBXObjectDefinition {

    byte[] content = new byte[0];

    public FBXMediaDefinition(long inUID, String inName) {
        super(inUID, inName);
    }
    
    public FBXMediaDefinition(long inUID) {
        super(inUID);
    }

    public void readNestedObject(String nestedName, FBXProperty[] properties) {
        if (nestedName.equals("Content")) {
            if (properties.length > 0) {
                content = properties[0].asByteArray();
            }
        }
    }
    
    public void readEmbeddedProperty(FBXProperty[] properties) {
        
    }

    @Override
    public void connect(FBXTextureDefinition inTexture) {
        if (content != null && content.length > 0) {
            inTexture.textureData = content;
        }
    }
}
