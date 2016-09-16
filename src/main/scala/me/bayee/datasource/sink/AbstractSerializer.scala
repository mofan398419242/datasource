package me.bayee.datasource.sink


import java.io.OutputStream

import org.apache.avro.Schema
import org.apache.avro.file.{CodecFactory, DataFileWriter}
import org.apache.avro.generic.{GenericDatumWriter, GenericRecord}
import org.apache.flume.Context
import org.apache.flume.serialization.AvroEventSerializerConfigurationConstants._
import org.apache.flume.serialization.EventSerializer
import org.apache.logging.log4j.LogManager

/**
  * Created by mofan on 16-9-11.
  */
abstract class AbstractSerializer(context: Context, out: OutputStream) extends EventSerializer {
  lazy val logger = LogManager.getLogger(this.getClass)

  def schemaPath: String

  lazy val schema = (new Schema.Parser).parse(this.getClass.getClassLoader.getResourceAsStream(schemaPath))

  lazy val writer = {
    val compressionCodec = context.getString(COMPRESSION_CODEC, DEFAULT_COMPRESSION_CODEC)
    val genericDatumWriter = new GenericDatumWriter[GenericRecord](schema)
    val dataFileWriter = new DataFileWriter[GenericRecord](genericDatumWriter)
    dataFileWriter.setCodec(CodecFactory.fromString(compressionCodec))
    dataFileWriter.create(schema, out)
    dataFileWriter
  }

  override def flush(): Unit = writer.flush()

  override def afterReopen(): Unit = throw new UnsupportedOperationException("avro don't support append.")

  override def supportsReopen(): Boolean = false

  override def beforeClose(): Unit = {}

  override def afterCreate(): Unit = {}

}
