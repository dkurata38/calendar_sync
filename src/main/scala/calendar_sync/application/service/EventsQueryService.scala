package calendar_sync.application.service


import calendar_sync.domain.{Duration, Event}
import calendar_sync.infrastracture.GoogleCalendarEventClientImpl

class EventsQueryService {
  def query(calendarId: String, duration: Duration): Either[Throwable, Seq[Event]]
    = new GoogleCalendarEventClientImpl().getEventsByCalendarId(calendarId, duration).toEither

  def query(calendarIds: Seq[String], duration: Duration): Either[Throwable, Seq[Event]] = calendarIds
    .map(calendarId => query(calendarId, duration))
    .reduce{
      case (Left(e) , _)=> Left(e)
      case (Right(_), Left(e))     => Left(e)
      case (Right(a), Right(list)) => Right(a ++ list)
    }
}
