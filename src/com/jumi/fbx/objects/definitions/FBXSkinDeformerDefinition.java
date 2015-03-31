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
package com.jumi.fbx.objects.definitions;

import com.jumi.scene.objects.JUMISkinDeformer;
import com.jumi.scene.objects.JUMISubDeformer;
import com.jumi.fbx.objects.FBXProperty;
import java.util.ArrayList;

/**
 * FBXSkinDeformerDefinition
 * 
 * Container for holding information on a model's skin.
 * 
 * @author Richard Greenlees
 */
public class FBXSkinDeformerDefinition extends FBXObjectDefinition {

    public float linkDeformAccuracy = 0.f;

    public ArrayList<FBXClusterDefinition> clusters = new ArrayList();

    public FBXSkinDeformerDefinition(long inUID, String inName) {
        super(inUID, inName);
    }

    public FBXSkinDeformerDefinition(long inUID) {
        super(inUID);
    }

    @Override
    public void readNestedObject(String nestedName, FBXProperty[] properties) {
        if (nestedName.equals("Link_DeformAcuracy")) {
            linkDeformAccuracy = properties[0].asLong();
        }
    }

    @Override
    public void readEmbeddedProperty(FBXProperty[] properties) {

    }

    public FBXLimbNodeDefinition getRootNode() {
        for (FBXClusterDefinition a : clusters) {
            if (a.limbNode != null) {
                return a.limbNode.getRoot();
            }
        }
        return null;
    }

    public JUMISkinDeformer createSkinDeformer() {
        JUMISkinDeformer result = new JUMISkinDeformer(name);

        result.deformers = new JUMISubDeformer[clusters.size()];

        for (int i = 0; i < clusters.size(); i++) {
            result.deformers[i] = clusters.get(i).createSubDeformer();
        }

        return result;
    }

    @Override
    public void connect(FBXModelDefinition inModel) {
        inModel.deformer = this;
    }

    @Override
    public void connect(FBXClusterDefinition inCluster) {
        clusters.add(inCluster);
    }

}
