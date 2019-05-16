package calendar_sync.domain

import java.time.LocalDate

case class Date(value: LocalDate) {
  def +(days: Int) = Duration(this, Date(this.value.plusDays(days)))
}

case object Date {
  def today = Date(LocalDate.now())
}