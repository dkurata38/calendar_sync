package calendar_sync.infrastracture.google.oauth

1import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.util.ByteString
import calendar_sync.domain.credential.{Credential, OAuthClient, RefreshedAccessToken}
import calendar_sync.infrastracture.google.ClientSecretsFile
import javax.inject.Singleton

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton
class GoogleOauthClient extends Directives with OAuthResponseJsonSupport with OAuthClient{
  private implicit val actorSystem: ActorSystem = ActorSystem()
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  private implicit val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  private val clientSecrets = ClientSecretsFile.load
  def refreshAccessToken(credential: Credential) = {
    val formData = FormData(
      "client_id" -> clientSecrets.clientId,
      "client_secret" -> clientSecrets.clientSecret,
      "refresh_token" -> credential.refreshToken,
      "grant_type" -> "offline")

    val request =
      HttpRequest(HttpMethods.POST).withUri(Uri("https://www.googleapis.com/oauth2/v4/token"))
      .withProtocol(HttpProtocol("HTTP/1.1"))
      .withEntity(formData.toEntity)

    val maybeRefreshedToken = Http().singleRequest(request).transformWith{
      case Success(Response(StatusCodes.OK, jsonString)) => jsonString.flatMap(Unmarshal(_).to[RefreshedAccessToken])
      case Success(Response(_, jsonString)) => jsonString.flatMap(json => Future.failed(new RuntimeException(json)))
      case Failure(exception) => Future.failed(exception)
    }

    Try(Await.result(maybeRefreshedToken, Duration.Inf))
  }

  object Response {
    def unapply(httpResponse: HttpResponse): Option[(StatusCode, Future[String])] =
      Some(httpResponse.status, httpResponse.entity.dataBytes.runFold(ByteString.empty)(_ ++ _).map(bs => bs.utf8String))
  }
}
