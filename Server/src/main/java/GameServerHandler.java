import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import pojo.SpaceshipMsg;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Sharable
public class GameServerHandler extends ChannelInboundHandlerAdapter {
    List<Channel> channels = new ArrayList<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
        System.out.println(ctx.channel().remoteAddress() + " connected...");
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SpaceshipMsg spaceshipMsg = (SpaceshipMsg) msg;
        spaceshipMsg.setName(ctx.channel().remoteAddress().toString());
        channels.forEach(channel -> channel.writeAndFlush(spaceshipMsg));
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channels.remove(ctx.channel());
        System.out.println(ctx.channel().remoteAddress() + " closed...");
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
        super.exceptionCaught(ctx, cause);
    }

    private ByteBuf stringToByteBuf(String s) {
        return Unpooled.copiedBuffer(s, Charset.forName("UTF-8"));
    }
}
