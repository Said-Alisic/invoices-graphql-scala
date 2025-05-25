package libs.db

import slick.jdbc.MySQLProfile.api._
import java.time.LocalDateTime

class Invoices(tag: Tag) extends Table[Invoice](tag, "invoices") {
  def id = column[String]("id", O.PrimaryKey)
  def recipientEmail = column[String]("recipientEmail")
  def recipientName = column[String]("recipientName")
  def createdAt = column[LocalDateTime]("createdAt")
  def updatedAt = column[LocalDateTime]("updatedAt")

  def * = (id, recipientEmail, recipientName, createdAt, updatedAt) <> (Invoice.tupled, Invoice.unapply)
}

object Tables {
  val invoices: TableQuery[Invoices] = TableQuery[Invoices]
}
