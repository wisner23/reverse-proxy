package org.reverse.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;


public class NettyChannelHandler extends ChannelInboundHandlerAdapter{

    private Channel outboundChannel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final Channel inboundChannel = ctx.channel();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.channel(inboundChannel.getClass());
        bootstrap.handler(new NettyBackendChannelHandler(inboundChannel));

        ChannelFuture future = bootstrap.connect("0.0.0.0", 8030);
        outboundChannel = future.channel();

        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture f) throws Exception {
                if(f.isSuccess()){
                    inboundChannel.read();
                }else{
                    inboundChannel.close();
                }
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(outboundChannel.isActive()){
            ctx.channel().writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){

        if(outboundChannel.isActive()){
            outboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()){
                        ctx.channel().read();
                    }else{
                        ctx.channel().close();
                    }
                }
            });
        }
    }

}
