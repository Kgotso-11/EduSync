package za.ac.tut.bl;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.sql.*;

@WebServlet("/ConfirmBookingServlet")
public class ConfirmBookingServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://172.20.10.3:3306/edusync_db";
    private static final String DB_USER = "kgj";
    private static final String DB_PASSWORD = "kgotso";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String message = "Booking status could not be updated.";
        Connection con = null;
        PreparedStatement ps = null;

        try {
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            String action = request.getParameter("action");

            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Now using tutorial_booking table instead of bookings
            String sql = null;
            if ("confirm".equalsIgnoreCase(action)) {
                sql = "UPDATE tutorial_booking SET status = 'Confirmed' WHERE id = ?";
            } else if ("reject".equalsIgnoreCase(action)) {
                sql = "UPDATE tutorial_booking SET status = 'Rejected' WHERE id = ?";
            }

            if (sql != null) {
                ps = con.prepareStatement(sql);
                ps.setInt(1, bookingId);

                int result = ps.executeUpdate();
                if (result > 0) {
                    message = "Booking status updated successfully.";
                }
            }

            response.sendRedirect("confirm_bookings.jsp?message=" + java.net.URLEncoder.encode(message, "UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("confirm_bookings.jsp?message=" + java.net.URLEncoder.encode("An error occurred.", "UTF-8"));
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (con != null) con.close(); } catch (Exception ignored) {}
        }
    }
}
