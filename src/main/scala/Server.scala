import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.circe._
import io.circe.Json
import io.circe.parser._
import io.circe.syntax._
import sangria.execution._
import sangria.parser.QueryParser
import sangria.marshalling.circe._

import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

object Server extends IOApp {

  implicit val decoder: EntityDecoder[IO, Json] = jsonOf[IO, Json]

  def graphqlEndpoint: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "graphql" / "invoices" =>
      req.as[Json].flatMap { body =>
        val query = body.hcursor.get[String]("query").getOrElse("")
        val operationName = body.hcursor.get[Option[String]]("operationName").getOrElse(None)
        val variables = body.hcursor.get[Option[Json]]("variables").getOrElse(Some(Json.obj()))

        QueryParser.parse(query) match {
          case Success(ast) =>
            val execution = Executor.execute(
              schema = InvoicesSchema.SchemaDefinition,
              queryAst = ast,
              userContext = (),
              variables = variables.getOrElse(Json.obj()),
              operationName = operationName
            )

            // Convert Future to IO
            IO.fromFuture(IO(execution))
              .map(result => Response[IO](status = Status.Ok).withEntity(result.noSpaces))
              .handleError {
                case error: QueryAnalysisError =>
                  Response[IO](status = Status.BadRequest).withEntity(error.resolveError.asJson.noSpaces)
                case error: ErrorWithResolver =>
                  Response[IO](status = Status.InternalServerError).withEntity(error.resolveError.asJson.noSpaces)
              }

          case Failure(error) =>
            BadRequest(Json.obj("error" -> Json.fromString(error.getMessage)))
        }
      }
  }

  override def run(args: List[String]): IO[ExitCode] = {
    val httpApp = graphqlEndpoint.orNotFound

    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(httpApp)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }
}
