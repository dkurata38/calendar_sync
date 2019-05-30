package calendar_sync

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import calendar_sync.application.coordinator.EventsSyncCoordinator
import calendar_sync.domain.Date
import com.amazonaws.services.lambda.runtime.Context
import com.google.inject.Guice
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters.collectionAsScalaIterableConverter
import scala.concurrent.ExecutionContext

class Handler {
  def handleRequest(input: String, context: Context): Unit = {
    val config = ConfigFactory.load()
    val sourceCalendarIds = config.getStringList("app.calendar.source").asScala.toSeq
    val targetCalendarId = config.getString("app.calendar.target")
    val injector = Guice.createInjector(new CalendarSyncModule)

    val coordinator = injector.getInstance(classOf[EventsSyncCoordinator])

    implicit val actorSystem: ActorSystem = ActorSystem()
    implicit val mateliarizer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global
    coordinator.sync(sourceCalendarIds, targetCalendarId, Date.today + 14)
  }
}
