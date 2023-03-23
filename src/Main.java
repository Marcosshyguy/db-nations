import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        String url = System.getenv("URL");
        String user = System.getenv("USER");
        String password = System.getenv("PASSWORD");

        Scanner scan = new Scanner(System.in);
        System.out.print("Insert a word ");
        String word = scan.nextLine();

        try(Connection conn = DriverManager.getConnection(url,user,password)){

            String query = """
                    SELECT countries.country_id , countries.name as country, regions.name as region, continents.name as continent FROM countries\s
                    inner join regions on countries.country_id = regions.region_id
                    inner join continents on regions.continent_id = continents.continent_id
                    WHERE countries.name like ?
                    ORDER by countries.name;
                    """;
            try(PreparedStatement ps = conn.prepareStatement(query)){
                ps.setString(1,"%" + word + "%");
                try(ResultSet rs = ps.executeQuery()){
                    while (rs.next()){
                        int id = rs.getInt("country_id");
                        String country = rs.getString("country");
                        String region = rs.getString("region");
                        String continent = rs.getString("continent");

                        System.out.println(id + " "+ country + " " + region + " " + continent);
                    }
                }catch (SQLException e){
                    System.out.println("qulacosa si è rotto su Result Set");
                }

            }catch (SQLException e){
                System.out.println("qulacosa si è rotto su PreparedStattement");
            }
        }catch (SQLException e){
            System.out.println("connessione fallita");
        }

    }
}