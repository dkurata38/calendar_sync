package calendar_sync.infrastracture.google.oauth

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.unmarshalling.Unmarshal
import calendar_sync.domain.credential.{Credential, OAuthClient, RefreshedAccessToken}
import calendar_sync.infrastracture.google.GoogleApiClient
import javax.inject.Singleton

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

@Singleton
class GoogleOauthClient extends Directives with OAuthResponseJsonSupport with OAuthClient with GoogleApiClient {
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
}
