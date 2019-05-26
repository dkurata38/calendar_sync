package calendar_sync.domain.event

import calendar_sync.domain.Duration

import scala.util.Try

trait CalendarEventClient {
  def getEventsByCalendarId(calendarId: String, duration: Duration): Try[Seq[Event]]

  def create(calendarId: String, event: Event): Try[Event]

  def delete(calendarId: String, eventId: String): Try[Unit]

}
