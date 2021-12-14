package scrapper;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CacheScraper implements Scraper {
    private Scraper scraper = new DefaultScraper();
    @Override @SneakyThrows
    public Home scrape(String url) {
        // Created connection to DB
        Connection connection = DriverManager.getConnection("jdbc:sqlite:/Users/innazhurba/Documents/lab10_OOP/src/main/java/Database.db");
        Statement statement = connection.createStatement();

        // Execute query
        String query = String.format("select count(*) as count from Homes where url='%s'", url);
        ResultSet rs = statement.executeQuery(query);

        // Extract result
        if (rs.getInt("count") > 0) {
            // Extract from DB
            query = String.format("select * from Homes where url='%s'", url);
            rs = statement.executeQuery(query);
            return new Home(rs.getInt("price"),
                    rs.getDouble("beds"),
                    rs.getDouble("baths"),
                    rs.getDouble("garage"));
        } else {
            // Call old scraper
            Home home = scraper.scrape(url);
            statement.executeUpdate("insert into Homes (url, price, beds, baths, garage) values ("+url+","+home.getPrice()+","+home.getBeds()+","+home.getBathes()+","+home.getGarages()+")");
        }
        return null;
    }
}
