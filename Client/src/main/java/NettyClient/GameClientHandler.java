package NettyClient;

import Client.Spaceship;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import pojo.SpaceshipMsg;
import processing.core.PVector;

import java.util.concurrent.ConcurrentHashMap;

public class GameClientHandler extends ChannelInboundHandlerAdapter {
    public ConcurrentHashMap<String, Spaceship> otherShips = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SpaceshipMsg spaceshipMsg = (SpaceshipMsg) msg;
        String name = spaceshipMsg.getName();
        if (otherShips.get(name) == null) {
            otherShips.put(name, new Spaceship(new PVector()));
        }
        Spaceship spaceship = otherShips.get(name);
        spaceship.position.x = spaceshipMsg.getPositionX();
        spaceship.position.y = spaceshipMsg.getPositionY();
        spaceship.angular = spaceshipMsg.getAngular();
        spaceship.isFire = spaceshipMsg.isFire();
        spaceship.alive = spaceshipMsg.isAlive();
        spaceship.name = spaceshipMsg.getName();
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("connect success...");
    }
}
