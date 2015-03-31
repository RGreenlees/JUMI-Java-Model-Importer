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
package com.jumi.fbx.objects;

import com.jumi.fbx.node.FBXNode;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * FBXProperty
 * 
 * A single property definition, belonging to a nested item or node
 * 
 * @author Richard Greenlees
 */
public class FBXProperty {

    public char typeCode;
    public String dataType;
    public int dataLength;
    private byte[] binaryData;
    private int dataSizeInBytes;
    private int startPosition;

    public FBXProperty(byte[] inputData, int cursorPosition) {
        startPosition = cursorPosition;
        typeCode = (char) (inputData[startPosition] & 0xFF);

        // Every FBX Property starts with a char indicating what data type it is. Self-explanatory hopefully.
        switch (typeCode) {
            case 'Y':
                dataType = "Short";
                dataSizeInBytes = 2;
                parseValue(inputData);
                break;
            case 'C':
                dataType = "Boolean";
                dataSizeInBytes = 1;
                parseValue(inputData);
                break;
            case 'F':
                dataType = "Float";
                dataSizeInBytes = 4;
                parseValue(inputData);
                break;
            case 'I':
                dataType = "Integer";
                dataSizeInBytes = 4;
                parseValue(inputData);
                break;
            case 'D':
                dataType = "Double";
                dataSizeInBytes = 8;
                parseValue(inputData);
                break;
            case 'L':
                dataType = "Long";
                dataSizeInBytes = 8;
                parseValue(inputData);
                break;
            case 'f':
            case 'i':
                dataType = "Integer Array";
                dataSizeInBytes = 4;
                parseArray(inputData);
                break;
            case 'd':
                dataType = "Double Array";
                dataSizeInBytes = 8;
                parseArray(inputData);
                break;
            case 'l':
                dataType = "Long Array";
                dataSizeInBytes = 8;
                parseArray(inputData);
                break;
            case 'b':
                dataType = "Boolean Array";
                dataSizeInBytes = 1;
                parseArray(inputData);
                break;
            case 'S':
                dataType = "String";
                parseBinary(inputData);
                break;
            case 'R':
                dataType = "Raw Binary Data";
                parseBinary(inputData);
                break;
            default:
                System.err.println("Unknown property type! " + typeCode);
                System.exit(-1);
                break;
        }
    }
    
    /* Simply extract the binary data based on the data size */
    private void parseValue(byte[] inputData) {
        binaryData = FBXNode.retrieveBytesFrom(inputData, dataSizeInBytes, startPosition + 1);
        dataLength = dataSizeInBytes + 1;
    }
    
    /* Slightly different, we need to determine the length of the binary data before we store it */
    private void parseBinary(byte[] inputData) {
        byte[] binaryLength = FBXNode.retrieveBytesFrom(inputData, 4, startPosition + 1);
        int binarySize = ByteBuffer.wrap(binaryLength).order(ByteOrder.LITTLE_ENDIAN).getInt();
        
        binaryData = FBXNode.retrieveBytesFrom(inputData, binarySize, startPosition + 5);

        dataLength = 5 + binarySize;
    }

