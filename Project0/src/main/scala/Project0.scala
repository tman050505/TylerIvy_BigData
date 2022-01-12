import java.util.Scanner
import java.sql.DriverManager
import java.sql.Connection
import java.util.Scanner
import java.io.PrintWriter
import java.io.File
import java.util.Calendar

object Project0{
    var scanner = new Scanner(System.in)

    def main(args: Array[String]): Unit = {
        println("Hello. Welcome to my banking application")
        
    val driver = "com.mysql.cj.jdbc.Driver"
    val url = "jdbc:mysql://127.0.0.1:3306/project_0" // Modify for whatever port you are running your DB on
    val username = "root"
    val password = "BadBitch" // Update to include your password
    val log = new PrintWriter(new File("query.log"))

    var connection:Connection = null

    try {
      // make the connection
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)
      var scanner = new Scanner(System.in)
        println("(1) Create Account")
        println("(2) Exit Application")
        val input:Int = scanner.nextInt()
    if(input== 1){
        println("Please enter a username")
        val username = scanner.nextLine()
        scanner.nextLine()
        println("Please enter your name")
        val name = scanner.nextLine()
        println("Please enter your email")
        val email = scanner.nextLine()
        println("Please enter your initial balance")
        val initbalance = scanner.nextLine()
        val statement = connection.createStatement()
        statement.executeQuery("Insert into Users (username, name, email, balance) Values ("+ username+", "+name+", " +email+", "+initbalance+");")
        log.write(Calendar.getInstance().getTimeInMillis + " - Excecuting 'Insert into Users (username, name, email, balance) Values ("+ username+", "+name+", " +email+", "+initbalance+");'\n")
    }else if(input == 2) {
        println("Thank you for utilizing my application. Have an amazing day.")

    }else{
        println("Whomp! Whomp! That was not a valid entry")}

    while (input == 1) {
        println("Hello! What would you like to do today?")
        println("[1] View Balance")
        println("[2] Make a Deposit")
        println("[3] Make a Withdrawal")
        println("[4] Close Account")
        println("[5] Log Off")
   
        var initbalance= 0
        var input2 = scanner.nextInt()
            input2 match{
            case 1  => println("Here is your balance:")
                        val statement = connection.createStatement()
                        val resultSet = statement.executeQuery("SELECT balance FROM Users where username = "+username+";")
                        log.write(Calendar.getInstance().getTimeInMillis + " - Excecuting 'SELECT balance FROM Users WHERE username = "+username+";'\n")
                        println(resultSet.getString(1)+""+resultSet.getString(2)+""+resultSet.getString(3)+""+ resultSet.getString(4))
                        println()
            case 2  => println("How much would you like to deposit?")
                        var amount = scanner.nextInt()
                        if( amount<0){ println("Not a valid amount")
                        }else{ println("This is your new balance:")
                                val  deposit1 = initbalance+=amount
                                println(deposit1)
                                val statement4 = connection.createStatement()
                                val resultSet4 = statement4.executeUpdate("Update users set balance = "+deposit1+"")
                        log.write(Calendar.getInstance().getTimeInMillis + " - Excecuting 'Update users set balance = "+deposit1+";'\n") }
            case 3  => println("How much would you like to withdraw?")
                        var amount = scanner.nextInt()
                        if(amount<0 || amount > initbalance){ 
                        println("Please enter a valid number")
                        }else { println("This is your new balance:")
                                val newBalance = (initbalance-=amount)
                                println(newBalance)
                                val statement3 = connection.createStatement()
                                val resultSet3 = statement3.executeUpdate("Update users Set balance = "+newBalance+"")
                                log.write(Calendar.getInstance().getTimeInMillis + " - Excecuting 'Update users Set balance  = "+newBalance+";'\n") }
            case 4  => println("Are you sure you want to close your account")
                        println("[1] Yes, Close Account")
                        println("[2] No, Cancel")
                        var input3 = scanner.nextInt()
                        if(input3 == 1){
                             println("Sorry to see you go. Account successully deleted.")
                        val statement5 = connection.createStatement()
                        val resultSet5 = statement5.executeUpdate("Delete from users where username= "+username+"")
                        log.write(Calendar.getInstance().getTimeInMillis + " - Excecuting 'Delete FROM Users WHERE username = "+username+";'\n")}
                        else if (input==2){
                            println("Account not deleted")}
                            else{
                                println("This is not a valid entry")
                            }

            case 5  => println("You have successfully logged off")
            case _  => println("Invalid Entry")
            }

    }}
     catch {
      case e: Exception => {
      e.printStackTrace
        log.write("Error! "+ e.getMessage())
      }
    }
    connection.close()
    log.close()
  }

}
