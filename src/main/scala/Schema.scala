import sangria.schema._
import sangria.macros.derive._
import java.time.LocalDateTime
import java.util.UUID
import scala.collection.mutable.ListBuffer
import DateTimeScalar.{DateTimeType, DateTimeCoerceViolation}

// No need to import Invoice if it's a top-level case class in same package

object InvoiceRepo {
  val invoices: ListBuffer[Invoice] = ListBuffer.empty

  def createInvoice(email: String, name: String): Invoice = {
    val now = LocalDateTime.now()
    val invoice = Invoice(UUID.randomUUID().toString, email, name, now, now)
    invoices += invoice
    invoice
  }
}

object Schema { // renamed to avoid shadowing sangria.schema.Schema
  implicit val InvoiceType: ObjectType[Unit, Invoice] =
    deriveObjectType[Unit, Invoice](
      ReplaceField("createdAt", Field("createdAt", DateTimeType, resolve = _.value.createdAt)),
      ReplaceField("updatedAt", Field("updatedAt", DateTimeType, resolve = _.value.updatedAt))
    )

  val MutationType = ObjectType("Mutation", fields[Unit, Unit](
    Field("createInvoice", InvoiceType,
      arguments = Argument("recipientEmail", StringType) :: Argument("recipientName", StringType) :: Nil,
      resolve = ctx => InvoiceRepo.createInvoice(
        ctx.args.arg[String]("recipientEmail"),
        ctx.args.arg[String]("recipientName")
      )
    )
  ))

  val QueryType = ObjectType("Query", fields[Unit, Unit](
    Field("invoices", ListType(InvoiceType), resolve = _ => InvoiceRepo.invoices.toList)
  ))

  val SchemaDefinition = sangria.schema.Schema(QueryType, Some(MutationType))
}
