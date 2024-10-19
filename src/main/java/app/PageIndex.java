package app;

import java.util.ArrayList;
import java.util.List;

import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Example Index HTML class using Javalin
 * <p>
 * Generate a static HTML page using Javalin
 * by writing the raw HTML into a Java String object
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 * @author Halil Ali, 2024. email: halil.ali@rmit.edu.au
 */

public class PageIndex implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/";

    @Override
    public void handle(Context context) throws Exception {
        List<String> names = new ArrayList<>();
        List<String> emails = new ArrayList<>();
        JDBCConnection.fetchStudentData(names, emails);
        // Create a simple HTML webpage in a String
        String html = "<html>";

        html = html + "<head>" +
                "<title>Food Wastage Information - Home</title>" +
                "<link rel='stylesheet' type='text/css' href='2b.css' />" +
                "</head>";

        html = html + "<body>";

        html = html + """
                    <div class='navbar'>
                        <a href='/'>Homepage</a>
                        <a href='mission.html'>Our Mission</a>
                        <a href='page2A.html'>Sub Task 2.A</a>
                        <a href='page2B.html'>Sub Task 2.B</a>
                        <a href='page3A.html'>Sub Task 3.A</a>
                        <a href='page3B.html'>Sub Task 3.B</a>
                    </div>
                """;

        html = html
                + """
                            <div class='header'>
                                <h1>

                                </h1>
                            </div>
                        """;

        html = html + "<div class='content-wrapper'>";
        html = html + "<div class='main-content'>";
        html = html + "<img src='FWI_Logo.png' class='top-image' alt='FWI logo' height='300'>";
        html = html
                + "<h2>Our food loss data, gathered from reputable sources spans 56 years, from 1966 to 2022, covering a wide range of countries and commodities.</h2>";

        ArrayList<Double> loss = getLossPercentage();
        ArrayList<String> commodity = getCommodityLoss();

        html = html
                + "<h2>The kind of data that you would expect to see on our page includes food loss percentages and their corresponding commodities. For example: </h2>";

        html = html + "<div><h3>The highest loss percentage in our data is: " + loss.get(0)
                + "%. The commodities that are responsible for this loss percentage of " + loss.get(0)
                + "% are: </h3><ul>";

        html = html + "<li>" + commodity.get(0) + "</li>";
        html = html + "<li>" + commodity.get(1) + "</li>";
        html = html + "<li>" + commodity.get(2) + "</li>";

        html = html + "</ul>";

        html = html + "<h2><u>How to reduce food waste in the home</u></h2>";
        html = html + "<p>Through the WWF, we can help you reduce food wastage in your own home.</p><ol>";
        html = html
                + "<li>Check the fridge before going shopping, and only buy what is needed with the help of a shopping list.</li>";
        html = html + "<li>Planning meals saves money, time and the wastage of food.</li>";
        html = html
                + "<li>Learning the difference between use by, best before and display until labels aids in saving money and reducing the wastage of food.</li>";
        html = html + "<li>Be creative with your meals and use leftovers.</li>";
        html = html + "</ol>";

        html = html + "</div></div></div>";
        html = html
                + "<script src='https://cdn.jsdelivr.net/gh/habibmhamadi/multi-select-tag@3.0.1/dist/js/multi-select-tag.js'></script>";
        html = html + "<script>new MultiSelectTag('category-dropdown')</script>";
        html = html + "<footer><p>Created by: </p>";
        html += "<p>" + names.get(0) + " " + emails.get(0) + "</p>";
        html += "<p>" + names.get(1) + " " + emails.get(1) + "</p></footer>";
        html = html + "</body></html>";

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        context.html(html);
    }

    public ArrayList<Double> getLossPercentage() {
        // Create the ArrayList of String objects to return
        ArrayList<Double> lossPercentage = new ArrayList<Double>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT lossPercentage FROM Losses ORDER BY lossPercentage DESC";

            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                Double loss = results.getDouble("lossPercentage");

                // Add the country object to the array
                lossPercentage.add(loss);
            }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just print the error
            System.err.println(e.getMessage());
            // e.printStackTrace();
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
                // e.printStackTrace();
            }
        }

        // Finally we return all of the countries
        return lossPercentage;
    }

    public ArrayList<String> getCommodityLoss() {
        // Create the ArrayList of String objects to return
        ArrayList<String> commodity = new ArrayList<String>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT lossPercentage, commodity FROM Losses ORDER BY lossPercentage DESC";

            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                String commName = results.getString("commodity");

                // Add the country object to the array
                commodity.add(commName);
            }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just print the error
            System.err.println(e.getMessage());
            // e.printStackTrace();
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
                // e.printStackTrace();
            }
        }

        // Finally we return all of the countries
        return commodity;
    }
}
