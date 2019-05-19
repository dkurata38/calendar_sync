package calendar_sync

import calendar_sync.application.coordinator.EventsSyncCoordinator
import calendar_sync.domain.Date
import com.amazonaws.services.lambda.runtime.Context
import com.google.inject.Guice
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters.collectionAsScalaIterableConverter

class Handler {
  def handleRequest(input: String, context: Context): Unit = {
    val config = ConfigFactory.load()
    val sourceCalendarIds = config.getStringList("app.calendar.source").asScala.toSeq
    val targetCalendarId = config.getString("app.calendar.target")
    val dummyText = config.getString("app.dummyText")
    val injector = Guice.createInjector(new CalendarSyncModule)

    val coordinator = injector.getInstance(classOf[EventsSyncCoordinator])
    coordinator.sync(sourceCalendarIds, targetCalendarId, Date.today + 14, dummyText)
  }
}
