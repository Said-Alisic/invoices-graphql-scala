import java.time.LocalDateTime

case class Invoice(
  id: String,
  recipientEmail: String,
  recipientName: String,
  createdAt: LocalDateTime,
  updatedAt: LocalDateTime
)
