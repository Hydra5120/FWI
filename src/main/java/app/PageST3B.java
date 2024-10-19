package app;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageST3B implements Handler {

    public static final String URL = "/page3B.html";

    @Override
    public void handle(Context ctx) throws Exception {
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

                + "<form action='/page3B.html' method='post'>"
                + "<label for='number-input'>Enter the number of groups to compare   </label>"
                + "<input type='number' id='number-input' name='num' class='number-input' required>"
                + "<select id='commodities' name='commodities' class='category-dropdown'>"
                + JDBCConnection.getDropdownHtmlCommodity()
                + "</select>"

                + """
                <h2>Sort by</h2>
                <label class="radio-container">
                    Average
                    <input type="radio" name="rad" id="rad-average" value="avg">
                    <span class="checkmark"></span>
                </label>
                <label class="radio-container">
                    Highest
                    <input type="radio" name="rad" id="rad-highest" value="max">
                    <span class="checkmark"></span>
                </label>
                <label class="radio-container">
                    Lowest
                    <input type="radio" name="rad" id="rad-lowest" value="min">
                    <span class="checkmark"></span>
                </label>
                """

                + "<button type='submit'>Submit</button>"
                + "</form>";
                
                
                String sortOrder = ctx.formParam("rad");
                String selectedFood = ctx.formParam("commodities");
                
                int numberInput=0;
                String numberInputString = (ctx.formParam("num"));
                List<String> r = new ArrayList<>();
                List<String> results = new ArrayList<>();
                String groupCode = extractCode(selectedFood);
                if (sortOrder!=null){
                r = JDBCConnection.fetchOriginal(groupCode, sortOrder);
                results = JDBCConnection.fetchSimilars(sortOrder, Float.valueOf(r.get(1)), groupCode, Integer.valueOf(numberInputString));
                }
                

                
            
                 // existing HTML setup up to <button type='submit'>Submit</button>
            
                // Fetch and append the table HTML
               html+=generateHTML(results,r);
               List<String> names = new ArrayList<>();
    List<String> emails = new ArrayList<>();
    JDBCConnection.fetchStudentData(names, emails);
                
            
                // Remaining HTML setup including scripts, footer, etc.
                html += "</div></div><script src='https://cdn.jsdelivr.net/gh/habibmhamadi/multi-select-tag@3.0.1/dist/js/multi-select-tag.js'></script>"
                        + "<script>new MultiSelectTag('category-dropdown')</script>"
                        + "<footer>"
                +"<p>Created by:</p>";
                
                                 
                        
                    html+= "<p>"+names.get(0)+ " " + emails.get(0)+"</p>";
                    html+= "<p>"+names.get(1)+ " "+emails.get(1)+"</p>";
               
            html+="</footer></body></html>"
                        + "</body></html>";
            
                ctx.html(html);

        
        
    }
    public static String generateHTML(List<String> results, List<String> r) {
        StringBuilder html = new StringBuilder();
        html.append("<table>")
            .append("<tr><th>Selected Food Group</th><th>Food Group Name</th><th>Loss Percentage</th><th>Similarity Score</th></tr>");
    
        if (!results.isEmpty()) {
            // Split the first result which is the user-selected group
            String[] firstResult = results.get(0).split("\\|");
            html.append("<tr>")
                .append("<td rowspan='").append(results.size()).append("'> Belongs to group: "+r.get(0)+", Loss Percentage: "+String.format("%.2f%%",Float.valueOf(r.get(1)))+"</td>")
                .append("<td>").append(firstResult[0]).append("</td>")
                .append("<td>").append(firstResult[1]).append("</td>")
                .append("<td>").append(firstResult[2]).append("</td>")
                .append("</tr>");
                        
            for (int i = 1; i < results.size(); i++) {
                String[] parts = results.get(i).split("\\|");
                html.append("<tr>")
                    .append("<td>").append(parts[0]).append("</td>")
                    .append("<td>").append(parts[1]).append("</td>")
                    .append("<td>").append(parts[2]).append("</td>")
                    .append("</tr>");
            }
        } else {
            html.append("<tr><td>No data</td><td>No Data</td><td>No Data</td><td>No Data</td></tr>"); // Handle empty list case
        }
        html.append("</table>");
        return html.toString();
    }
    
    public static String extractCode(String input) {
        String extractedNumber = null;
        try {
            if (input == null) {
                return "e";
            }

        // Regular expression to match numbers inside brackets
        String regex = "\\((\\d+)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            // Group 1 contains the number inside the brackets
            String fullNumber = matcher.group(1);
            // Extract the first three characters
            extractedNumber = fullNumber.length() >= 3 ? fullNumber.substring(0, 3) : fullNumber;
        }
    } catch (NullPointerException e) {
        System.err.println(e.getMessage());
    }

        return extractedNumber;
    }

    
}
    


