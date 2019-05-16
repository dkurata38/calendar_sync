package calendar_sync.application.service

import calendar_sync.domain.Event
import calendar_sync.infrastracture.GoogleCalendarClient

class EventsCommandService {
  def delete(calendarId: String, eventId: String) = new GoogleCalendarClient().delete(calendarId, eventId)

  def create(calendarId: String, event: Event) = new GoogleCalendarClient().create(calendarId, event)
}
