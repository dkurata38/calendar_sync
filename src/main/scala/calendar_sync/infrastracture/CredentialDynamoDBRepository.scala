package calendar_sync.infrastracture

import calendar_sync.domain.{Credential, CredentialRepository}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.model._
import com.typesafe.config.ConfigFactory
import javax.inject.Singleton

import scala.collection.JavaConverters._
import scala.util.Try

@Singleton
class CredentialDynamoDBRepository extends CredentialRepository{
  private val pkey = "user"
  private val configuration = ConfigFactory.load
  private val endpointConfiguration =
    new EndpointConfiguration(
      configuration.getString("dynamo_db.endpoint"),
      configuration.getString("dynamo_db.region")
    )
  private val dynamoDB =
    AmazonDynamoDBClientBuilder.standard()
      .withEndpointConfiguration(endpointConfiguration)
      .build()

  override def get: Try[Credential] = {
    Try{
      val keyToGet = Map("id" -> new AttributeValue(pkey)).asJava
      val request = new GetItemRequest()
        .withKey(keyToGet)
        .withTableName("calendar-sync-dev")
      dynamoDB.getItem(request).toCredential
    }
  }

  override def update(credential: Credential): Try[Unit] = {
    Try {
      val pkeyMap = Map("id" -> new AttributeValue(pkey)).asJava
      val updateValues = credential.toUpdateValues
      dynamoDB.updateItem("calendar-sync-dev", pkeyMap, updateValues)
    }
  }

  implicit class CredentialTable(credential: Credential) {
    val ID = "id"
    val ACCESS_TOKEN = "access_token"
    val EXPIRES_IN = "expires_in"
    val REFRESH_TOKEN = "refresh_token"

    def toAddValues = Map(
      ID -> new AttributeValue(pkey),
      ACCESS_TOKEN -> new AttributeValue(credential.accessToken),
      EXPIRES_IN -> new AttributeValue(credential.expiresIn.toString),
      REFRESH_TOKEN -> new AttributeValue(credential.refreshToken)
    ).asJava

    def toUpdateValues = Map(
      ACCESS_TOKEN -> new AttributeValueUpdate(new AttributeValue(credential.accessToken), AttributeAction.PUT),
      EXPIRES_IN -> new AttributeValueUpdate(new AttributeValue(credential.expiresIn.toString), AttributeAction.PUT),
      REFRESH_TOKEN -> new AttributeValueUpdate(new AttributeValue(credential.refreshToken), AttributeAction.PUT)
    ).asJava
  }

  implicit class GetItemResponse(getItemResult: GetItemResult) {
    def toCredential = {
      val item = getItemResult.getItem.asScala
      Credential(
        item("access_token").getS,
        item("expires_in").getN.toLong,
        item("refresh_token").getS
      )
    }
  }

}
