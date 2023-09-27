package com.ddf.fakeplayer.util;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.Promise;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.RakPing;
import org.cloudburstmc.netty.channel.raknet.RakPong;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.protocol.bedrock.BedrockPong;

import java.net.InetSocketAddress;
import java.util.concurrent.*;

public class PingUtil {
    private static final NioEventLoopGroup PING_EVENT_LOOP_GROUP = new NioEventLoopGroup();

    public static Future<BedrockPong> ping(InetSocketAddress address) {
        return ping(address, 10, TimeUnit.SECONDS);
    }

    public static Future<BedrockPong> ping(InetSocketAddress address, long timeout, TimeUnit timeUnit) {
        // Don't create a new instance of NioEventLoopGroup every time you ping,
        // create it once somewhere and re-use it.
        EventLoop eventLoop = PING_EVENT_LOOP_GROUP.next();
        Promise<BedrockPong> promise = eventLoop.newPromise();

        new Bootstrap()
                .channelFactory(RakChannelFactory.client(NioDatagramChannel.class))
                .group(eventLoop)
                .option(RakChannelOption.RAK_GUID, ThreadLocalRandom.current().nextLong())
                .handler(new PingHandler(promise, timeout, timeUnit))
                .bind(0) // lets the system pick a port
                .addListener((ChannelFuture future) -> {
                    if (future.isSuccess()) {
                        RakPing ping = new RakPing(System.currentTimeMillis(), address);
                        future.channel().writeAndFlush(ping).addListener(future1 -> {
                            if (!future1.isSuccess()) {
                                promise.tryFailure(future1.cause());
                                future.channel().close();
                            }
                        });
                    } else {
                        promise.tryFailure(future.cause());
                        future.channel().close();
                    }
                });

        return promise;
    }

    public static class PingHandler extends ChannelDuplexHandler {
        private final Promise<BedrockPong> future;
        private final long timeout;
        private final TimeUnit timeUnit;
        private ScheduledFuture<?> timeoutFuture;

        public PingHandler(Promise<BedrockPong> future, long timeout, TimeUnit timeUnit) {
            this.future = future;
            this.timeout = timeout;
            this.timeUnit = timeUnit;
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) {
            this.timeoutFuture = ctx.channel().eventLoop().schedule(() -> {
                ctx.channel().close();
                this.future.tryFailure(new TimeoutException());
            }, this.timeout, this.timeUnit);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (!(msg instanceof RakPong)) {
                super.channelRead(ctx, msg);
                return;
            }
            RakPong pong = (RakPong) msg;

            if (this.timeoutFuture != null) {
                this.timeoutFuture.cancel(false);
                this.timeoutFuture = null;
            }

            ctx.channel().close();
            this.future.trySuccess(BedrockPong.fromRakNet(pong.getPongData()));
        }

        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            super.close(ctx, promise);

            if (this.timeoutFuture != null) {
                this.timeoutFuture.cancel(false);
                this.timeoutFuture = null;
            }
        }
    }
}
