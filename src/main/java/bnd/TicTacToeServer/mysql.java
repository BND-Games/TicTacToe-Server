package bnd.TicTacToeServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class mysql {

    //Variablen
    Connection conn = null;
    Statement s = null;
    ResultSet rs = null;
    ResultSetMetaData md = null;
    int columns = 0;

    mysql() {

    }

    //Verbindung mit der MySQL verbinden
    public void verbinden() {
        try {
            String userName = "root";
            String password = "realschule";
            String url = "jdbc:mysql://localhost/tictactoe";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            this.conn = DriverManager.getConnection(url, userName, password);
            this.s = this.conn.createStatement();
            System.out.println("Datanbank erfolgreich hergestellt \n");
        } catch (Exception e) {
            System.out.println("Cannot connect to database server:\n" + e);
            System.exit(0);
            return;
        }
    }

    //Verbindung trennen
    public void abbrechen() {

        if (conn != null) {
            try {
                conn.close();
                System.out.println("Database connection terminated \n");
            } catch (Exception e) { /* ignore close errors */ }
        }
    }
}