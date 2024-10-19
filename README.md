# FWI - Food Wastage Info

FWI is a web-based platform that displays food wastage data by country. The data is dynamically retrieved using SQL queries with **JDBC** and the site is hosted using **Javalin**, a lightweight web framework for Java.

## Features

- **Country-Specific Food Wastage Data**: Users can explore detailed food wastage statistics for different countries.
- **Database Querying**: The platform uses SQL queries to pull data from the database in real-time.
- **Java Backend**: Powered by **JDBC** to communicate with the SQL database.
- **Hosted with Javalin**: Built with **Javalin**, making the server lightweight and fast.
- **User-Friendly Interface**: Intuitive design for ease of data exploration and comparison between countries.

## Technology Stack

- **Backend**: Java with **Javalin** as the web framework.
- **Database**: SQL database for storing and retrieving food wastage data.
- **JDBC**: Used for executing SQL queries and interacting with the database.
- **Frontend**: HTML, CSS, and JavaScript for a simple, clean user interface.
- **Hosting**: Javalin server hosting the platform.

## SQL Queries

FWI retrieves data using optimized SQL queries. Example query:

```sql
SELECT country_name, total_waste
FROM food_waste_data
WHERE country_name = 'Australia';
