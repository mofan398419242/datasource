package me.bayee.datasource.source;

import com.google.protobuf.MessageLite;
import me.bayee.datasource.util.EventTools;
import me.bayee.datasource.util.Tools;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.FlumeException;
import org.apache.flume.conf.Configurable;
import org.apache.flume.conf.Configurables;
import org.apache.flume.instrumentation.SourceCounter;
import org.apache.flume.source.AbstractSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;

/**
 * Created by mofan on 16-9-10.
 */
public class NdeLogUdpSource extends AbstractSource implements Configurable, EventDrivenSource, Runnable {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private String host;

    private int port;
    private DatagramSocket udpSocket;
    private Thread workerThread;
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
        try {
            this.udpSocket = (this.host != null ? new DatagramSocket(new InetSocketAddress(
                    this.host, this.port)) : new DatagramSocket(new InetSocketAddress(this.port)));
            this.udpSocket.setReceiveBufferSize(409600);
            this.udpSocket.setSoTimeout(100);
        } catch (SocketException e) {
            logger.error(e);
            throw new FlumeException(e);
        }

        this.workerThread = new Thread(this);
        this.workerThread.start();

        this.sourceCounter.start();
        super.start();
    }

    public void run() {
        byte[] buffer = new byte[65536];

        while (!Thread.currentThread().isInterrupted()) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                this.udpSocket.receive(packet);
            } catch (SocketTimeoutException e) {
                continue;
            } catch (IOException e) {
                logger.error(e.getMessage());
                continue;
            }

            if (packet.getLength() < 8) {
                logger.warn("DatagramPacket length less than 8.");
            } else {
                int totalLen = packet.getData()[0] & 0xFF;
                totalLen <<= 8;
                totalLen += (packet.getData()[1] & 0xFF);
                totalLen <<= 8;
                totalLen += (packet.getData()[2] & 0xFF);
                totalLen <<= 8;
                totalLen += (packet.getData()[3] & 0xFF);

                if (packet.getLength() < totalLen - 4) {
                    logger.warn("DatagramPacket length less than protocol length.");
                } else {
                    int logclass = packet.getData()[4] & 0xFF;
                    logclass <<= 8;
                    logclass += (packet.getData()[5] & 0xFF);
                    logclass <<= 8;
                    logclass += (packet.getData()[6] & 0xFF);
                    logclass <<= 8;
                    logclass += (packet.getData()[7] & 0xFF);

                    if ((logclass != 2) &&
                            (logclass != 3) &&
                            (logclass != 4) &&
                            (logclass != 5) &&
                            (logclass != 6) &&
                            (logclass != 20)) {
                        logger.warn("Unsupport this logclass:" + logclass);
                    } else {
                        MessageLite builder = NdeLogClassMap.get(logclass);
                        if (builder == null) {
                            logger.warn("Unknown this logclass:" + logclass);
                        } else
                            try {
                                NdeLog ndelog = new NdeLog(logclass, builder
                                        .newBuilderForType()
                                        .mergeFrom(packet.getData(), 8, packet.getLength() - 8)
                                        .build());

                                Event event = EventTools.buildEvent(ndelog);
                                if(event != null) {
                                    getChannelProcessor().processEvent(event);
                                    sourceCounter.incrementEventReceivedCount();
                                }
                            } catch (Exception e) {
                                logger.error("mergeFrom logclass(" + logclass + "-" +
                                        Tools.formatLogclass(logclass) + ") error: " +
                                        e);
                            }
                    }
                }
            }
        }
        logger.warn(getClass().toString() + " isInterrupted!");
    }

    public void stop() {
        this.workerThread.interrupt();
        this.udpSocket.close();

        this.sourceCounter.stop();
        super.stop();

        logger.info("NdeLog UDP Source: " + getName() + " Metrics: " + this.sourceCounter);
    }
}
