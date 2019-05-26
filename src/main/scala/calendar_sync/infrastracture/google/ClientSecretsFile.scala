package calendar_sync.infrastracture.google

import calendar_sync.infrastracture.google.client_secrets.{ClientSecrets, ClientSecretsJsonSupport}
import spray.json.enrichString

import scala.io.{Codec, Source}

case class ClientSecretsFile(clientSecrets: ClientSecrets)

case object ClientSecretsFile extends ClientSecretsJsonSupport {
  private val clientSecretsJson = Source
    .fromInputStream(getClass.getResourceAsStream("/token/client_secrets.json"), Codec.UTF8.name)
    .getLines()
    .mkString(" ")
    .parseJson.convertTo[ClientSecretsFile]
    .clientSecrets

  def load: ClientSecrets = clientSecretsJson
}