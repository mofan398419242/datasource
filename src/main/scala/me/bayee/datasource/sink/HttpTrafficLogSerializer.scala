package me.bayee.datasource.sink

import java.io.OutputStream

import org.apache.avro.Schema
import org.apache.avro.file.{CodecFactory, DataFileWriter}
import org.apache.avro.generic.{GenericData, GenericDatumWriter, GenericRecord}
import org.apache.flume.serialization.AvroEventSerializerConfigurationConstants._
import org.apache.flume.{Context, Event}
import org.apache.flume.serialization.EventSerializer

import scala.io.Source

/**
  * Created by mofan on 16-9-4.
  */
class HttpTrafficLogSerializer(context: Context, out: OutputStream) extends EventSerializer {
  lazy val schema = (new Schema.Parser).parse(this.getClass.getClassLoader.getResourceAsStream("schema/HttpTrafficLog.avsc"))

  lazy val writer = {
    val compressionCodec = context.getString(COMPRESSION_CODEC, DEFAULT_COMPRESSION_CODEC)
    val genericDatumWriter = new GenericDatumWriter[GenericRecord](schema)
    val dataFileWriter = new DataFileWriter[GenericRecord](genericDatumWriter)
    dataFileWriter.setCodec(CodecFactory.fromString(compressionCodec))
    dataFileWriter.create(schema, out)
    dataFileWriter
  }

  override def flush(): Unit = writer.flush()

  override def write(event: Event): Unit = {
    val record = new GenericData.Record(schema)
    val splits = new String(event.getBody).split("\",\"").map(_.replace("\"",""))
    record.put("visit_time",splits(0))
    record.put("warden",splits(1))
    record.put("http_code",splits(2).toInt)
    record.put("client_c_address",splits(3))
    record.put("server_ip",splits(4))
    record.put("refer_info",splits(5))
    record.put("server_port",splits(6).toInt)
    record.put("client_request_size",splits(7).toLong)
    record.put("server_response_size",splits(8).toLong)
    record.put("dns",splits(9))
    record.put("cookie",splits(10))
    record.put("client_ip",splits(11))
    record.put("user_agent",splits(12))
    record.put("uri",splits(13))
    record.put("query_param",splits(14))
    record.put("http_method",splits(15))
    record.put("server_c_address",splits(16))
    record.put("httplog",splits(17))
    writer.append(record)
  }

  override def afterReopen(): Unit = throw new UnsupportedOperationException("avro don't support append.")

  override def supportsReopen(): Boolean = false

  override def beforeClose(): Unit = {}

  override def afterCreate(): Unit = {}
}

class HttpTrafficLogSerializerBuilder extends EventSerializer.Builder {
  override def build(context: Context, outputStream: OutputStream): EventSerializer = new HttpTrafficLogSerializer(context, outputStream)
}