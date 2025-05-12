package za.ac.tut.bl;

import java.io.IOException;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import za.ac.tut.entities.AppUser;

@WebServlet("/ReviewServlet.do")
public class ReviewServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://172.20.10.3:3306/edusync_db";
    private static final String DB_USER = "kgj";
    private static final String DB_PASSWORD = "kgotso";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        AppUser user = (AppUser) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String tutor_name = request.getParameter("tutor_name");
        int rate = Integer.parseInt(request.getParameter("rate"));
        String review_comment = request.getParameter("review_comment");
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            if (rate < 1 || rate > 5 || tutor_name == null || tutor_name.trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid rating or tutor name.");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Invalid input provided. Please try again.");
            request.getRequestDispatcher("review.jsp").forward(request, response);
            return;
        }
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "INSERT INTO review (userid, tutor_name, rate, review_comment) VALUES (?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, user.getUserId());
            ps.setString(2, tutor_name);
            ps.setInt(3, rate);
            ps.setString(4, review_comment);
            
            ps.executeUpdate();
            
            request.setAttribute("message", "Review submitted successfully.");
            request.getRequestDispatcher("students_dashboard.jsp").forward(request, response);

            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        response.sendRedirect("students_dashboard.jsp");
    }
}
