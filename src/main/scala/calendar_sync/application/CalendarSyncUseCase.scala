package calendar_sync.application

import java.time.LocalDate

import calendar_sync.domain.IGoogleCalendarClient

class CalendarSyncUseCase(val calendarClient: IGoogleCalendarClient) {

  def sync(): Unit = {
    val fromDate = LocalDate.now()
    val toDate = fromDate.plusDays(14)

    val toCalendarId: String = ""
    val mainCalendarEvents = calendarClient.getEventsByCalendarId(toCalendarId, fromDate, toDate)
    mainCalendarEvents.foreach(
      event => event.eventId.foreach(
        id => calendarClient.delete(event.calendarId, id)
      )
    )

    val fromCalendarIds: Seq[String] = Nil
    fromCalendarIds.foreach{calendarId =>
      val fromCalendarEvents = calendarClient.getEventsByCalendarId(calendarId, fromDate, toDate)
      fromCalendarEvents.map(event => event.mask("私用")).foreach(e => calendarClient.create(e))
    }

  }
}
