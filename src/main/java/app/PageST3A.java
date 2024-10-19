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

public class PageST3A implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page3A.html";

    @Override
    public void handle(Context context) throws Exception {
        List<String> names = new ArrayList<>();
        List<String> emails = new ArrayList<>();
        JDBCConnection.fetchStudentData(names, emails);
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Head information
        html = html + "<head>" +
                "<title>Commodities Shared By Two Countries</title>";

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

        ArrayList<String> countryList = getAllCountries();
        ArrayList<Integer> yearsList = getAllYears();

        // Add header content block

        // Add Div for page Content
        html = html + "<div class='content-wrapper'>";
        html = html + "<div class='main-content'>";
        html = html + "<h1>Commodities Shared By Two Countries</h1>";

        html = html + """
                <form action='/page3A.html' method='post'>
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
                <label for="country">First country:</label>
                <select id="country" name="country" class = 'category-dropdown'>
                """;

        for (String c : countryList) {
            html = html + "<option>" + c + "</option>";
        }

        html = html + """
                </select>

                <label for="country_other">Second country:</label>
                <select id="country_other" name="country_other" class = 'category-dropdown'>
                """;
        for (String c : countryList) {
            html = html + "<option>" + c + "</option>";
        }
        html = html
                + """
                        </select>
                        <br>

                        <h2>Filters</h2>

                        <label class="checkbox-container">Activity
                                <input type="checkbox" name="activity_filter"  id="activity" value="True"><span class="checkmark"></span></label>

                                <label class="checkbox-container">Stage
                                <input type="checkbox" name="stage_filter"  id="stage" value="True"><span class="checkmark"></span></label>

                                <label class="checkbox-container">Cause
                                <input type="checkbox" name="cause_filter"  id="cause" value="True"><span class="checkmark"></span></label>

                        <button type="submit">Submit</button>
                        </form>
                        """;

        String selectedStartYear = context.formParam("start_year");
        String selectedEndYear = context.formParam("end_year");
        String selectedCountry = context.formParam("country");
        String selectedCountryTwo = context.formParam("country_other");
        String selectedActivity = context.formParam("activity_filter");
        String selectedStage = context.formParam("stage_filter");
        String selectedCause = context.formParam("cause_filter");

        html = html + outputTable(selectedStartYear, selectedEndYear, selectedCountry, selectedCountryTwo,
                selectedActivity, selectedCause, selectedStage);

        // Close Content div
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

    public String outputTable(String selectedStartYear, String selectedEndYear, String selectedCountry,
            String selectedCountryTwo, String selectedActivity, String selectedCause, String selectedStage) {

        String html = "";
        int u = 0;
        int b = 0;

        String[] commodities = new String[2829];
        ArrayList<String> commodityList = getCommodityData(selectedCountry, selectedStartYear, selectedEndYear);
        for (u = 0; u < commodityList.size() - 1; u++) {
            commodities[u] = commodityList.get(u);
        }

        String[] commoditiesOther = new String[2829];
        ArrayList<String> commodityListOther = getCommodityData(selectedCountryTwo, selectedStartYear, selectedEndYear);
        for (b = 0; b < commodityListOther.size() - 1; b++) {
            commoditiesOther[b] = commodityListOther.get(b);
        }

        ArrayList<String> yearsList = getYearData(selectedCountry, selectedStartYear, selectedEndYear);
        ArrayList<String> yearsListOther = getYearData(selectedCountryTwo, selectedStartYear, selectedEndYear);

        ArrayList<Double> percentageList = getLossPercentage(selectedCountry, selectedStartYear, selectedEndYear);
        ArrayList<Double> percentageListOther = getLossPercentage(selectedCountryTwo, selectedStartYear,
                selectedEndYear);

        ArrayList<String> activityList = getActivityData(selectedCountry, selectedStartYear, selectedEndYear);
        ArrayList<String> activityListOther = getActivityData(selectedCountryTwo, selectedStartYear, selectedEndYear);

        ArrayList<String> stageList = getStageData(selectedCountry, selectedStartYear, selectedEndYear);
        ArrayList<String> stageListOther = getStageData(selectedCountryTwo, selectedStartYear, selectedEndYear);

        ArrayList<String> causeList = getCauseData(selectedCountry, selectedStartYear, selectedEndYear);
        ArrayList<String> causeListOther = getCauseData(selectedCountryTwo, selectedStartYear, selectedEndYear);

        int length = 0;

        int i = 0;
        int h = 0;

        html = html + "<table>";
        html = html + "<tr>";
        html = html
                + "<th>Country 1</th><th>Year</th><th>Percentage loss in Country 1</th><th>Activity</th><th>Stage</th><th>Cause</th><th>Common Commodity</th><th>Country 2</th><th>Year</th><th>Percentage loss in Country 2</th><th>Activity</th><th>Stage</th><th>Cause</th>";
        html = html + "</tr>";

        length = 2828;

        try {
            while (i != length) {
                if (commodities[i] != null && commoditiesOther[i] != null) {
                    if (commodities[i].equals(commoditiesOther[i])) {
                        html = html + "<tr>";
                        html = html + "<td>" + selectedCountry + "</td>";
                        html = html + "<td>" + yearsList.get(i) + "</td>";
                        html = html + "<td>" + percentageList.get(i) + "</td>";
                        if (selectedActivity != null) {
                            html = html + "<td>" + activityList.get(i) + "</td>";
                        } else {
                            html = html + "<td>Activity filter was not selected</td>";
                        }
                        if (selectedStage != null) {
                            html = html + "<td>" + stageList.get(i) + "</td>";
                        } else {
                            html = html + "<td>Stage filter was not selected</td>";
                        }
                        if (selectedCause != null) {
                            html = html + "<td>" + causeList.get(i) + "</td>";
                        } else {
                            html = html + "<td>Cause filter was not selected</td>";
                        }
                        html = html + "<td>" + commodities[i] + "</td>";
                        html = html + "<td>" + selectedCountryTwo + "</td>";
                        html = html + "<td>" + yearsListOther.get(i) + "</td>";
                        html = html + "<td>" + percentageListOther.get(i) + "</td>";
                        if (selectedActivity != null) {
                            html = html + "<td>" + activityListOther.get(i) + "</td>";
                        } else {
                            html = html + "<td>Activity filter was not selected</td>";
                        }
                        if (selectedStage != null) {
                            html = html + "<td>" + stageListOther.get(i) + "</td>";
                        } else {
                            html = html + "<td>Stage filter was not selected</td>";
                        }
                        if (selectedCause != null) {
                            html = html + "<td>" + causeListOther.get(i) + "</td>";
                        } else {
                            html = html + "<td>Cause filter was not selected</td>";
                        }
                        html = html + "</tr>";
                    } else {
                        if (h == 0) {
                            html = html + "<tr>";
                            html = html + "<td>" + selectedCountry + "</td>";
                            html = html + "<td></td>";
                            html = html + "<td></td>";
                            html = html + "<td></td>";
                            html = html + "<td></td>";
                            html = html + "<td></td>";
                            html = html + "<td></td>";
                            html = html + "<td>" + selectedCountryTwo + "</td>";
                            html = html + "<td></td>";
                            html = html + "<td></td>";
                            html = html + "<td></td>";
                            html = html + "<td></td>";
                            html = html + "<td></td>";
                            html = html + "</tr>";
                            h++;
                        }

                    }
                }
                i++;
            }
        } catch (java.lang.IndexOutOfBoundsException e) {
            html = html + "<p>Potential Java Error; Try swapping the selected countries to resolve.</p>";
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
            String selectedEndYear) {
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
            query = "SELECT commodity FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                    + selectedStartYear + "'AND year <= '" + selectedEndYear + "'ORDER BY commodity ASC";

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
        return commodities;
    }

    public ArrayList<Double> getLossPercentage(String selectedCountry, String selectedStartYear,
            String selectedEndYear) {
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
            query = "SELECT lossPercentage FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                    + selectedStartYear + "'AND year <= '" + selectedEndYear + "'ORDER BY commodity ASC";

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
            String selectedEndYear) {
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
            query = "SELECT year FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                    + selectedStartYear + "'AND year <= '" + selectedEndYear + "'ORDER BY commodity ASC";

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

    public ArrayList<String> getActivityData(String selectedCountry, String selectedStartYear, String selectedEndYear) {
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
            query = "SELECT activity FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                    + selectedStartYear + "'AND year <= '" + selectedEndYear + "'ORDER BY commodity ASC";
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

    public ArrayList<String> getStageData(String selectedCountry, String selectedStartYear, String selectedEndYear) {
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
            query = "SELECT foodSupplyStage FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                    + selectedStartYear + "'AND year <= '" + selectedEndYear + "'ORDER BY commodity ASC";

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

    public ArrayList<String> getCauseData(String selectedCountry, String selectedStartYear, String selectedEndYear) {
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
            query = "SELECT causeOfLoss FROM Losses WHERE countryName = '" + selectedCountry + "' AND year >= '"
                    + selectedStartYear + "'AND year <= '" + selectedEndYear + "'ORDER BY commodity ASC";

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

}
