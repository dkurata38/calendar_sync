package calendar_sync.domain

import scala.util.Try

trait GoogleCalendarEventClient {
  def getEventsByCalendarId(calendarId: String, duration: Duration): Try[Seq[Event]]

  def create(calendarId: String, event: Event): Try[Event]

  def delete(calendarId: String, eventId: String): Try[Unit]
}
