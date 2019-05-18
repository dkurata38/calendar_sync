package calendar_sync.application.service


import calendar_sync.domain.{Duration, Event, GoogleCalendarEventClient}
import javax.inject.{Inject, Singleton}

@Singleton
class EventsQueryService @Inject() (private val client: GoogleCalendarEventClient) {
  def query(calendarId: String, duration: Duration): Either[Throwable, Seq[Event]]
    = client.getEventsByCalendarId(calendarId, duration).toEither

  def query(calendarIds: Seq[String], duration: Duration): Either[Throwable, Seq[Event]] = calendarIds
    .map(calendarId => query(calendarId, duration))
    .reduce[Either[Throwable, Seq[Event]]]{
      case (Left(e) , _) => Left(e)
      case (Right(_), Left(e))     => Left(e)
      case (Right(a), Right(list)) => Right(a ++ list)
    }
}
