package com.eventstream.googlepubsub.consumer

import java.io.FileInputStream
import java.util.concurrent.LinkedBlockingDeque

import com.google.api.core.ApiService.State
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.pubsub.v1.{AckReplyConsumer, MessageReceiver, Subscriber}
import com.google.pubsub.v1.{ProjectSubscriptionName, PubsubMessage}

import scala.util.Try

trait Consumer {

  def consume()

  def shutdown(): Try[State]
}

trait EventProcessor {
  def process(eventID: String, event: String): String
}

class GooglePubSubConsoleEventProcessor extends EventProcessor {

  override def process(eventID: String, event: String): String = {
    println("processing " + eventID + ":" + event)
    eventID
  }

}

case class ConsumerConfig(projectName: String,
                          consumerMasterName: String,
                          credentials: String)

//TODO inject a processor
class GooglePubSubConsumer(consumerConfig: ConsumerConfig, eventProcessor: EventProcessor) extends Consumer {

  private val events = new LinkedBlockingDeque[PubsubMessage]()

  private val consumer = Try(Subscriber.newBuilder(ProjectSubscriptionName.of(consumerConfig.projectName, consumerConfig.consumerMasterName),
    (message: PubsubMessage, consumer: AckReplyConsumer) => {
      events.offer(message)
      println("update event offset")
      //TODO update inside processor after actually processsing
      consumer.ack()
    })
    .setCredentialsProvider(cred)
    .build())

  override def consume(): Unit = {

    consumer.map(_.startAsync().awaitRunning())

    while (true) {
      val event = events.take()
      eventProcessor.process(event.getMessageId, event.getData.toStringUtf8)
    }
  }

  override def shutdown(): Try[State] = consumer.map(_.stopAsync()).map(_.state())

  private def cred = FixedCredentialsProvider.create(GoogleCredentials.fromStream(new FileInputStream(consumerConfig.credentials)))

}
