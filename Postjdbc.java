package postgresql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Postjdbc {

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost/postgres";
        String user = "postgres";
        String pass = "12345";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            // Set auto-commit to false to manage transactions manually and adhering to atomicity
            conn.setAutoCommit(false);

            // Set isolation level to SERIALIZABLE to ensure isolation
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            try (Statement stmt = conn.createStatement()) {
                // Step 5: Add product (p100, cd, 5) in Product and (p100, d2, 50) in Stock
                stmt.executeUpdate("INSERT INTO product (prodid, pname, price) VALUES ('p100', 'cd', 5.00);");
                stmt.executeUpdate("INSERT INTO stock (prodid, depid, quantity) VALUES ('p100', 'd2', 50);");

                // Step 6: Add depot (d100, Chicago, 100) in Depot and (p1, d100, 100) in Stock
                stmt.executeUpdate("INSERT INTO depot (depid, addr, volume) VALUES ('d100', 'Chicago', 100);");
                stmt.executeUpdate("INSERT INTO stock (prodid, depid, quantity) VALUES ('p1', 'd100', 100);");

                // Step 3: Change product p1 to pp1 in Product and Stock (cascading update handled by foreign key constraint)
                stmt.executeUpdate("UPDATE product SET prodid = 'pp1' WHERE prodid = 'p1';");

                // Step 4: Change depot d1 to dd1 in Depot and Stock (cascading update handled by foreign key constraint)
                stmt.executeUpdate("UPDATE depot SET depid = 'dd1' WHERE depid = 'd1';");

                // Step 1: Delete product p1 (now pp1) from Product and Stock (cascading delete handled by foreign key constraint)
                stmt.executeUpdate("DELETE FROM product WHERE prodid = 'pp1';");

                // Step 2: Delete depot d1 (now dd1) from Depot and Stock (cascading delete handled by foreign key constraint)
                stmt.executeUpdate("DELETE FROM depot WHERE depid = 'dd1';");

                // Commit the transaction
                conn.commit();
                System.out.println("All Transaction committed successfully.");
            } catch (SQLException e) {
                // Rollback in case of any errors
                if (conn != null) {
                    try {
                        conn.rollback();
                        System.out.println("Transaction rolled back due to an error.");
                    } catch (SQLException rollbackEx) {
                        rollbackEx.printStackTrace();
                    }
                }
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}