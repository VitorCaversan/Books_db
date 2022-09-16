/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Browsing;
import java.sql.*;
import java.io.*;

/**
 *
 * @author joao
 */
public class BookBrowser {
    
    public static void main(String args[]) throws Exception
    {
        // Load MySQL driver
        // Can be fully removed when using MySQL. Must only be used when
        // multiple connections are beeing set.
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Start server connection
        String url      = "jdbc:mysql://localhost/livros_db";
        String user     = "root";
        String password = "Mysql-12";
        Connection con = DriverManager.getConnection(url, user, password);

        // Create a statement: an execution line
        Statement start = con.createStatement();
        PreparedStatement prepStm = con.prepareStatement("update livros set capa = ? where livro_id = ?");  

        ResultSet res_set = start.executeQuery("select livro_id from livros");

        while (res_set.next())
        {
            System.out.println(res_set.getInt(1));

            //Loading BLOB
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            FileInputStream file;
            file = new FileInputStream("/home/joao/Documents/FACULDADE/6_PERIODO/BancoDeDados/trabalhos/Livros_db/livros/"+res_set.getInt(1)+".jpg");
            int readByte = file.read();
            while(readByte != -1){
                buffer.write(readByte);
                readByte = file.read();
            }
            
            prepStm.setInt(2, res_set.getInt(1));
            prepStm.setBytes(1, buffer.toByteArray());
            prepStm.executeUpdate();          
        }   
            
        
    }
    
}
