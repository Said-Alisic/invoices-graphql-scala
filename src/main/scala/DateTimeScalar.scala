import java.time.LocalDateTime
import sangria.schema._
import sangria.ast.StringValue
import sangria.validation.Violation

object DateTimeScalar {
  case object DateTimeCoerceViolation extends Violation {
    override def errorMessage: String = "Invalid DateTime format"
  }

  val DateTimeType: ScalarType[LocalDateTime] = ScalarType[LocalDateTime](
    name = "DateTime",
    coerceOutput = (ldt, _) => ldt.toString,
    coerceUserInput = {
      case s: String => Right(LocalDateTime.parse(s))
      case _         => Left(DateTimeCoerceViolation)
    },
    coerceInput = {
      case StringValue(s, _, _, _, _) => Right(LocalDateTime.parse(s))
      case _                          => Left(DateTimeCoerceViolation)
    }
  )
}
