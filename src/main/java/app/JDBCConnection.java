package app;

import java.util.*;

import javassist.compiler.ast.Stmnt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

/**
 * Class for Managing the JDBC Connection to a SQLLite Database.
 * Allows SQL queries to be used with the SQLLite Databse in Java.
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 * @author Halil Ali, 2024. email: halil.ali@rmit.edu.au
 */

public class JDBCConnection {

    // Name of database file (contained in database folder)
    public static final String DATABASE = "jdbc:sqlite:database/foodloss.db";

    /**
     * This creates a JDBC Object so we can keep talking to the database
     */
    public JDBCConnection() {
        System.out.println("Created JDBC Connection Object");
    }

    /**
     * Get all of the Countries in the database.
     * @return
     *    Returns an ArrayList of Country objects
     */
    public ArrayList<Country> getAllCountries() {
        // Create the ArrayList of Country objects to return
        ArrayList<Country> countries = new ArrayList<Country>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT * FROM Country";
            
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Lookup the columns we need
                String m49Code     = results.getString("m49code");
                String name  = results.getString("countryName");

                // Create a Country Object
                Country country = new Country(m49Code, name);

                // Add the Country object to the array
                countries.add(country);
            }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just pring the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the countries
        return countries;
    }

    public static String getDropdownHtml() {
        String sql = "SELECT className , classNo FROM Class WHERE LENGTH(classNo) = 3";
        StringBuilder html = new StringBuilder();

        try (Connection conn = DriverManager.getConnection(DATABASE);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String className = rs.getString("className");
                    String classNo = rs.getString("classNo");
                    
                    html.append("<option value='").append(className).append(" ("+classNo+")").append("'>")
                        .append(className).append(" (").append(classNo).append(")</option>");
                }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return "<option>Error loading classes</option>";  // Return error in dropdown if there's a DB error
        }

        return html.toString();
    }

    public static String getDropdownHtmlCommodity() {
        String sql = "SELECT className, classNo\r\n" + //
                        "FROM Class\r\n" + //
                        "WHERE LENGTH(classNo) > 3\r\n" + //
                        "AND (classNo NOT LIKE 'Division %' AND classNo NOT LIKE 'Section %')";
        StringBuilder html = new StringBuilder();

        try (Connection conn = DriverManager.getConnection(DATABASE);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String className = rs.getString("className");
                    String classNo = rs.getString("classNo");
                    
                    html.append("<option value='").append(className).append(" ("+classNo+")").append("'>")
                        .append(className).append(" (").append(classNo).append(")</option>");
                }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return "<option>Error loading classes</option>";  // Return error in dropdown if there's a DB error
        }

        return html.toString();
    }


    public static double fetchAverageLoss(String year, String classCodePrefix) throws SQLException {
        String query = "SELECT AVG(lossPercentage) FROM Losses WHERE cpcCode LIKE ? AND YEAR = ?";
        try (Connection conn = DriverManager.getConnection(DATABASE);
        PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, classCodePrefix + "%");
            stmt.setString(2, year);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0;
    }

    public static void fetchPersonaData(List<String> names, List<String> descriptions) throws SQLException {
        String query = "SELECT name, description FROM persona";
        try (Connection conn = DriverManager.getConnection(DATABASE);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                names.add(rs.getString("name"));
                descriptions.add(rs.getString("description"));
            }
        }
    }

    public static void fetchStudentData(List<String> names, List<String> emails) throws SQLException {
        String query = "SELECT name, email FROM students";
        try (Connection conn = DriverManager.getConnection(DATABASE);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                names.add(rs.getString("name"));
                emails.add(rs.getString("email"));
            }
        }
    }

    public static List<String> fetchModalValues(String code, String startYear, String endYear) throws SQLException {
        String query = "SELECT " +
            "(SELECT activity FROM Losses WHERE activity IS NOT NULL AND activity <> '' AND cpcCode LIKE ? AND year BETWEEN ? AND ? GROUP BY activity ORDER BY COUNT(activity) DESC LIMIT 1) AS ModalActivity, " +
            "(SELECT foodSupplyStage FROM Losses WHERE foodSupplyStage IS NOT NULL AND foodSupplyStage <> '' AND cpcCode LIKE ? AND year BETWEEN ? AND ? GROUP BY foodSupplyStage ORDER BY COUNT(foodSupplyStage) DESC LIMIT 1) AS ModalSupplyStage, " +
            "(SELECT causeOfLoss FROM Losses WHERE causeOfLoss IS NOT NULL AND causeOfLoss <> '' AND cpcCode LIKE ? AND year BETWEEN ? AND ? GROUP BY causeOfLoss ORDER BY COUNT(causeOfLoss) DESC LIMIT 1) AS ModalCauseOfLoss;";

        try (Connection conn = DriverManager.getConnection(DATABASE);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int i = 1; i <= 9; i += 3) {
                stmt.setString(i, code + "%");
                stmt.setString(i + 1, startYear);
                stmt.setString(i + 2, endYear);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Arrays.asList(rs.getString("ModalActivity"), rs.getString("ModalSupplyStage"), rs.getString("ModalCauseOfLoss"));
                }
            }
        }
        return null;  
    }

    public static List<String> fetchOriginal(String code, String order) throws SQLException {
        String h = order.toUpperCase();
        List<String> r = new ArrayList<>();
        String query = String.format("""
                SELECT c.className, %s(l.lossPercentage) AS lossPercentage
FROM Class c
JOIN Losses l ON SUBSTR(l.cpcCode, 1, 3) = c.classNo
WHERE c.classNo = '%s'
GROUP BY c.className;

                """, h,code);
        try (Connection conn = DriverManager.getConnection(DATABASE);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery();) {

            while (rs.next()) {
                r.add(rs.getString("className"));
                r.add(rs.getString("lossPercentage"));
            }
            
            return r;
        }
    }



    public static List<String> fetchSimilars(String order, float percentage, String group, int num) throws SQLException {
        String query;
        List<String> results = new ArrayList<>();
    
        if (order=="max"){query = """
                SELECT c.classNo, c.className,
       MAX(l.lossPercentage) AS max_loss_percentage,
       100 * EXP(-ABS(MAX(l.lossPercentage) - ?) / 10.0) AS similarity_score
FROM Class c
JOIN Losses l ON SUBSTR(l.cpcCode, 1, 3) = c.classNo
WHERE LENGTH(c.classNo) = 3 AND c.classNo != ? 
GROUP BY c.classNo, c.className
ORDER BY similarity_score DESC;
LIMIT ?;


                """;}
                 if (order=="min"){query="""
                        SELECT c.classNo, c.className,
       MIN(l.lossPercentage) AS min_loss_percentage,
       100 * EXP(-ABS(MIN(l.lossPercentage) - ?) / 10.0) AS similarity_score
FROM Class c
JOIN Losses l ON SUBSTR(l.cpcCode, 1, 3) = c.classNo
WHERE LENGTH(c.classNo) = 3 AND c.classNo != ? 
GROUP BY c.classNo, c.className
ORDER BY similarity_score DESC
LIMIT ?;

                        """;}
                        else {query = """
                                SELECT c.classNo, c.className,
       AVG(l.lossPercentage) AS average_loss_percentage,
       100 * EXP(-ABS(AVG(l.lossPercentage) - ?) / 10.0) AS similarity_score
FROM Class c
JOIN Losses l ON SUBSTR(l.cpcCode, 1, 3) = c.classNo
WHERE LENGTH(c.classNo) = 3 AND c.classNo != ? 
GROUP BY c.classNo, c.className
ORDER BY similarity_score DESC
LIMIT ?;

                                """;}

        try (Connection conn = DriverManager.getConnection(DATABASE);
             PreparedStatement stmt = conn.prepareStatement(query);
             ) {
                stmt.setString(1, String.valueOf(percentage));
                stmt.setString(2 , group);
                stmt.setString(3, String.valueOf(num));
                ResultSet rs = stmt.executeQuery();
                

                while (rs.next()) {
                    String classNo = rs.getString("classNo");
                    String className = rs.getString("className");
                    double averageLossPercentage1 = rs.getDouble("average_loss_percentage");
                    String averageLossPercentage = String.format("%.2f%%", averageLossPercentage1);
                    double similarityScore1 = rs.getDouble("similarity_score");
                    String similarityScore = String.format("%.2f%%", similarityScore1);
                    
                    results.add(className+"|"+averageLossPercentage+"|"+similarityScore);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            return results;
        }

       
        

    // public static String extractGroupCode(String input) {
    //     // Updated pattern to capture digits immediately following the opening parenthesis
    //     String pattern = "\\((\\d{3})";
        
    //     // Create a Pattern object
    //     java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        
    //     // Create matcher object
    //     java.util.regex.Matcher m = p.matcher(input);
        
    //     if (m.find()) {
    //         // Extract the first three digits right after the opening parenthesis
    //         return m.group(1); // Directly returns the captured first three digits
    //     }
        
    //     // Return null or an appropriate value if no match is found
    //     return null;
    // }

   

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // public static String fetchActivity(String code, String startYear, String endYear) throws SQLException {
    //     // Corrected SQL query with appropriate spaces and placeholders for parameters
    //     String query = "SELECT activity " +
    //                    "FROM Losses " +
    //                    "WHERE activity IS NOT NULL " +
    //                    "AND activity <> '' " +
    //                    "AND cpcCode LIKE ? " +  // Using placeholders for parameters
    //                    "AND year BETWEEN ? AND ? " +
    //                    "GROUP BY activity " +
    //                    "ORDER BY COUNT(activity) DESC " +
    //                    "LIMIT 1;";
    
    //     String modalValue = null;
    
    //     // Assuming DATABASE is a valid database connection string defined elsewhere
    //     try (Connection conn = DriverManager.getConnection(DATABASE);
    //          PreparedStatement stmt = conn.prepareStatement(query)) {
    
    //         // Set the parameters for the prepared statement
    //         stmt.setString(1, code + "%");
    //         stmt.setString(2, startYear);
    //         stmt.setString(3, endYear);
    
    //         try (ResultSet rs = stmt.executeQuery()) {
    //             if (rs.next()) {
    //                 modalValue = rs.getString(1);
    //             }
    //         }
    //     }
    
    //     return modalValue;
    // }


    
    // public static String fetchSupply(String code, String startYear, String endYear) throws SQLException {
    //     // Corrected SQL query with appropriate spaces and placeholders for parameters
    //     String query = "SELECT foodSupplyStage " +
    //                    "FROM Losses " +
    //                    "WHERE foodSupplyStage IS NOT NULL " +
    //                    "AND foodSupplyStage <> '' " +
    //                    "AND cpcCode LIKE ? " +  // Using placeholders for parameters
    //                    "AND year BETWEEN ? AND ? " +
    //                    "GROUP BY foodSupplyStage " +
    //                    "ORDER BY COUNT(foodSupplyStage) DESC " +
    //                    "LIMIT 1;";
    
    //     String modalValue = null;
    
    //     // Assuming DATABASE is a valid database connection string defined elsewhere
    //     try (Connection conn = DriverManager.getConnection(DATABASE);
    //          PreparedStatement stmt = conn.prepareStatement(query)) {
    
    //         // Set the parameters for the prepared statement
    //         stmt.setString(1, code + "%");
    //         stmt.setString(2, startYear);
    //         stmt.setString(3, endYear);
    
    //         try (ResultSet rs = stmt.executeQuery()) {
    //             if (rs.next()) {
    //                 modalValue = rs.getString(1);
    //             }
    //         }
    //     }
    
    //     return modalValue;
    // }


    // public static String fetchCause(String code, String startYear, String endYear) throws SQLException {
    //     // Corrected SQL query with appropriate spaces and placeholders for parameters
    //     String query = "SELECT causeOfLoss " +
    //                    "FROM Losses " +
    //                    "WHERE causeOfLoss IS NOT NULL " +
    //                    "AND causeOfLoss <> '' " +
    //                    "AND cpcCode LIKE ? " +  // Using placeholders for parameters
    //                    "AND year BETWEEN ? AND ? " +
    //                    "GROUP BY causeOfLoss " +
    //                    "ORDER BY COUNT(causeOfLoss) DESC " +
    //                    "LIMIT 1;";
    
    //     String modalValue = null;
    
    //     // Assuming DATABASE is a valid database connection string defined elsewhere
    //     try (Connection conn = DriverManager.getConnection(DATABASE);
    //          PreparedStatement stmt = conn.prepareStatement(query)) {
    
    //         // Set the parameters for the prepared statement
    //         stmt.setString(1, code + "%");
    //         stmt.setString(2, startYear);
    //         stmt.setString(3, endYear);
    
    //         try (ResultSet rs = stmt.executeQuery()) {
    //             if (rs.next()) {
    //                 modalValue = rs.getString(1);
    //             }
    //         }
    //     }
    
    //     return modalValue;
    // }

    




    

}
