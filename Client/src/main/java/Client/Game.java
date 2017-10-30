package Client;

import NettyClient.GameClient;
import pojo.SpaceshipMsg;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;
import utils.InTraingle;

import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;

public class Game extends PApplet {
    Spaceship ship;
    GameClient gameClient;
    Thread sockThread;
    Map<String, Spaceship> otherShips;

    public static void main(String[] args) {
        PApplet.main("Client.Game");
    }

    @Override
    public void settings() {
        size(600, 600);
    }

    @Override
    public void setup() {
        Properties setting = new Properties();
        try {
            String rootPath = System.getProperty("user.dir");
            FileInputStream in = new FileInputStream(rootPath + "/config.properties");
            setting.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        gameClient = new GameClient();
        sockThread = new Thread(() -> {
            try {
                gameClient.start(setting.getProperty("host"), Integer.parseInt(setting.getProperty("port")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        sockThread.start();
        ship = new Spaceship(new PVector(width / 2, height / 2));
        background(255);
        while (gameClient.getChannelFuture() == null || !gameClient.getChannelFuture().isSuccess()) ;
        otherShips = gameClient.gameClientHandler.otherShips;
        ship.name = gameClient.getChannelFuture().channel().localAddress().toString();
    }

    @Override
    public void draw() {
        background(255);
        if (keyPressed || frameCount % 30 == 0) {
            outMessage(this.ship);
        }

        //显示其他飞船信息
        otherShips.forEach((name, ship) -> {
            if (!name.equals(this.ship.name)) {
                ship.display(this);
                if (this.ship.isFire && ship.alive) {
                    detectHit(this.ship, ship);
                }
            } else {
                //仅更新生命信息
                this.ship.alive = ship.alive;
            }
        });
        this.ship.checkEdge(this);
        this.ship.update();
        this.ship.display(this);
    }

    //将飞船信息发送到服务器
    private void outMessage(Spaceship outShip) {
        SpaceshipMsg msg = new SpaceshipMsg();
        msg.setName(outShip.name);
        msg.setRa(outShip.ra);
        msg.setPositionY(outShip.position.y);
        msg.setPositionX(outShip.position.x);
        msg.setFire(outShip.isFire);
        msg.setAlive(outShip.alive);
        gameClient.getChannelFuture().channel().writeAndFlush(msg);
    }

    //检测是否击中
    private void detectHit(Spaceship hitter, Spaceship spaceship) {
        if (hitter.alive && spaceship.alive) {
            float weaponLength = 25;
            float granularity = 8;
            for (int i = 0; i <= weaponLength; i = (int) (i + granularity)) {
                if (InTraingle.inTraingle(
                        new PVector(hitter.position.x + i * sin(hitter.ra) - spaceship.position.x,
                                hitter.position.y - i * cos(hitter.ra) - spaceship.position.y),
                        new PVector(0, 0),
                        new PVector(-15, 30).rotate(spaceship.ra),
                        new PVector(15, 30).rotate(spaceship.ra))) {
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
            ship.veloR(-0.15F);
        }
        if (keyCode == RIGHT) {
            ship.veloR(0.15F);
        }
        if (keyCode == UP) {
            ship.velo();
        }
        //attack
        if (key == 'z') {
            ship.fire(true);
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        if (keyCode == UP) {
            ship.stop();
        }
        if (keyCode == LEFT) {
            ship.veloR(0);
        }
        if (keyCode == RIGHT) {
            ship.veloR(0);
        }
        if (key == 'z') {
            ship.fire(false);
            outMessage(this.ship);
        }
    }

}
