package me.bayee.datasource.test

import java.io.FileInputStream
import java.net.{DatagramPacket, DatagramSocket, InetAddress}

/**
  * Created by mofan on 16-10-26.
  */
object Test extends App {
  override def main(args: Array[String]): Unit = {
    var buffer = new Array[Byte](50981763)
    new FileInputStream(args(0)).read(buffer)
    val dataSocket = new DatagramSocket()
    while(buffer.length > 100) {
      val cutIndex = buffer.indexOfSlice("------------".getBytes)
      val data = buffer.take(cutIndex)
      buffer = buffer.drop(cutIndex + "------------".getBytes.length)
      val dataPacket = new DatagramPacket(data,data.length,InetAddress.getByName("192.168.0.229"),8433)
      dataSocket.send(dataPacket)
    }
  }
}
