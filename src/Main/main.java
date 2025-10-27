package Main;

import Config.config;
import java.util.*;

public class main {

    public static void main(String[] args) {
        config con = new config();
        con.connectDB();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== WELCOME TO CATERING RESERVATION SYSTEM =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");

            int choice = dashboard.getIntInput(sc, "Enter choice: ");
            sc.nextLine();

            if (choice == 1) {
                String email = dashboard.getEmailInput(sc);
                System.out.print("Enter Password: ");
                String pass = sc.nextLine();
                
                String hash = con.hashPassword(pass);
                
                List<Map<String, Object>> result = con.fetchRecords(
                    "SELECT * FROM tbl_users WHERE u_email=? AND u_pass=?", email, hash);

                if (result.isEmpty()) {
                    System.out.println("❌ Invalid credentials!");
                } else {
                    Map<String, Object> user = result.get(0);
                    String type = user.get("u_type").toString();
                    String status = user.get("u_status").toString();
                    int userId = Integer.parseInt(user.get("u_id").toString());

                    if (status.equalsIgnoreCase("Pending")) {
                        System.out.println("⏳ Account pending admin approval.");
                    } else {
                        System.out.println("✅ Login successful!");

                        switch (type) {
                            case "Admin":
                                dashboard.adminDashboard(sc, con);
                                break;

                            case "Staff":
                                List<Map<String, Object>> staffRes = con.fetchRecords(
                                    "SELECT staff_id FROM tbl_staff WHERE u_id=?", userId);
                                int staffId = staffRes.isEmpty() ? 0 :
                                    Integer.parseInt(staffRes.get(0).get("staff_id").toString());
                                dashboard.staffDashboard(sc, con, staffId);
                                break;

                            case "Customer":
                                List<Map<String, Object>> custRes = con.fetchRecords(
                                    "SELECT customer_id FROM tbl_customers WHERE u_id=?", userId);
                                int customerId = custRes.isEmpty() ? 0 :
                                    Integer.parseInt(custRes.get(0).get("customer_id").toString());
                                dashboard.customerDashboard(sc, con, customerId);
                                break;
                        }
                    }
                }

            } else if (choice == 2) {
                System.out.println("\n--- REGISTER ACCOUNT ---");
                String name = dashboard.getNonEmptyInput(sc, "Full Name: ");
                String email = dashboard.getEmailInput(sc);
                System.out.print("Enter Password: ");
                String pass = sc.nextLine();
                int typeSel = dashboard.getIntInput(sc, "Select Type (1=Admin, 2=Staff, 3=Customer): ");
                String userType = (typeSel == 1) ? "Admin" : (typeSel == 2 ? "Staff" : "Customer");

                String hash = con.hashPassword(pass);
                
                con.addRecord("INSERT INTO tbl_users(u_name, u_email, u_pass, u_type, u_status) VALUES (?, ?, ?, ?, 'Pending')",
                        name, email, hash, userType);
                System.out.println("✅ Registration submitted! Await admin approval.");

            } else if (choice == 3) {
                System.out.println("👋 Thank you for using the system. Goodbye!");
                break;
            } else {
                System.out.println("Invalid choice!");
            }
        }
    }
}
