package calendar_sync.infrastracture

import java.util
import java.util.Collections

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.calendar.CalendarScopes
import com.google.inject.Scopes

class AbstractGoogleCalendarClient {
  protected val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance
  protected val httpTransport: HttpTransport = GoogleNetHttpTransport.newTrustedTransport()
  protected val scopes: util.List[String] = Collections.singletonList(CalendarScopes.CALENDAR)

}
