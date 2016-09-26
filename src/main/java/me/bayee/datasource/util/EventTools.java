package me.bayee.datasource.util;

import com.google.protobuf.MessageLite;
import me.bayee.datasource.source.NdeLog;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sp.AuditLog;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mofan on 16-9-10.
 */
public class EventTools {
    static final Logger logger = LogManager.getLogger(EventTools.class);

    public static Event buildEvent(NdeLog ent) {
        MessageLite ns = ent.getLog();
        if (!(ns instanceof AuditLog.T_AUDIT_LOG)) {
            logger.warn("Unsupport MessageLife: " + ns);
            return null;
        }
        AuditLog.T_AUDIT_LOG auditLog = (AuditLog.T_AUDIT_LOG) ns;
        int logClass = ent.getLogclass();
        int auditLogTypeId = auditLog.getAuditLogtypeid();
        Map<String, String> headers = new HashMap<String, String>();
        if (auditLogTypeId == -16 && logClass == 6) {
            headers.put("logtype", "bomc_log");
        } else if (auditLogTypeId == -25 && logClass == 6) {
            headers.put("logtype", "vgop_log");
        } else if (auditLogTypeId == -38 && logClass == 6) {
            headers.put("logtype", "cloud_desktop_log");
        } else if (auditLogTypeId == -39 && logClass == 6) {
            headers.put("logtype", "intelligent_terminal_log");
        } else if (auditLogTypeId == -19 && logClass == 6) {
            headers.put("logtype", "smp_log");
        } else if (auditLogTypeId == -49 && logClass == 6) {
            headers.put("logtype", "app_connect_log");
        } else if (auditLogTypeId == -18 && logClass == 6) {
            headers.put("logtype", "esop_log");
        } else if (auditLogTypeId == -5 && logClass == 6) {
            headers.put("logtype", "bass_log");
        } else if (logClass == 20) {
            headers.put("logtype", "4A_platform_log");
        } else if (logClass == 2) {
            headers.put("logtype", "internet_session_log");
        } else if (logClass == 3) {
            headers.put("logtype", "server_db_operation_log");
        } else if (logClass == 6 && auditLogTypeId == -36) {
            headers.put("logtype", "unix_server_syslog_log");
        } else if (auditLogTypeId == -35 && logClass == 6) {
            headers.put("logtype", "detour_log");
        } else if (logClass == 5) {
            headers.put("logtype", "gold_operation_log");
        } else if (logClass == 4) {
            headers.put("logtype", "gold_session_log");
        }else {
            logger.warn("not supported logclass : " + logClass + " audit_logtypeid : " + auditLogTypeId);
        }
        return EventBuilder.withBody(ns.toByteArray(),headers);
    }
}
