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

import com.jumi.data.Color;
import com.jumi.scene.objects.JUMIMaterial;
import com.jumi.scene.objects.JUMITexture;
import com.jumi.fbx.objects.FBXProperty;
import java.util.ArrayList;

/**
 * FBXMaterialDefinition
 * 
 * Container for holding information on a material.
 * 
 * @author Richard Greenlees
 */
public class FBXMaterialDefinition extends FBXObjectDefinition {

    public Color ambientColor = new Color(1.f, 1.f, 1.f);
    public Color diffuseColor = new Color(1.f, 1.f, 1.f);
    public Color specularColor = new Color(1.f, 1.f, 1.f);
    public Color emissiveColor = new Color(1.f, 1.f, 1.f);

    public float specularFactor = 1.0f;
    public float shininessExponent = 1.0f;
    public float transparencyFactor = 1.0f;
    public float emissiveFactor = 1.0f;
    public float reflectionFactor = 1.0f;
    public float shininess = 1.0f;
    public float reflectivity = 1.0f;
    public float opacity = 1.0f;

    public ArrayList<FBXTextureDefinition> textures = new ArrayList();

    public FBXMaterialDefinition(long inUID, String inName) {
        super(inUID, inName);
    }

    public FBXMaterialDefinition(long inUID) {
        super(inUID);
    }

    @Override
    public void readEmbeddedProperty(FBXProperty[] properties) {
        String propertyName = properties[0].asString();
        String propertyType = properties[1].asString();

        switch (propertyType) {
            case "Color":
            case "ColorRGB":
                float r = properties[properties.length - 3].asFloat();
                float g = properties[properties.length - 2].asFloat();
                float b = properties[properties.length - 1].asFloat();

                Color newColor = new Color(r, g, b);
                
                switch(propertyName) {
                    case "Ambient":
                    case "AmbientColor":
                        ambientColor = newColor; break;
                    case "Specular":
                    case "SpecularColor":
                        specularColor = newColor; break;
                    case "Diffuse":
                    case "DiffuseColor":
                        diffuseColor = newColor; break;
                    case "Emissive":
                    case "EmissiveColor":
                        emissiveColor = newColor; break;
                    default: break;
                }
                break;
            case "TransparencyFactor":
                transparencyFactor = properties[properties.length - 1].asFloat(); break;
            case "SpecularFactor":
                specularFactor = properties[properties.length - 1].asFloat(); break;
            case "ReflectionFactor":
                reflectionFactor = properties[properties.length - 1].asFloat(); break;
            case "Shininess":
                shininess = properties[properties.length - 1].asFloat(); break;
            case "Opacity":
                opacity = properties[properties.length - 1].asFloat(); break;
            case "Reflectivity":
                reflectivity = properties[properties.length - 1].asFloat(); break;
            default: break;
        }
    }

    @Override
    public void readNestedObject(String nestedName, FBXProperty[] properties) {

    }

    public JUMIMaterial createMaterial() {
        JUMIMaterial result = new JUMIMaterial();

        result.name = name;
        result.ambientColor = ambientColor;
        result.diffuseColor = diffuseColor;
        result.emissiveColor = emissiveColor;
        result.emissiveFactor = emissiveFactor;
        result.opacity = opacity;
        result.reflectionFactor = reflectionFactor;
        result.reflectivity = reflectivity;
        result.shininess = shininess;
        result.shininessExponent = shininessExponent;
        result.specularColor = specularColor;
        result.specularFactor = specularFactor;
        result.transparencyFactor = transparencyFactor;

        result.textures = new JUMITexture[textures.size()];

        for (int i = 0; i < textures.size(); i++) {
            result.textures[i] = textures.get(i).createTexture();
        }
        return result;
    }

    @Override
    public void connect(FBXModelDefinition inModel) {
        inModel.addMaterialDefinition(this);
    }

    @Override
    public void connect(FBXTextureDefinition inTexture) {
        textures.add(inTexture);
    }

}
