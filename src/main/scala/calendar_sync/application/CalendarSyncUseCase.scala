package calendar_sync.application

import java.time.LocalDate

import calendar_sync.domain.EventsService

class CalendarSyncUseCase {
  def sync(): Unit = {
    val fromDate = LocalDate.now()
    val toDate = fromDate.plusDays(14)
    val eventService = new EventsService
    eventService.sync(fromDate, toDate)
  }
}
