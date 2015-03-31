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
package com.jumi.fbx.node;


import com.jumi.fbx.objects.FBXProperty;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * FBXNode
 * 
 * Base class for Object and Connection nodes
 * 
 * @author Richard Greenlees
 */
public abstract class FBXNode {

    public int endOffset;
    public int numProperties;
    public int propertyListLength;
    public int cursorPosition;
    public int nameLength;
    public String name;
    public int sizeInBytes;
    
    public ArrayList<FBXProperty> properties = new ArrayList();
    
    // Constructor. Sets up some useful info about the node
    public FBXNode(byte[] inputData, int propertyOffset) {
        sizeInBytes = inputData.length;

        numProperties = getNumProperties(inputData);

        int propertyStartOffset = propertyOffset;

        for (int i = 0; i < numProperties; i++) {
            FBXProperty newProp = new FBXProperty(inputData, propertyStartOffset);
            propertyStartOffset += newProp.dataLength;
            properties.add(newProp);
        }
        
        cursorPosition = propertyStartOffset;
    }
   
    public abstract void parseData(byte[] inputData, int propertyOffset);

    // Helper method to extract the data specific to this node
    public final byte[] extractRawData(byte[] inputData, int startPosition, int endOffset) {
        byte[] rawData = new byte[endOffset - startPosition];

        for (int i = 0; i < rawData.length; i++) {
            rawData[i] = inputData[startPosition + i];
        }

        return rawData;
    }

    public final int getNumProperties(byte[] inputData) {
        byte[] bytes = retrieveBytesFrom(inputData, 4, 4);
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    
    public final int getNumProperties(byte[] inputData, int startPosition) {
        byte[] bytes = retrieveBytesFrom(inputData, 4, 4+startPosition);
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public final String getName(byte[] inputData) {
        nameLength = getNameLength(inputData);
        return new String(retrieveBytesFrom(inputData, nameLength, 13));
    }

    public final int getNameLength(byte[] inputData) {
        return inputData[12];
    }

    public final int getPropertyListLength(byte[] inputData) {
        byte[] bytes = retrieveBytesFrom(inputData, 4, 8);
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public final int getEndOffset(byte[] inputData, int startPosition) {
        byte[] result = retrieveBytesFrom(inputData, 4, startPosition);
        return ByteBuffer.wrap(result).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public final static byte[] retrieveBytesFrom(byte[] inputData, int numBytes, int offSet) {
        byte[] result = new byte[numBytes];

        for (int i = offSet; i < numBytes + offSet; i++) {
            result[i - offSet] = inputData[i];
        }

        return result;
    }
}
