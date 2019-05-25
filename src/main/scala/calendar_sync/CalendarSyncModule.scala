package calendar_sync

import calendar_sync.domain.GoogleCalendarEventClient
import calendar_sync.infrastracture.GoogleCalendarEventClientImpl
import com.google.inject.AbstractModule

class CalendarSyncModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[GoogleCalendarEventClient]).to(classOf[GoogleCalendarEventClientImpl])
  }
}
