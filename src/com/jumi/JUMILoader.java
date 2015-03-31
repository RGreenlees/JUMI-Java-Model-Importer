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
package com.jumi;

import com.jumi.fbx.FBXLoader;
import com.jumi.obj.OBJLoader;
import com.jumi.scene.JUMIScene;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * JUMILoader
 *
 * This is the main loading class. Determines the file type being loaded and calls the appropriate loader.
 * Catches any exceptions thrown during the loading process.
 *
 * @author Richard Greenlees
 */
public class JUMILoader {

    /** Load the supplied file and return a standardised JUMIScene data structure
     * 
     * @param filename The file to load
     * @return JUMIScene - A simplified data structure containing key elements
     */
    public static JUMIScene loadModel(String filename) {
        try {
            String fileExtension = filename.substring(filename.indexOf('.') + 1, filename.length()).toUpperCase();
            switch (fileExtension) {
                case "FBX":
                    return FBXLoader.importModel(filename);
                case "OBJ":
                    return OBJLoader.importModel(filename);
                default:
                    return null;
            }
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: JUMILoader failed to find specified file at " + filename);
        } catch (IOException e) {
            System.err.println("ERROR: JUMILoader encountered an issue while parsing file " + filename);
        }

        return null;
    }

}
