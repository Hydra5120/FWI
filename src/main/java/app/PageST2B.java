package app;

import java.util.*;

import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

public class PageST2B implements Handler {
    public static final String URL = "/page2B.html";

    @Override
    public void handle(Context ctx) throws SQLException {

        // Construct the HTML response using concatenation
        String html = "<!DOCTYPE html><html lang='en'><head>"
                + "<meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "<title>Select Class Categories</title>"
                + "<link rel='stylesheet' href='2b.css'>"
                + "<link rel='stylesheet' href='https://cdn.jsdelivr.net/gh/habibmhamadi/multi-select-tag@3.0.1/dist/css/multi-select-tag.css'>"
                + "</head><body>"
                + "<div class='navbar'>"
                + "<a href='/'>Homepage</a>"
                + "<a href='mission.html'>Our Mission</a>"
                + "<a href='page2A.html'>Sub Task 2.A</a>"
                + "<a href='page2B.html'>Sub Task 2.B</a>"
                + "<a href='page3A.html'>Sub Task 3.A</a>"
                + "<a href='page3B.html'>Sub Task 3.B</a>"
                 + "</div>"
        + "<div class='content-wrapper'>"
        + "<div class='main-content'>"
        + "<h1>Waste Change by Food Group</h1>"

        + "<form action='/page2B.html' method='post'>"
        + "<select name='category-dropdown' id='category-dropdown' multiple>"
        + JDBCConnection.getDropdownHtml() // Assuming this returns HTML for options
        + "</select>"
        + "<label for='start-year'>Start Year</label>"
        + "<select class='category-dropdown' name='start-year' id='start-year'>";

for (int year = 1966; year <= 2022; year++) {
    html += "<option value='" + year + "'>" + year + "</option>";
}

html += "</select> "

        + "<label for='end-year'>End Year</label>"
        + "<select name='end-year' id='end-year'>";

for (int year = 1966; year <= 2022; year++) {
    html += "<option value='" + year + "'>" + year + "</option>";
}

html += "</select>"
        + "<h2>Filters</h2>"
        + """
                <label class="checkbox-container">
                    Activity
                    <input type="checkbox" name="activity" id="activity" value="True">
                    <span class="checkmark"></span>
                </label>
                <label class="checkbox-container">
                    Food Supply Stage
                    <input type="checkbox" name="supply-stage" id="supply-stage" value="True">
                    <span class="checkmark"></span>
                </label>
                <label class="checkbox-container">
                    Cause of Loss
                    <input type="checkbox" name="cause" id="cause" value="True">
                    <span class="checkmark"></span>
                </label>
                """
        + """
                <h2>Sort by</h2>
                <label class="radio-container">
                    Ascending
                    <input type="radio" name="rad" id="ascending" value="ascending">
                    <span class="checkmark"></span>
                </label>
                <label class="radio-container">
                    Descending
                    <input type="radio" name="rad" id="descending" value="descending">
                    <span class="checkmark"></span>
                </label>
                """
        + "<button type='submit'>Submit</button>"
        + "</form>";

                List<String> selectedCategories = ctx.formParams("category-dropdown");
                String startYear = ctx.formParam("start-year");
                String endYear = ctx.formParam("end-year");
                String activityMark = ctx.formParam("activity");
                String supplyStageMark = ctx.formParam("supply-stage");
                String causeMark = ctx.formParam("cause");
                String sortOrder = ctx.formParam("rad");
                List<String> names = new ArrayList<>();
                List<String> emails = new ArrayList<>();
                JDBCConnection.fetchStudentData(names, emails);
                
                try {
                    html += "<div class='table-container'>";
                
                    String tableHtml = generateHtmlTable(selectedCategories, startYear, endYear, activityMark, supplyStageMark, causeMark, sortOrder);
                    html += tableHtml + "</div>";
                
                } catch (SQLException e) {
                    html += "<p>Error retrieving data</p>";
                    e.printStackTrace();
                }
                
                html += "</div>" // Close main-content
                        + "</div>" // Close content-wrapper
                        + "<script src='https://cdn.jsdelivr.net/gh/habibmhamadi/multi-select-tag@3.0.1/dist/js/multi-select-tag.js'></script>"
                        + "<script>new MultiSelectTag('category-dropdown')</script>"
                        + "<footer>"
                        + "<p>Created by:</p>";
                
                for (int i = 0; i < names.size(); i++) {
                    html += "<p>" + names.get(i) + " " + emails.get(i) + "</p>";
                }
                
                html += "</footer>"
                        + "</body></html>";

        ctx.html(html);

    }

