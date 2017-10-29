package utils;

import processing.core.PVector;

public class InTraingle {

    public static boolean inTraingle(PVector p, PVector a, PVector b, PVector c) {
        boolean res = toLeft(a, b, p);
        if (res != toLeft(b, c, p))
            return false;
        if (res != toLeft(c, a, p))
            return false;
        if (cross(a, b, c) == 0)    //ABC is in one line
            return false;
        return true;
    }

    private static boolean toLeft(PVector p, PVector a, PVector b) {
        return cross(a, b, p) > 0;
    }

    private static int cross(PVector a, PVector b, PVector p) {
        return (int) ((b.x - a.x) * (p.y - a.y) - (b.y - a.y) * (p.x - a.x));
    }

}
