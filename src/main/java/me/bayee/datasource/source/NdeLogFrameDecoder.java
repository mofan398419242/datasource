package me.bayee.datasource.source;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * Created by mofan on 16-9-11.
 */
public class NdeLogFrameDecoder extends FrameDecoder {
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer)
            throws Exception
    {
        if (buffer.readableBytes() < 4)
        {
            return null;
        }

        buffer.markReaderIndex();


        int length = buffer.readInt();

        if (length > 4194304)
        {
            buffer.clear();
            throw new Exception("Packet length more than 1024 * 1024 * 4.");
        }


        if (buffer.readableBytes() < length - 4)
        {
            buffer.resetReaderIndex();
            return null;
        }



        return buffer.readBytes(length - 4);
    }
}
