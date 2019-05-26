package calendar_sync

import calendar_sync.domain.{CredentialRepository, GoogleCalendarEventClient}
import calendar_sync.infrastracture.GoogleCalendarEventClientImpl
import calendar_sync.infrastracture.credential.CredentialDynamoDBRepository
import com.google.inject.AbstractModule

class CalendarSyncModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[GoogleCalendarEventClient]).to(classOf[GoogleCalendarEventClientImpl])
    bind(classOf[CredentialRepository]).to(classOf[CredentialDynamoDBRepository])
  }
}
