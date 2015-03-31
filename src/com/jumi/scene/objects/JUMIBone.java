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
package com.jumi.scene.objects;

import com.jumi.data.Vector3;
import java.util.ArrayList;

/**
 *
 * @author RGreenlees
 */
public class JUMIBone {

    private int[] indices = new int[0];
    private float[] weights = new float[0];
    private float[] transforms = new float[0];
    private float[] transformLinks = new float[0];

    private JUMIBone parent;
    private final ArrayList<JUMIBone> children = new ArrayList();

    private Vector3 localTranslation = new Vector3();
    private Vector3 localRotation = new Vector3();
    private Vector3 localScaling = new Vector3();

    private String name;

    public JUMIBone() {
        super();
    }

    public JUMIBone(String inName) {
        name = inName;
    }

    public JUMIBone(String inName, int[] inIndices, float[] inWeights, float[] inTransforms, float[] inTransformLinks, Vector3 inTranslation, Vector3 inRotation, Vector3 inScaling) {
        name = inName;
        indices = inIndices;
        weights = inWeights;
        transforms = inTransforms;
        transformLinks = inTransformLinks;
        localTranslation = inTranslation;
        localRotation = inRotation;
        localScaling = inScaling;
    }
    
    public Vector3 getLocalScaling() {
        return localScaling;
    }
    
    public JUMIBone(String inName, Vector3 inTranslation, Vector3 inRotation, Vector3 inScaling) {
        name = inName;
        localTranslation = inTranslation;
        localRotation = inRotation;
        localScaling = inScaling;
    }
    
    public Vector3 getLocalTranslation() {
        return localTranslation;
    }
    
    public Vector3 getLocalRotation() {
        return localRotation;
    }
    
    public float[] getTransforms() {
        return transforms;
    }
    
    public float[] getTransformLinks() {
        return transformLinks;
    }

    public void setParent(JUMIBone newParent) {
        parent = newParent;
    }

    public void addChild(JUMIBone newChild) {
        children.add(newChild);
        newChild.setParent(this);
    }

    public void removeChild(JUMIBone childToRemove) {
        children.remove(childToRemove);
        childToRemove.setParent(null);
    }

    public void removeChild(String childName) {
        int index = -1;
        for (JUMIBone a : children) {
            if (a.name.equals(childName)) {
                index = children.indexOf(a);
                break;
            }
        }

        if (index > -1) {
            children.remove(index);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public int[] getVertexIndices() {
        return indices;
    }

    public float[] getWeights() {
        return weights;
    }

    public JUMIBone getParent() {
        return parent;
    }

    public JUMIBone[] getChildren() {
        return children.toArray(new JUMIBone[children.size()]);
    }

    public JUMIBone[] getDescendants() {
        ArrayList<JUMIBone> allBones = new ArrayList();

        addDescendantsToList(allBones);

        JUMIBone[] result = new JUMIBone[allBones.size()];
        allBones.toArray(result);
        return result;
    }

    public JUMIBone[] getFullSkeleton() {
        ArrayList<JUMIBone> allBones = new ArrayList();
        JUMIBone root = getRoot();
        allBones.add(root);
        root.addDescendantsToList(allBones);

        JUMIBone[] result = new JUMIBone[allBones.size()];
        allBones.toArray(result);
        return result;
    }

    public JUMIBone getRoot() {
        if (parent == null) {
            return this;
        } else {
            return parent.getRoot();
        }
    }

    private void addDescendantsToList(ArrayList<JUMIBone> boneList) {
        for (JUMIBone a : children) {
            boneList.add(a);
            a.addDescendantsToList(boneList);
        }
    }
    
    public JUMIBone findDescendantByName(String inName) {
        for (JUMIBone a : children) {
            if (a.getName().equals(inName)) {
                return a;
            }
            a.findDescendantByName(inName);
        }
        return null;
    }
    
    public String toString() {
        return "JUMIBone:\n\tName: " + name + "\n\tParent: " + ((parent != null) ? parent.name : "None") + "\n\tChildren: " + children.size() + "\n\tDescendants: " + getDescendants().length;
    }

}
