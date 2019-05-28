package calendar_sync.infrastracture.google.calendar

import java.time.{LocalDate, LocalDateTime}

import calendar_sync.domain.event._

case class GoogleEvent(id: String, summary: String, start: GoogleEventDateTime, end: GoogleEventDateTime) {
  def toEvent: Event = this match {
    case GoogleEvent(_1, _2, _3, _4) => new Event(EventId(_1), _2, _3, _4)
  }
}

object GoogleEvent {
  def unapply(arg: GoogleEvent): Option[(String, String, EventDateAndTime, EventDateAndTime)] = {
    val start = arg.start match {
      case GoogleEventDateTime(Some(date), None) => EventDate(date)
      case GoogleEventDateTime(None, Some(dateTime)) => EventDateTime(dateTime)
    }
    val end = arg.end match {
      case GoogleEventDateTime(Some(date: LocalDate), None) => EventDate(date)
      case GoogleEventDateTime(None, Some(dateTime: LocalDateTime)) => EventDateTime(dateTime)
    }
    Some(arg.id, arg.summary, start, end)
  }

  def of(event: Event) = {
    val start = event.start match {
      case EventDate(value) => GoogleEventDateTime.of(value)
      case EventDateTime(value) => GoogleEventDateTime.of(value)
    }

    val end = event.end match {
      case EventDate(value) => GoogleEventDateTime.of(value)
      case EventDateTime(value) => GoogleEventDateTime.of(value)
    }

    GoogleEvent("", event.title, start, end)
  }
}
