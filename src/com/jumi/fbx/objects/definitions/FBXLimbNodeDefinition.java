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

import com.jumi.data.Vector3;
import com.jumi.fbx.objects.FBXProperty;
import com.jumi.scene.objects.JUMIBone;
import java.util.ArrayList;

/**
 * FBXLimbNodeDefinition
 * 
 * Container for holding information on the translation and rotation of a bone
 * 
 * @author Richard Greenlees
 */
public class FBXLimbNodeDefinition extends FBXObjectDefinition {

    public Vector3 localTranslation = new Vector3(0, 0, 0);
    public Vector3 localRotation = new Vector3(0, 0, 0);
    public Vector3 localScaling = new Vector3(0, 0, 0);

    public ArrayList<FBXLimbNodeDefinition> children = new ArrayList();
    public FBXLimbNodeDefinition parent = null;

    public FBXClusterDefinition cluster = null;

    public boolean isRoot = true;

    public FBXLimbNodeDefinition(long inUID, String inName) {
        super(inUID, inName);
    }

    public FBXLimbNodeDefinition(long inUID) {
        super(inUID);
    }

    public JUMIBone createSkeleton() {
        JUMIBone result = getRoot().createBone();
        createBoneHierarchy(result);
        return result;
    }

    public void createBoneHierarchy(JUMIBone parent) {
        for (FBXLimbNodeDefinition child : children) {
            JUMIBone newBone = child.createBone();
            newBone.setParent(parent);
            parent.addChild(newBone);
            child.createBoneHierarchy(newBone);
        }
    }

    public void createChildBones(JUMIBone parent) {
        for (FBXLimbNodeDefinition a : children) {
            parent.addChild(a.createBone());
        }
    }

    public void createDescendantBones(JUMIBone parent) {

    }

    public void readNestedObject(String nestedName, FBXProperty[] properties) {

    }
    
    public void setName(String newName) {
        super.setName(newName);        
    }

    public void readEmbeddedProperty(FBXProperty[] properties) {
        String propertyName = properties[0].asString();

        if (propertyName.equals("Lcl Translation")) {
            float x = properties[properties.length - 3].asFloat();
            float y = properties[properties.length - 2].asFloat();
            float z = properties[properties.length - 1].asFloat();

            localTranslation.set(x, y, z);
        } else if (propertyName.equals("Lcl Rotation")) {
            float x = properties[properties.length - 3].asFloat();
            float y = properties[properties.length - 2].asFloat();
            float z = properties[properties.length - 1].asFloat();

            localRotation.set(x, y, z);
        } else if (propertyName.equals("Lcl Scaling")) {
            float x = properties[properties.length - 3].asFloat();
            float y = properties[properties.length - 2].asFloat();
            float z = properties[properties.length - 1].asFloat();

            localScaling.set(x, y, z);
        }
    }

    public JUMIBone createBone() {
        if (cluster != null) {
            return new JUMIBone(name, cluster.indexes, cluster.weights, cluster.transforms, cluster.transformLinks, localTranslation, localRotation, localScaling);
        } else {
            return new JUMIBone(name, localTranslation, localRotation, localScaling);
        }
    }

    @Override
    public void connect(FBXModelDefinition inModel) {
        //System.out.println("Joining this Limb Node " + name + " to Model " + inModel.name);
        inModel.rootNode = this;
    }

    public void connect(FBXLimbNodeDefinition inLimbNode) {
        //System.out.println("Joining this Limb Node " + name + " to Limb Node " + inLimbNode.name);
        inLimbNode.children.add(this);
        parent = inLimbNode;
        isRoot = false;
    }

    @Override
    public void connect(FBXTextureDefinition inTexture) {
        //System.out.println("Joining this Limb Node " + name + " to Texture " + inTexture.name);
    }

    @Override
    public void connect(FBXMaterialDefinition inMaterial) {
        //System.out.println("Joining this Limb Node " + name + " to Material " + inMaterial.name);
    }

    @Override
    public void connect(FBXMediaDefinition inMedia) {
        //System.out.println("Joining this Limb Node " + name + " to Model " + inMedia.name);
    }

    @Override
    public void connect(FBXSkinDeformerDefinition inSkinDeformer) {
        //System.out.println("Connecting this Limb Node " + name + " to Skin Deformer " + inSkinDeformer.name);
    }

    @Override
    public void connect(FBXCameraDefinition inCamera) {
        //System.out.println("Connecting this Limb Node " + name + " to Camera " + inCamera.name);
    }

    @Override
    public void connect(FBXShapeDefinition inShape) {
        //System.out.println("Connecting this Limb Node " + name + " to Shape Key " + inShape.name);
    }

    @Override
    public void connect(FBXClusterDefinition inCluster) {
        //System.out.println("Connecting this Limb Node " + name + " to Cluster " + inCluster.name);
        inCluster.limbNode = this;
        cluster = inCluster;
    }

    @Override
    public void connect(FBXAnimCurveDefinition inAnimCurve) {
        //System.out.println("Joining this Limb Node " + name + " to Animation Curve " + inAnimCurve.name);
    }

    @Override
    public void connect(FBXAnimCurveNodeDefinition inAnimCurveNode) {
        //System.out.println("Joining this Limb Node " + name + " to Animation Curve Node " + inAnimCurveNode.name);
    }

    @Override
    public void connect(FBXAnimLayerDefinition inAnimLayer) {
        //System.out.println("Joining this Limb Node " + name + " to Animation Layer " + inAnimLayer.name);
    }

    @Override
    public void connect(FBXAnimStackDefinition inAnimStack) {
        //System.out.println("Joining this Limb Node " + name + " to Animation Stack " + inAnimStack.name);
    }

    public String toString() {
        return "Limb Node: " + name + "\n\tTranslation: " + localTranslation + "\n\tRotation: " + localRotation + "\n\tScaling: " + localScaling;
    }

    public FBXLimbNodeDefinition getRoot() {
        if (isRoot || parent == null) {
            return this;
        } else {
            return parent.getRoot();
        }
    }

    public void printSkeleton() {
        if (isRoot || parent == null) {
            printNodeRecursive(0);
        } else {
            getRoot().printNodeRecursive(0);
        }
    }

    public void printNodeRecursive(int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }

        System.out.println("- " + name);

        for (FBXLimbNodeDefinition child : children) {
            child.printNodeRecursive(level + 1);
        }

    }

}
