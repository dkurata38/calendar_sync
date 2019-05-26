package calendar_sync.domain

case class Credential(accessToken: String, expiresIn: Long, refreshToken: String)
