package calendar_sync.domain

import java.time.LocalDate

trait IEventsService {
  def sync(startDate: LocalDate, endDate: LocalDate)
}
