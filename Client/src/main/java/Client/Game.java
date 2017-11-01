package Client;

import NettyClient.GameClient;
import pojo.SpaceshipMsg;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PShapeSVG;
import processing.core.PVector;
import processing.event.KeyEvent;
import utils.InTraingle;

import java.util.Map;

public class Game extends PApplet {
    Spaceship myShip;
    GameClient gameClient;
    Thread sockThread;
    Map<String, Spaceship> otherShips;
    double[][] ells = {{1009.0318, 623.7608},
            {734.2482, 534.18054},
            {188.86328, 530.5219},
            {101.76337, 667.459},
            {535.6365, 760.9304},
            {638.4929, 333.65817},
            {860.2104, 526.9345},
            {768.6866, 430.33615},
            {658.1058, 89.11095},
            {730.5657, 565.74036}};

    public static void main(String[] args) {
        PApplet.main("Client.Game");
    }

    @Override
    public void settings() {
        size(1200, 800);
    }

    @Override
    public void setup() {
//        Properties setting = new Properties();
//        try {
//            String rootPath = System.getProperty("user.dir");
//            FileInputStream in = new FileInputStream(rootPath + "/config.properties");
//            setting.load(in);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        gameClient = new GameClient();
        sockThread = new Thread(() -> {
            try {
//                gameClient.start(setting.getProperty("host"), Integer.parseInt(setting.getProperty("port")));
                gameClient.start("127.0.0.1", 8000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        sockThread.start();
        myShip = new Spaceship(new PVector(width / 2, height / 2));
        background(200);
        while (gameClient.getChannelFuture() == null || !gameClient.getChannelFuture().isSuccess()) ;
        otherShips = gameClient.gameClientHandler.otherShips;
        myShip.name = gameClient.getChannelFuture().channel().localAddress().toString();
        textSize(30);
    }

    @Override
    public void draw() {
        background(200);
        pushStyle();
        noStroke();
        fill(0);
        for (int i = 0; i < 10; i++) {
            ellipse((float) ells[i][0], (float) ells[i][1], 60, 60);
        }
        popStyle();

        if (keyPressed || frameCount % 30 == 0) {
            outMessage(this.myShip);
        }

        //显示其他飞船信息
        otherShips.forEach((name, other) -> {
            if (!name.equals(this.myShip.name)) {
                other.display(this);
                if (this.myShip.isFire && !this.myShip.recoverMode && other.alive) {
                    detectHit(this.myShip, other);
                }
            } else {
                //仅更新生命信息
                this.myShip.alive = other.alive;
            }
        });
        text(myShip.cntBuulet, 1100, 750);
        this.myShip.checkEdge(this);
        this.myShip.update();
        this.myShip.display(this);
    }

    //将飞船信息发送到服务器
    private void outMessage(Spaceship outShip) {
        SpaceshipMsg msg = new SpaceshipMsg();
        msg.setName(outShip.name);
        msg.setAngular(outShip.angular);
        msg.setPositionY(outShip.position.y);
        msg.setPositionX(outShip.position.x);
        msg.setFire(outShip.isFire && !outShip.recoverMode);
        msg.setAlive(outShip.alive);
        gameClient.getChannelFuture().channel().writeAndFlush(msg);
    }

    //检测是否击中
    private void detectHit(Spaceship hitter, Spaceship spaceship) {
        if (hitter.alive && spaceship.alive) {
            float granularity = 8;
            for (int i = 0; i <= hitter.weaponLength; i = (int) (i + granularity)) {
                if (InTraingle.inTraingle(
                        new PVector(hitter.position.x + i * sin(hitter.angular) - spaceship.position.x,
                                hitter.position.y - i * cos(hitter.angular) - spaceship.position.y),
                        new PVector(0, 0),
                        new PVector(-15, 30).rotate(spaceship.angular),
                        new PVector(15, 30).rotate(spaceship.angular))) {
                    spaceship.alive = false;
                    outMessage(spaceship);
                    break;
                }
            }
        }
    }

    @Override
    public void keyPressed() {
        if (keyCode == LEFT) {
            myShip.rotateLeft();
        }
        if (keyCode == RIGHT) {
            myShip.rotateRight();
        }
        if (keyCode == UP) {
            myShip.speedOn();
        }
        if (key == 'x') {
            myShip.speedUp();
        }
        //attack
        if (key == 'z') {
            myShip.fire(true);
        }
    }


    @Override
    public void keyReleased(KeyEvent event) {
        if (keyCode == UP) {
            myShip.speedOff();
        }
        if (keyCode == LEFT) {
            myShip.rotateOff();
        }
        if (keyCode == RIGHT) {
            myShip.rotateOff();
        }
        if (key == 'x') {
            myShip.speedDown();
        }
        if (key == 'z') {
            myShip.fire(false);
            outMessage(this.myShip);
        }
    }

}
