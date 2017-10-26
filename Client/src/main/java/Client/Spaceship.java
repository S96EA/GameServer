package Client;

import processing.core.PApplet;
import processing.core.PVector;


import static processing.core.PApplet.cos;
import static processing.core.PApplet.sin;

public class Spaceship {
    public PVector position;
    PVector velocity;
    PVector acceleration;
    float ra = 0.00F;
    boolean isFire;

    public Spaceship(PVector position) {
        this.position = position.get();
        acceleration = new PVector();
        velocity = new PVector();
    }

    void update() {
        velocity.add(acceleration);
        position.add(velocity);
    }

    void acce() {
        acceleration.x = 0.01F * sin(ra);
        acceleration.y = -0.01F * cos(ra);
        isFire = true;
    }

    public void velo() {
        velocity.x = 1.5F * sin(ra);
        velocity.y = -1.5F * cos(ra);
        isFire = true;
    }

    public void stop() {
        isFire = false;
        velocity.x = 0;
        velocity.y = 0;
    }

    void clearAcce() {
        acceleration.mult(0);
        isFire = false;
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