package calendar_sync.domain

import scala.util.Try

trait GoogleOAuthClient {
  def getCredential: Try[Credential]
}
