package za.ac.tut.bl;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.sql.*;

@WebServlet("/UploadContentServlet.do")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1MB
    maxFileSize = 1024 * 1024 * 5,        // 5MB
    maxRequestSize = 1024 * 1024 * 6      // 6MB
)
public class UploadContentServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://172.20.10.3:3306/edusync_db";
    private static final String DB_USER = "kgj";
    private static final String DB_PASSWORD = "kgotso";
    private static final String SAVE_DIR = "uploaded_files";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String subject = request.getParameter("subject");
        Part filePart = request.getPart("file");
        String orderBy = "";
        String uploadDateFilter = request.getParameter("upload_date");

        HttpSession session = request.getSession(false);
        Long userId = (session != null) ? (Long) session.getAttribute("userId") : null;

        if (userId == null) {
            response.sendRedirect("upload_content.jsp?message=User+not+logged+in");
            return;
        }

        if (filePart == null || filePart.getSize() == 0) {
            response.sendRedirect("upload_content.jsp?message=No+file+selected");
            return;
        }

        if (filePart.getSize() > 5 * 1024 * 1024) {
            response.sendRedirect("upload_content.jsp?message=File+too+large.+Max+5MB");
            return;
        }

        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String appPath = request.getServletContext().getRealPath("");
        String savePath = appPath + File.separator + SAVE_DIR;

        File fileSaveDir = new File(savePath);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdirs();
        }

        String absolutePath = savePath + File.separator + fileName;
        String relativePath = SAVE_DIR + "/" + fileName;

        try (InputStream fileContent = filePart.getInputStream()) {
            Files.copy(fileContent, Paths.get(absolutePath), StandardCopyOption.REPLACE_EXISTING);
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "INSERT INTO academic_content (userid, title, description, subject, contenturl, file_name, upload_date) VALUES (?, ?, ?, ?, ?, ?, NOW())";
                try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, userId);
                    ps.setString(2, title);
                    ps.setString(3, description);
                    ps.setString(4, subject);
                    ps.setString(5, relativePath);
                    ps.setString(6, fileName);

                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        ResultSet rs = ps.getGeneratedKeys();
                        if (rs.next()) {
                            response.sendRedirect("upload_content.jsp?message=Content+uploaded+successfully");
                        } else {
                            response.sendRedirect("upload_content.jsp?message=Content+uploaded,+but+ID+missing");
                        }
                    } else {
                        response.sendRedirect("upload_content.jsp?message=Failed+to+upload+content");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("upload_content.jsp?message=An+error+occurred");
        }
                
        if ("recent".equals(uploadDateFilter)) {
            orderBy = " ORDER BY upload_date DESC";
        } else if ("oldest".equals(uploadDateFilter)) {
            orderBy = " ORDER BY upload_date ASC";
        }
        
        String sql = "SELECT * FROM academic_content WHERE title LIKE ? OR description LIKE ? " + orderBy;
    }
}
