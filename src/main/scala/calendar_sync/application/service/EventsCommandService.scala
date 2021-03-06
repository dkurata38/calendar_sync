package calendar_sync.application.service

import calendar_sync.domain.{Event, GoogleCalendarEventClient}
import javax.inject.{Inject, Singleton}

@Singleton
class EventsCommandService @Inject()(private val client: GoogleCalendarEventClient) {
  def delete(calendarId: String, eventId: String) = client.delete(calendarId, eventId).toEither

  def create(calendarId: String, event: Event) = client.create(calendarId, event).toEither
}
