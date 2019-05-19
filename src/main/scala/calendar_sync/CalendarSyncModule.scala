package calendar_sync

import calendar_sync.domain.{GoogleCalendarEventClient, GoogleOAuthClient}
import calendar_sync.infrastracture.{GoogleCalendarEventClientImpl, GoogleOAuthClientImpl}
import com.google.inject.AbstractModule

class CalendarSyncModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[GoogleCalendarEventClient]).to(classOf[GoogleCalendarEventClientImpl])
    bind(classOf[GoogleOAuthClient]).to(classOf[GoogleOAuthClientImpl])
  }
}
