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
package com.jumi.scene;


import com.jumi.scene.objects.JUMIMesh;
import com.jumi.scene.objects.JUMITexture;
import java.util.ArrayList;
import java.util.EnumSet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author RGreenlees
 */
public class JUMIScene {
    
    JUMIMesh[] meshes = new JUMIMesh[0];
    JUMITexture[] textures = new JUMITexture[0];
    
    public static enum MeshAttributes {
        VERTICES, NORMALS, TEXTURECOORDINATES, TANGENTS;
        EnumSet<MeshAttributes> ALLATTRIBUTES = EnumSet.allOf(MeshAttributes.class);
    }
    
    public void addMeshes(ArrayList<JUMIMesh> newMeshes) {
        meshes = new JUMIMesh[newMeshes.size()];
        for (int i = 0; i < meshes.length; i++) {
            meshes[i] = newMeshes.get(i);
        }
    }
    
    public void addTextures(ArrayList<JUMITexture> newTextures) {
        textures = new JUMITexture[newTextures.size()];
        for (int i = 0; i < textures.length; i++) {
            textures[i] = newTextures.get(i);
        }
    }
    
    public JUMIMesh getMeshByName(String name) {
        for (JUMIMesh i : meshes) {
            if (i.name.equals(name)) {
                return i;
            }
        }
        return null;
    }
    
    public JUMIMesh getMeshByIndex(int index) {
        if (index >= meshes.length) {
            return null;
        }        
        return meshes[index];
    }
    
    public JUMITexture getTextureByIndex(int index) {
        if (index >= textures.length) {
            return null;
        }        
        return textures[index];
    }
    
    public JUMIMesh[] getAllMeshes() {
        return meshes;
    }
    
    public String toString() {
        String result = "JUMIScene: " + "\n\tMeshes: ";
        for (int i = 0; i < meshes.length; i++) {
            result = result + "\n\t\t" + meshes[i].name;
        }
        
        result = result + "\n\tTextures:";
        
        for (int i = 0; i < textures.length; i++) {
            result = result + "\n\t\t" + textures[i].name;
        }       
        
        return result;
    }
    
    public int numMeshes() {
        return meshes.length;
    }
    
}
