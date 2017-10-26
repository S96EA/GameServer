package NettyClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;

import java.net.InetSocketAddress;

public class GameClient {
    ChannelFuture channelFuture;
    public final GameClientHandler gameClientHandler = new GameClientHandler();

    public void start(String host, int port) throws Exception {
        EpollEventLoopGroup eventLoopGroup = new EpollEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(EpollSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            ch.pipeline().addLast(gameClientHandler);
                        }
                    });
            channelFuture = bootstrap.connect().sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully().sync();
        }
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

    public static void main(String[] args) throws Exception {
        GameClient gameClient = new GameClient();
        gameClient.start("127.0.0.1", 8000);
    }
}
