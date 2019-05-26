package calendar_sync.infrastracture.credential

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.model.{GetItemRequest, UpdateItemRequest}
import com.typesafe.config.ConfigFactory
import javax.inject.Singleton

import scala.util.Try

@Singleton
class DynamoDBClient {
  private val configuration = ConfigFactory.load
  private val endpointConfiguration =
    new EndpointConfiguration(
      configuration.getString("dynamoDb.endpoint"),
      configuration.getString("dynamoDb.region")
    )
  private val dynamoDB =
    AmazonDynamoDBClientBuilder.standard()
      .withEndpointConfiguration(endpointConfiguration)
      .build()


  def getItem(getItemRequest: GetItemRequest) = {
    Try(dynamoDB.getItem(getItemRequest))
  }

  def updateItem(updateItemRequest: UpdateItemRequest) = {
    Try(dynamoDB.updateItem(updateItemRequest))
  }
}
