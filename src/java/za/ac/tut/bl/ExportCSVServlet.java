/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.ac.tut.bl;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

/**
 *
 * @author jonas
 */
@WebServlet(name = "ExportCSVServlet", urlPatterns = {"/ExportCSVServlet.do"})
public class ExportCSVServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://172.20.10.3:3306/edusync_db";
    private static final String DB_USER = "kgj";
    private static final String DB_PASSWORD = "kgotso";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String filterDate = request.getParameter("filterDate");

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=consultation_history.csv");

        try (PrintWriter writer = response.getWriter()) {
            writer.println("Date,Student,Topic");

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "SELECT booking_date, student_name, reason FROM tutorial_booking";
            if (filterDate != null && !filterDate.isEmpty()) {
                sql += " WHERE booking_date = ?";
            }

            PreparedStatement ps = conn.prepareStatement(sql);
            if (filterDate != null && !filterDate.isEmpty()) {
                ps.setString(1, filterDate);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String date = rs.getString("booking_date");
                String student = rs.getString("student_name").replaceAll(",", " ");
                String topic = rs.getString("reason").replaceAll(",", " ");
                writer.printf("%s,%s,%s%n", date, student, topic);
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
