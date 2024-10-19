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

public class PageST2A implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page2A.html";

    @Override
    public void handle(Context context) throws Exception {
        List<String> names = new ArrayList<>();
        List<String> emails = new ArrayList<>();
        JDBCConnection.fetchStudentData(names, emails);
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Head information
        html = html + "<head>" +
                "<title>Food Wastage By Country</title>";

        // Add some CSS (external file)
        html = html + "<link rel='stylesheet' type='text/css' href='2b.css' />";
        html = html
                + "<link rel='stylesheet' href='https://cdn.jsdelivr.net/gh/habibmhamadi/multi-select-tag@3.0.1/dist/css/multi-select-tag.css'>";
        html = html + "</head>";

        // Add the body
        html = html + "<body>";

        // Add the topnav
        // This uses a Java v15+ Text Block
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

        // Add Div for page Content
        html = html + "<div class='content-wrapper'>";
        html = html + "<div class='main-content'>";
        html = html + "<h1>Food Wastage By Country</h1>";

        // Add HTML for the page content
        ArrayList<String> countryList = getAllCountries();
        ArrayList<Integer> yearsList = getAllYears();
        // Add HTML for the forms
        html = html + """
                <form action='/page2A.html' method='post'>
                <label for="start_year">Start Year:</label>
                <select id="start_year" name="start_year" class = 'category-dropdown'>
                """;

        for (int y : yearsList) {
            html = html + "<option>" + y + "</option>";
        }

        html = html + """
                </select>
                <label for="end_year">End Year:</label>
                        <select id="end_year" name="end_year" class = 'category-dropdown'>
                        """;

        for (int y : yearsList) {
            html = html + "<option>" + y + "</option>";
        }

        html = html + """

                </select>
                <br>
                <label for="country">Country:</label>
                <select id="country" name="country" class = 'category-dropdown'>
                """;

        for (String c : countryList) {
            html = html + "<option>" + c + "</option>";
        }

        html = html
                + """
                        </select>

                        <h2>Filters</h2>

                        <label class="checkbox-container">Commodity
                        <input type="checkbox" name="commodity_filter" id="commodity" value="True"><span class="checkmark"></span></label>

                        <label class="checkbox-container">Activity
                        <input type="checkbox" name="activity_filter"  id="activity" value="True"><span class="checkmark"></span></label>

                        <label class="checkbox-container">Food Supply Stage
                        <input type="checkbox" name="stage_filter" id="stage" value="True"><span class="checkmark"></span></label>

                        <label class="checkbox-container">Cause of loss
                        <input type="checkbox" name="cause_filter" id="cause" value="True"><span class="checkmark"></span></label>

                        <h2>Sort by</h2>
                        <label  class="radio-container">Ascending Loss/Waste Value
                        <input type="radio" name="sorting_filter" id="ascending" value="ascending"><span class="checkmark"></span></label>

                        <label class="radio-container">Descending Loss/Waste Value
                        <input type="radio" name="sorting_filter" id="descending" value="descending"><span class="checkmark"></span></label>

                        <button type="submit">Submit</button>
                        </form>
                        """;

        String selectedStartYear = context.formParam("start_year");
        String selectedEndYear = context.formParam("end_year");
        String selectedCountry = context.formParam("country");
        String selectedCommodity = context.formParam("commodity_filter");
        String selectedActivity = context.formParam("activity_filter");
        String selectedStage = context.formParam("stage_filter");
        String selectedCause = context.formParam("cause_filter");
        String selectedAscending = context.formParam("sorting_filter");

        html = html + outputTable(selectedCountry, selectedStartYear, selectedEndYear, selectedCommodity,
                selectedActivity, selectedStage,
                selectedCause, selectedAscending);

        // Close Content div

        // Footer
        html = html + "</div>"
                + "</div>"
                + "<script src='https://cdn.jsdelivr.net/gh/habibmhamadi/multi-select-tag@3.0.1/dist/js/multi-select-tag.js'></script>"
                + "<script>new MultiSelectTag('category-dropdown')</script>"
                + "<footer>"
                + "<p>Created by: </p>"
                + "<p>" + names.get(0) + " " + emails.get(0) + "</p>"
                + "<p>" + names.get(1) + " " + emails.get(1) + "</p>"
                + "</footer>"
                + "</body></html>";

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        context.html(html);
    }

    public String outputTable(String selectedCountry, String selectedStartYear, String selectedEndYear,
            String selectedCommodity,
            String selectedActivity,
            String selectedStage, String selectedCause, String selectedAscending) {

        String html = "";
        ArrayList<String> yearsList = getYearData(selectedCountry, selectedStartYear, selectedEndYear,
                selectedAscending);
        ArrayList<String> commodityList = getCommodityData(selectedCountry, selectedStartYear, selectedEndYear,
                selectedAscending);
        ArrayList<String> activityList = getActivityData(selectedCountry, selectedStartYear, selectedEndYear,
                selectedAscending);
        ArrayList<String> stageList = getStageData(selectedCountry, selectedStartYear, selectedEndYear,
                selectedAscending);
        ArrayList<String> causeList = getCauseData(selectedCountry, selectedStartYear, selectedEndYear,
                selectedAscending);
        ArrayList<Double> percentageLoss = getLossPercentage(selectedCountry, selectedStartYear, selectedEndYear,
                selectedAscending);
        int length = commodityList.size();
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        int m = 0;
        int n = 0;

        html = html + "<table>";
        html = html + "<tr>";
        html = html
                + "<th>Country</th><th>Year</th><th>Commodity</th><th>Activity</th><th>Food Supply Stage</th><th>Cause of Loss</th><th>Percent of food lost</th>";
        html = html + "</tr>";

        while (i != length) {
            html = html + "<tr>";

            if (n == 0) {
                html = html + "<td>" + selectedCountry + "</td>";
                n++;
            } else {
                html = html + "<td></td>";
            }

            html = html + "<td>" + yearsList.get(i) + "</td>";

            if (selectedCommodity != null) {
                if (commodityList.get(i).length() == 0) {
                    html = html + "<td>No Commodity Found</td>";
                } else {
                    html = html + "<td>" + commodityList.get(i) + "</td>";
                }
            } else if (j == 0) {
                html = html + "<td>Commodity filter was not selected</td>";
                j++;
            } else {
                html = html + "<td></td>";
            }

            if (selectedActivity != null) {
                if (activityList.get(i).length() == 0) {
                    html = html + "<td>No Activity Found</td>";
                } else {
                    html = html + "<td>" + activityList.get(i) + "</td>";
                }
            } else if (k == 0) {
                html = html + "<td>Activity filter was not selected</td>";
                k++;
            } else {
                html = html + "<td></td>";
            }

            if (selectedStage != null) {
                if (stageList.get(i).length() == 0) {
                    html = html + "<td>No Stage Found</td>";
                } else {
                    html = html + "<td>" + stageList.get(i) + "</td>";
                }
            } else if (l == 0) {
                html = html + "<td>Stage filter was not selected</td>";
                l++;
            } else {
                html = html + "<td></td>";
            }

            if (selectedCause != null) {
                if (causeList.get(i).length() == 0) {
                    html = html + "<td>No Cause Found</td>";
                } else {
                    html = html + "<td>" + causeList.get(i) + "</td>";
                }
            } else if (m == 0) {
                html = html + "<td>Cause filter was not selected</td>";
                m++;
            } else {
                html = html + "<td></td>";
            }

            html = html + "<td>" + percentageLoss.get(i) + "</td>";

            html = html + "</tr>";
            i++;
        }
        html = html + "</table>";

        return html;
    }

    /**
     * Get the names of the countries in the database.
     */
    public ArrayList<String> getAllCountries() {
        // Create the ArrayList of String objects to return
        ArrayList<String> countries = new ArrayList<String>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT * FROM country ORDER BY countryName ASC";

            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                String countryName = results.getString("countryName");

                // Add the country object to the array
                countries.add(countryName);
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
        return countries;
    }

    public ArrayList<Integer> getAllYears() {
        // Create the ArrayList of String objects to return
        ArrayList<Integer> years = new ArrayList<Integer>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT year FROM Date";

            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                Integer year = results.getInt("year");

                // Add the country object to the array
                years.add(year);
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
        return years;
    }

    public ArrayList<String> getCommodityData(String selectedCountry, String selectedStartYear,
            String selectedEndYear, String selectedAscending) {
        // Create the ArrayList of String objects to return
        ArrayList<String> commodities = new ArrayList<String>();
        String query = "";

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            if (selectedAscending == null) {
                query = "SELECT commodity FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                        + selectedStartYear + "' AND year <= '" + selectedEndYear + "'";
            } else if (selectedAscending.equalsIgnoreCase("descending")) {
                query = "SELECT commodity FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                        + selectedStartYear + "' AND year <= '" + selectedEndYear + "' ORDER BY lossPercentage DESC";
            } else if (selectedAscending.equalsIgnoreCase("ascending")) {
                query = "SELECT commodity FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                        + selectedStartYear + "' AND year <= '" + selectedEndYear + "' ORDER BY lossPercentage ASC";
            }

            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                String commodityName = results.getString("commodity");
                // Add the country object to the array
                commodities.add(commodityName);
            }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just print the erroractivity
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
        return commodities;
    }

    public ArrayList<String> getActivityData(String selectedCountry, String selectedStartYear, String selectedEndYear,
            String selectedAscending) {
        // Create the ArrayList of String objects to return
        ArrayList<String> activities = new ArrayList<String>();
        String query = "";

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            if (selectedAscending == null) {
                query = "SELECT activity FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                        + selectedStartYear + "' AND year <= '" + selectedEndYear + "'";
            } else if (selectedAscending.equalsIgnoreCase("descending")) {
                query = "SELECT activity FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                        + selectedStartYear + "' AND year <= '" + selectedEndYear + "' ORDER BY lossPercentage DESC";
            } else if (selectedAscending.equalsIgnoreCase("ascending")) {
                query = "SELECT activity FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                        + selectedStartYear + "' AND year <= '" + selectedEndYear + "' ORDER BY lossPercentage ASC";
            }
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                String activityName = results.getString("activity");
                // Add the country object to the array
                activities.add(activityName);
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
        return activities;
    }

    public ArrayList<String> getStageData(String selectedCountry, String selectedStartYear, String selectedEndYear,
            String selectedAscending) {
        // Create the ArrayList of String objects to return
        ArrayList<String> stages = new ArrayList<String>();
        String query = "";

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            if (selectedAscending == null) {
                query = "SELECT foodSupplyStage FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                        + selectedStartYear + "' AND year <= '" + selectedEndYear + "'";
            } else if (selectedAscending.equalsIgnoreCase("descending")) {
                query = "SELECT foodSupplyStage FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                        + selectedStartYear + "' AND year <= '" + selectedEndYear + "' ORDER BY lossPercentage DESC";
            } else if (selectedAscending.equalsIgnoreCase("ascending")) {
                query = "SELECT foodSupplyStage FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                        + selectedStartYear + "' AND year <= '" + selectedEndYear + "' ORDER BY lossPercentage ASC";
            }

            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                String stageName = results.getString("foodSupplyStage");
                // Add the country object to the array
                stages.add(stageName);
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
        return stages;
    }

    public ArrayList<String> getCauseData(String selectedCountry, String selectedStartYear, String selectedEndYear,
            String selectedAscending) {
        // Create the ArrayList of String objects to return
        ArrayList<String> causes = new ArrayList<String>();
        String query = "";

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            if (selectedAscending == null) {
                query = "SELECT causeOfLoss FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                        + selectedStartYear + "' AND year <= '" + selectedEndYear + "'";
            } else if (selectedAscending.equalsIgnoreCase("descending")) {
                query = "SELECT causeOfLoss FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                        + selectedStartYear + "' AND year <= '" + selectedEndYear + "' ORDER BY lossPercentage DESC";
            } else if (selectedAscending.equalsIgnoreCase("ascending")) {
                query = "SELECT causeOfLoss FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                        + selectedStartYear + "' AND year <= '" + selectedEndYear + "' ORDER BY lossPercentage ASC";
            }

            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                String causeName = results.getString("causeOfLoss");
                // Add the country object to the array
                causes.add(causeName);
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
        return causes;
    }

    public ArrayList<Double> getLossPercentage(String selectedCountry, String selectedStartYear, String selectedEndYear,
            String selectedAscending) {
        // Create the ArrayList of String objects to return
        ArrayList<Double> lossPercentage = new ArrayList<Double>();
        String query = "";

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            if (selectedAscending == null) {
                query = "SELECT lossPercentage FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                        + selectedStartYear + "' AND year <= '" + selectedEndYear + "'";
            } else if (selectedAscending.equalsIgnoreCase("descending")) {
                query = "SELECT lossPercentage FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                        + selectedStartYear + "' AND year <= '" + selectedEndYear + "' ORDER BY lossPercentage DESC";
            } else if (selectedAscending.equalsIgnoreCase("ascending")) {
                query = "SELECT lossPercentage FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                        + selectedStartYear + "' AND year <= '" + selectedEndYear + "' ORDER BY lossPercentage ASC";
            }

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

    public ArrayList<String> getYearData(String selectedCountry, String selectedStartYear,
            String selectedEndYear, String selectedAscending) {
        // Create the ArrayList of String objects to return
        ArrayList<String> years = new ArrayList<String>();
        String query = "";

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            if (selectedAscending == null) {
                query = "SELECT year FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                        + selectedStartYear + "' AND year <= '" + selectedEndYear + "'";
            } else if (selectedAscending.equalsIgnoreCase("descending")) {
                query = "SELECT year FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                        + selectedStartYear + "' AND year <= '" + selectedEndYear + "' ORDER BY lossPercentage DESC";
            } else if (selectedAscending.equalsIgnoreCase("ascending")) {
                query = "SELECT year FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                        + selectedStartYear + "' AND year <= '" + selectedEndYear + "' ORDER BY lossPercentage ASC";
            }

            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                String year = results.getString("year");
                // Add the country object to the array
                years.add(year);
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
        return years;
    }

}
