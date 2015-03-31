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
package com.jumi.fbx;

import com.jumi.JUMILoader;
import com.jumi.fbx.node.FBXConnectionsNode;
import static com.jumi.fbx.node.FBXNode.retrieveBytesFrom;
import com.jumi.fbx.node.FBXObjectNode;
import com.jumi.fbx.objects.FBXConnection;
import com.jumi.fbx.objects.definitions.FBXModelDefinition;
import com.jumi.fbx.objects.definitions.FBXObjectDefinition;
import com.jumi.fbx.objects.definitions.FBXTextureDefinition;
import com.jumi.scene.JUMIScene;
import com.jumi.scene.objects.JUMIMesh;
import com.jumi.scene.objects.JUMITexture;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * FBXLoader
 * 
 * The main FBX loading class. Can be invoked directly if the user wishes, but is normally invoked by JUMILoader.
 * Parses the supplied FBX binary file and returns a JUMIScene object.
 * 
 * @author Richard Greenlees
 */
public class FBXLoader extends JUMILoader {

    /** Import a FBX binary file, parse it and return a JUMIScene object containing the scene data
     * 
     * @param fileName Location of the FBX file to load
     * @return JUMIScene containing scene data
     * @throws IOException 
     */
    public static JUMIScene importModel(String fileName) throws IOException {
        FBXObjectNode objectsNode = null;
        FBXConnectionsNode connectionsNode = null;

        ArrayList<JUMIMesh> allMeshes = new ArrayList();
        ArrayList<JUMITexture> allTextures = new ArrayList();

        byte[] mybytes = readBytes(fileName);

        // Read bytes 23 - 26 to retrieve version number
        byte[] versionData = retrieveBytesFrom(mybytes, 4, 23);
        int version = ByteBuffer.wrap(versionData).order(ByteOrder.LITTLE_ENDIAN).getInt();

        float versionID = (float) version / 1000.0f;

        if (version < 7100) {
            String file = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
            System.out.println("WARNING: Asset " + file + " uses an older version of the FBX SDK (" + versionID + "), animation data will not be imported.");
            System.out.println("\tFor best results, please use 7.1 (SDK 2010) or later.");
        } else if (version > 7300) {
            String file = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
            System.out.println("WARNING: Asset " + file + " uses a newer version of the FBX SDK (" + versionID + ") than the max supported version (7.3), this may produce unexpected results.");
            System.out.println("\tFor best results, please use 7.3 (SDK 2011).");
        }

        // Start reading the binary data at byte 27, the first byte after the header
        int offset = 27;

        while (true) {
            int endOffset;

            // Retrieve the end point of the next FBX node
            byte[] offsetData = retrieveBytesFrom(mybytes, 4, offset);
            endOffset = ByteBuffer.wrap(offsetData).order(ByteOrder.LITTLE_ENDIAN).getInt();

            // This shouldn't happen but you never know...
            if (endOffset <= 0) {
                break;
            }

            // Retrieve the data for the next node, using the calculated endoffset
            // TODO: Handle this better so we're not storing the data twice in memory (once in mybytes and once here)
            byte[] nextNodeData = retrieveBytesFrom(mybytes, endOffset - offset, offset);

            // Retrieve the length in bytes of the next node's name
            int nextNodeNameLength = nextNodeData[12];
            // Retrieve the name data
            byte[] nextNodeNameData = retrieveBytesFrom(nextNodeData, nextNodeNameLength, 13);

            String nextNodeName = new String(nextNodeNameData);

            // The Objects node holds all the definitions
            if (nextNodeName.equals("Objects")) {
                objectsNode = new FBXObjectNode(nextNodeData, (nextNodeNameLength + 13));
                objectsNode.parseData(nextNodeData, (nextNodeNameLength + 13));
            // The Connections node hooks the objects together to create useful data structures
            } else if (nextNodeName.equals("Connections")) {
                connectionsNode = new FBXConnectionsNode(nextNodeData, (nextNodeNameLength + 13));
                connectionsNode.parseData(nextNodeData, (nextNodeNameLength + 13));
            // This is the deprecated animation system, but is still present. Once we reach this point we've parsed all useful data
            // TODO: Handle this better so we're not reliant on the Takes node to determine when we've finished parsing the file
            } else if (nextNodeName.equals("Takes")) {
                break;
            }

            offset = endOffset;
        }

        // For each connection defined in the Connections node, hook them up
        for (FBXConnection connection : connectionsNode.connections) {
            FBXObjectDefinition a = objectsNode.getConnectable(connection.getLeftUID());
            FBXObjectDefinition b = objectsNode.getConnectable(connection.getRightUID());

            if (a != null && b != null) {
                a.connect(b);
            }
        }

        // Retrieve all the model definitions and turn them into JUMIMeshes
        for (FBXModelDefinition a : objectsNode.getMeshDefinitions()) {
            allMeshes.add(a.createMesh());
        }

        // Retrieve all texture definitions that don't have a parent mesh and turn them into JUMITextures
        for (FBXTextureDefinition a : objectsNode.getTextureDefinitions()) {
            allTextures.add(a.createTexture());
        }

        // Build the scene
        JUMIScene result = new JUMIScene();
        result.addMeshes(allMeshes);
        result.addTextures(allTextures);
        return result;
    }

    /** Read the supplied file and extract the binary data from it */
    protected static byte[] readBytes(String aInputFileName) throws IOException {
        File file = new File(aInputFileName);
        byte[] result = new byte[(int) file.length()];

        int totalBytesRead = 0;
        try (InputStream input = new BufferedInputStream(new FileInputStream(file))) {
            while (totalBytesRead < result.length) {
                int bytesRemaining = result.length - totalBytesRead;
                int bytesRead = input.read(result, totalBytesRead, bytesRemaining);
                if (bytesRead > 0) {
                    totalBytesRead = totalBytesRead + bytesRead;
                }
            }
        }
        
        return result;
    }
    
}
