package calendar_sync.domain.credential

case class Credential(accessToken: String, expiresIn: Long, refreshToken: String) {
  def refresh(refreshedAccessToken: RefreshedAccessToken) =
    Credential(refreshedAccessToken.accessToken, refreshedAccessToken.expiresIn, refreshToken)
}
