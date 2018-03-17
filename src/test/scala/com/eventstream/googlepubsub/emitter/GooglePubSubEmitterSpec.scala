package com.eventstream.googlepubsub.emitter

import java.time.LocalDateTime

object GooglePubSubEmitterSpec {

  val emitter = new GooglePubSubEmitter(
    EmitterConfig("customer-support-610a3",
      "orders-streaming",
      "src/test/resources/customer-support-6c89413a89ad.json"))

  def main(args: Array[String]): Unit = {

    val result = emitter.emit(
      s"""
      {
        "eventID": "1",
        "customerID": "prayagupd",
        "orderID": "DUWAMISH-001",
        "created": s"${LocalDateTime.now().toString}"
      }
    """.stripMargin)

    println(result)
  }

}
