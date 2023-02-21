import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public class Scan {
    private JPanel Main;
    private JTextField txtName;
    private JTextField txtSurname;
    private JTextField txtPesel;
    private JButton zapiszButton;
    private JTable table1;
    private JButton aktualizujButton;
    private JButton usunButton;
    private JButton szukajButton;
    private JTextField txtid;
    private JTextField txtImg;
    private JScrollPane table_1;
    private JButton skanujButton;
    private JButton wczytajSkanButton;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Scan");
        frame.setContentPane(new Scan().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    Connection con;
    PreparedStatement pst;

    public void connect()
    {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/skanbaza", "root","");
            System.out.println("Successs");
        }
        catch (ClassNotFoundException ex)
        {
            ex.printStackTrace();

        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }


    void table_load(){
        try
        {
            pst = con.prepareStatement("select * from skany");
            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }


    public Scan() {
        connect();
        table_load();
        zapiszButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String name,surname,pesel,img;
                name = txtName.getText();
                surname = txtSurname.getText();
                pesel = txtPesel.getText();
                img = txtImg.getText();

                try {
                    pst = con.prepareStatement("insert into skany(imie,nazwisko,pesel,skan)values(?,?,?,?)");
                    pst.setString(1, name);
                    pst.setString(2, surname);
                    pst.setString(3, pesel);
                    pst.setString(4, img);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Dodano.");
                    table_load();
                    txtName.setText("");
                    txtSurname.setText("");
                    txtPesel.setText("");
                    txtImg.setText("");
                    txtName.requestFocus();
                }

                catch (SQLException e1)
                {

                    e1.printStackTrace();
                }

            }


        });
        szukajButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    String skanid = txtid.getText();

                    pst = con.prepareStatement("select imie,nazwisko,pesel,skan from skany where id = ?");
                    pst.setString(1, skanid);
                    ResultSet rs = pst.executeQuery();

                    if(rs.next()==true)
                    {
                        String emimie = rs.getString(1);
                        String emnazwisko = rs.getString(2);
                        String empesel = rs.getString(3);
                        String emimg = rs.getString(4);

                        txtName.setText(emimie);
                        txtSurname.setText(emnazwisko);
                        txtPesel.setText(empesel);
                        txtImg.setText(emimg);

                    }
                    else
                    {
                        txtName.setText("");
                        txtSurname.setText("");
                        txtPesel.setText("");
                        txtImg.setText("");
                        JOptionPane.showMessageDialog(null,"Blędne ID");

                    }
                }
                catch (SQLException ex)
                {
                    ex.printStackTrace();
                }
            }




        });
        aktualizujButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String skanid, name,surname,pesel,img;

                name = txtName.getText();
                surname = txtSurname.getText();
                pesel = txtPesel.getText();
                img = txtImg.getText();
                skanid = txtid.getText();

                try {
                    pst = con.prepareStatement("update skany set imie = ?,nazwisko = ?,pesel = ?,skan = ? where id = ?");
                    pst.setString(1, name);
                    pst.setString(2, surname);
                    pst.setString(3, pesel);
                    pst.setString(4, img);
                    pst.setString(5, skanid);

                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Zaktualizowano.");
                    table_load();
                    txtName.setText("");
                    txtSurname.setText("");
                    txtPesel.setText("");
                    txtImg.setText("");
                    txtName.requestFocus();
                }

                catch (SQLException e1)
                {
                    e1.printStackTrace();
                }

            }
        });
        usunButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                String skanid;
                skanid = txtid.getText();

                try {
                    pst = con.prepareStatement("delete from skany where id = ?");

                    pst.setString(1, skanid);

                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Usunięto.");
                    table_load();
                    txtName.setText("");
                    txtSurname.setText("");
                    txtPesel.setText("");
                    txtImg.setText("");
                    txtName.requestFocus();
                }

                catch (SQLException e1)
                {

                    e1.printStackTrace();
                }
            }


        });
        skanujButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Runtime.getRuntime().exec("src/SDKSkaner/WorkedEx.exe");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }


            }
        });
        wczytajSkanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("src/SDKSkaner/DataBase/Bmp"));
                fileChooser.setDialogTitle("Wybierz plik");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.getName().toLowerCase().endsWith(".bnp") || file.isDirectory();
                    }

                    @Override
                    public String getDescription() {
                        return "Pliki BNP (*.bnp)";
                    }
                });
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println("Wybrany plik: " + selectedFile.getAbsolutePath());
                    txtImg.setText(selectedFile.getAbsolutePath());

                }

            }
        });
    }
}
