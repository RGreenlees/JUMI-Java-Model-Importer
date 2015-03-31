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
package com.jumi.obj.objects.definitions;

import com.jumi.data.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author RGreenlees
 */
public class OBJMatLibDefinition {
    
    public ArrayList<OBJMaterialDefinition> materials = new ArrayList();
    private OBJMaterialDefinition currentMaterial = null;

    public OBJMatLibDefinition() {
        super();
    }

    // Parses the supplied MTL
    public void parseMTL(String mtlLocation) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(mtlLocation));

        String line;

        while ((line = reader.readLine()) != null) {
            String[] lineTokens = line.split(" ");
            
            if (lineTokens.length == 0) {
                continue;
            }
            
            // Get rid of those whitespaces!
            switch (lineTokens[0].trim()) {
                // New material definition, add it to the list and set it to be the current material
                case "newmtl":
                    if (lineTokens.length > 1) {
                        OBJMaterialDefinition newMat = new OBJMaterialDefinition(lineTokens[1]);
                        materials.add(newMat);
                        currentMaterial = newMat;
                    }
                    break;
                // Various attributes to be applied to whichever material is current
                case "Ka": currentMaterial.ambientColour = new Color(Float.valueOf(lineTokens[1]), Float.valueOf(lineTokens[2]), Float.valueOf(lineTokens[3])); break;
                case "Kd": currentMaterial.diffuseColour = new Color(Float.valueOf(lineTokens[1]), Float.valueOf(lineTokens[2]), Float.valueOf(lineTokens[3])); break;
                case "Ks": currentMaterial.specularColour = new Color(Float.valueOf(lineTokens[1]), Float.valueOf(lineTokens[2]), Float.valueOf(lineTokens[3])); break;
                case "Ns": currentMaterial.specularWeight = Float.valueOf(lineTokens[1]); break;
                case "d":
                case "Tr": currentMaterial.alpha = Float.valueOf(lineTokens[1]); break;
                case "illum": currentMaterial.illum = Integer.valueOf(lineTokens[1]); break;
                // Various specific textures like diffuse, specular etc. Take the whole line incase the filename contains spaces
                case "map_Ka":
                    currentMaterial.alphaMap = line.substring(line.indexOf(" ") + 1, line.length());
                case "map_Kd":
                    currentMaterial.diffuseMap = line.substring(line.indexOf(" ") + 1, line.length());
                    break;
                case "map_Ks":
                    currentMaterial.specularMap = line.substring(line.indexOf(" ") + 1, line.length());
                    break;
                case "map_Ns":
                    currentMaterial.specularHighlightMap = line.substring(line.indexOf(" ") + 1, line.length());
                    break;
                case "bump":
                case "map_bump":
                    currentMaterial.bumpMap = line.substring(line.indexOf(" ") + 1, line.length());
                    break;
                case "disp":
                case "map_disp":
                    currentMaterial.displaceMap = line.substring(line.indexOf(" ") + 1, line.length());
                    break;
                case "decal":
                case "map_decal":
                    currentMaterial.decalMap = line.substring(line.indexOf(" ") + 1, line.length());
                    break;
                default: break;
            }
        }
    }
    
    public OBJMaterialDefinition getMaterialDefinition(String matName) {
        for (OBJMaterialDefinition a : materials) {
            if (a.name.equals(matName)) {
                return a;
            }
        }
        
        return null;
    }

}
