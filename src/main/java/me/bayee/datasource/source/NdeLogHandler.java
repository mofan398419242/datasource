package me.bayee.datasource.source;

import me.bayee.datasource.util.EventTools;
import org.apache.flume.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;

/**
 * Created by mofan on 16-9-11.
 */
public class NdeLogHandler extends SimpleChannelUpstreamHandler {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private final NdeLogTcpSource source;

    public NdeLogHandler(NdeLogTcpSource source) {
        this.source = source;
    }


    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        if ((e.getMessage() == null) || (!(e.getMessage() instanceof NdeLog))) {
            return;
        }

        Event event = EventTools.buildEvent((NdeLog) e.getMessage());
        if(event != null) {
            source.getChannelProcessor().processEvent(event);
            source.getSourceCounter().incrementEventReceivedCount();
        }
    }

    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        this.source.addChildChannel(e.getChannel());
        this.source.getSourceCounter().incrementEventAcceptedCount();
    }


    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        closeOnFlush(e.getChannel());
    }


    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception {
        logger.error(e.getCause().getMessage());
        closeOnFlush(e.getChannel());
    }


    private void closeOnFlush(Channel ch) {
        this.source.delChildChannel(ch);
        if (ch.isConnected()) {
            ch.write(ChannelBuffers.EMPTY_BUFFER).addListener(
                    ChannelFutureListener.CLOSE);
        }
    }
}
