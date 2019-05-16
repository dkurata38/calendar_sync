package calendar_sync

import calendar_sync.application.coordinator.EventsSyncCoordinator
import calendar_sync.domain.{Date, Duration}
import com.amazonaws.services.lambda.runtime.Context
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters.asScalaBufferConverter

class Handler {
  def handleRequest(input: String, context: Context): Unit = {
    val coordinator = new EventsSyncCoordinator
    val config = ConfigFactory.load()
    val sourceCalendarIds = config.getStringList("app.calendar.source").asScala
    val targetCalendarId = config.getString("app.calendar.target")
    val dummyText = config.getString("app.dummyText")

    coordinator.sync(sourceCalendarIds, targetCalendarId, Date.today + 14, dummyText) match {
      case Left(e) => println(e.toString)
      case _ => ()
    }
  }
}
