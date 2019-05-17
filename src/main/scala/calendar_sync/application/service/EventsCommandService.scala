package calendar_sync.application.service

import calendar_sync.domain.Event
import calendar_sync.infrastracture.GoogleCalendarEventClientImpl

class EventsCommandService {
  def delete(calendarId: String, eventId: String) = new GoogleCalendarEventClientImpl().delete(calendarId, eventId).toEither

  def create(calendarId: String, event: Event) = new GoogleCalendarEventClientImpl().create(calendarId, event).toEither
}
