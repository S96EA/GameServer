package Client;


import NettyClient.GameClient;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;

public class Game extends PApplet {
    Spaceship ship;
    GameClient gameClient;
    Thread sockThread;

    public static void main(String[] args) {
        PApplet.main("Client.Game");
    }

    @Override
    public void settings() {
        size(600, 600);
    }

    @Override
    public void setup() {
        gameClient = new GameClient();
        sockThread = new Thread(() -> {
            try {
                gameClient.start("127.0.0.1", 8000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        sockThread.start();
        ship = new Spaceship(new PVector(width / 2, height / 2));
        background(255);
        while (gameClient.getChannelFuture() == null || !gameClient.getChannelFuture().isSuccess()) ;
    }

    @Override
    public void draw() {
        background(255);
        gameClient.getChannelFuture().channel()
                .writeAndFlush(Unpooled.copiedBuffer(ship.position.x + "," + ship.position.y + "\n", CharsetUtil.UTF_8));
        synchronized (Lock.class) {
            gameClient.gameClientHandler.otherShips.forEach((addr, ship) -> {
                if (!addr.equals(gameClient.getChannelFuture().channel().localAddress().toString())) {
                    ship.display(this);
                }
            });
        }
        ship.checkEdge(this);
        ship.update();
        ship.display(this);
    }

    @Override
    public void keyPressed() {
        if (keyCode == LEFT) {
            ship.ra -= 0.2;
        }
        if (keyCode == RIGHT) {
            ship.ra += 0.2;
        }
        if (keyCode == UP) {
//            ship.acce();
            ship.velo();
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        if (keyCode == UP) {
            ship.stop();
        }
    }

    @Override
    public void keyReleased() {
        if (keyCode == UP) {
            ship.clearAcce();
        }
    }
}
