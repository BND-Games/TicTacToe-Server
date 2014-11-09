package bnd.TicTacToeServer;

import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class engine {

    private static mysql sql;
    private static Scanner in;
    private static PrintWriter out;

    public static void main(String args[]) {
        try {
            //MySql verbindung aufbauen
            sql = new mysql();
            sql.verbinden();

            //ServerSocket mit port 8090 erstellen
            ServerSocket server = new ServerSocket(1122);
            while (true) {
                //Variable deklarieren
                Socket client = null;
                try {
                    //Auf client warten
                    client = server.accept();
                    handleConnection(client);
                } catch (IOException e) {
                    System.out.println("Fehler: " + e);
                    server.close();
                    return;
                    
                }
                server.close();

            }
        } catch (IOException ex) {
            Logger.getLogger(engine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void handleConnection(Socket client) throws IOException {
        //Variable factor1 als String deklarieren
        String factor1 = null;

        in = new Scanner(client.getInputStream());
        try {
            //Das gesendete vom Client in die Variable factor1 reinpacken
            factor1 = in.nextLine();
        } catch (Exception e) {
            System.out.println("Fehler: " + e);
        }

        try {
            //Printweiter um an den Client schicken zu können
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException ex) {
            System.out.println("Fehler: " + ex);
        }

        //Variable factorInt deklarieren
        int factorInt = 0;
        try {

            //factor1 in Integer umwalndeln
            factorInt = Integer.parseInt(factor1);
        } catch (NumberFormatException numberFormatException) {
            System.out.println("Fehler: " + numberFormatException);
        }

        //Methode auswerten aufrufen und die Variablen factorIntt und Client mitgeben
        auswerten(factorInt, client);
    }

    public static void auswerten(int methodeINT, Socket client) throws IOException {

        //Abfragen ob Matrix 1 angefordert wurde
        switch (methodeINT) {
            case 0:
                version_pruefen_java(client);
                break;
            case 1:
                Spieler_Registrieren(client);
                break;
            case 2:
                Spieler_Einloggen(client);
                break;
            case 10:
                einladungen_laden(client);
                break;
            case 11:
                einladungen_annehmen(client);
                break;
            case 12:
                einladungen_ablehnen(client);
                break;
            case 20:
                Spieler_Herausfordern(client);
                break;
            case 30:
                spiel_laden_herausforderer(client);
                break;
            case 31:
                spiel_laden_mitspieler(client);
                break;
            case 32:
                spieler_amZug(client);
                break;
            case 33:
                kaestchen_gesetzt_pruefen(client);
                break;
            case 34:
                kaestchen_setzen(client);
                break;
            case 35:
                kaestchen_aktualesieren(client);
                break;
            case 36:
                gewinner_eintragen(client);
                break;
           case 40:
                version_pruefen_csharp(client);
                break;     

        }

    }

    public static void version_pruefen_java(Socket client) {

        //Aktuelle Version
        String version = in.nextLine();

        //Select Befehl
        String SelectString = "Select * from version Where Programm = '" + "tictactoe_java" + "'";
        try {
            sql.rs = sql.s.executeQuery(SelectString);
        } catch (SQLException ex) {
            System.out.println("Fehler: " + ex);
        }
        try {
            if (sql.rs.next()) {

                //Version überprüfen
                String version_db = sql.rs.getString("version");
                if (version_db.equals(version)) {
                    out.println("erfolgreich");
                } else {
                    out.println("fehlgeschlagen");
                }
            }
        } catch (SQLException ex) {
            System.out.println("Fehler: " + ex);
        }
    }
    
    public static void version_pruefen_csharp(Socket client) {

        //Aktuelle Version
        String version = in.nextLine();

        //Select Befehl
        String SelectString = "Select * from version Where Programm = '" + "tictactoe_csharp" + "'";
        try {
            sql.rs = sql.s.executeQuery(SelectString);
        } catch (SQLException ex) {
            System.out.println("Fehler: " + ex);
        }
        try {
            if (sql.rs.next()) {

                //Version überprüfen
                String version_db = sql.rs.getString("version");
                if (version_db.equals(version)) {
                    out.println("erfolgreich");
                } else {
                    out.println("fehlgeschlagen");
                }
            }
        } catch (SQLException ex) {
            System.out.println("Fehler: " + ex);
        }
    }

    public static void Spieler_Registrieren(Socket client) {

        //Werte vom Client annehmen
        String Name = in.nextLine();
        String Passwort = in.nextLine();
        String Email = in.nextLine();

        try {

            //Select String
            String SelectString = "Select * from account Where Name = '" + Name + "'";

            //Select String ausführen
            sql.rs = sql.s.executeQuery(SelectString);
            if (!sql.rs.next()) {

                //Passwort verschlüsseln
                String passwort = md5Java(Passwort);

                //Insert string
                String insertString = "Insert into account (Name, Passwort, Email) Values ('"
                        + Name + "','"
                        + passwort + "','"
                        + Email + "');";

                //String ausführen
                sql.s.executeUpdate(insertString);
                out.println("erfolgreich");

            } else {
                out.println("fehlgeschlagen");
            }

        } catch (SQLException ex) {
            System.out.println("Fehler: " + ex);
        }

    }

    public static void Spieler_Einloggen(Socket client) {

        String Name = in.nextLine();
        String Passwort = in.nextLine();

        try {

            //Passwort verschlüsseln
            String passwort = md5Java(Passwort);

            //Select String
            String SelectString = "Select * from account Where Name = '" + Name + "' AND Passwort = '" + passwort + "'";

            //String ausführen
            sql.rs = sql.s.executeQuery(SelectString);

            //Ergebnis an Client schicken
            if (sql.rs.next()) {

                out.println("erfolgreich");
            } else {
                out.println("fehlgeschlagen");
            }

        } catch (SQLException ex) {
            System.out.println("Fehler: " + ex);
        }

    }

    //Md5 Methode
    public static String md5Java(String message) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(message.getBytes("UTF-8"));

            //converting byte array to Hexadecimal String
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }

            digest = sb.toString();
        } catch (UnsupportedEncodingException ex) {

        } catch (NoSuchAlgorithmException ex) {

        }
        return digest;
    }

    public static void einladungen_laden(Socket client) {

        //Werte vom Client empfangen
        String Name = in.nextLine();

        try {

            //Select String
            String SelectString = "SELECT Herausforderer FROM spiel Where Mitspieler = '" + Name + "' AND Status = '" + "ausstehend" + "'";
            sql.rs = sql.s.executeQuery(SelectString);

            //Ergebnis an Server schicken
            while (sql.rs.next()) {
                out.println(sql.rs.getString("Herausforderer"));
            }
            out.println("");

        } catch (Exception ex) {
            System.out.println("Fehler " + ex);
        }
    }

    public static void einladungen_annehmen(Socket client) {

        //Werte vom Client empfangen
        String Mitspieler = in.nextLine();
        String Herausforderer = in.nextLine();

        try {
            //Update ausführen
            sql.s.executeUpdate("Update spiel Set Status = '" + "angenommen" + "' Where Herausforderer = '" + Herausforderer + "' AND Mitspieler = '" + Mitspieler + "'");
        } catch (SQLException ex) {
            System.out.println("Fehler " + ex);
        }
    }

    public static void einladungen_ablehnen(Socket client) {

        //Werte vom Client empfangen
        String Mitspieler = in.nextLine();
        String Herausforderer = in.nextLine();

        try {
            //Update ausführen
            sql.s.executeUpdate("Update spiel Set Status = '" + "abgelehnt" + "' Where Herausforderer = '" + Herausforderer + "' AND Mitspieler = '" + Mitspieler + "'");
        } catch (SQLException ex) {
            System.out.println("Fehler " + ex);
        }
    }

    private static void Spieler_Herausfordern(Socket client) {

        //Werte vom Client empfangen
        String Angemeldeter_Name = in.nextLine();
        String Herausgeforderter_Spieler = in.nextLine();

        //Zeitstempel
        Timestamp timestamp = new Timestamp(new Date().getTime());

        //Select String
        String SelectString = "Select * from account Where Name = '" + Herausgeforderter_Spieler + "'";
        try {

            //String ausführen
            sql.rs = sql.s.executeQuery(SelectString);

            //Überprüfen ob Spieler exestiert
            if (sql.rs.next()) {

                //Select String
                SelectString = "Select * from spiel Where Mitspieler = '" + Herausgeforderter_Spieler + "' AND Herausforderer = '" + Angemeldeter_Name + "' AND ( Status = '" + "angenommen" + "' OR Status = '" + "ausstehend" + "')";

                //Select ausführen
                sql.rs = sql.s.executeQuery(SelectString);

                //Überprüfen ob Spiel exestiert
                if (!sql.rs.next()) {

                    //Select String
                    SelectString = "Select * from spiel Where Herausforderer = '" + Herausgeforderter_Spieler + "' AND Mitspieler = '" + Angemeldeter_Name + "' AND ( Status = '" + "angenommen" + "' OR Status = '" + "ausstehend" + "')";

                    //Select ausführen
                    sql.rs = sql.s.executeQuery(SelectString);

                    //Überprüfen ob Spiel exestiert
                    if (!sql.rs.next()) {

                        //Insert into befehl
                        String insertString = "Insert into spiel (Herausforderer, Mitspieler, Beginn, Status, Ende) Values ('"
                                + Angemeldeter_Name + "','"
                                + Herausgeforderter_Spieler + "','"
                                + timestamp + "','"
                                + "ausstehend" + "', '"
                                + "0000-00-00 00:00:00" + "');";

                        out.println("Erfolg");

                        try {

                            //Spiel eintragen
                            sql.s.executeUpdate(insertString);
                        } catch (SQLException ex) {
                            System.out.println("Fehler: " + ex);
                        }
                    } else {
                        out.println("Vorhanden");
                    }
                } else {
                    out.println("Vorhanden");
                }
            } else {
                out.println("Exestiert_Nicht");
            }
        } catch (SQLException ex) {
            System.out.println("Fehler: " + ex);
        }

    }

    public static void spiel_laden_herausforderer(Socket client) {

        try {

            String Angemeldeter_Name = in.nextLine();

            String SelectString = "SELECT * FROM spiel Where Herausforderer = '" + Angemeldeter_Name + "' AND Status = '" + "angenommen" + "'";
            sql.rs = sql.s.executeQuery(SelectString);

            while (sql.rs.next()) {

                out.println(sql.rs.getString("Mitspieler"));
            }

            out.println("");

        } catch (SQLException ex) {
            System.out.println("Fehler " + ex);
        }
    }

    public static void spiel_laden_mitspieler(Socket client) {

        try {

            String Angemeldeter_Name = in.nextLine();

            String SelectString = "SELECT * FROM spiel Where Mitspieler = '" + Angemeldeter_Name + "' AND Status = '" + "angenommen" + "'";
            sql.rs = sql.s.executeQuery(SelectString);

            while (sql.rs.next()) {
                out.println(sql.rs.getString("Herausforderer"));
            }

            out.println("");

        } catch (SQLException ex) {
            System.out.println("Fehler " + ex);
        }
    }

    public static void spieler_amZug(Socket client) {
        String Angemeldeter_Name = in.nextLine();
        String Mitspieler = in.nextLine();
        String amZug = null;

        String Herausgeforderter = null;
        int spielID = 0;

        try {
            String SelectString = "SELECT * FROM spiel Where Mitspieler = '" + Angemeldeter_Name + "' AND Herausforderer = '" + Mitspieler + "'";
            sql.rs = sql.s.executeQuery(SelectString);

            if (sql.rs.next()) {
                spielID = sql.rs.getInt("ID");
                Herausgeforderter = sql.rs.getString("Herausforderer");

            } else {
                SelectString = "SELECT * FROM spiel Where Herausforderer = '" + Angemeldeter_Name + "' AND Mitspieler = '" + Mitspieler + "'";
                sql.rs = sql.s.executeQuery(SelectString);

                if (sql.rs.next()) {
                    spielID = sql.rs.getInt("ID");
                    Herausgeforderter = sql.rs.getString("Herausforderer");
                }
            }

            SelectString = "SELECT * FROM kreuz Where SpielID = '" + spielID + "' ORDER BY Zeit DESC Limit 1";
            sql.rs = sql.s.executeQuery(SelectString);

            if (sql.rs.next()) {
                amZug = sql.rs.getString("Spieler");
                spieler_reihe_check(Angemeldeter_Name, amZug, spielID, client);
            } else {
                spieler_reihe_check(Angemeldeter_Name, Herausgeforderter, spielID, client);
            }

        } catch (SQLException ex) {
            System.out.println("Fehler " + ex);
        }

    }

    public static void spieler_reihe_check(String Spieler, String amZug, int spielID, Socket client) {

        if (Spieler.toString().equals(amZug.toString())) {
            out.println("ER");
        } else {
            out.println("ICH");

        }
    }

    public static void kaestchen_gesetzt_pruefen(Socket client) {
        try {

            String kaestchen = in.nextLine();
            String Spieler = in.nextLine();
            String Gegner = in.nextLine();

            int spielID = spielID_finden(Spieler, Gegner);

            String SelectString = "SELECT * FROM kreuz Where SpielID = '" + spielID + "' AND Kaestchen = '" + kaestchen + "'";
            sql.rs = sql.s.executeQuery(SelectString);
            if (sql.rs.next()) {
                out.println("BESETZT");
            } else {
                out.println("LEER");
            }
        } catch (SQLException ex) {
            System.out.println("Fehler " + ex);
        }
    }

    public static void kaestchen_setzen(Socket client) {
        try {

            Timestamp timestamp = new Timestamp(new Date().getTime());
            String kaestchen = in.nextLine();
            String Spieler = in.nextLine();
            String Gegner = in.nextLine();

            int spielID = spielID_finden(Spieler, Gegner);

            String InsertString = "Insert into kreuz (SpielID, Kaestchen, Zeit, Spieler) Values ('" + spielID + "', '" + kaestchen + "', '" + timestamp + "', '" + Spieler + "');";
            sql.s.executeUpdate(InsertString);

            out.println("Eingetragen");

        } catch (SQLException ex) {
            System.out.println("Fehler " + ex);
        }
    }

    private static void kaestchen_aktualesieren(Socket client) {
        try {

            String Spieler = in.nextLine();
            String Gegner = in.nextLine();

            int spielID = spielID_finden(Spieler, Gegner);

            String SelectString = "SELECT * FROM kreuz Where SpielID = '" + spielID + "'";
            sql.rs = sql.s.executeQuery(SelectString);
            while (sql.rs.next()) {

                out.println(sql.rs.getInt("Kaestchen"));
                out.println(sql.rs.getString("Spieler"));
            }
            out.println("");

        } catch (SQLException ex) {
            System.out.println("Fehler " + ex);
        }
    }

    private static int spielID_finden(String Spieler, String Gegner) {
        int spielID = 0;
        try {

            String SelectString = "SELECT * FROM spiel Where Herausforderer = '" + Spieler + "' AND Mitspieler = '" + Gegner + "'";
            sql.rs = sql.s.executeQuery(SelectString);

            if (sql.rs.next()) {
                spielID = sql.rs.getInt("ID");
            } else {
                SelectString = "SELECT * FROM spiel Where Herausforderer = '" + Gegner + "' AND Mitspieler = '" + Spieler + "'";
                sql.rs = sql.s.executeQuery(SelectString);

                if (sql.rs.next()) {
                    spielID = sql.rs.getInt("ID");
                }
            }

        } catch (SQLException ex) {
            System.out.println("Fehler " + ex);
        }
        return spielID;
    }

    public static void gewinner_eintragen(Socket client) {

        String Spieler = in.nextLine();
        String Gegner = in.nextLine();

        try {

            String SelectString = "SELECT * FROM spiel Where Herausforderer = '" + Spieler + "' AND Mitspieler = '" + Gegner + "'";
            sql.rs = sql.s.executeQuery(SelectString);

            if (sql.rs.next()) {

                //Update String ausführen
                sql.s.executeUpdate("Update spiel Set Status = '" + "abgeschlossen" + "' Where Herausforderer = '" + Spieler + "' AND Mitspieler = '" + Gegner + "'");
                sql.s.executeUpdate("Update spiel Set Gewinner = '" + Spieler + "' Where Herausforderer = '" + Spieler + "' AND Mitspieler = '" + Gegner + "'");

            } else {
                SelectString = "SELECT * FROM spiel Where Herausforderer = '" + Gegner + "' AND Mitspieler = '" + Spieler + "'";
                sql.rs = sql.s.executeQuery(SelectString);

                if (sql.rs.next()) {
                    
                    //Update String ausführen
                    sql.s.executeUpdate("Update spiel Set Status = '" + "abgeschlossen" + "' Where Herausforderer = '" + Gegner + "' AND Mitspieler = '" + Spieler + "'");
                    sql.s.executeUpdate("Update spiel Set Gewinner = '" + Spieler + "' Where Herausforderer = '" + Gegner + "' AND Mitspieler = '" + Spieler + "'");
                }
            }

        } catch (SQLException ex) {
            System.out.println("Fehler: " + ex);
        }

    }
}
