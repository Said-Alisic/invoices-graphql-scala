package libs.config

import io.github.cdimascio.dotenv.Dotenv
import slick.jdbc.MySQLProfile.api._

object DatabaseConfig {
  private val dotenv = Dotenv.load() // Loads from root .env file

  private val dbUrl = dotenv.get("DB_URL")
  private val dbUser = dotenv.get("MYSQL_USER")
  private val dbPassword = dotenv.get("MYSQL_PASSWORD")

  val DB: Database = Database.forURL(
    url = dbUrl,
    user = dbUser,
    password = dbPassword,
    driver = "com.mysql.cj.jdbc.Driver"
  )
}
