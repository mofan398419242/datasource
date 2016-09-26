package me.bayee.datasource.source;

import org.apache.flume.Context;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.conf.Configurables;
import org.apache.flume.instrumentation.SourceCounter;
import org.apache.flume.source.AbstractSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.bootstrap.Bootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created by mofan on 16-9-11.
 */
public class NdeLogTcpSource extends AbstractSource implements Configurable, EventDrivenSource {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private String host;
    private int port;
    private Bootstrap bootstrap;
    private Channel socketChannel;
    private ChannelGroup childrenChannel = new org.jboss.netty.channel.group.DefaultChannelGroup();
    private SourceCounter sourceCounter;

    public void configure(Context context) {
        Configurables.ensureRequiredNonNull(context, new String[]{"port"});

        this.host = context.getString("bind");
        this.port = context.getInteger("port").intValue();

        if (this.sourceCounter == null) {
            this.sourceCounter = new SourceCounter(getName());
        }
    }

    public void start() {
        logger.warn(this + " Starting...");

        this.bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));

        this.bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();

                pipeline.addLast("decoder1", new NdeLogFrameDecoder());
                pipeline.addLast("decoder2", new NdeLogDecoder());
                pipeline.addLast("handler1", new NdeLogHandler(NdeLogTcpSource.this));

                return pipeline;
            }

        });
        this.bootstrap.setOption("child.keepAlive", Boolean.valueOf(true));
        this.bootstrap.setOption("child.tcpNoDelay", Boolean.valueOf(true));
        this.bootstrap.setOption("child.reuseAddress", Boolean.valueOf(true));
        this.bootstrap.setOption("child.receiveBufferSize", Integer.valueOf(409600));

        Channel channel = this.host != null ?
                ((ServerBootstrap) this.bootstrap).bind(new InetSocketAddress(this.host, this.port)) :
                ((ServerBootstrap) this.bootstrap).bind(new InetSocketAddress(this.port));

        setSocketChannel(channel);

        this.sourceCounter.start();
        super.start();
    }

    public void stop() {
        logger.warn(this + " isInterrupted!");
        this.childrenChannel.close().awaitUninterruptibly();
        this.socketChannel.close().awaitUninterruptibly();
        this.bootstrap.releaseExternalResources();

        this.sourceCounter.stop();
        super.stop();

        logger.info("NdeLog TCP Source: " + getName() + " Metrics: " + this.sourceCounter);
    }

    public Channel getSocketChannel() {
        return this.socketChannel;
    }

    public void setSocketChannel(Channel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void addChildChannel(Channel channel) {
        this.childrenChannel.add(channel);
    }

    public void delChildChannel(Channel channel) {
        this.childrenChannel.remove(channel);
    }

    public SourceCounter getSourceCounter() {
        return this.sourceCounter;
    }

    public void setSourceCounter(SourceCounter sourceCounter) {
        this.sourceCounter = sourceCounter;
    }

}
