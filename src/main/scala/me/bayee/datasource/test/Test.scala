package me.bayee.datasource.test

import java.io.FileInputStream
import java.net.{DatagramPacket, DatagramSocket, InetAddress, InetSocketAddress}
import java.util

import me.bayee.datasource.source.{NdeLog, NdeLogClassMap}
import sp.AuditLog
import scala.collection.JavaConversions._

/**
  * Created by mofan on 16-10-26.
  */
object Test extends App {
  override def main(args: Array[String]): Unit = {
    var buffer = new Array[Byte](50981763)
    new FileInputStream(args(0)).read(buffer)
    val dataSocket = new DatagramSocket()
    while (buffer.length > 100) {
      val cutIndex = buffer.indexOfSlice("----------".getBytes)
      val data = buffer.take(cutIndex)
      buffer = buffer.drop(cutIndex + "----------".getBytes.length)
      val dataPacket = new DatagramPacket(data, data.length, InetAddress.getByName("192.168.0.229"), 8433)
      dataSocket.send(dataPacket)
    }
  }
}


object Test2 extends App {
  override def main(args: Array[String]): Unit = {
    //    val udpSocket = new DatagramSocket(new InetSocketAddress(args(0), args(1).toInt))
    //    udpSocket.setReceiveBufferSize(409600)
    //    udpSocket.setSoTimeout(100)
    //
    //    while (true) {
    //      val buffer = new Array[Byte](1024 * 64)
    //      val packet = new DatagramPacket(buffer, buffer.length)
    //      try {
    //        udpSocket.receive(packet)
    //        if (packet.getLength < 8) println(s"DatagramPacket length less than 8. data: ${new String(packet.getData)}")
    //        var totalLen = packet.getData.apply(0) & 0xff
    //        totalLen <<= 8
    //        totalLen = packet.getData.apply(1) & 0xff
    //        totalLen <<= 8
    //        totalLen = packet.getData.apply(2) & 0xff
    //        totalLen <<= 8
    //        totalLen = packet.getData.apply(3) & 0xff
    //        if (packet.getLength < totalLen - 4) println(s"DatagramPacket length less than protocol length. data: ${new String(packet.getData)}")
    //        else {
    //          var logclass = packet.getData.apply(4) & 0xff
    //          logclass <<= 8
    //          logclass += packet.getData.apply(5) & 0xff
    //          logclass <<= 8
    //          logclass += packet.getData.apply(6) & 0xff
    //          logclass <<= 8
    //          logclass += packet.getData.apply(7) & 0xff
    //          val builder = NdeLogClassMap.get(logclass)
    //          if (builder == null) println(s"Unknow this logclass:$logclass, data: ${new String(packet.getData)}")
    //          else if (args.length > 2) println(s"success logclass:$logclass")
    //        }
    //      } catch {
    //        case t: Throwable => t.printStackTrace()
    //      }
    //    }

    val result = new util.HashMap[String,Int]()
    result.put("bomc_log",0)
    result.put("vgop_log",0)
    result.put("cloud_desktop_log",0)
    result.put("intelligent_terminal_log",0)
    result.put("smp_log",0)
    result.put("app_connect_log",0)
    result.put("esop_log",0)
    result.put("bass_log",0)
    result.put("4A_platform_log",0)
    result.put("internet_session_log",0)
    result.put("server_db_operation_log",0)
    result.put("unix_server_syslog_log",0)
    result.put("detour_log",0)
    result.put("gold_operation_log",0)
    result.put("gold_session_log",0)


    var buffer  : util.List[Byte]= new util.ArrayList[Byte]()
    var b = new Array[Byte](5000)
    val inputStream = new FileInputStream("/home/mofan/Downloads/log/mylog/logdata203-1")
    var length = inputStream.read(b)

    b.take(length).foreach(buffer.add)
    var count = 0
    var error = 0
    while (buffer.size() > 100) {
      if(buffer.size < 3000) {
        b = new Array[Byte](5000)
        length = inputStream.read(b)
        b.take(length).foreach(buffer.add)
      }
      val cutIndex = buffer.indexOfSlice("##@@##".getBytes)
      val data = buffer.subList(0,cutIndex)
      buffer = buffer.subList(cutIndex + "##@@##".getBytes.length,buffer.size())
      if (data.length > 8) {
        count += 1
        if(count % 1000 == 0) println(s"count: $count...")
        var totalLen = data(0) & 0xff
        totalLen <<= 8
        totalLen = data(1) & 0xff
        totalLen <<= 8
        totalLen = data(2) & 0xff
        totalLen <<= 8
        totalLen = data(3) & 0xff
        if (data.length < totalLen - 4) {
//          println(s"DatagramPacket length less than protocol length. data: ${new String(data)}")
          error += 1
        }
        else {
          var logclass = data(4) & 0xff
          logclass <<= 8
          logclass += data(5) & 0xff
          logclass <<= 8
          logclass += data(6) & 0xff
          logclass <<= 8
          logclass += data(7) & 0xff
          val builder = NdeLogClassMap.get(logclass)
          if (builder == null) {
//            println(s"Unknow this logclass:$logclass, data: ${new String(data)}")
            error += 1
          }
          else {
            val ndelog = new NdeLog(logclass,builder.newBuilderForType().mergeFrom(data.toList.toArray[Byte],8,data.length - 8).build())
            if(!ndelog.getLog.isInstanceOf[AuditLog.T_AUDIT_LOG]) {
              error += 1
            }else {
              val auditLog = ndelog.getLog.asInstanceOf[AuditLog.T_AUDIT_LOG]
              val auditLogTypeId = auditLog.getAuditLogtypeid
              if (auditLogTypeId == -16 && logclass == 6) {
                result.put("bomc_log",result.get("bomc_log") + 1)
              } else if (auditLogTypeId == -25 && logclass == 6) {
                result.put("vgop_log", result.get("vgop_log")+1)
              } else if (auditLogTypeId == -38 && logclass == 6) {
                result.put("cloud_desktop_log", result.get("cloud_desktop_log")+1)
              } else if (auditLogTypeId == -39 && logclass == 6) {
                result.put("intelligent_terminal_log", result.get("intelligent_terminal_log")+1)
              } else if (auditLogTypeId == -19 && logclass == 6) {
                result.put("smp_log", result.get("smp_log")+1)
              } else if (auditLogTypeId == -49 && logclass == 6) {
                result.put("app_connect_log", result.get("app_connect_log")+1)
              } else if (auditLogTypeId == -18 && logclass == 6) {
                result.put("esop_log", result.get("esop_log")+1)
              } else if (auditLogTypeId == -5 && logclass == 6) {
                result.put("bass_log", result.get("bass_log")+1)
              } else if (logclass == 20) {
                result.put("4A_platform_log", result.get("4A_platform_log")+1)
              } else if (logclass == 2) {
                result.put("internet_session_log", result.get("internet_session_log")+1)
              } else if (logclass == 3) {
                result.put("server_db_operation_log", result.get("server_db_operation_log")+1)
              } else if (logclass == 6 && auditLogTypeId == -36) {
                result.put("unix_server_syslog_log", result.get("unix_server_syslog_log")+1)
              } else if (auditLogTypeId == -35 && logclass == 6) {
                result.put("detour_log", result.get("detour_log")+1)
              } else if (logclass == 5) {
                result.put("gold_operation_log", result.get("gold_operation_log")+1)
              } else if (logclass == 4) {
                result.put("gold_session_log", result.get("gold_session_log")+1)
              }else {
                error += 1
              }
            }
          }
        }
      }
    }
    result.foreach(kv => println(s"[table]${kv._1} [count]${kv._2}"))
    println(s"[total]$count [error]$error")
  }
}
