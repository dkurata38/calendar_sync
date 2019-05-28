package calendar_sync.infrastracture.dynamo_db

import calendar_sync.domain.credential.{Credential, CredentialRepository}
import javax.inject.{Inject, Singleton}

import scala.util.Try

@Singleton
class CredentialDynamoDBRepository @Inject() (client: DynamoDBClient) extends CredentialRepository{
  override def get: Try[Option[Credential]] = {
    val request = CredentialTable.getRequest
    client.getItem(request)
      .map(response => CredentialTable(response).map(_.toCredential))
  }

  override def update(credential: Credential): Try[Unit] = {
    client.updateItem(CredentialTable(credential).updateItemRequest)
      .map(_ => ())
  }
}
