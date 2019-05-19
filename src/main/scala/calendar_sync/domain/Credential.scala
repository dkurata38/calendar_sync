package calendar_sync.domain

case class Credential(accessToken: String, tokenType: String, expiresIn: Long, refreshToken: String)
