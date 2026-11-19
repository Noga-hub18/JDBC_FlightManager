import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Updates the delay status for flight {@code LX354} in the FlightDB database.
 *
 * <p>The previous implementation built SQL by concatenating the status entered by the user
 * into a {@code Statement} query. Because the input is untrusted, SQL syntax embedded in it
 * (such as a quote followed by additional SQL) could change the meaning of the query and
 * potentially update records beyond the intended flight. A {@code PreparedStatement} avoids
 * this injection by keeping the SQL structure fixed and binding the status as a value with
 * {@link PreparedStatement#setString(int, String)}. Characters in the status are therefore
 * treated as data, not executable SQL. Do not interpolate user-provided values into the SQL
 * string; bind them through the appropriate setter instead.</p>
 */
public class SimpleFlightUpdater {
    private static final String URL =
            "jdbc:mysql://localhost:3306/FlightDB?allowLoadLocalInfile=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jerusalem&useSSL=false";
    private static final String USER = "root";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Please enter your MySQL root password: ");
            String password = scanner.nextLine();

            System.out.print("Enter the new delay status for flight LX354: ");
            String delayStatus = scanner.nextLine();

            String sql = "UPDATE Arrivals SET delay_status = ? WHERE flight_number = 'LX354'";
            try (Connection connection = DriverManager.getConnection(URL, USER, password);
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, delayStatus);
                int affectedRows = statement.executeUpdate();
                System.out.println("Number of affected rows: " + affectedRows);
            } catch (SQLException e) {
                System.err.println("Failed to update the delay status.");
                System.err.println("Message: " + e.getMessage());
            }
        }
    }
}