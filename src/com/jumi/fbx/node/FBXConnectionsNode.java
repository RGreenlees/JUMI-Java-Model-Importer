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

import com.jumi.fbx.objects.FBXConnection;
import com.jumi.fbx.objects.FBXProperty;
import java.util.ArrayList;

/**
 * FBXConnectionsNode
 * 
 * Parses the FBX Connections node data to produce a set of object connections
 * 
 * @author RGreenlees
 */
public class FBXConnectionsNode extends FBXNode {

    public ArrayList<FBXConnection> connections = new ArrayList();

    public FBXConnectionsNode(byte[] inputData, int propertyOffset) {
        super(inputData, propertyOffset);
    }

    /** Method to parse the binary data for the Connections node
     * @param inputData The binary data being parsed
     * @param propertyOffset Where to start parsing from */
    @Override
    public void parseData(byte[] inputData, int propertyOffset) {

        // Allow for 13 null bytes at the end of the node (standard in FBX files)
        while (sizeInBytes - cursorPosition > 13) {
            
            // Determine the name of the nested node. It should never be anything except "C" or "Connect" but you never know
            int nestedNameLength = inputData[cursorPosition + 12];
            byte[] nestedNameData = FBXNode.retrieveBytesFrom(inputData, nestedNameLength, cursorPosition + 13);
            String nestedName = new String(nestedNameData);
            

            // Find out how many properties we have. Should always be 3 but again, you never know
            int nestedProperties = getNumProperties(inputData, cursorPosition);  
            

            FBXProperty[] connectionProperties = new FBXProperty[nestedProperties];
            cursorPosition += nestedNameLength + 13;
            
            /* Parse all of the properties of this nested item. The first property indicates if it's
               connecting two objects ("OO") or an object to a property ("OP"). The next two properties
               are the UIDs of the two objects or object and property to hook up */
            for (int i = 0; i < nestedProperties; i++) {
                FBXProperty newProp = new FBXProperty(inputData, cursorPosition);
                cursorPosition += newProp.dataLength;
                connectionProperties[i] = newProp;
            }

            // Determine the two objects to connect and create a connection object for them to be handled later
            if (nestedName.equals("C") || nestedName.equals("Connect")) {
                switch (connectionProperties[0].asString()) {
                    case "OO":
                        if (connectionProperties[1].dataType.equals("Long")) {
                            connections.add(new FBXConnection(FBXConnection.FBXConnectionType.OBJECT_OBJECT, connectionProperties[1].asLong(), connectionProperties[2].asLong()));
                        } else {
                            String leftID = connectionProperties[1].asString().substring(0, connectionProperties[1].asString().indexOf('\0'));
                            String rightID = connectionProperties[2].asString().substring(0, connectionProperties[2].asString().indexOf('\0'));
                            connections.add(new FBXConnection(FBXConnection.FBXConnectionType.OBJECT_OBJECT, leftID, rightID));
                        }   break;
                    case "OP":
                        if (connectionProperties[1].dataType.equals("Long")) {
                            connections.add(new FBXConnection(FBXConnection.FBXConnectionType.OBJECT_PROPERTY, connectionProperties[1].asLong(), connectionProperties[2].asLong()));
                        } else {
                            String leftID = connectionProperties[1].asString().substring(0, connectionProperties[1].asString().indexOf('\0'));
                            String rightID = connectionProperties[2].asString().substring(0, connectionProperties[2].asString().indexOf('\0'));
                            connections.add(new FBXConnection(FBXConnection.FBXConnectionType.OBJECT_PROPERTY, leftID, rightID));
                    }   break;
                }
            }
        }
    }


}
