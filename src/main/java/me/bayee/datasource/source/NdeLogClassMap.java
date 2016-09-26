package me.bayee.datasource.source;

import com.google.protobuf.MessageLite;
import sp.AuditEnum;
import sp.AuditLog;
import sp.AuditUsess;
import sp.Cps;

import java.util.HashMap;


public class NdeLogClassMap {
    private static final HashMap<Integer, MessageLite> logclassMap = new HashMap();

    static {
        logclassMap.put(Integer.valueOf(AuditEnum.LOGCLASS.LC_USERSESSION.getNumber()),
                AuditUsess.T_USER_SESSION.getDefaultInstance()
                        .getDefaultInstanceForType());

        logclassMap.put(Integer.valueOf(AuditEnum.LOGCLASS.LC_STREAM.getNumber()),
                AuditLog.T_AUDIT_LOG.getDefaultInstance()
                        .getDefaultInstanceForType());

        logclassMap.put(Integer.valueOf(AuditEnum.LOGCLASS.LC_OPER.getNumber()),
                AuditLog.T_AUDIT_LOG.getDefaultInstance()
                        .getDefaultInstanceForType());

        logclassMap.put(Integer.valueOf(AuditEnum.LOGCLASS.LC_TREASURY.getNumber()),
                AuditLog.T_AUDIT_LOG.getDefaultInstance()
                        .getDefaultInstanceForType());

        logclassMap.put(Integer.valueOf(AuditEnum.LOGCLASS.LC_TREAOPER.getNumber()),
                AuditLog.T_AUDIT_LOG.getDefaultInstance()
                        .getDefaultInstanceForType());

        logclassMap.put(Integer.valueOf(AuditEnum.LOGCLASS.LC_HBA.getNumber()),
                AuditLog.T_AUDIT_LOG.getDefaultInstance()
                        .getDefaultInstanceForType());

        logclassMap.put(Integer.valueOf(AuditEnum.LOGCLASS.LC_CPSPROXY.getNumber()),
                Cps.PROXY_LOG.getDefaultInstance().getDefaultInstanceForType());

        logclassMap.put(Integer.valueOf(AuditEnum.LOGCLASS.LC_IAM.getNumber()),
                AuditLog.T_AUDIT_LOG.getDefaultInstance()
                        .getDefaultInstanceForType());

        logclassMap.put(Integer.valueOf(AuditEnum.LOGCLASS.LC_DETOUR.getNumber()),
                AuditLog.T_AUDIT_LOG.getDefaultInstance()
                        .getDefaultInstanceForType());

        logclassMap.put(Integer.valueOf(AuditEnum.LOGCLASS.LC_EVENT.getNumber()),
                AuditLog.T_AUDIT_LOG.getDefaultInstance()
                        .getDefaultInstanceForType());

        logclassMap.put(Integer.valueOf(AuditEnum.LOGCLASS.LC_KEYANA.getNumber()),
                AuditLog.T_AUDIT_LOG.getDefaultInstance()
                        .getDefaultInstanceForType());

        logclassMap.put(Integer.valueOf(AuditEnum.LOGCLASS.LC_STATSANA.getNumber()),
                AuditLog.T_AUDIT_LOG.getDefaultInstance()
                        .getDefaultInstanceForType());

        logclassMap.put(Integer.valueOf(AuditEnum.LOGCLASS.LC_WARN.getNumber()),
                AuditLog.T_AUDIT_LOG.getDefaultInstance()
                        .getDefaultInstanceForType());

        logclassMap.put(Integer.valueOf(AuditEnum.LOGCLASS.LC_SUBJECT.getNumber()),
                AuditLog.T_AUDIT_LOG.getDefaultInstance()
                        .getDefaultInstanceForType());

        logclassMap.put(Integer.valueOf(AuditEnum.LOGCLASS.LC_DETOURANA.getNumber()),
                AuditLog.T_AUDIT_LOG.getDefaultInstance()
                        .getDefaultInstanceForType());
    }

    public static MessageLite get(int key) {
        return (MessageLite) logclassMap.get(Integer.valueOf(key));
    }
}


/* Location:              /Users/pinter/Downloads/nde-solr/nde-solr-hbase.jar!/com/boco/nde/flume/source/NdeLogClassMap.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */