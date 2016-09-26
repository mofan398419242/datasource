package me.bayee.datasource.source;

import com.google.protobuf.MessageLite;

public class NdeLog {
    private MessageLite log;
    private int logclass;

    public NdeLog(int logclass, MessageLite log) {
        this.logclass = logclass;
        this.log = log;
    }

    public int getLogclass() {
        return this.logclass;
    }

    public MessageLite getLog() {
        return this.log;
    }
}


/* Location:              /Users/pinter/Downloads/nde-solr/nde-solr-hbase.jar!/com/boco/nde/flume/source/NdeLog.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */