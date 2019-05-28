package calendar_sync.infrastracture.google.calendar

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
      case GoogleEventDateTime(Some(date), None) => EventDate(date)
      case GoogleEventDateTime(None, Some(dateTime)) => EventDateTime(dateTime)
    }
    Some(arg.id, arg.summary, start, end)
  }

  def apply(event: Event) = {
    val start = event.start match {
      case EventDate(value) => GoogleEventDateTime(value)
      case EventDateTime(value) => GoogleEventDateTime(value)
    }

    val end = event.end match {
      case EventDate(value) => GoogleEventDateTime(value)
      case EventDateTime(value) => GoogleEventDateTime(value)
    }

    GoogleEvent("", event.title, start, end)
  }
}
