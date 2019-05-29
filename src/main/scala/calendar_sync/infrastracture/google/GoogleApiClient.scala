package calendar_sync.infrastracture.google

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpResponse, StatusCode}
import akka.stream.ActorMaterializer
import akka.util.ByteString
import calendar_sync.infrastracture.google.client_secrets.ClientSecrets

import scala.concurrent.{ExecutionContext, Future}

trait GoogleApiClient {
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  val clientSecrets: ClientSecrets = ClientSecrets.load


  object Response {
    def unapply(httpResponse: HttpResponse): Option[(StatusCode, Future[String])] =
      Some(httpResponse.status, httpResponse.entity.dataBytes.runFold(ByteString.empty)(_ ++ _).map(bs => bs.utf8String))
  }
}
