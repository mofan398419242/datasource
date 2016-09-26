package me.bayee.datasource.source;

import com.google.protobuf.MessageLite;
import me.bayee.datasource.util.Tools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

/**
 * Created by mofan on 16-9-11.
 */
public class NdeLogDecoder extends OneToOneDecoder {
    private final Logger logger = LogManager.getLogger(this.getClass());

    protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg)
            throws Exception {
        if ((msg == null) || (!(msg instanceof ChannelBuffer))) {
            return null;
        }

        ChannelBuffer data = (ChannelBuffer) msg;

        int logclass = data.readInt();
        if ((logclass != 2) &&
                (logclass != 3) &&
                (logclass != 4) &&
                (logclass != 5) &&
                (logclass != 6) &&
                (logclass != 20)) {
            logger.warn("Unsupport this logclass:" + logclass);
            return null;
        }
        MessageLite builder = NdeLogClassMap.get(logclass);
        if (builder == null) {
            throw new Exception("Unknown this logclass:" + logclass);
        }

        NdeLog ndelog = null;
        try {
            if (data.hasArray()) {
                ndelog = new NdeLog(logclass, builder
                        .newBuilderForType()
                        .mergeFrom(data.array(),
                                data.arrayOffset() + data.readerIndex(),
                                data.readableBytes()).build());
            } else {
                ndelog = new NdeLog(logclass, builder.newBuilderForType()
                        .mergeFrom(new ChannelBufferInputStream(data)).build());
            }
        } catch (Exception e) {
            throw new Exception("mergeFrom logclass(" + logclass + "-" +
                    Tools.formatLogclass(logclass) + ") error: " +
                    e);
        }

        return ndelog;
    }
}
