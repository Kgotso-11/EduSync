/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.ac.tut.bl;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/DownloadUploadServlet.do")
public class DownloadUploadServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"uploaded_materials_report.csv\"");

        try (PrintWriter writer = response.getWriter();
            Connection conn = DriverManager.getConnection("jdbc:mysql://172.20.10.3:3306/edusync_db","kgj", "kgotso");
             PreparedStatement stmt = conn.prepareStatement("SELECT userid, title, contenturl, description, subject, upload_date, file_name FROM academic_content");
             ResultSet rs = stmt.executeQuery()) {

            // Write CSV header
            writer.println("userid,title,content_url,description,subject,upload_date,file_name");

            // Write rows
            while (rs.next()) {
                writer.printf("\"%d\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                    rs.getInt("userid"),
                    rs.getString("title"),
                    rs.getString("contenturl"),
                    rs.getString("description"),
                    rs.getString("subject"),
                    rs.getDate("upload_date").toString(),
                    rs.getString("file_name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

