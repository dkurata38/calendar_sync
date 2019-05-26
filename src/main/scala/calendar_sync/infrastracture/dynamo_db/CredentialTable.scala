package calendar_sync.infrastracture.dynamo_db

import calendar_sync.domain.Credential
import com.amazonaws.services.dynamodbv2.model.{AttributeAction, AttributeValue, AttributeValueUpdate, GetItemRequest, GetItemResult, UpdateItemRequest}
import CredentialTable._
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters._

case class CredentialTable(accessToken: String, expiresIn: Long, refreshToken: String) {
  private def attributeValueUpdates: Map[String, AttributeValueUpdate] = Map(
    ACCESS_TOKEN -> new AttributeValueUpdate(new AttributeValue(accessToken), AttributeAction.PUT),
    EXPIRES_IN -> new AttributeValueUpdate(new AttributeValue(expiresIn.toString), AttributeAction.PUT),
    REFRESH_TOKEN -> new AttributeValueUpdate(new AttributeValue(refreshToken), AttributeAction.PUT)
  )

  def updateItemRequest = {
    new UpdateItemRequest()
      .withTableName(tableName)
      .withKey(pkeyAttributeValue.asJava)
      .withAttributeUpdates(attributeValueUpdates.asJava)
  }

  def toCredential = {
    Credential(accessToken, expiresIn, refreshToken)
  }
}

object CredentialTable {
  private val ID = "id"
  private val ACCESS_TOKEN = "access_token"
  private val EXPIRES_IN = "expires_in"
  private val REFRESH_TOKEN = "refresh_token"
  private val key = "user"
  private val configuration = ConfigFactory.load
  private val tableName = configuration.getString("dynamoDb.table")

  def apply(credential: Credential) = {
    apply(credential.accessToken, credential.expiresIn, credential.refreshToken)
  }

  def apply(getItemResult: GetItemResult) = {
    val maybeItem = Option(getItemResult.getItem).map(item => item.asScala)

    maybeItem.flatMap(item =>
      if(item.isEmpty) None else Some(
        apply(
          item(ACCESS_TOKEN).getS,
          item(EXPIRES_IN).getN.toLong,
          item(REFRESH_TOKEN).getS
        )
      )
    )
  }

  private def pkeyAttributeValue: Map[String, AttributeValue] = Map(ID -> new AttributeValue(key))

  def getRequest = {
    new GetItemRequest()
      .withKey(CredentialTable.pkeyAttributeValue.asJava)
      .withTableName(tableName)
  }
}
