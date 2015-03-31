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

import com.jumi.data.Color;

/**
 *
 * @author RGreenlees
 */
public class JUMIMaterial {

    public String name;

    public Color ambientColor = new Color();
    public Color diffuseColor = new Color();
    public Color specularColor = new Color();
    public Color emissiveColor = new Color();

    public float specularFactor;
    public float shininessExponent;
    public float transparencyFactor;
    public float emissiveFactor;
    public float reflectionFactor;
    public float shininess;
    public float reflectivity;
    public float opacity;
    
    public JUMITexture diffuseTexture = null;
    public JUMITexture normalTexture = null;
    public JUMITexture specularTexture = null;

    public JUMITexture[] textures = new JUMITexture[0];

    public String toString() {
        String result = "JUMIMaterial:\n\tName: " + name
                + "\n\tAmbient Color: " + ambientColor
                + "\n\tDiffuse Color: " + diffuseColor
                + "\n\tSpecular Color: " + specularColor + ", Factor = " + specularFactor
                + "\n\tEmissive Color: " + emissiveColor
                + "\n\tDiffuse Texture: " + ((diffuseTexture != null) ? diffuseTexture.name : "None") 
                + "\n\tNormal Texture: " + ((normalTexture != null) ? normalTexture.name : "None") 
                + "\n\tSpecular Texture: " + ((specularTexture != null) ? specularTexture.name : "None") 
                + "\n\tUnassociated Textures: " + textures.length;

        for (JUMITexture a : textures) {
            result = result + "\n\t\t" + a.name;
        }

        return result;
    }
    
    /** Returns the texture at the supplied index for the unsorted textures only */
    public JUMITexture getTextureByIndex(int index) {
        if (index >= textures.length) {
            return null;
        }
        return textures[index];
    }
    
    /** Returns the diffuse texture for this material, or null if none exists */
    public JUMITexture getDiffuseTexture() {
        return diffuseTexture;
    }
    
    /** Returns the specular texture for this material, or null if none exists */
    public JUMITexture getSpecularTexture() {
        return specularTexture;
    }
    
    /** Returns the normal texture for this material, or null if none exists */
    public JUMITexture getNormalTexture() {
        return normalTexture;
    }
    
    /** Searches all textures, including unsorted, for the given name */
    public JUMITexture getTextureByName(String name) {
        
        if (diffuseTexture.name.equals(name)) {
            return diffuseTexture;
        }
        
        if (specularTexture.name.equals(name)) {
            return specularTexture;
        }
        
        if (normalTexture.name.equals(name)) {
            return normalTexture;
        }
        
        for (JUMITexture a : textures) {
            if (a.name.equals(name)) {
                return a;
            }
        }
        return null;
    }
}
