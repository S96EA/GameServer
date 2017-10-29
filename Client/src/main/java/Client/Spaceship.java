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
    boolean isWalk;
    public boolean isFire;
    public boolean alive = true;
    public String name;

    public Spaceship(PVector position) {
        this.position = position.copy();
        velocity = new PVector();
    }

    void update() {
        if (alive) {
            position.add(velocity);
            ra += vra;
            if (isWalk) {
                velocity.x = 2.5F * sin(ra);
                velocity.y = -2.5F * cos(ra);
            }
        }
    }

    public void velo() {
        isWalk = true;
    }

    public void veloR(float f) {
        vra = f;
    }

    public void stop() {
        isWalk = false;
        velocity.x = 0;
        velocity.y = 0;
    }

    void display(PApplet applet) {
        applet.pushMatrix();
        applet.translate(position.x, position.y);
        applet.rotate(ra);
        applet.noStroke();
        if (alive) {
            applet.fill(0);
        } else {
            applet.fill(0, 255, 0);
        }
        applet.triangle(-15, 30, 0, 0, 15, 30);
        if (isWalk) applet.fill(255, 0, 0);
        applet.rect(-12, 30, 6, 6);
        applet.rect(6, 30, 6, 6);
        if (isFire) {
            applet.stroke(255, 50, 50, 255);
            applet.strokeWeight(6);
            applet.line(0, 0, 0, -25);
        }
        applet.popMatrix();
    }

    void checkEdge(PApplet applet) {
        if (position.x < 0) position.x = applet.width;
        if (position.y < 0) position.y = applet.height;
        if (position.x > applet.width) position.x = 0;
        if (position.y > applet.height) position.y = 0;
    }

    public void fire(boolean fire) {
        isFire = fire;
        if (!alive) {
            isFire = false;
        }
    }
}