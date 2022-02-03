import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.sql.SQLContext
import java.sql.DriverManager
import java.sql.Connection
import java.util.Scanner
import java.sql.ResultSet
import java.sql.Statement
import java.io.PrintWriter
import java.io.File
import java.io.FileInputStream

object Project1Ex {
  var statment: Statement = null
  var log = new PrintWriter(new File("query.log"))
  var resultSet: ResultSet = null
  var username = ""
  var password = ""
  val adminName = ""
  val input = ""
  var oldPassword = ""
  
  //val sc = new SparkContext(conf)
  //val hiveCtx = new HiveContext(sc)
  val continue = true
  var usernameDelete = ""
  var scanner = new Scanner(System.in)
  def main(args: Array[String]): Unit = {
    // This block of code is all necessary for spark/hive/hadoop
  val hiveCtx = new HiveContext(sc)
  val sc = new SparkContext(conf)
  val conf = new SparkConf().setMaster("local").setAppName("Project1")
    System.setSecurityManager(null)
    System.setProperty(
      "hadoop.home.dir",
      "C:\\hadoop\\"
    ) // change if winutils.exe is in a different bin folder
    //val conf = new SparkConf()
    // .setMaster("local")
    //  .setAppName("Project1") // Change to whatever app name you want

    sc.setLogLevel("ERROR")

    import hiveCtx.implicits._

    //This block to connect to mySQL
    val driver = "com.mysql.cj.jdbc.Driver"
    val url =
      "jdbc:mysql://localhost:3306/project1" // Modify for whatever port you are running your DB on
    val username = "root"
    val password = "#####" // Update to include your password
    var connection: Connection = null
    try {
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)
      val statement = connection.createStatement()
      while (true) {
        mainPage()

        def mainPage() {
          println("Welcome to the main page")
          println("Would you like to:")
          println("1: Create Admin Account")
          println("2: Create User Account")
          println("3: Admin Login")
          println("4: User Login")
          var scan = (scanner.nextInt())

          if (scan == 1) {
            createAdminAccount(scanner)
          } else if (scan == 2) {
            createUserAccount(scanner)
          } else if (scan == 3) {
            adminLogin()
          } else if (scan == 4) {
            userLogin()
          } else if (scan != 1 || scan != 2 || scan != 3 || scan != 4) {
            println("Invalid Option")
          }

          def createUserAccount(scanner: Scanner): Unit = {
            var username = ""
            var password = ""
            var userType = ""
            var resultSet =
              statement.executeQuery("Select username From users;")

            println("Enter a username")
            username = (scanner.nextLine())
            var resultSetUsername = resultSet.getString("username")
            if (username == resultSetUsername) {
              println("Username must be unique. Try again")
              mainPage()
            } else {
              println("Enter a password")

              password = (scanner.nextLine())
              println("Accounted Created")
              userPage(scanner)
              val resultSet2 = statement.executeUpdate(
                "INSERT INTO users (username, upassword) VALUES ('" + username + "', '" + password + "');"
              )
              log.write(
                "Executing 'INSERT INTO users (username, userPassword,) VALUES ('" + username + "', '" + password + "');\n"
              )
            }
          }

          def createAdminAccount(scanner: Scanner): Unit = {
            var adminName = ""
            var adminPassword = ""

            var resultSet2 =
              statement.executeQuery("Select username From admins;")
            println("Enter a admin name")
            adminName = (scanner.nextLine())
            var resultSetAdminName = resultSet.getString("adminName")
            if (adminName == resultSetAdminName) {
              println("Admin name must be unique. Try again")
              createAdminAccount(scanner)
            } else {
              println("Enter a password")
              adminPassword = (scanner.nextLine())
              println("Accounted Created")
              adminPage()
              val resultSet3 = statement.executeUpdate(
                "INSERT INTO admins (adminName, adminPassword) VALUES ('" + adminName + "', '" + adminPassword + "');"
              )
              log.write(
                "Executing 'INSERT INTO admins (adminName, adminPassword) VALUES ('" + adminName + "', '" + adminPassword + "');\n"
              )
            }
          }

          def userLogin(): Unit = {
            println(" Enter username")
            var username = scanner.nextLine().trim()
            println("")
            println(" Enter password")
            var password = scanner.nextLine().trim()
            val resultSet4 = statement.executeQuery(
              "SELECT COUNT(*) FROM users WHERE username='" + username + "' AND password='" + password + "';"
            )
            log.write(
              "Executing 'SELECT COUNT(*) FROM users WHERE username='" + username + "' AND password='" + password + "');\n"
            )
            while (resultSet4.next()) {
              if (resultSet4.getString(1) == "1") {
                println("You Have Logged In Successfully")
                userPage(scanner)

              } else {
                println("Username/password combo not found. Try again!")
                userLogin()

              }
            }
          }

          def adminLogin() {
            println(" Enter admin name")
            var adminName = scanner.nextLine().trim()
            println("")
            println(" Enter password")
            var adminPassword = scanner.nextLine().trim()
            val resultSet5 = statement.executeQuery(
              "SELECT COUNT(*) FROM admins WHERE adminName='" + adminName + "' AND adminPassword='" + adminPassword + "';"
            )
            log.write(
              "Executing 'SELECT COUNT(*) FROM admins WHERE adminName='" + adminName + "' AND adminPassword='" + adminPassword + "');\n"
            )
            while (resultSet5.next()) {
              if (resultSet5.getString(1) == "1") {
                println("You Have Logged In Successfully")
                adminPage()

              } else {
                println("Account not found. Try again!")
                adminLogin()
              }
            }
          }

          def adminPage(): Unit = {
            var createAdminPage = true
            var adminScan = scanner.nextLine().trim()
            while (createAdminPage) {
              println("What would you like to do?")
              println("1: Users with the highest income")
              println("2: Users with the lowest income")
              println("3: Users with the highest credit usage")
              println("4: Users with the lowest credit usage")
              println("5: Users with the highest credit limit")
              println("6: Users with the lowest credit limit")
              println("7: Update password")
              println("8: Delete User")
              println("9: LogOff")

              if (adminScan == "1") {
                highestTransactionAmount()
              } else if (adminScan == "2") {
                lowestTransactionAmount()
              } else if (adminScan == "3") {
                highestRevolvingBalance()
              } else if (adminScan == "4") {
                lowestRevolvingBalance()
              } else if (adminScan == "5") {
                highestCreditLimit()
              } else if (adminScan == "6") {
                lowestCreditLimit()
              } else if (adminScan == "7") {
                deleteUser()
              } else if (adminScan == "8") {
                updateUser()
              } else if (adminScan == "9") {
                createAdminPage = false
                println("GoodBye")
              } else {
                println("Invalid option")
              }
            }
          }

          def userPage(scanner: Scanner): Unit = {
            var displayUserPage = true
            while (displayUserPage) {
              println("What would you like to do?")
              println("1: Users with the highest income")
              println("2: Users with the lowest income")
              println("3: Users with the highest credit usage")
              println("4: Users with the lowest credit usage")
              println("5: Users with the highest credit limit")
              println("6: Users with the lowest credit limit")
              println("7: LogOff")
              var userScan = scanner.nextLine().trim
              if (userScan == "1") {
                highestTransactionAmount()
              } else if (userScan == "2") {
                lowestTransactionAmount()
              } else if (userScan == "3") {
                highestRevolvingBalance()
              } else if (userScan == "4") {
                lowestRevolvingBalance()
              } else if (userScan == "5") {
                highestCreditLimit()
              } else if (userScan == "6") {
                lowestCreditLimit()
              } else if (userScan == "7") {
                displayUserPage = false
                println("GoodBye")
              } else {
                println("Invalid option")
              }

            }

          }

          def deleteUser() {
            var usernameDelate = ""
            println(" Choose A User To Delete ")

            val resultSet6 = statement.executeQuery("SELECT * FROM users")
            log.write("Executing 'SELECT * FROM user';\n")
            println(" Type the username you wish to delete")
            var resultSetAdminName = resultSet.getString("adminName")
            if (adminName == resultSetAdminName) {

              usernameDelete = scanner.nextLine().trim()
              val resultSet8 = statement.executeUpdate(
                "DELETE FROM users WHERE username = ('" + usernameDelete + "');"
              )
              log.write("Executing 'DELETE User from database' \n")
              println("User Deleted")
              val resultSet7 = statement.executeQuery("SELECT * FROM users")
              log.write("Executing 'SELECT * FROM users';\n")
              adminPage()
            } else {
              println("Please enter a valid username")
              deleteUser()
            }
          }

          def updateUser() {
            var oldUsername = ""
            var userNameUpdate = ""
            var oldPassword = ""
            println("1:Update Username")
            println("2: Update Password")
            var input = (scanner.nextInt())
            (scanner.nextLine())
            if (input == 1) {
              println("Enter your old username")
              oldUsername = scanner.nextLine().trim
              println("Enter a new username")
              userNameUpdate = scanner.nextLine().trim()
              val resultSet9 = statement.executeUpdate(
                "UPDATE users SET username = ('" + userNameUpdate + "') WHERE username = ('" + oldUsername + "');"
              )
              log.write(
                "UPDATE users SET username = ('" + userNameUpdate + "') WHERE username = ('" + oldUsername + "') \n"
              )
              println("UserName Updated")
              adminPage()

            } else if (input == 2) {
              println("Type old password")
              var oldPassword = scanner.nextLine().trim()
              println(" Type New Password")

              var passwordUpdate = scanner.nextLine().trim()
              val resultSet10 = statement.executeUpdate(
                "UPDATE users SET password = ('" + passwordUpdate + "') WHERE password = ('" + oldPassword + "');"
              )
              log.write(
                "UPDATE users SET password = ('" + passwordUpdate + "') WHERE password = ('" + oldPassword + "') \n"
              )
              adminPage()
            } else {
              println("Invalid Option")
              adminPage()
            }
          }
        }

        def insertCreditCardData() {
          hiveCtx.sql(
            "LOAD DATA LOCAL INPATH 'input/BankChurners.csv' OVERWRITE INTO TABLE data1"
          )

          val output = hiveCtx.read
            .format("csv")
            .option("inferSchema", "true")
            .option("header", "true")
            .load("input/BankChurners.csv")
          output.limit(15).show()

          output.createOrReplaceTempView("temp_data")
          hiveCtx.sql("SET hive.enforce.dynamic.partition.mode=nonstrict")
          hiveCtx.sql("SET hive.enforce.bucketing=false")
          hiveCtx.sql("SET hive.enforce.sorting=false")
          hiveCtx.sql(
            "CREATE TABLE IF NOT EXISTS data1 (CLIENTNUM INT, Attrition_Flag STRING, Customer_Age INT, Gender STRING, Dependant_count INT, Education_Level STRING, Marital_Status STRING, Income_Category STRING, Card_Category STRING, Months_on_book INT, Total_Relationhip_Count INT, Months_Inactive_12_mon INT, Contact_Count_12_mon INT, Credit_Limit INT, Total_Revolving_Bal INT, Avg_Open_To_Buy INT, Total_Amt_Chng_Q4_Q1 DOUBLE, Total_Trans_Amt INT, Total_Trans_Ct INT, Total_Ct_Chng_Q4_Q1 INT, Avg_Utilization_Ratio DOUBLE, Naive_Bayes_Classifier_Attrition_Flag_Card_Category_Contacts_Count_12_mon_Dependant_count_Education_Level_Month_Inactive_12_mon_1 DOUBLE, Naive_Bayes_Classifier_Attrition_Flag_Card_Category_Contacts_Count_12_mon_Dependent_count_Education_Level_Months_Inactive_12_mon_2 DOUBLE )"
          )
          hiveCtx.sql("INSERT INTO data1 SELECT * FROM temp_data")

          val summary = hiveCtx.sql("SELECT * FROM data1 LIMIT 10")
          summary.show()
        }

        def highestTransactionAmount(): Unit = {
          val result = hiveCtx.sql(
            "SELECT MAX(Total_Trans_Amt) from data1 group by Total_Trans_Amt DESC LIMIT 10"
          )
          result.show()
          result.write.csv("results/highestTransactionAmount")
        }
        def lowestTransactionAmount(): Unit = {
          val result =
            hiveCtx.sql("SELECT MIN(Total_Trans_Amt) from data1 DESC LIMIT 10")
          result.show()
          result.write.csv("results/lowestTransactionAmount")
        }

        def highestRevolvingBalance(): Unit = {
          val result =
            hiveCtx.sql(
              "SELECT MAX(Total_Revolving_Bal FROM data1 DESC LIMIT 10"
            )
          result.show()
          result.write.csv("results/highestRevolvingBalance")

        }
        def lowestRevolvingBalance(): Unit = {
          val result =
            hiveCtx.sql(
              "SELECT MAX(Total_Revolving_Bal FROM data1 DESC LIMIT 10"
            )
          result.show()
          result.write.csv("results/lowestRevolvingBalance")
        }
        def highestCreditLimit(): Unit = {
          val result =
            hiveCtx.sql("SELECT MAX(Credit_Limit) from data1 limit 10")
          result.show()
          result.write.csv("results/highestCreditUsage")
        }
        def lowestCreditLimit(): Unit = {
          val result =
            hiveCtx.sql("SELECT MIN(Credit_Limit) from data1 limit 10")
          result.show()
          result.write.csv("results/lowestCreditLimit")
        }
      }
    } catch { case e: Exception => e.printStackTrace }
    connection.close()
    log.close()
  }
}
