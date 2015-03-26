package com.fortysevendeg.rest.client

import akka.actor.ActorSystem
import spray.http.{HttpEntity, MediaTypes}
import spray.httpx.marshalling.Marshaller
import spray.httpx.unmarshalling.Unmarshaller

import scala.concurrent.ExecutionContext

trait ClientSupport {

  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  implicit val actorSystem: ActorSystem = ActorSystem("http-spray-client")

  val baseUrl = "http://sampleUrl"

  import play.api.libs.json.Json

  implicit val readsResponse = Json.reads[SampleResponse]
  implicit val readsRequest = Json.reads[SampleRequest]
  implicit val writesRequest = Json.writes[SampleRequest]

  implicit val marshaller =
    Marshaller.of[SampleRequest](MediaTypes.`application/json`) { (value, contentType, ctx) =>
      ctx.marshalTo(HttpEntity(contentType, Json.toJson(value).toString()))
    }

  implicit val unmarshaller =
    Unmarshaller[SampleResponse](MediaTypes.`text/plain`, MediaTypes.`application/json`) {
      case HttpEntity.NonEmpty(contentType, data) ⇒
        Json.parse(scala.io.Source.fromBytes(data.toByteArray, contentType.charset.value).mkString).as[SampleResponse]
    }

}
