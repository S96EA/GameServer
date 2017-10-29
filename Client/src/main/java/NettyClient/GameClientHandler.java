package NettyClient;

import Client.Spaceship;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import pojo.SpaceshipMsg;
import processing.core.PVector;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public class GameClientHandler extends ChannelInboundHandlerAdapter {
    public Map<String, Spaceship> otherShips = new HashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SpaceshipMsg spaceshipMsg = (SpaceshipMsg) msg;
        String name = spaceshipMsg.getName();
        synchronized (Lock.class) {
            if (otherShips.get(name) == null) {
                otherShips.put(name, new Spaceship(new PVector()));
            }
        }
        Spaceship spaceship = otherShips.get(name);
        spaceship.position.x = spaceshipMsg.getPositionX();
        spaceship.position.y = spaceshipMsg.getPositionY();
        spaceship.ra = spaceshipMsg.getRa();
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("connect success...");
    }
}
