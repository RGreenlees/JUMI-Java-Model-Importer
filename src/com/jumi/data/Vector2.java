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
 * Vector2
 * 
 * A simple container for holding 2-dimensional float data (such as UVs)
 * 
 * @author Richard Greenlees
 */
public class Vector2 {
 
    public float x;
    public float y;
    
    public Vector2(float newX, float newY) {
        x = newX;
        y = newY;
    }
    
    public void set(float newX, float newY) {
        x = newX;
        y = newY;
    }
    
    public Vector2() {
        super();
    }
    
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
