package com.plur.kanditag;

import android.graphics.Point;

/**
 * Created by Jim on 2/26/16.
 */
public class Node extends mPoint {

    public mPoint velocity;
    public mPoint netForce;

    public Node() {
        velocity = new mPoint();
        netForce = new mPoint();
    }

}
