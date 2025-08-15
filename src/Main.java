import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import  java.sql.PreparedStatement;
import  java.sql.ResultSet;


public class Main {
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "buntyshalu@123";

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            Connection connection = DriverManager.getConnection(url, username, password);


            while (true) {
                System.out.println("""
                        HotelManagement System
                        1.new reservation
                        2.view reservation
                        3.Get room no
                        4.Update reservation
                        5.Delete reservation
                        6.exit""");
                System.out.println("enter your choice:");
                Scanner scanner = new Scanner(System.in);
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        newreservation(scanner, connection);
                        break;
                    case 2:
                        viewreservation (connection);
                        break;
                    case 3:
                        Getroom (scanner, connection);
                        break;
                    case 4:
                        Updatereservation (scanner, connection);
                        break;
                    case 5:
                        Deletereservation (scanner, connection);
                        break;
                    case 6:
                        exit();
                        scanner.close();
                        return;
                    default:System.out.println("invalid choice.Try again");
                }
            }

        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        catch (InterruptedException e){
            throw new RuntimeException(e);
        }



    }
    private static void newreservation(Scanner scanner,Connection connection)throws SQLException{
        try {


            System.out.println("enter name");
            String name = scanner.next();
            scanner.nextLine();
            System.out.println("enter room no");
            int room_no = scanner.nextInt();
            System.out.println("enter contact_no");
            int ph_no = scanner.nextInt();
            String query = "INSERT INTO reservations (name, room_no, contact) VALUES (?, ?, ?)";

            try(PreparedStatement pstmt = connection.prepareStatement(query)){
                pstmt.setString(1, name);
                pstmt.setInt(2, room_no);
                pstmt.setLong(3, ph_no);
                int rowaffected = pstmt.executeUpdate();
                if (rowaffected > 0) {
                    System.out.println("successfully added");
                } else {
                    System.out.println("Reservation Failed");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());

        }
    }
    private static void viewreservation (Connection connection) throws SQLException{
        String query="SELECT * FROM reservations";
        try (
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query)){
            while(rs.next()) {
                int reservation_id=rs.getInt("reservation_id");
                String name =rs.getString("name");
                int room_no =rs.getInt("room_no");
                int contact=rs.getInt("contact");
                String reservation_date=rs.getTimestamp("reservation_date").toString();

                System.out.println();
                System.out.println("-----------------");
                System.out.println("id "+ reservation_id);
                System.out.println("name "+ name);
                System.out.println("room_no "+ room_no);
                System.out.println("contact "+contact);
                System.out.println("reservation_date "+reservation_date);


            }
        }catch (SQLException e){
            System.err.println("Error: " + e.getMessage());
        }


    }
    private static void Getroom(Scanner scanner,Connection connection){
        System.out.println("enter reservation id");
        int id=scanner.nextInt();
        String query = "SELECT reservation_id, room_no FROM reservations WHERE reservation_id = ?" ;

        try(
            PreparedStatement pstmt = connection.prepareStatement(query)){
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                int reservation_id = rs.getInt("reservation_id");
                int room_no = rs.getInt("room_no");
                System.out.println();
                System.out.println("-----------------");
                System.out.println("id " + reservation_id);
                System.out.println("room_no " + room_no);
            }



        }catch (SQLException e){
            System.err.println("Error: " + e.getMessage());
        }

    }
    private static void Updatereservation (Scanner scanner,Connection connection) throws SQLException{
        try{
            System.out.print("Enter reservation ID to update: ");

            int id = scanner.nextInt();
            scanner.nextLine();

        if (!doesreservationExists(connection, id)) {
            System.out.println("Reservation not found for the given ID.");
            return;
        }
        System.out.println("enter name");
        String name = scanner.nextLine();
        System.out.println("enter room no");
        int room_no = scanner.nextInt();
        System.out.println("enter contact_no");
        int ph_no = scanner.nextInt();
        String query = "UPDATE reservations SET name = ?, room_no = ?, contact = ? WHERE reservation_id = ?";
        try (
            PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, room_no);
            pstmt.setLong(3, ph_no);
            pstmt.setInt(4,id);


            int rowaffected = pstmt.executeUpdate();
            if (rowaffected > 0) {
                System.out.println("successfully updated");
            } else {
                System.out.println("updation Failed id not found");
            }

        }
        }
        catch (SQLException e){
            System.err.println("Error: " + e.getMessage());
        }


    }
    private static void Deletereservation (Scanner scanner,Connection connection) throws SQLException{

        try{
            System.out.print("Enter reservation ID to update: ");

            int id = scanner.nextInt();
            scanner.nextLine();

            if (!doesreservationExists(connection, id)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }
        String query="DELETE  FROM reservations WHERE reservation_id=?" ;
        try(
            PreparedStatement pstmt = connection.prepareStatement(query)){
            pstmt.setInt(1, id);
            int rowaffected= pstmt.executeUpdate();
            if(rowaffected>0){
                System.out.println("Successfully deleted");
            }
            else{
                System.out.println("deletion failed id not found");
            }
        }
        }
        catch (SQLException e){
            System.err.println("Error: " + e.getMessage());
        }

    }


    private static boolean doesreservationExists( Connection connection,int reservation_id) throws SQLException{

           String sqlquery = "SELECT reservation_id FROM reservations WHERE reservation_id = ?";
           try (PreparedStatement ps = connection.prepareStatement(sqlquery)) {
           ps.setInt(1, reservation_id);
           try (ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    }
        catch (SQLException e){
            System.err.println("Error: " + e.getMessage());
            return false;
        }

    }
    private  static void exit() throws InterruptedException{
        System.out.println("Existing System");
        int i=5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(500);
            i--;
        }
        System.out.println("\nSystem Exited Successfully");
    }

}