package com.eventstream.googlepubsub.emitter

import java.io.FileInputStream

import com.google.api.core.{ApiFutureCallback, ApiFutures}
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.pubsub.v1.Publisher
import com.google.protobuf.ByteString
import com.google.pubsub.v1.{ProjectTopicName, PubsubMessage}

import scala.util.{Failure, Success, Try}

trait Emitter {
  def emit(event: String): String
}

case class EmitterConfig(projectID: String, topicName: String, credentials: String)

class GooglePubSubEmitter(config: EmitterConfig) extends Emitter {

  val publisher = Try(Publisher.newBuilder(ProjectTopicName.of(config.projectID, config.topicName))
    .setCredentialsProvider(FixedCredentialsProvider.create(GoogleCredentials.fromStream(new FileInputStream(config.credentials))))
    .build())

  override def emit(event: String): String = {

    val eventMessage = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(event)).build()

    val result = publisher.map(_.publish(eventMessage)).map { emitResult =>

      ApiFutures.addCallback(emitResult, new ApiFutureCallback[String] {
        override def onFailure(t: Throwable) = t.printStackTrace()

        override def onSuccess(result: String) = println(result)
      })
    } match {
      case Success(_) => "success"
      case Failure(e) =>
        e.printStackTrace()
        "failed"
    }

    publisher.map(_.shutdown())

    result
  }
}
