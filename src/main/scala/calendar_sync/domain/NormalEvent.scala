package calendar_sync.domain

import java.time.LocalDateTime

class NormalEvent (calendarId: String, eventId: Option[String], title: String, val startDateTime: LocalDateTime, val endDateTime: LocalDateTime) extends Event(calendarId, eventId, title) {
  override def mask(newTitle: String): Event = {
    NormalEvent.apply(calendarId, None, newTitle, startDateTime, endDateTime)
  }
}

object NormalEvent {
  def apply(calendarId: String, eventId: Option[String], title: String, startDateTime: LocalDateTime, endDateTime: LocalDateTime): NormalEvent =
    new NormalEvent(calendarId, eventId, title, startDateTime, endDateTime)

  def unapply(arg: NormalEvent): Option[(String, Option[String], String, LocalDateTime, LocalDateTime)] = {
    Some(arg.calendarId, arg.eventId, arg.title, arg.startDateTime, arg.endDateTime)
  }
}
