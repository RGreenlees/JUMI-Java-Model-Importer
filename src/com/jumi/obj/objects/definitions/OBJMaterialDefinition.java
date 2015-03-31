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

/**
 *
 * @author RGreenlees
 */
public class OBJMaterialDefinition {
    
    public Color ambientColour = new Color();
    public Color diffuseColour = new Color();
    public Color specularColour = new Color();
    
    public float specularWeight = 0.0f;
    public float alpha = 1.0f;
    
    public int illum = 0;
    
    public String name;
    public String ambientMap = "";
    public String diffuseMap = "";
    public String alphaMap = "";
    public String bumpMap = "";
    public String displaceMap = "";
    public String decalMap = "";
    public String specularMap = "";
    public String specularHighlightMap = "";
    
    public OBJMaterialDefinition(String inName) {
        super();
        name = inName;
    }
    
    public OBJMaterialDefinition() {
        super();
        name = "NULL";
    }
}
