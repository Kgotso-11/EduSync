package za.ac.tut.bl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import javax.servlet.ServletException;

@WebServlet("/GenerateReviewReportServlet.do")
public class GenerateReviewReportServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://172.20.10.3:3306/edusync_db";
    private static final String DB_USER = "kgj";
    private static final String DB_PASSWORD = "kgotso";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Set response headers
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=review_report.csv");

            PrintWriter out = response.getWriter();

            // Load JDBC driver (optional for newer MySQL drivers)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Open DB connection
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "SELECT tutor_name, rate, review_comment FROM review";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            // Write CSV header
            out.println("Tutor Name,Rating,Comment");

            // Check if data exists
            if (!rs.isBeforeFirst()) {
                System.out.println("No data found in review table");
                out.println("No data available");
            } else {
                // Write review data
                while (rs.next()) {
                    String tutor_name = rs.getString("tutor_name");
                    int rate = rs.getInt("rate");
                    String review_comment = rs.getString("review_comment");
                    String escapedComment = review_comment != null ? "\"" + review_comment.replace("\"", "\"\"") + "\"" : "";
                    String line = (tutor_name != null ? tutor_name : "") + "," + rate + "," + escapedComment;
                    System.out.println("Writing: " + line);
                    out.println(line);
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}