package Main;

import Config.config;
import java.util.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class dashboard {

    // ====== INPUT HELPERS ======
    public static int getIntInput(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            try {
                return sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine();
            }
        }
    }

    public static double getDoubleInput(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            try {
                return sc.nextDouble();
            } catch (InputMismatchException e) {
                System.out.println("Invalid number, try again.");
                sc.nextLine();
            }
        }
    }

    public static String getNonEmptyInput(Scanner sc, String message) {
        String input;
        do {
            System.out.print(message);
            input = sc.nextLine().trim();
        } while (input.isEmpty());
        return input;
    }

    public static String getEmailInput(Scanner sc) {
        String pattern = "^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$";
        while (true) {
            System.out.print("Enter Email: ");
            String email = sc.nextLine();
            if (email.matches(pattern)) return email;
            System.out.println("Invalid email format.");
        }
    }

    public static String getDateInput(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String date = sc.nextLine();
            try {
                LocalDate.parse(date);
                return date;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Use YYYY-MM-DD.");
            }
        }
    }

    public static String getTimeInput(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String time = sc.nextLine();
            try {
                LocalTime.parse(time);
                return time;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time. Use HH:MM:SS.");
            }
        }
    }

    // ====== MENU MANAGEMENT ======
    public static void menuManagement(Scanner sc, config con) {
        int choice;
        do {
            System.out.println("\n--- MENU MANAGEMENT ---");
            System.out.println("1. Add Menu");
            System.out.println("2. View Menus");
            System.out.println("3. Update Menu");
            System.out.println("4. Delete Menu");
            System.out.println("5. Back");
            choice = getIntInput(sc, "Enter choice: ");
            sc.nextLine();

            switch (choice) {
                case 1:
                    String name = getNonEmptyInput(sc, "Menu Name: ");
                    String desc = getNonEmptyInput(sc, "Description: ");
                    double price = getDoubleInput(sc, "Price per person: ");
                    sc.nextLine();
                    String cat = getNonEmptyInput(sc, "Category: ");
                    con.addRecord("INSERT INTO tbl_menu(menu_name, description, price_per_person, category, status) VALUES (?, ?, ?, ?, 'Active')",
                            name, desc, price, cat);
                    System.out.println("✅ Menu added!");
                    break;
                case 2:
                    con.viewRecords("SELECT * FROM tbl_menu",
                            new String[]{"ID", "Name", "Description", "Price", "Category", "Status"},
                            new String[]{"menu_id", "menu_name", "description", "price_per_person", "category", "status"});
                    break;
                case 3:
                    int id = getIntInput(sc, "Enter Menu ID to update: ");
                    sc.nextLine();
                    String newName = getNonEmptyInput(sc, "New Name: ");
                    String newDesc = getNonEmptyInput(sc, "New Description: ");
                    double newPrice = getDoubleInput(sc, "New Price: ");
                    sc.nextLine();
                    String newCat = getNonEmptyInput(sc, "New Category: ");
                    con.updateRecord("UPDATE tbl_menu SET menu_name=?, description=?, price_per_person=?, category=? WHERE menu_id=?",
                            newName, newDesc, newPrice, newCat, id);
                    System.out.println("✅ Menu updated!");
                    break;
                case 4:
                    int del = getIntInput(sc, "Enter Menu ID to delete: ");
                    sc.nextLine();
                    con.deleteRecord("DELETE FROM tbl_menu WHERE menu_id=?", del);
                    System.out.println("✅ Menu deleted!");
                    break;
            }
        } while (choice != 5);
    }

    // ====== EVENT TYPE MANAGEMENT ======
    public static void eventTypeManagement(Scanner sc, config con) {
        int choice;
        do {
            System.out.println("\n--- EVENT TYPE MANAGEMENT ---");
            System.out.println("1. Add Event Type");
            System.out.println("2. View Event Types");
            System.out.println("3. Update Event Type");
            System.out.println("4. Delete Event Type");
            System.out.println("5. Back");
            choice = getIntInput(sc, "Enter choice: ");
            sc.nextLine();

            switch (choice) {
                case 1:
                    String ename = getNonEmptyInput(sc, "Event Name: ");
                    String desc = getNonEmptyInput(sc, "Description: ");
                    con.addRecord("INSERT INTO tbl_event_type(event_name, description, status) VALUES (?, ?, 'Active')",
                            ename, desc);
                    System.out.println("✅ Event Type added!");
                    break;
                case 2:
                    con.viewRecords("SELECT * FROM tbl_event_type",
                            new String[]{"ID", "Event Name", "Description", "Status"},
                            new String[]{"event_type_id", "event_name", "description", "status"});
                    break;
                case 3:
                    int eid = getIntInput(sc, "Enter Event Type ID: ");
                    sc.nextLine();
                    String n = getNonEmptyInput(sc, "New Event Name: ");
                    String d = getNonEmptyInput(sc, "New Description: ");
                    con.updateRecord("UPDATE tbl_event_type SET event_name=?, description=? WHERE event_type_id=?",
                            n, d, eid);
                    System.out.println("✅ Event updated!");
                    break;
                case 4:
                    int del = getIntInput(sc, "Enter Event Type ID: ");
                    sc.nextLine();
                    con.deleteRecord("DELETE FROM tbl_event_type WHERE event_type_id=?", del);
                    System.out.println("✅ Event deleted!");
                    break;
            }
        } while (choice != 5);
    }
// ====== RESERVATION FUNCTIONS ======
   public static void makeReservation(Scanner sc, config con, int customerId) {
    System.out.println("\n--- MAKE RESERVATION ---");
    
    // Show event types
    con.viewRecords("SELECT * FROM tbl_event_type WHERE status='Active'",
            new String[]{"ID", "Event Name", "Description"},
            new String[]{"event_type_id", "event_name", "description"});
    int eventTypeId = getIntInput(sc, "Enter Event Type ID: ");

    // Show menus
    con.viewRecords("SELECT * FROM tbl_menu WHERE status='Active'",
            new String[]{"ID", "Name", "Description", "Price", "Category"},
            new String[]{"menu_id", "menu_name", "description", "price_per_person", "category"});
    int menuId = getIntInput(sc, "Enter Menu ID: ");
    sc.nextLine();

    // Retrieve price per person for selected menu
    String queryPrice = "SELECT price_per_person FROM tbl_menu WHERE menu_id=" + menuId;
    double pricePerPerson = con.getSingleDouble(queryPrice); // Assuming getSingleDouble returns the value

    // Event details
    String date = getDateInput(sc, "Enter Event Date (YYYY-MM-DD): ");
    String time = getTimeInput(sc, "Enter Event Time (HH:MM:SS): ");
    int guests = getIntInput(sc, "Enter Guest Count: ");
    sc.nextLine();
    String venue = getNonEmptyInput(sc, "Enter Venue: ");

    // Calculate total
    double total = pricePerPerson * guests;
    System.out.println("💰 Total Payment: ₱" + total);

    // Insert reservation
    con.addRecord(
        "INSERT INTO tbl_reservations(customer_id, event_type_id, menu_id, event_date, event_time, guest_count, venue, total_amount, reservation_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Pending')",
        customerId, eventTypeId, menuId, date, time, guests, venue, total
    );

    System.out.println("✅ Reservation created! Awaiting admin approval.");
}
public static void viewMyReservations(config con, int customerId) {
    String query = "SELECT r.reservation_id, e.event_name, m.menu_name, r.event_date, r.event_time, " +
                   "r.guest_count, r.venue, r.total_amount, r.reservation_status " +
                   "FROM tbl_reservations r " +
                   "JOIN tbl_event_type e ON r.event_type_id = e.event_type_id " +
                   "JOIN tbl_menu m ON r.menu_id = m.menu_id " +
                   "WHERE r.customer_id = ?";

    String[] headers = {"Reservation ID", "Event", "Menu", "Date", "Time", "Guests", "Venue", "Total Payment", "Status"};
    String[] columns = {"reservation_id", "event_name", "menu_name", "event_date", "event_time", "guest_count", "venue", "total_amount", "reservation_status"};

    // Use parameterized fetchRecords
    java.util.List<java.util.Map<String, Object>> records = con.fetchRecords(query, customerId);

    // Print headers
    System.out.println("-----------------------------------------------------------------------------------------------------------");
    for (String h : headers) {
        System.out.printf("%-15s", h);
    }
    System.out.println("\n-----------------------------------------------------------------------------------------------------------");

    // Print each reservation
    for (java.util.Map<String, Object> row : records) {
        for (String col : columns) {
            Object value = row.get(col);
            if ("total_amount".equals(col) && value != null) {
                System.out.printf("₱%-14.2f", ((Number)value).doubleValue());
            } else {
                System.out.printf("%-15s", value != null ? value.toString() : "");
            }
        }
        System.out.println();
    }
    System.out.println("-----------------------------------------------------------------------------------------------------------");
}


    // ====== STAFF MANAGEMENT ======
