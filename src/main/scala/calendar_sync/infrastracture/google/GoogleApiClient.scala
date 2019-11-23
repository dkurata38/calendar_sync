package calendar_sync.infrastracture.google

import akka.http.scaladsl.model.{HttpResponse, StatusCode}
import akka.stream.ActorMaterializer
import calendar_sync.infrastracture.google.client_secrets.ClientSecrets

import scala.concurrent.duration.DurationDouble
import scala.concurrent.{ExecutionContext, Future}

trait GoogleApiClient {
  val clientSecrets: ClientSecrets = ClientSecrets.load

  object Response {
    def unapply(httpResponse: HttpResponse)(implicit materializer: ActorMaterializer, executionContext: ExecutionContext): Option[(StatusCode, Future[String])] =
      Some(httpResponse.status, httpResponse.entity.toStrict(10.seconds).map(_.data.utf8String))
  }
}
