<%@page import="java.sql.DriverManager"%>
<%@page import="java.util.Calendar"%>
<%@page import="za.ac.tut.entities.AppUser"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Fetch Report </title>
        <style>
        body {
            font-family: Arial, sans-serif;
            background: #f5f5f5;
            padding: 20px;
        }
        table {
            border-collapse: collapse;
            width: 90%;
            margin: auto;
        }
        th, td {
            border: 1px solid #ccc;
            padding: 12px;
        }
        th {
            background: #4e54c8;
            color: white;
        }
        .export-buttons {
            text-align: center;
            margin: 20px;
        }
        .export-buttons a {
            margin: 0 10px;
            padding: 10px 15px;
            background-color: #4e54c8;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }
        </style>
    </head>
    <body>
        <%
    AppUser user = (AppUser) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    String url = "jdbc:mysql://172.20.10.3:3306/edusync_db";
    String dbUser = "kgj";
    String dbPass = "kgotso";
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    Calendar cal = Calendar.getInstance();
    int Day = cal.get(Calendar.DAY_OF_MONTH) + 1; // 0-based in Java
    int month = cal.get(Calendar.MONTH) + 1; // 0-based in Java
    int year = cal.get(Calendar.YEAR);

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(url, dbUser, dbPass);
        String sql = "SELECT booking_date, time_slot, reason FROM tutorial_booking WHERE userid = ? AND MONTH(booking_date) = ? AND YEAR(booking_date) = ?";
        ps = conn.prepareStatement(sql);
        ps.setLong(1, user.getUserId());
        ps.setInt(2, month);
        ps.setInt(3, year);
        rs = ps.executeQuery();
%>

<h2 style="text-align:center;">Your Consultations - <%= new java.text.DateFormatSymbols().getMonths()[month-1] + " " + year %></h2>

<table>
    <tr>
        <th>Date</th>
        <th>Time Slot</th>
        <th>Reason</th>
    </tr>
    <% while (rs.next()) { %>
        <tr>
            <td><%= rs.getDate("booking_date") %></td>
            <td><%= rs.getString("time_slot") %></td>
            <td><%= rs.getString("reason") %></td>
        </tr>
    <% } %>
</table>

<div class="export-buttons">
    <a href="ExportReportServlet?format=csv">Export to CSV</a>
</div>

<%
    } catch(Exception e) {
        out.println("<p style='color:red;text-align:center;'>Error fetching report.</p>");
        e.printStackTrace();
    } finally {
        if (rs != null) rs.close();
        if (ps != null) ps.close();
        if (conn != null) conn.close();
    }
%>
    </body>
</html>