public static void manageStaff(Scanner sc, config con) {
    int choice;
    do {
        System.out.println("\n--- MANAGE STAFF ---");
        System.out.println("1. Add Staff");
        System.out.println("2. View Staff");
        System.out.println("3. Update Staff");
        System.out.println("4. Delete Staff");
        System.out.println("5. Back");

        choice = getIntInput(sc, "Enter choice: ");
        sc.nextLine();

        switch (choice) {

            case 1: // ADD STAFF
                String name = getNonEmptyInput(sc, "Enter Staff Name: ");
                String email = getEmailInput(sc);
                String phone = getNonEmptyInput(sc, "Enter Contact Number: ");
                String role = getNonEmptyInput(sc, "Enter Role (e.g., Waiter, Chef): ");

                con.addRecord("INSERT INTO tbl_staff(staff_name, email, contact, role, status) VALUES (?, ?, ?, ?, 'Active')",
                        name, email, phone, role);

                System.out.println("✅ Staff added successfully!");
                break;

            case 2: // VIEW STAFF
                viewStaff(con);
                break;

            case 3: // UPDATE STAFF
                viewStaff(con);
                int sid = getIntInput(sc, "Enter Staff ID to update: ");
                sc.nextLine();

                String newName = getNonEmptyInput(sc, "New Name: ");
                String newEmail = getEmailInput(sc);
                String newPhone = getNonEmptyInput(sc, "New Contact Number: ");
                String newRole = getNonEmptyInput(sc, "New Role: ");

                con.updateRecord("UPDATE tbl_staff SET staff_name=?, email=?, contact=?, role=? WHERE staff_id=?",
                        newName, newEmail, newPhone, newRole, sid);

                System.out.println("✅ Staff updated successfully!");
                break;

            case 4: // DELETE STAFF
                viewStaff(con);
                int deleteId = getIntInput(sc, "Enter Staff ID to delete: ");
                sc.nextLine();

                con.deleteRecord("DELETE FROM tbl_staff WHERE staff_id=?", deleteId);
                System.out.println("🗑 Staff deleted successfully!");
                break;
        }

    } while (choice != 5);
}

    // ====== MANAGE RESERVATIONS (NEW) ======
