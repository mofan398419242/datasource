package me.bayee.datasource.sink

import java.io.OutputStream

import org.apache.avro.generic.GenericData
import org.apache.flume.serialization.EventSerializer
import org.apache.flume.{Context, Event}
import sp.AuditLog

/**
  * Created by mofan on 16-9-11.
  */
class IntelligentTerminalLogSerializer(context: Context, out: OutputStream) extends AbstractSerializer(context, out) {
  override def schemaPath: String = "schema/IntelligentTerminalLog.avsc"

  override def write(event: Event): Unit = try {
    val bytes = event.getBody
    val auditLog = AuditLog
      .T_AUDIT_LOG
      .getDefaultInstance
      .getDefaultInstanceForType
      .newBuilderForType().mergeFrom(bytes, 0, bytes.length)
      .build()
      .asInstanceOf[AuditLog.T_AUDIT_LOG]

    if (auditLog.getAuditLogtypeid == -39 && auditLog.getLogclass == 6) {
      val record = new GenericData.Record(schema)
      record.put("sid", new String(auditLog.getSid.toByteArray))
      record.put("ana_policy_id", auditLog.getAnaPolicyId)
      record.put("audit_logtypeid", auditLog.getAuditLogtypeid)
      record.put("audit_logtypename", new String(auditLog.getAuditLogtypename.toByteArray))
      record.put("ast_id", auditLog.getAstId)
      record.put("ast_ip", new String(auditLog.getAstIp.toByteArray))
      record.put("ast_name", new String(auditLog.getAstName.toByteArray))
      record.put("ast_region_code", new String(auditLog.getAstRegionCode.toByteArray))
      record.put("ast_region_name", new String(auditLog.getAstRegionName.toByteArray))
      record.put("hgid", auditLog.getHgid)
      record.put("hgname", new String(auditLog.getHgname.toByteArray))
      record.put("progkey", new String(auditLog.getProgkey.toByteArray))
      record.put("ast_type_id", auditLog.getAstTypeId)
      record.put("ast_type_code", new String(auditLog.getAstTypeCode.toByteArray))
      record.put("ast_type_name", new String(auditLog.getAstTypeName.toByteArray))
      record.put("ast_admin", new String(auditLog.getAstAdmin.toByteArray))
      record.put("key_ast", auditLog.getKeyAst)
      record.put("engine_id", auditLog.getEngineId)
      record.put("engine_type", auditLog.getEngineType.getNumber)
      record.put("engine_ip", new String(auditLog.getEngineIp.toByteArray))
      record.put("engine_type_name", new String(auditLog.getEngineTypeName.toByteArray))
      record.put("log_source_ip", new String(auditLog.getLogSourceIp.toByteArray))
      record.put("userid", auditLog.getUserid)
      record.put("logname", new String(auditLog.getLogname.toByteArray))
      record.put("uname", new String(auditLog.getUname.toByteArray))
      record.put("ouid", auditLog.getOuid)
      record.put("ouname", new String(auditLog.getOuname.toByteArray))
      record.put("logname_type_id", auditLog.getLognameTypeId)
      record.put("logname_type", new String(auditLog.getLognameType.toByteArray))
      record.put("logname_region_code", new String(auditLog.getLognameRegionCode.toByteArray))
      record.put("logname_region", new String(auditLog.getLognameRegion.toByteArray))
      record.put("logname_status", auditLog.getLognameStatus)
      record.put("account_id", auditLog.getAccountId)
      record.put("account", new String(auditLog.getAccount.toByteArray))
      record.put("account_region_code", new String(auditLog.getAccountRegionCode.toByteArray))
      record.put("account_region", new String(auditLog.getAccountRegion.toByteArray))
      record.put("logtime", (auditLog.getLogtimeSec * 1000L + auditLog.getLogtimeMsec).toString)
      record.put("gather_time", (auditLog.getGatherSec * 1000L + auditLog.getGatherMsec).toString)
      record.put("sip", new String(auditLog.getSip.toByteArray))
      record.put("dip", new String(auditLog.getDip.toByteArray))
      record.put("dport", auditLog.getDport)
      record.put("risk_level", auditLog.getRiskLevel.getNumber)
      record.put("operate", new String(auditLog.getOperate.toByteArray))
      record.put("obj_id", new String(auditLog.getObjId.toByteArray))
      record.put("obj", new String(auditLog.getObj.toByteArray))
      record.put("hbasession", new String(auditLog.getHbasession.toByteArray))
      record.put("reserved0", new String(auditLog.getReserved0.toByteArray))
      record.put("reserved2", new String(auditLog.getReserved2.toByteArray))
      record.put("ana_policy_type", auditLog.getAnaPolicyType.getNumber)
      record.put("ana_policy_name", new String(auditLog.getAnaPolicyName.toByteArray))
      record.put("clientip", new String(auditLog.getClientip.toByteArray))
      record.put("clientinfo", new String(auditLog.getClientinfo.toByteArray))
      record.put("logclass", auditLog.getLogclass)
      writer.append(record)
    }
  } catch {
    case e: Exception => logger.warn(e.toString, e)
  }
}

class IntelligentTerminalLogSerializerBuilder extends EventSerializer.Builder {
  override def build(context: Context, outputStream: OutputStream): EventSerializer = new IntelligentTerminalLogSerializer(context, outputStream)
}