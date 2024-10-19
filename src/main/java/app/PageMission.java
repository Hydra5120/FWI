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

public class PageMission implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/mission.html";
    

    @Override
    public void handle(Context context) throws Exception {
    List<String> names = new ArrayList<>();
    List<String> emails = new ArrayList<>();
    JDBCConnection.fetchStudentData(names, emails);

    List<String> persona = new ArrayList<>();
    List<String> descriptions = new ArrayList<>();
    JDBCConnection.fetchPersonaData(persona, descriptions);
        
        String html = """
                <html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Webpage with Navigation and Carousel</title>
    <link rel="stylesheet" href="stylem.css">
</head>
<body>

    <div class="navbar">
        <a href='/'>Homepage</a>
                        <a href='mission.html'>Our Mission</a>
                        <a href='page2A.html'>Sub Task 2.A</a>
                        <a href='page2B.html'>Sub Task 2.B</a>
                        <a href='page3A.html'>Sub Task 3.A</a>
                        <a href='page3B.html'>Sub Task 3.B</a>
    </div>

    <div class="carousel">
        <div class="carousel-item active">
            <img src="https://cloudfront-us-east-1.images.arcpublishing.com/bostonglobe/V4SZWXYQVNYCLNGYK3OCMDXLMA.JPEG" alt="Image 1">
            <div class="carousel-text">
                <h2>Our Vision</h2>
                <p>We envision a world where food is valued as a precious resource, and waste is minimized. By shining a spotlight on food loss and providing the tools and knowledge necessary to combat it, we aim to contribute to a future where food security is ensured for all, and our planet's resources are used wisely.</p>
            </div>
        </div>
        <div class="carousel-item">
            <img src="https://s1.1zoom.me/b4850/553/Vegetables_Texture_516103_3840x2160.jpg" alt="Image 2">
            <div class="carousel-text">
                <h2>Education</h2>
                <p>We are committed to educating individuals, businesses, and policymakers about the causes and consequences of food loss. By disseminating knowledge, we empower our audience to make informed decisions and take effective actions to mitigate waste.</p>
            </div>
        </div>
        <div class="carousel-item">
            <img src="https://www.onegreenplanet.org/wp-content/uploads/2023/09/shutterstock_2297030141-scaled.jpg" alt="Image 3">
            <div class="carousel-text">
                <h2>Transparency</h2>
                <p>We believe that access to clear, reliable data is crucial for understanding the full impact of food loss. 
                Our platform offers comprehensive statistics to highlight the scale of food waste locally and globally.</p>
            </div>
        </div>
        <a class="prev" onclick="changeSlide(-1)">&#10094;</a>
        <a class="next" onclick="changeSlide(1)">&#10095;</a>
    </div>

    <div class="container">
        <div class="box">
            <img src="145731.png" alt="Image 1">
            """;
                    
                    
            html= html+ "<p>Name: "+persona.get(0)+ "</p>";
            html= html+ "<p>"+descriptions.get(0)+ "</p>";
       html= html+"""
               
                </div>
        <div class="box">
            <img src="202419.png" alt="Image 2">
            """;                  
                
            html+= "<p>Name: "+persona.get(1)+ "</p>";
            html+= "<p>"+descriptions.get(1)+ "</p>";
       html= html+"""
        </div>
        <div class="box">
            <img src="202432.png" alt="Image 3">
           """;                  
                
            html+= "<p>Name: "+persona.get(2)+ "</p>";
            html+= "<p>"+descriptions.get(2)+ "</p>";
       html= html+"""
        </div>
    </div>

    <div class="screenshot-section">
        <img src="333.png" alt="Screenshot Image">
        <div class="text-box">
            <h2>How to use this website</h2>
            <p>Click the dropdowns to select the countries you want to see. Select your years and choose the columns you want to see. Pick the order you want the data to be listed in. </p>
        </div>
    </div>

    <div class="screenshot-section">
    <div class="text-box">
        <h2>Sorting by Food groups</h2>
        <p>Click the dropdown and add the food groups you want to see. Pick your start and end year and pick how you want the results sorted and press submit</p>
    </div>
    <img src="12.png" alt="Screenshot Image">
</div>
</div>
<footer>
        <p>Created by:</p>
        """;                  
                
            html+= "<p>"+names.get(0)+ " " + emails.get(0)+"</p>";
            html+= "<p>"+names.get(1)+ " "+emails.get(1)+"</p>";
       html= html+"""
    </footer>
    <script src="script1.js"></script>
</body>
</html>
                """;
        context.html(html);
    }

}
