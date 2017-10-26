package NettyClient;

import Client.Spaceship;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import processing.core.PVector;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public class GameClientHandler extends ChannelInboundHandlerAdapter {
    public Map<String, Spaceship> otherShips = new HashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        String[] receive = byteBuf.toString(Charset.forName("UTF-8")).split("#");
        synchronized (Lock.class) {
            if (otherShips.get(receive[0]) == null) {
                otherShips.put(receive[0], new Spaceship(new PVector()));
            }
        }
        String[] position = receive[1].split(",");
        otherShips.get(receive[0]).position.x = Float.parseFloat(position[0]);
        otherShips.get(receive[0]).position.y = Float.parseFloat(position[1]);
        otherShips.get(receive[0]).ra = Float.parseFloat(position[2]);
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("connect success...");
    }

    public ByteBuf stringToByteBuf(String s) {
        return Unpooled.copiedBuffer(s, CharsetUtil.UTF_8);
    }
}
