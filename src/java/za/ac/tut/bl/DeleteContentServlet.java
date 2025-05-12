import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.nio.file.*;

@WebServlet("/DeleteContentServlet.do")
public class DeleteContentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get contentId and filePath parameters from the form
        String contentId = request.getParameter("contentId");
        String filePath = request.getParameter("filePath");

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            // Load database driver and establish connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://172.20.10.3:3306/edusync_db", "kgj", "kgotso");

            // Delete the file from the server
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);

            // Also delete the record from the database
            String deleteQuery = "DELETE FROM academic_content WHERE contentid = ?";
            ps = conn.prepareStatement(deleteQuery);
            ps.setInt(1, Integer.parseInt(contentId));
            ps.executeUpdate();

            // Redirect back to the uploaded content page with success message
            response.sendRedirect("upload_content.jsp?message=File+deleted+successfully");
        } catch (Exception e) {
            // Handle error and show failure message
            response.sendRedirect("upload_content.jsp?message=Error+deleting+file");
            e.printStackTrace();
        } finally {
            
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
