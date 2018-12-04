package calendar_sync.domain

import java.time.LocalDate

class AllDayEvent(calendarId: String, eventId: Option[String], title: String, val startDate: LocalDate, val endDate: LocalDate) extends Event (calendarId, eventId, title) {
  override def mask(newTitle: String): Event = {
    AllDayEvent.apply(calendarId, None, newTitle, startDate, endDate)
  }
}

object AllDayEvent {
  def apply(calendarId: String, eventId: Option[String], title: String, startDate: LocalDate, endDate: LocalDate): AllDayEvent = 
    new AllDayEvent(calendarId, eventId, title, startDate, endDate)

  def unapply(arg: AllDayEvent): Option[(String, Option[String], String, LocalDate, LocalDate)] = {
    Some(arg.calendarId, arg.eventId, arg.title, arg.startDate, arg.endDate)
  }
}