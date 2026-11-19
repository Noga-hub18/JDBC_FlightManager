import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class FlightViewer {
    private static final String URL =
            "jdbc:mysql://localhost:3306/FlightDB?allowLoadLocalInfile=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jerusalem&useSSL=false";
    private static final String USER = "root";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Please enter your MySQL root password: ");
            String password = scanner.nextLine();

            String terminal;
            while (true) {
                System.out.print("Enter terminal number (2, 3, 4, or 5): ");
                terminal = scanner.nextLine().trim();
                if (terminal.equals("2") || terminal.equals("3")
                        || terminal.equals("4") || terminal.equals("5")) {
                    break;
                }
                System.out.println("Invalid terminal. Please enter 2, 3, 4, or 5.");
            }

            String sql = "SELECT Arrivals.id, Arrivals.flight_number, FlightRoutes.from_city, "
                    + "Arrivals.scheduled, Arrivals.delay_status, Arrivals.terminal "
                    + "FROM Arrivals INNER JOIN FlightRoutes "
                    + "ON Arrivals.flight_number = FlightRoutes.flight_number "
                    + "WHERE Arrivals.terminal = ? "
                    + "ORDER BY Arrivals.scheduled ASC";

            try (Connection conn = DriverManager.getConnection(URL, USER, password);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, terminal);

                try (ResultSet rs = pstmt.executeQuery()) {
                    boolean found = false;
                    while (rs.next()) {
                        if (!found) {
                            System.out.println();
                            System.out.printf("%-6s | %-14s | %-20s | %-20s | %-20s | %-8s%n",
                                    "ID", "Flight", "From City", "Scheduled", "Delay Status", "Terminal");
                            System.out.println("-----------------------------------------------------------------------------------------------");
                            found = true;
                        }
                        System.out.printf("%-6d | %-14s | %-20s | %-20s | %-20s | %-8s%n",
                                rs.getInt("id"),
                                rs.getString("flight_number"),
                                rs.getString("from_city"),
                                rs.getString("scheduled"),
                                rs.getString("delay_status"),
                                rs.getString("terminal"));
                    }

                    if (!found) {
                        System.out.println("No arriving flights found for terminal " + terminal + ".");
                    }
                }
            } catch (SQLException e) {
                System.err.println("Failed to retrieve arriving flights.");
                System.err.println("Message: " + e.getMessage());
            }
        }
    }
}
