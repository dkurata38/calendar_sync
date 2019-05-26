package calendar_sync.infrastracture.google.client_secrets

import calendar_sync.infrastracture.google.ClientSecretsFile
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait ClientSecretsProtocol extends DefaultJsonProtocol{
  implicit val clientSecretsFileFormat: RootJsonFormat[ClientSecretsFile] = jsonFormat(ClientSecretsFile.apply, "installed")
  implicit val clientSecretsFormat: RootJsonFormat[ClientSecrets] = jsonFormat(ClientSecrets.apply, "client_id", "client_secret")
}
