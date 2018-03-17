package com.eventstream.googlepubsub.consumer

object GooglePubSubConsumerSpec {

  val consumer = new GooglePubSubConsumer(ConsumerConfig(
    "customer-support-610a3",
    "orders-streaming-consumer",
    "src/test/resources/customer-support-6c89413a89ad.json"), new GooglePubSubConsoleEventProcessor)

  def main(args: Array[String]): Unit = {

    consumer.consume()

  }
}
