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
package com.jumi.data;

/**
 * Color
 *
 * A simple container for holding RGBA colour data
 *
 * @author Richard Greenlees
 */
public class Color {
    public float r;
    public float g;
    public float b;
    public float a;
    
    public Color() {
        r = 0.0f;
        g = 0.0f;
        b = 0.0f;
        a = 1.0f;
    }
    
    public Color(float newR, float newG, float newB, float newA) {
        r = newR;
        g = newG;
        b = newB;
        a = newA;
    }
    
    public Color(float newR, float newG, float newB) {
        r = newR;
        g = newG;
        b = newB;
        a = 1.0f;
    }
    
    public String toString() {
        return "{ R=" + r + ", G=" + g + ", B=" + b + ", A=" + a + "}";
    }
}
