package Client;

import processing.core.PApplet;
import processing.core.PVector;


import static processing.core.PApplet.cos;
import static processing.core.PApplet.sin;

public class Spaceship {
    public String name;
    public boolean isFire;
    public PVector position;
    PVector velocity;
    public float angular = 0.00F;
    public boolean alive = true;
    float[] speeds = {0, 2.0F, 5F};
    float speedModule = speeds[0];
    float[] angularSpeeds = {0, 0.15F};
    float angularSpeed = angularSpeeds[0];
    float weaponLength = 40;
    boolean isMove;
    int cntBuulet = 120;
    boolean recoverMode;

    public Spaceship(PVector position) {
        this.position = position.copy();
        velocity = new PVector();
    }

    protected Spaceship() {
    }

    void update() {

        if (cntBuulet <= 0) {
            recoverMode = true;
        } else if (cntBuulet == 120) {
            recoverMode = false;
        }
        if (recoverMode) {
            cntBuulet++;
        } else if (isFire) {
            cntBuulet--;
        }
        if (alive) {
            position.add(velocity);
            angular += angularSpeed;
            velocity.x = speedModule * sin(angular);
            velocity.y = -speedModule * cos(angular);
        }
    }

    public void rotateLeft() {
        angularSpeed = -angularSpeeds[1];
    }

    public void rotateRight() {
        angularSpeed = angularSpeeds[1];
    }

    public void rotateOff() {
        angularSpeed = angularSpeeds[0];
    }

    public void speedOn() {
        isMove = true;
        speedModule = speeds[1];
    }

    public void veloR(float f) {
        angularSpeed = f;
    }

    public void speedOff() {
        isMove = false;
        speedModule = speeds[0];
    }

    void display(PApplet applet) {
        applet.pushStyle();
        applet.pushMatrix();
        applet.translate(position.x, position.y);
        applet.rotate(angular);
        applet.noStroke();
        if (alive) {
            applet.fill(0);
        } else {
            applet.fill(0, 255, 0);
        }
        applet.triangle(-15, 30, 0, 0, 15, 30);
        if (speedModule != 0) applet.fill(255, 0, 0);
        applet.rect(-12, 30, 6, 6);
        applet.rect(6, 30, 6, 6);
        if (isFire && !recoverMode) {
            applet.stroke(255, 50, 50, 255);
            applet.strokeWeight(6);
            applet.line(0, 0, 0, -weaponLength);
        }
        applet.popMatrix();
        applet.popStyle();
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

    public void speedUp() {
        if (isMove) {
            speedModule = speeds[2];
        }
    }

    public void speedDown() {
        if (isMove) {
            speedModule = speeds[1];
        } else {
            speedModule = speeds[0];
        }
    }
}