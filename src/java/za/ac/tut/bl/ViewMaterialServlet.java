package za.ac.tut.bl;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.Date;

@WebServlet("/ViewMaterialServlet.do")
public class ViewMaterialServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://172.20.10.3:3306/edusync_db";
    private static final String DB_USER = "kgj";
    private static final String DB_PASSWORD = "kgotso";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        if (idParam == null) {
            response.sendRedirect("uploadedmaterial.jsp?error=missingid");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);

            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

                String sql = "SELECT title, description, contenturl FROM academic_content WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setLong(1, id);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    ContentUpload content = new ContentUpload();
                    content.setTitle(rs.getString("title"));
                    content.setDescription(rs.getString("description"));
                    content.setFileName(rs.getString("contenturl")); 

                    // You could manually add uploadDate if you want to simulate it
                    content.setUploadDate(new Date());

                    request.setAttribute("material", content);
                    RequestDispatcher dispatcher = request.getRequestDispatcher("view_materials.jsp");
                    dispatcher.forward(request, response);
                } else {
                    response.sendRedirect("uploadedmaterial.jsp?error=notfound");
                }

                rs.close();
                ps.close();
            }

        } catch (NumberFormatException e) {
            response.sendRedirect("uploadedmaterial.jsp?error=invalidid");
        } catch (Exception e) {
            throw new ServletException("Error fetching material", e);
        }
    }
}
