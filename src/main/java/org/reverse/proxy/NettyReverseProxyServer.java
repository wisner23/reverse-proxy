package org.reverse.proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

public class NettyReverseProxyServer {

    public static void main(String[] args) {
        try{
            SSLEngine engine = NettySslEngine.getSSLContext().createSSLEngine();
            engine.setUseClientMode(false);

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(new NioEventLoopGroup());
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) {
                    ch.pipeline().addLast(
                            new SslHandler(engine),
                            new NettyChannelHandler()
                    );
                }
            });
            bootstrap.bind(80);
        }
        catch(Exception ex){ ex.printStackTrace();}
    }


}