    /* Slightly tricker. We need to determine the length of the data and whether it is compressed or not. Decompress if necessary */
    private void parseArray(byte[] inputData) {
        byte[] arrayData = null;

        byte[] arrayLengthData = FBXNode.retrieveBytesFrom(inputData, 4, startPosition + 1);
        int arrayLength = ByteBuffer.wrap(arrayLengthData).order(ByteOrder.LITTLE_ENDIAN).getInt();

        byte[] encodingBytes = FBXNode.retrieveBytesFrom(inputData, 4, startPosition + 5);
        int encoding = ByteBuffer.wrap(encodingBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();

        byte[] compressionSizeBytes = FBXNode.retrieveBytesFrom(inputData, 4, startPosition + 9);
        int compressedSize = ByteBuffer.wrap(compressionSizeBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();

        if (encoding == 1) {
            byte[] compressedData = FBXNode.retrieveBytesFrom(inputData, compressedSize, startPosition + 13);
            arrayData = decompressData(compressedData);

        } else {
            arrayData = FBXNode.retrieveBytesFrom(inputData, arrayLength * dataSizeInBytes, startPosition + 13);
        }

        binaryData = arrayData;

        if (encoding == 1) {
            dataLength = compressedSize + 13;
        } else {
            dataLength = (arrayLength * dataSizeInBytes) + 13;
        }
    }

    /* Inflates compressed binary data to give you the final binary data in an array */
    private byte[] decompressData(byte[] compressedData) {

        Inflater decompressor = new Inflater();
        decompressor.setInput(compressedData);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(compressedData.length);
        byte[] buffer = new byte[1024];
        while (!decompressor.finished()) {
            try {
                int count = decompressor.inflate(buffer);
                outputStream.write(buffer, 0, count);
            } catch (DataFormatException e) {
                e.printStackTrace();
            }
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        decompressor.end();
        return outputStream.toByteArray();

    }

    /** Express this property as an array of integers */
    public int[] asIntArray() {
        IntBuffer intBuffer = ByteBuffer.wrap(binaryData).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
        int[] result = new int[intBuffer.remaining()];
        intBuffer.get(result);

        return result;
    }
    
    /** Express this property as an array of longs */
    public long[] asLongArray() {
        LongBuffer longBuffer = ByteBuffer.wrap(binaryData).order(ByteOrder.LITTLE_ENDIAN).asLongBuffer();
        long[] result = new long[longBuffer.remaining()];
        longBuffer.get(result);

        return result;
    }

    /** Express this property as an array of doubles */
    public double[] asDoubleArray() {
        DoubleBuffer doubleBuffer = ByteBuffer.wrap(binaryData).order(ByteOrder.LITTLE_ENDIAN).asDoubleBuffer();
        double[] result = new double[doubleBuffer.remaining()];
        doubleBuffer.get(result);

        return result;
    }

    /** Express this property as an array of floats */
    public float[] asFloatArray() {
        float[] result;
        DoubleBuffer doubleBuffer = ByteBuffer.wrap(binaryData).order(ByteOrder.LITTLE_ENDIAN).asDoubleBuffer();
        double[] interim = new double[doubleBuffer.remaining()];
        doubleBuffer.get(interim);

        result = new float[interim.length];

        for (int i = 0; i < interim.length; i++) {
            result[i] = (float) interim[i];
        }

        return result;
    }

    /** Express this property as a String */
    public String asString() {
        return new String(binaryData);
    }
    
    /** Express this property as a short */
    public short asShort() {
        return ByteBuffer.wrap(binaryData).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    /** Express this property as a long */
    public long asLong() {
        return ByteBuffer.wrap(binaryData).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }
    
    /** Express this property as a double */
    public double asDouble() {
        return ByteBuffer.wrap(binaryData).order(ByteOrder.LITTLE_ENDIAN).getDouble();
    }
    
    /** Express this property as a float */
    public float asFloat() {
        if (dataType.equals("Float")) {
            return ByteBuffer.wrap(binaryData).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        } else {
            return (float) ByteBuffer.wrap(binaryData).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        }
    }
    
    /** Express this property as a boolean */
    public boolean asBoolean() {
        return (binaryData[0] != 0);
    }
    
    /** Express this property as an array of booleans */
    public boolean[] asBooleanArray() {
        boolean[] result = new boolean[binaryData.length];
        for (int i = 0; i < binaryData.length; i++) {
            result[i] = (binaryData[i] != 0);
        }
        return result;
    }
    
    /** Express this property as raw binary */
    public byte[] asByteArray() {
        return binaryData;
    }
    
    /** Express this property as an integer */
    public int asInteger() {
        return ByteBuffer.wrap(binaryData).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    
    public String toString() {
        String result = dataType + ": ";
        
        switch (typeCode) {
            case 'Y':
                result = result + asShort();
                break;
            case 'C':
                result = result + asBoolean();
                break;
            case 'I':
                result = result + asInteger();
                break;
            case 'F':
                result = result + asFloat();
                break;
            case 'D':
                result = result + asDouble();
                break;
            case 'L':
                result = result + asLong();
                break;
            case 'f':
            case 'i':
                result = result + "{ " + asIntArray()[0] + "...}";
                break;
            case 'd':
                result = result + "{ " + asDoubleArray()[0] + "...}";
                break;
            case 'l':
                result = result + "{ " + asLongArray()[0] + " ...}";
                break;
            case 'b':
                result = result + "{ " + asBooleanArray()[0] + " ...}";
                break;
            case 'S':
                result = result + asString();
                break;
            case 'R':
                result = result + "{ BINARY ... (" + binaryData.length + " bytes) }";
                break;
            default:
                result = result + "ERROR! Unknown type";
                break;
        }
        return result;
    }

}
