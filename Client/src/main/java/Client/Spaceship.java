package Client;

import processing.core.PApplet;
import processing.core.PVector;


import static processing.core.PApplet.cos;
import static processing.core.PApplet.sin;

public class Spaceship {
    public PVector position;
    PVector velocity;
    public float ra = 0.00F;
    float vra;
    boolean isFire;

    public Spaceship(PVector position) {
        this.position = position.copy();
        velocity = new PVector();
    }

    void update() {
        position.add(velocity);
        ra += vra;
        if (isFire) {
            velocity.x = 2.5F * sin(ra);
            velocity.y = -2.5F * cos(ra);
        }
    }

    public void velo() {
        isFire = true;
    }

    public void veloR(float f) {
        vra = f;
    }

    public void stop() {
        isFire = false;
        velocity.x = 0;
        velocity.y = 0;
    }

    void display(PApplet applet) {
        applet.pushMatrix();
        applet.translate(position.x, position.y);
        applet.rotate(ra);
        applet.noStroke();
        applet.fill(0);
        applet.triangle(-15, 30, 0, 0, 15, 30);
        if (isFire) applet.fill(255, 0, 0);
        applet.rect(-12, 30, 6, 6);
        applet.rect(6, 30, 6, 6);
        applet.popMatrix();
    }

    void checkEdge(PApplet applet) {
        if (position.x < 0) position.x = applet.width;
        if (position.y < 0) position.y = applet.height;
        if (position.x > applet.width) position.x = 0;
        if (position.y > applet.height) position.y = 0;
    }

}