    public String generateHtmlTable(List<String> selectedCategories, String start_year, String end_year,
            String activityMark, String supply_stageMark, String causeMark, String sort)
            throws SQLException {
        List<FoodGroupLossData> lossData = new ArrayList<>();
        

        for (String category : selectedCategories) {
            String classCode = category.substring(category.indexOf('(') + 1, category.indexOf(')'));
            double lossStart = JDBCConnection.fetchAverageLoss(start_year, classCode);
            double lossEnd = JDBCConnection.fetchAverageLoss(end_year, classCode);
            double lossChange = lossEnd - lossStart;
            List<String> modalValues = JDBCConnection.fetchModalValues(classCode, start_year, end_year);
            String activity = modalValues.get(0);
            String supply_stage = modalValues.get(1);
            String cause = modalValues.get(2);

            lossData.add(new FoodGroupLossData(category.split(" ")[0], lossChange, activity, supply_stage, cause));
        }

        // Sorting by loss change
        Comparator<FoodGroupLossData> comparator = Comparator.comparingDouble(FoodGroupLossData::getLossChange);
        if ("desc".equalsIgnoreCase(sort) || "descending".equalsIgnoreCase(sort)) {
            comparator = comparator.reversed(); // Reverse for endlssort order
        }
        Collections.sort(lossData,comparator);

        // Building HTML Table
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<table><tr><th>Food Group</th><th>Loss Change</th>");
        if (activityMark != null)
            htmlBuilder.append("<th>Activity</th>");
        if (supply_stageMark != null)
            htmlBuilder.append("<th>Supply Stage</th>");
        if (causeMark != null)
            htmlBuilder.append("<th>Cause</th>");
        htmlBuilder.append("</tr>");

        for (FoodGroupLossData data : lossData) {
            if (data.getLossChange()==0){htmlBuilder.append("<tr><td>").append(data.getFoodGroup()).append("</td><td>")
            .append("No data available").append("</td>");}
            else {
            htmlBuilder.append("<tr><td>").append(data.getFoodGroup()).append("</td><td>")
                    .append(String.format("%.3f%%", data.getLossChange())).append("</td>");}
            if (activityMark != null)
                 htmlBuilder.append("<td>").append(data.getActivity()).append("</td>");
            if (supply_stageMark != null)
                htmlBuilder.append("<td>").append(data.getsupplyStage()).append("</td>");
            if (causeMark != null)
                 htmlBuilder.append("<td>").append(data.getcause()).append("</td>");
            htmlBuilder.append("</tr>");
        }
        htmlBuilder.append("</table>");
        return htmlBuilder.toString();
    }

    // Inner class to hold loss data
    class FoodGroupLossData {
        private String foodGroup;
        private double lossChange;
        private String activity;
        private String supplyStage;
        private String cause;

        public FoodGroupLossData(String foodGroup, double lossChange, String activity, String supplyStage,
                String cause) {
            this.foodGroup = foodGroup;
            this.lossChange = lossChange;
            this.activity = activity;
            this.supplyStage = supplyStage;
            this.cause = cause;
        }

        public String getFoodGroup() {
            return foodGroup;
        }

        public double getLossChange() {
            return lossChange;
        }

        public String getActivity() {
            if (activity==null){return "No data available";}
            return activity;
        }

        public String getsupplyStage() {
            if (supplyStage==null){return "No data available";}
            return supplyStage;
        }

        public String getcause() {
            if (cause==null){return "No data available";}
            return cause;
        }
        
    }

}
