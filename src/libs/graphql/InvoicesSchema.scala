package libs.graphql

import sangria.schema._
import sangria.macros.derive._
import java.time.LocalDateTime
import java.util.UUID
import scala.collection.mutable.ListBuffer
import slick.jdbc.MySQLProfile.api._
import libs.db.{Invoice, InvoiceRepo}
import libs.types.DateTimeScalar.{DateTimeType, DateTimeCoerceViolation}

object InvoicesSchema {
  implicit val InvoiceType: ObjectType[Unit, Invoice] =
    deriveObjectType[Unit, Invoice](
      ReplaceField("createdAt", Field("createdAt", DateTimeType, resolve = _.value.createdAt)),
      ReplaceField("updatedAt", Field("updatedAt", DateTimeType, resolve = _.value.updatedAt))
    )

  val MutationType = ObjectType("Mutation", fields[Unit, Unit](
    Field("createInvoice", InvoiceType,
      arguments = Argument("recipientEmail", StringType) ::
                 Argument("recipientName", StringType) :: Nil,
      resolve = ctx => InvoiceRepo.createInvoice(
        ctx.args.arg[String]("recipientEmail"),
        ctx.args.arg[String]("recipientName")
      )
    ),
    Field("updateInvoiceById", OptionType(InvoiceType),
      arguments = Argument("id", IDType) ::
                  Argument("recipientEmail", StringType) ::
                  Argument("recipientName", StringType) :: Nil,
      resolve = ctx => InvoiceRepo.updateInvoiceById(
        ctx.args.arg[String]("id"),
        ctx.args.arg[String]("recipientEmail"),
        ctx.args.arg[String]("recipientName")
      )
    )
  ))

  val QueryType = ObjectType("Query", fields[Unit, Unit](
    Field("getInvoices", ListType(InvoiceType),
      resolve = _ => InvoiceRepo.getInvoices
    ),
    Field("getInvoiceById", OptionType(InvoiceType),
      arguments = Argument("id", StringType) :: Nil,
      resolve = ctx => InvoiceRepo.getInvoiceById(ctx.args.arg[String]("id"))
    )
  ))

  val SchemaDefinition = sangria.schema.Schema(QueryType, Some(MutationType))
}
