package calendar_sync

import calendar_sync.domain.CalendarEventClient
import calendar_sync.domain.credential.{CredentialRepository, OAuthClient}
import calendar_sync.infrastracture.GoogleCalendarEventClientImpl
import calendar_sync.infrastracture.dynamo_db.CredentialDynamoDBRepository
import calendar_sync.infrastracture.google.oauth.GoogleOauthClient
import com.google.inject.AbstractModule

class CalendarSyncModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[OAuthClient]).to(classOf[GoogleOauthClient])
    bind(classOf[CalendarEventClient]).to(classOf[GoogleCalendarEventClientImpl])
    bind(classOf[CredentialRepository]).to(classOf[CredentialDynamoDBRepository])
  }
}
