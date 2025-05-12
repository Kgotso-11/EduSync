package za.ac.tut.bl;

import za.ac.tut.bl.ContentUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/UploadedMaterial.do")
public class UploadedMaterial extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://172.20.10.3:3306/edusync_db";
    private static final String DB_USER = "kgj";
    private static final String DB_PASSWORD = "kgotso";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<ContentUpload> materials = new ArrayList<>();
        String search = request.getParameter("search");
        String subject = request.getParameter("subject");

        String sql = "SELECT * FROM  WHERE 1=1";
        List<String> conditions = new ArrayList<>();
        List<String> params = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            sql += " AND LOWER(title) LIKE ?";
            params.add("%" + search.toLowerCase() + "%");
        }

        if (subject != null && !subject.trim().isEmpty()) {
            sql += " AND LOWER(subject) = ?";
            params.add(subject.toLowerCase());
        }

        sql += " ORDER BY upload_date DESC";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                for (int i = 0; i < params.size(); i++) {
                    ps.setString(i + 1, params.get(i));
                }

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    ContentUpload content = new ContentUpload();
                    content.setTitle(rs.getString("title"));
                    content.setDescription(rs.getString("description"));
                    content.setSubject(rs.getString("subject"));
                    content.setUploadDate(rs.getTimestamp("upload_date"));
                    content.setFileName(rs.getString("file_name"));
                    materials.add(content);
                }
            }
        } catch (Exception e) {
            throw new ServletException("Failed to retrieve materials", e);
        }

        request.setAttribute("materials", materials);
        request.getRequestDispatcher("uploadedmaterial.jsp").forward(request, response);
    }
}
