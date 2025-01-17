import java.sql.DriverManager;
import  java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Scanner;

public class HotelReservation
{
     private  static  final String url="jdbc:mysql://localhost:3306/hotel_db";
     private static final  String username="root";
     private static final String password="1379";

     public static void main(String args[])throws  ClassNotFoundException,SQLException
     {
         try
         {
             Class.forName("com.mysql.cj.jdbc.Driver");

         }
         catch (ClassNotFoundException e)
         {
             System.out.println(e.getMessage());
         }
         try{
             Connection con=DriverManager.getConnection(url,username,password);
             while(true)
             {
                 System.out.println();
                 System.out.println("-----HOTEL RESERVATION MANAGEMENT-----");
                 Scanner sc=new Scanner(System.in);
                 System.out.println("1.Reserve a room");
                 System.out.println("2.View Reservation");
                 System.out.println("3.Get Room Number");
                 System.out.println("4.Update Reservation");
                 System.out.println("5.Delete Reservation");
                 System.out.println("0.Exit");
                 System.out.println("ENTER YOUR OPTION");
                 int choice=sc.nextInt();
                 switch (choice)
                 {
                     case 1:
                         reserveRoom(con,sc);
                         break;
                     case 2:
                         viewReservation(con);
                         break;
                     case 3:
                         getRoomNo(con,sc);
                         break;
                     case 4:
                         updateReservation(con,sc);
                         break;
                     case 5:
                         deleteReservation(con,sc);
                         break;
                     case 0:
                         exit();
                         sc.close();
                         return;
                     default:
                         System.out.println("INVALID CHOICE.TRY AGAIN");



                 }



             }
         }
         catch (SQLException e)
         {
             System.out.println(e.getMessage());
         }
         catch (InterruptedException e)
         {
             throw new RuntimeException(e);
         }

     }
    private static void reserveRoom(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter guest name: ");
            String guestName = scanner.next();
            scanner.nextLine();
            System.out.print("Enter room number: ");
            int roomNumber = scanner.nextInt();
            System.out.print("Enter contact number: ");
            String contactNumber = scanner.next();

            String sql = "INSERT INTO reservation_sys (guest_name, room_number, contact_number) " +
                    "VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "')";

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation successful!");
                } else {
                    System.out.println("Reservation failed.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
     private static void viewReservation(Connection con)throws  SQLException{
         String sql="select reservation_id,guestname,room_no,contact,reservation_date from reservations";
         try(Statement st=con.createStatement();
             ResultSet rs=st.executeQuery(sql)){
             System.out.println("Current Reservations");
             System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
             System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
             System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
             while(rs.next())
             {
                 int reserveid=rs.getInt("reservation_id");
                 String guest=rs.getString("guestname");
                 int roomno=rs.getInt("room_no");
                 String contact=rs.getString("contact");
                 String resdate=rs.getTimestamp("reservation_date").toString();
                 System.out.println(reserveid +" "+guest+ " " +roomno+ " " +contact+resdate);

             }
         }
     }
     private static void getRoomNo(Connection con,Scanner sc)
     {
         try
         {
             System.out.println("ENTER THE RESERVATION ID");
             int resid=sc.nextInt();
             System.out.println("ENTER GUEST NAME");
             String guestname=sc.next();
             String sql="select room_no from reservations"+
                     "where reservation_id="+resid+
                     "and guestname="+guestname+"'";
             try(Statement st=con.createStatement();
             ResultSet rs=st.executeQuery(sql))
             {
                 if(rs.next())
                 {
                     int roomno=rs.getInt("room_no");
                     System.out.println("ROOM NUMBER FOR RESERVATION ID "+resid+
                             "AND GUEST NAME="+guestname+"IS "+roomno);
                 }
                 else
                     System.out.println("NO RESERVATION FOUND");

             }


         }catch (SQLException e)
         {
             System.out.println(e.getMessage());
         }
     }
     private  static void updateReservation(Connection con,Scanner sc)
     {
         try{
             System.out.println("ENTER THE RESERVATION ID");
             int resid=sc.nextInt();
             sc.nextLine();
             if(!reservationExists(con,resid))
             {
                 System.out.println("RESERVAITON NOT FOUND");
                 return;
             }
             System.out.println("ENTER THE NEW GUEST NAME");
             String newguest=sc.nextLine();
             System.out.println("ENTER NEW ROOM NUMBER");
             int newroom=sc.nextInt();
             System.out.println("ENTER NEW CONTACT NUMBER");
             String newcon=sc.next();

             String sql="update reservations set guestname='"+newguest+ "',"+
                     "room_no="+newroom+","+
                     "contact='"+newcon+"'"+
                     "where reservation_id="+resid;
             try(Statement st =con.createStatement()) {
                 int affected=st.executeUpdate(sql);
                 if(affected>0)
                 {
                     System.out.println("UPDATED SUCCESSFULLY");

                 }
                 else {
                     System.out.println("NOT UPDATED");
                 }

             }

         }catch (SQLException e)
         {
             System.out.println(e.getMessage());
         }
     }
    private static void deleteReservation(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID to delete: ");
            int reservationId = scanner.nextInt();

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private static boolean reservationExists(Connection connection, int reservationId) {
        try {
            String sql = "SELECT reservation_id FROM reservation_sys WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                return resultSet.next(); // If there's a result, the reservation exists
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false; // Handle database errors as needed
        }
    }

    public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }


}
