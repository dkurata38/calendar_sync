package calendar_sync.infrastracture

import java.time._
import java.util.Date

import calendar_sync.domain.{AllDayEvent, Event, IGoogleCalendarClient, NormalEvent}
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.EventDateTime
import com.google.api.services.calendar.{Calendar, model}

import scala.collection.JavaConverters._

class GoogleCalendarClient extends IGoogleCalendarClient{
  /**
    * https://developers.google.com/calendar/v3/reference/events/list
    * @param calendarId
    * @param startDateTime
    * @param endDateTime
    * @return
    */
  override def getEventsByCalendarId(calendarId: String, startDateTime: LocalDate, endDateTime: LocalDate): Seq[Event] = {
    val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
    val jsonFactory = JacksonFactory.getDefaultInstance
    val service = new Calendar.Builder(httpTransport, jsonFactory, null).setApplicationName("").build()

    val timeMin = new DateTime(Date.from(startDateTime.atStartOfDay(ZoneId.systemDefault()).toInstant))
    val timeMax = new DateTime(Date.from(endDateTime.atStartOfDay(ZoneId.systemDefault()).toInstant))

    def getEvents(pageToken: String): Seq[Event] = {
      @scala.annotation.tailrec
      def recursive(pageToken: String, aevents: Seq[Event]): Seq[Event] = {
        val response = service.events().list("").setPageToken(pageToken).setTimeMin(timeMin).setTimeMax(timeMax).execute()
        val events = response.getItems.asScala.map{i =>
          if (i.getStart.getDate == null) {
            NormalEvent(calendarId, Some(i.getId), i.getStatus,
              LocalDateTime.ofInstant(Instant.ofEpochMilli(i.getStart.getDateTime.getValue), ZoneId.of("GMT")),
              LocalDateTime.ofInstant(Instant.ofEpochMilli(i.getEnd.getDateTime.getValue), ZoneId.of("GMT")))
          } else {
            AllDayEvent(calendarId, Some(i.getId), i.getStatus,
              LocalDateTime.ofInstant(Instant.ofEpochMilli(i.getStart.getDate.getValue), ZoneId.of("GMT")).toLocalDate,
              LocalDateTime.ofInstant(Instant.ofEpochMilli(i.getEnd.getDate.getValue), ZoneId.of("GMT")).toLocalDate)
          }
        }
        if (pageToken == null) return aevents ++: events
        else recursive(response.getNextPageToken, aevents ++: events)
      }

      recursive(null, Seq.empty)
    }

    getEvents(null)
  }

  /**
    * https://developers.google.com/calendar/v3/reference/events/insert
    * @param event
    */
  override def create(event: Event): Unit = {
    val newEvent = event match {
      case NormalEvent(_, _, title, start, end) => {
        val startDateTime = new DateTime(Date.from(start.atZone(ZoneId.systemDefault()).toInstant))
        val endDateTime = new DateTime(Date.from(end.atZone(ZoneId.systemDefault()).toInstant))
        new model.Event()
          .setSummary(title)
          .setStart(new EventDateTime()
            .setDateTime(startDateTime)
            .setTimeZone(ZoneId.systemDefault().toString))
          .setEnd(new EventDateTime()
            .setDateTime(endDateTime)
            .setTimeZone(ZoneId.systemDefault().toString))
      }
      case AllDayEvent(_, _, title, start, end) => {
        val startDateTime = new DateTime(Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant))
        val endDateTime = new DateTime(Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant))
        new model.Event()
          .setSummary(title)
          .setStart(new EventDateTime()
            .setDate(startDateTime)
            .setTimeZone(ZoneId.systemDefault().toString))
          .setEnd(new EventDateTime()
            .setDate(endDateTime)
            .setTimeZone(ZoneId.systemDefault().toString))
      }
    }
    val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
    val jsonFactory = JacksonFactory.getDefaultInstance
    val service = new Calendar.Builder(httpTransport, jsonFactory, null).setApplicationName("").build()

    service.events().insert(event.calendarId, newEvent)
  }

  override def delete(calendarId: String, eventId: String): Unit = {
    val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
    val jsonFactory = JacksonFactory.getDefaultInstance
    val service = new Calendar.Builder(httpTransport, jsonFactory, null).setApplicationName("").build()

    service.events().delete(calendarId, eventId)
  }
}
