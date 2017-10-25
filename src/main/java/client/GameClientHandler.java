package client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.util.Scanner;

public class GameClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println(byteBuf.toString(Charset.forName("UTF-8")));
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("connect success...");
        new Thread(() -> {
            while (true) {
                Scanner scanner = new Scanner(System.in);
                String cmd = scanner.nextLine();
                if (cmd.trim().equals("exit")) {
                    break;
                }
                ctx.writeAndFlush(stringToByteBuf(cmd));
            }
            ctx.close();
        }).start();
    }


    public ByteBuf stringToByteBuf(String s) {
        return Unpooled.copiedBuffer(s, CharsetUtil.UTF_8);
    }
}
