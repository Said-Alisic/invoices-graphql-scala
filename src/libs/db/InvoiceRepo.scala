package libs.db

import slick.jdbc.MySQLProfile.api._
import scala.concurrent.Future
import java.time.LocalDateTime
import java.util.UUID
import libs.config.DatabaseConfig.DB
import scala.concurrent.ExecutionContext.Implicits.global
import libs.db.Tables

object InvoiceRepo {

  private val invoices = Tables.invoices

  def getInvoices: Future[Seq[Invoice]] = {
    DB.run(invoices.result)
  }

  def getInvoiceById(id: String): Future[Option[Invoice]] = {
    DB.run(invoices.filter(_.id === id).result.headOption)
  }

  def createInvoice(email: String, name: String): Future[Invoice] = {
    val now = LocalDateTime.now()
    val invoice = Invoice(UUID.randomUUID().toString, email, name, now, now)
    val insertAction = invoices += invoice
    DB.run(insertAction).map(_ => invoice)
  }

  def updateInvoiceById(id: String, newEmail: String, newName: String): Future[Option[Invoice]] = {
    val now = LocalDateTime.now()
    val query = invoices.filter(_.id === id)
    val updateAction = for {
      existing <- query.result.headOption
      updated <- existing match {
        case Some(invoice) =>
          val updatedInvoice = invoice.copy(
            recipientEmail = newEmail,
            recipientName = newName,
            updatedAt = now
          )
          query.update(updatedInvoice).map(_ => Some(updatedInvoice))
        case None => DBIO.successful(None)
      }
    } yield updated

    DB.run(updateAction.transactionally)
  }
}
