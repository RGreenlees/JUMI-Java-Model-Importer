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

/**
 *
 * @author RGreenlees
 */
public class JUMISkinDeformer {
    
    String name;

    public JUMISubDeformer[] deformers = new JUMISubDeformer[0];
    
    public JUMISkinDeformer(String inName) {
        name = inName;
    }
    
    public String toString() {
        String result = "Skin Deformer " + name + ":";
        for (int i = 0; i < deformers.length; i++) {
            result = result + "\n\t" + deformers[i].name;
        }
        return result;
    }
    
    public JUMISubDeformer getSubDeformerByIndex(int index) {
        if (index >= deformers.length) {
            return null;
        }
        return deformers[index];
    }
    
    public JUMISubDeformer getSubDeformerByName(String inName) {
        for (JUMISubDeformer a : deformers) {
            if (a.name.equals(inName)) {
                return a;
            }
        }
        return null;
    }
    
}
