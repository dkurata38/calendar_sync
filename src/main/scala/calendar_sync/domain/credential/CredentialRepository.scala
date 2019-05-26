package calendar_sync.domain.credential

import scala.util.Try

trait CredentialRepository {
  def get: Try[Option[Credential]]
  def update(credential: Credential): Try[Unit]
}
