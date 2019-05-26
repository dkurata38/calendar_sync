package calendar_sync.domain.credential

import scala.util.Try

trait OAuthClient {
  def refreshAccessToken(credential: Credential): Try[RefreshedAccessToken]
}
