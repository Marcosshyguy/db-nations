import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private final static String url = System.getenv("URL");
    private final static String user = System.getenv("USER");
    private final static String password = System.getenv("PASSWORD");
    public static void main(String[] args) {


        Scanner scan = new Scanner(System.in);
        System.out.print("Search ");
        String word = scan.nextLine();

        try(Connection conn = DriverManager.getConnection(url,user,password)){
            boolean hasResult = false;
            String query = """
                    select countries.country_id as ID, countries.name as COUNTRY, regions.name as REGION , continents.name AS CONTINENT\s
                    from countries\s
                    join regions on countries.region_id = regions.region_id\s
                    join continents on regions.continent_id = continents.continent_id
                    WHERE countries.name LIKE ?
                    ORDER BY countries.name
                    """;

            try(PreparedStatement ps = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)){
                ps.setString(1,"%" + word + "%");
                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()){
                        hasResult = true;
                        do{
                            int id = rs.getInt("ID");
                            String country = rs.getString("COUNTRY");
                            String region = rs.getString("REGION");
                            String continent = rs.getString("CONTINENT");

                            System.out.printf( "%4s%40s%30s%20s\n",id, country, region,continent);
                        }while (rs.next());
                    }else {
                        System.out.println("no coutries found");
                    }



                }catch (SQLException e){
                    System.out.println("something went wrong with ResultSet");
                }

            }catch (SQLException e){
                System.out.println("something went wrong with PreparedStatement");
            }

//LANGUAGES

            if (hasResult) {
                System.out.print("Choose id: ");
                int userId = Integer.parseInt(scan.nextLine());

                String queryLanguages = """
                        select languages.language
                        from countries\s
                        join country_languages on country_languages.country_id = countries.country_id
                        join languages on languages.language_id = country_languages.language_id
                        WHERE countries.country_id = ?
                        """;

                try(PreparedStatement ps = conn.prepareStatement(queryLanguages, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)){
                    ps.setInt(1, userId);
                    try(ResultSet rs = ps.executeQuery()){
                        System.out.print("Lingue parlate:");
                        while (rs.next()){
                            String  language = " " + rs.getString("language");
                            System.out.print(language);
                            if(!rs.isLast()){
                                System.out.print(", ");
                            }else {
                                System.out.print(".");
                            }
                        }

                    }catch (SQLException e){
                        System.out.println("something went wrong with ResultSet");
                    }
                }catch (SQLException e){
                    System.out.println("something went wrong with PreparedStatement");
                }

                String statsQuery = """
                        select countries.country_id, countries.name , country_stats.year as YEAR , country_stats.gdp as GDP,country_stats.population as POPULATION  from countries\s
                        join country_stats on countries.country_id = country_stats.country_id\s
                        where countries.country_id = ?
                        order by country_stats.year desc
                        limit 1;
                        """;
                System.out.println();
                try(PreparedStatement ps = conn.prepareStatement(statsQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)){
                    ps.setInt(1, userId);
                    try(ResultSet rs = ps.executeQuery()){

                        while (rs.next()){
                            long gdp = rs.getLong("GDP");
                            String population = rs.getString("POPULATION");
                            int year = rs.getInt("YEAR");
                            System.out.println("GDP: " + gdp + "\n" + "Population: " +  population + "\n" + "Year: " + year );
                        }

                    }catch (SQLException e){
                        System.out.println("something went wrong with ResultSet");
                    }
                }catch (SQLException e){
                    System.out.println("something went wrong with PreparedStatement");
                }
            }


        }catch (SQLException e){
            System.out.println("connessione fallita");
        }
    scan.close();
    }
}

