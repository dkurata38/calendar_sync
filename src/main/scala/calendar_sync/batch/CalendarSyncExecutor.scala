package calendar_sync.batch

import calendar_sync.application.coordinator.EventsSyncCoordinator
import calendar_sync.domain.Date
import com.typesafe.config.ConfigFactory
import javax.inject.{Inject, Singleton}

import scala.collection.JavaConverters.asScalaBufferConverter

@Singleton
class CalendarSyncExecutor @Inject() (private val coordinator: EventsSyncCoordinator) {
  def execute = {
    val config = ConfigFactory.load()
    val sourceCalendarIds = config.getStringList("app.calendar.source").asScala
    val targetCalendarId = config.getString("app.calendar.target")
    val dummyText = config.getString("app.dummyText")
    coordinator.sync(sourceCalendarIds, targetCalendarId, Date.today + 14, dummyText)
  }
}
