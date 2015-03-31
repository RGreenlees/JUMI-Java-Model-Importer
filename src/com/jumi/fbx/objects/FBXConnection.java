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


/**
 * FBXConnection
 * 
 * A container for an FBX connection definition.
 * 
 * @author Richard Greenlees
 */
public class FBXConnection {
    public long leftObjectUID;
    public long rightObjectUID;
    
    public String leftObjectName;
    public String rightObjectName;
    
    public static enum FBXConnectionType  {OBJECT_OBJECT, OBJECT_PROPERTY };
    
    public FBXConnectionType connectionType;
    
    public FBXConnection(FBXConnectionType newConnectionType, long leftUID, long rightUID) {
        connectionType = newConnectionType;
        leftObjectUID = leftUID;
        rightObjectUID = rightUID;
    }
    
    public FBXConnection(FBXConnectionType newConnectionType, String leftUID, String rightUID) {
        connectionType = newConnectionType;
        leftObjectName = leftUID;
        rightObjectName = rightUID;
    }
    
    /** Does this connection reference the given long ID? */
    public boolean containsReferenceTo(long ref) {
        return leftObjectUID == ref || rightObjectUID == ref;
    }
    
    /** Does this connection reference the given String ID? */
    public boolean containsReferenceTo(String ref) {
        return leftObjectName.equals(ref) || rightObjectName.equals(ref);
    }
    
    public String toString() {
        String result = "Connection: ";
        
        if (leftObjectUID > 0) {
            result = result + leftObjectUID + " to ";
        } else {
            result = result + leftObjectName + " to ";
        }
        
        if (rightObjectUID > 0) {
            result = result + rightObjectUID;
        } else {
            result = result + rightObjectName;
        }
        
        return result;
    }
    
    public boolean hasLeftUID() {
        return leftObjectUID > 0;
    }
    
    public boolean hasRightUID() {
        return rightObjectUID > 0;
    }

    public String getLeftUID() {
        if (leftObjectUID > 0) {
            return String.valueOf(leftObjectUID);
        } else {
            return leftObjectName;
        }
    }
    
    public String getRightUID() {
        if (rightObjectUID > 0) {
            return String.valueOf(rightObjectUID);
        } else {
            return rightObjectName;
        }
    }
    
}
