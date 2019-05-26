package calendar_sync.infrastracture.google.oauth

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import calendar_sync.domain.credential.RefreshedAccessToken
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait OAuthResponseJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val oauthResponseFormat: RootJsonFormat[RefreshedAccessToken] =
    jsonFormat(RefreshedAccessToken.apply, "access_token", "expires_in")
}