public static void manageReservations(Scanner sc, config con) {
    int choice;
    do {
        System.out.println("\n--- MANAGE RESERVATIONS ---");
        System.out.println("1. View Reservations");
        System.out.println("2. View Resevations");
        System.out.println("3. Approve Reservation");
        System.out.println("4. Reject Reservation");
        System.out.println("5. Assign Reservation to Staff");
        System.out.println("6. Back");

        choice = getIntInput(sc, "Enter choice: ");
        sc.nextLine();

        switch (choice) {

            case 1: // VIEW ALL
                con.viewRecords("SELECT * FROM tbl_reservations",
                        new String[]{"Reservation ID", "Customer", "Event", "Date", "Time", "Guests", "Venue", "Status", "Staff ID"},
                        new String[]{"reservation_id", "customer_id", "event_type_id", "event_date", "event_time", "guest_count", "venue", "reservation_status", "staff_id"});
                break;
             case 2: // VIEW STAFF (NEW)
                viewStaff(con);
                break;

            case 3: // APPROVE
                int approveId = getIntInput(sc, "Enter Reservation ID to approve: ");
                sc.nextLine();
                con.updateRecord("UPDATE tbl_reservations SET reservation_status='Approved' WHERE reservation_id=?", approveId);
                System.out.println("✅ Reservation Approved!");
                break;

            case 4: // REJECT
                int rejectId = getIntInput(sc, "Enter Reservation ID to reject: ");
                sc.nextLine();
                con.updateRecord("UPDATE tbl_reservations SET reservation_status='Rejected' WHERE reservation_id=?", rejectId);
                System.out.println("❌ Reservation Rejected!");
                break;

            case 5: // ASSIGN STAFF
                int resId = getIntInput(sc, "Enter Reservation ID: ");
                sc.nextLine();
                int staffId = getIntInput(sc, "Enter Staff ID to assign: ");
                sc.nextLine();
                con.updateRecord("UPDATE tbl_reservations SET staff_id=? WHERE reservation_id=?", staffId, resId);
                System.out.println("👷 Staff Assigned Successfully!");
                break;
        }

    } while (choice != 6);
}


    // ====== PAYMENT MANAGEMENT ======
   public static void viewAllPayments(config con) {
    String query = "SELECT p.payment_id, r.reservation_id, c.first_name || ' ' || c.last_name AS customer_name, " +
                   "e.event_name, m.menu_name, r.venue, r.total_amount, r.reservation_status, " +
                   "p.payment_date, p.amount_paid, p.payment_method, p.payment_status " +
                   "FROM tbl_payment p " +
                   "JOIN tbl_reservations r ON p.reservation_id = r.reservation_id " +
                   "JOIN tbl_customers c ON r.customer_id = c.customer_id " +
                   "JOIN tbl_event_type e ON r.event_type_id = e.event_type_id " +
                   "JOIN tbl_menu m ON r.menu_id = m.menu_id";

    String[] headers = {"Payment ID", "Reservation ID", "Customer", "Event", "Menu", "Venue", "Total Amount", "Reservation Status",
                        "Payment Date", "Amount Paid", "Method", "Payment Status"};

    String[] columns = {"payment_id", "reservation_id", "customer_name", "event_name", "menu_name", "venue", "total_amount",
                        "reservation_status", "payment_date", "amount_paid", "payment_method", "payment_status"};

    java.util.List<java.util.Map<String, Object>> records = con.fetchRecords(query);

    // Print headers
    System.out.println("----------------------------------------------------------------------------------------------------------------------------");
    for (String h : headers) {
        System.out.printf("%-15s", h);
    }
    System.out.println("\n----------------------------------------------------------------------------------------------------------------------------");

    // Print each record
    for (java.util.Map<String, Object> row : records) {
        for (String col : columns) {
            Object value = row.get(col);
            if ((col.equals("total_amount") || col.equals("amount_paid")) && value != null) {
                System.out.printf("₱%-14.2f", ((Number)value).doubleValue());
            } else {
                System.out.printf("%-15s", value != null ? value.toString() : "");
            }
        }
        System.out.println();
    }
    System.out.println("----------------------------------------------------------------------------------------------------------------------------");
}


    public static void addPayment(Scanner sc, config con, int reservationId) {
        String date = LocalDate.now().toString();
        double amount = getDoubleInput(sc, "Enter amount: ");
        sc.nextLine();
        String method = getNonEmptyInput(sc, "Payment Method (Cash/Card): ");
        con.addRecord("INSERT INTO tbl_payment(reservation_id, payment_date, amount_paid, payment_method, payment_status) VALUES (?, ?, ?, ?, 'Paid')",
                reservationId, date, amount, method);
        System.out.println("✅ Payment added!");
    }

    public static void viewCustomerPayments(config con, int customerId) {
        String query = "SELECT p.payment_id, p.payment_date, p.amount_paid, p.payment_method, p.payment_status, r.reservation_id " +
               "FROM tbl_payment p JOIN tbl_reservations r ON p.reservation_id = r.reservation_id " +
               "WHERE r.customer_id = " + customerId; // <-- Only payments of the logged-in customer

        con.viewRecords(query,
                new String[]{"Payment ID", "Reservation", "Date", "Amount", "Method", "Status"},
                new String[]{"payment_id", "reservation_id", "payment_date", "amount_paid", "payment_method", "payment_status"});
    }
    public static void viewStaff(config con) {
    con.viewRecords("SELECT * FROM tbl_staff",
            new String[]{"ID", "Name", "Email", "Contact", "Role", "Status"},
            new String[]{"staff_id", "staff_name", "email", "contact", "role", "status"});
}


    public static void updatePaymentStatus(Scanner sc, config con) {
        viewAllPayments(con);
        int payId = getIntInput(sc, "Enter Payment ID: ");
        sc.nextLine();
        String newStatus = getNonEmptyInput(sc, "Enter new status (Paid/Refunded/Pending): ");
        con.updateRecord("UPDATE tbl_payment SET payment_status=? WHERE payment_id=?", newStatus, payId);
        System.out.println("✅ Payment status updated!");
    }

    // ====== STAFF DASHBOARD ======
    public static void staffDashboard(Scanner sc, config con, int staffId) {
        int choice;
        do {
            System.out.println("\n--- STAFF DASHBOARD ---");
            System.out.println("1. View Assigned Reservations");
            System.out.println("2. Update Reservation Status");
            System.out.println("3. Logout");
            choice = getIntInput(sc, "Enter choice: ");
            sc.nextLine();

            switch (choice) {
                case 1:
                   
                    con.viewRecords("SELECT * FROM tbl_reservations WHERE staff_id=" + staffId,
                            new String[]{"Reservation ID", "Customer", "Event", "Date", "Status"},
                            new String[]{"reservation_id", "customer_id", "event_type_id", "event_date", "reservation_status"});
                    break;
                case 2:
                    int resId = getIntInput(sc, "Enter Reservation ID: ");
                    sc.nextLine();
                    String newStatus = getNonEmptyInput(sc, "New Status (Approved/Completed/Cancelled): ");
                    con.updateRecord("UPDATE tbl_reservations SET reservation_status=? WHERE reservation_id=?", newStatus, resId);
                    System.out.println("✅ Reservation status updated!");
                    break;
            }
        } while (choice != 3);
    }
 // ====== VIEW USERS ======
    public static void viewUsers(config con) {
        String query = "SELECT * FROM tbl_users";
        String[] headers = {"ID", "Name", "Email", "Type", "Status"};
        String[] columns = {"u_id", "u_name", "u_email", "u_type", "u_status"};
        con.viewRecords(query, headers, columns);
    }
    // ====== ADMIN DASHBOARD ======
    public static void adminDashboard(Scanner sc, config con) {
        int action;
        do {
            System.out.println("\n--- ADMIN DASHBOARD ---");
            System.out.println("1. View Users");
            System.out.println("2. Approve Users");
            System.out.println("3. Menu Management");
            System.out.println("4. Event Type Management");
            System.out.println("5. Manage Reservations");
            System.out.println("6. Manage Staff");
            System.out.println("7. Payment Management");
            System.out.println("8. Logout");
            action = getIntInput(sc, "Enter choice: ");
            sc.nextLine();

            switch (action) {
                case 1: viewUsers(con); break;
                case 2:
                    viewUsers(con);
                    int userId = getIntInput(sc, "Enter User ID to approve: ");
                    sc.nextLine();
                    con.updateRecord("UPDATE tbl_users SET u_status='Approved' WHERE u_id=?", userId);
                    System.out.println("✅ User approved!");
                    break;
                case 3: menuManagement(sc, con); break;
                case 4: eventTypeManagement(sc, con); break;
                case 5:
                        manageReservations(sc, con);
                               break;
                case 6:
                          manageStaff(sc, con);
                         break;               

                    
                case 7:
                    int payChoice;
                    do {
                        System.out.println("\n--- PAYMENT MANAGEMENT ---");
                        System.out.println("1. View All Payments");
                        System.out.println("2. Update Payment Status");
                        System.out.println("3. Back");
                        payChoice = getIntInput(sc, "Enter choice: ");
                        sc.nextLine();

                        switch (payChoice) {
                            case 1: viewAllPayments(con); break;
                            case 2: updatePaymentStatus(sc, con); break;
                        }
                    } while (payChoice != 3);
                    break;
            }
        } while (action != 8);
    }

    // ====== CUSTOMER DASHBOARD ======
    public static void customerDashboard(Scanner sc, config con, int customerId) {
        int action;
        do {
            System.out.println("\n--- CUSTOMER DASHBOARD ---");
            System.out.println("1. View Menus");
            System.out.println("2. Make Reservation");
            System.out.println("3. View My Reservations");
            System.out.println("4. View My Payments");
            System.out.println("5. Make a Payment");
            System.out.println("6. Logout");
            action = getIntInput(sc, "Enter choice: ");
            sc.nextLine();

            switch (action) {
                case 1:
                    con.viewRecords("SELECT * FROM tbl_menu WHERE status='Active'",
                            new String[]{"ID", "Name", "Price", "Category"},
                            new String[]{"menu_id", "menu_name", "price_per_person", "category"});
                    break;
                case 2: makeReservation(sc, con, customerId); break;
                case 3: viewMyReservations(con, customerId); break;
                case 4: viewCustomerPayments(con, customerId); break;
                case 5:
                    // Make payment: first list the customer's reservations
                viewMyReservations(con, customerId);
                int resId = getIntInput(sc, "Enter Reservation ID to pay for: ");
                sc.nextLine();

                // Security check: ensure the reservation belongs to this customer
                String checkQuery = "SELECT COUNT(*) FROM tbl_reservations WHERE reservation_id=? AND customer_id=?";
                int count = con.getSingleInt(checkQuery, resId, customerId); // Assumes getSingleInt exists
                if (count == 1) {
                    addPayment(sc, con, resId);
                } else {
                    System.out.println("❌ Invalid Reservation ID. You can only pay for your own reservations.");
                }
                break;
        }
    } while (action != 6);
}
    
}
