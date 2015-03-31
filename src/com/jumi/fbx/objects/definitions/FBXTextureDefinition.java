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

import com.jumi.scene.objects.JUMITexture;
import com.jumi.fbx.objects.FBXProperty;

/**
 * FBXTextureDefinition
 * 
 * Container for holding information on a texture definition.
 * 
 * @author Richard Greenlees
 */
public class FBXTextureDefinition extends FBXObjectDefinition {

    public String textureName = "";
    public String fileLocation = "";

    public String mediaType = "";
    public String fileName = "";
    public String relativeFilename = "";

    public byte[] textureData = new byte[0];
    
    public FBXObjectDefinition parent = null;

    public FBXTextureDefinition(long inUID, String inName) {
        super(inUID, inName);
    }
    
    public FBXTextureDefinition(long inUID) {
        super(inUID);
    }

    public void readNestedObject(String nestedName, FBXProperty[] properties) {
        if (nestedName.equals("Type")) {
            mediaType = properties[0].asString();
        } else if (nestedName.equals("TextureName")) {
            textureName = properties[0].asString().substring(0, properties[0].asString().indexOf('\0'));
        } else if (nestedName.equals("FileName")) {
            fileName = properties[0].asString();
        } else if (nestedName.equals("RelativeFilename")) {
            relativeFilename = properties[0].asString();
        }        
    }
    
    public void readEmbeddedProperty(FBXProperty[] properties) {

    }

    public JUMITexture createTexture() {
        JUMITexture result = new JUMITexture();
        result.name = name;
        result.fullFilePath = fileName;
        result.relativeFilename = relativeFilename;
        result.fileName = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.length());
        result.setTextureData(textureData);
        return result;
    }

    @Override
    public void connect(FBXModelDefinition inModel) {
        inModel.textures.add(this);
        parent = inModel;
    }

    @Override
    public void connect(FBXTextureDefinition inTexture) {
        parent = inTexture;
    }
    
    public void connect(FBXLimbNodeDefinition inLimbNode) {
        parent = inLimbNode;
    }

    @Override
    public void connect(FBXMaterialDefinition inMaterial) {
        inMaterial.textures.add(this);
        parent = inMaterial;
    }

    @Override
    public void connect(FBXMediaDefinition inMedia) {
        if (inMedia.content != null && inMedia.content.length > 0) {
            textureData = inMedia.content;
        }
        parent = inMedia;
    }
}
