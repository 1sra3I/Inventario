/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package mx.unison.pagina;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author rober
 */
public class PanelLogin extends javax.swing.JPanel {

    private Connection conn;
    private PaginaPrincipal ventanaPrincipal;

    /**
     * Creates new form PanelLogin
     */
    public PanelLogin(PaginaPrincipal ventana) {
        this.ventanaPrincipal = ventana;
        initComponents();
        centrado();
        conectarBD();
        configurarEventos();
        aplicarBordesRedondeados();
    }

    private void aplicarBordesRedondeados() {
        javax.swing.border.Border borde8px = new javax.swing.border.AbstractBorder() {
            private final int radius = 8;
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
                g.setColor(Color.GRAY);
                g.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            }
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(6, 6, 6, 6); 
            }
            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        };
        nombre.setBorder(borde8px);
        contraseña.setBorder(borde8px);
        ingresar.setBorder(borde8px);
    }
    
    

    private void centrado() {
        jPanel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Panel superior (jPanel2) - logos
        gbc.gridy = 0;
        gbc.weighty = 0;
        jPanel1.add(jPanel2, gbc);

        // Panel central con formulario de login
        javax.swing.JPanel panelCentral = new javax.swing.JPanel();
        panelCentral.setBackground(new java.awt.Color(255, 255, 255));
        panelCentral.setLayout(new GridBagLayout());
        GridBagConstraints gbcCentral = new GridBagConstraints();
        gbcCentral.gridx = 0;
        gbcCentral.insets = new Insets(10, 0, 10, 0);

        // Título
        gbcCentral.gridy = 0;
        gbcCentral.anchor = GridBagConstraints.CENTER;
        panelCentral.add(Label1, gbcCentral);

        // Panel de formulario con imagen
        javax.swing.JPanel formPanel = new javax.swing.JPanel(new GridBagLayout());
        formPanel.setBackground(new java.awt.Color(255, 255, 255));
        GridBagConstraints gbcForm = new GridBagConstraints();
        gbcForm.insets = new Insets(10, 10, 10, 10);

        // Columna izquierda - Formulario
        javax.swing.JPanel loginFields = new javax.swing.JPanel(new GridBagLayout());
        loginFields.setBackground(new java.awt.Color(255, 255, 255));
        GridBagConstraints gbcFields = new GridBagConstraints();
        gbcFields.insets = new Insets(5, 5, 5, 5);
        gbcFields.anchor = GridBagConstraints.WEST;

        // Usuario
        gbcFields.gridx = 0;
        gbcFields.gridy = 0;
        loginFields.add(Label2, gbcFields);
        gbcFields.gridx = 1;
        nombre.setPreferredSize(new java.awt.Dimension(200, 25));
        loginFields.add(nombre, gbcFields);

        // Contraseña
        gbcFields.gridx = 0;
        gbcFields.gridy = 1;
        loginFields.add(Label3, gbcFields);
        gbcFields.gridx = 1;
        contraseña.setPreferredSize(new java.awt.Dimension(200, 25));
        loginFields.add(contraseña, gbcFields);

        // Botón
        gbcFields.gridx = 0;
        gbcFields.gridy = 2;
        gbcFields.gridwidth = 2;
        gbcFields.anchor = GridBagConstraints.CENTER;
        ingresar.setPreferredSize(new java.awt.Dimension(140, 30));
        loginFields.add(ingresar, gbcFields);

        gbcForm.gridx = 0;
        gbcForm.gridy = 0;
        formPanel.add(loginFields, gbcForm);

        // Columna derecha - Imagen
        gbcForm.gridx = 1;
        gbcForm.gridy = 0;
        formPanel.add(jLabel1, gbcForm);

        gbcCentral.gridy = 1;
        panelCentral.add(formPanel, gbcCentral);

        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        jPanel1.add(panelCentral, gbc);

        // Panel inferior (jPanel3) - banner centrado
        jPanel3.setLayout(new GridBagLayout());
        GridBagConstraints gbcPanel3 = new GridBagConstraints();
        gbcPanel3.gridx = 0;
        gbcPanel3.weightx = 1.0;
        gbcPanel3.fill = GridBagConstraints.HORIZONTAL;

        // Panel amarillo arriba
        gbcPanel3.gridy = 0;
        gbcPanel3.weighty = 0;
        jPanel8.setPreferredSize(new java.awt.Dimension(800, 36));
        jPanel3.add(jPanel8, gbcPanel3);

        // Panel para centrar el banner
        javax.swing.JPanel panelBannerCentrado = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
        panelBannerCentrado.setBackground(new java.awt.Color(1, 80, 155));
        panelBannerCentrado.add(jLabel8);

        gbcPanel3.gridy = 1;
        gbcPanel3.weighty = 1.0;
        gbcPanel3.fill = GridBagConstraints.BOTH;
        jPanel3.add(panelBannerCentrado, gbcPanel3);

        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jPanel1.add(jPanel3, gbc);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        ingresar = new javax.swing.JButton();
        Label1 = new javax.swing.JLabel();
        Label2 = new javax.swing.JLabel();
        Label3 = new javax.swing.JLabel();
        nombre = new javax.swing.JTextField();
        contraseña = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 0, 204));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        ingresar.setBackground(new java.awt.Color(0, 82, 158));
        ingresar.setForeground(new java.awt.Color(255, 255, 255));
        ingresar.setText("Iniciar Sesión");

        Label1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        Label1.setForeground(new java.awt.Color(0, 0, 0));
        Label1.setText("SISTEMA DE INVENTARIO");

        Label2.setBackground(new java.awt.Color(204, 255, 255));
        Label2.setForeground(new java.awt.Color(0, 0, 0));
        Label2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Label2.setText("Usuario");

        Label3.setBackground(new java.awt.Color(204, 255, 255));
        Label3.setForeground(new java.awt.Color(0, 0, 0));
        Label3.setText("Contraseña");

        nombre.setBackground(new java.awt.Color(204, 204, 255));

        contraseña.setBackground(new java.awt.Color(204, 204, 255));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imalog.jpeg"))); // NOI18N

        jPanel2.setBackground(new java.awt.Color(248, 187, 0));

        jPanel4.setBackground(new java.awt.Color(1, 80, 155));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenuni.png"))); // NOI18N

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/unisonv.jpg"))); // NOI18N

        jTextField7.setEditable(false);
        jTextField7.setBackground(new java.awt.Color(0, 82, 158));
        jTextField7.setForeground(new java.awt.Color(255, 255, 255));
        jTextField7.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField7.setText("Blvd. Luis Encinas J, Calle Av. Rosales &, Centro, 83000 Hermosillo, Son.");
        jTextField7.setBorder(null);
        jTextField7.setFocusable(false);

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ubicacionesv.png"))); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 22, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3.setBackground(new java.awt.Color(1, 80, 155));

        jPanel8.setBackground(new java.awt.Color(248, 187, 0));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 873, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 36, Short.MAX_VALUE)
        );

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenban.png"))); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 686, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(78, 78, 78))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Label3)
                    .addComponent(Label2, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(contraseña, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ingresar, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(46, 46, 46))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(251, 251, 251)
                .addComponent(Label1, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Label1)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 76, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Label2))
                        .addGap(43, 43, 43)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(contraseña, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Label3))
                        .addGap(50, 50, 50)
                        .addComponent(ingresar)
                        .addGap(31, 31, 31)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void conectarBD() {
        try {
        conn = DatabaseManager.getConnection();
        crearTablaUsuarios();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
                "Error al conectar con la BD: " + e.getMessage());
            }
    }

    private void crearTablaUsuarios() {
        try {
            String sqlCrearTabla = """
                                CREATE TABLE IF NOT EXISTS usuarios (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    nombre TEXT NOT NULL UNIQUE,
                                    contrasena TEXT NOT NULL,
                                    inicio_sesion TEXT,
                                    rol TEXT NOT NULL DEFAULT 'ADMIN'
                                );
                                """;

            PreparedStatement ps = conn.prepareStatement(sqlCrearTabla);
            ps.execute();

            String sqlCount = "SELECT COUNT(*) as total FROM usuarios";
            PreparedStatement psCount = conn.prepareStatement(sqlCount);
            ResultSet rs = psCount.executeQuery();

            if (rs.next() && rs.getInt("total") == 0) {
                String sqlInsert = "INSERT INTO usuarios (nombre, contrasena, inicio_sesion, rol) VALUES (?, ?, ?, ?)";
                PreparedStatement psInsert = conn.prepareStatement(sqlInsert);

                psInsert.setString(1, "Admin");
                psInsert.setString(2, Seguridad.hashPassword("admin23"));
                psInsert.setString(3, "");
                psInsert.setString(4, "ADMIN");
                psInsert.executeUpdate();

                psInsert.setString(1, "almacen");
                psInsert.setString(2, Seguridad.hashPassword("almacenll"));
                psInsert.setString(3, "");
                psInsert.setString(4, "ALMACENES");
                psInsert.executeUpdate();

                psInsert.setString(1, "producto");
                psInsert.setString(2, Seguridad.hashPassword("producto19"));
                psInsert.setString(3, "");
                psInsert.setString(4, "PRODUCTOS");
                psInsert.executeUpdate();

            }
        } catch (SQLException e) {
            System.err.println("Error al crear tabla: " + e.getMessage());
        }
    }

    private void configurarEventos() {
        ingresar.addActionListener(e -> Login());
        contraseña.addActionListener(e -> Login());
    }

    private void Login() {
        String usuario = nombre.getText().trim();
        String contrasenaTexto = new String(contraseña.getPassword());

    if (usuario.isEmpty() || contrasenaTexto.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Por favor, completa todo");
        return;
    }

    try {
        String sql = "SELECT * FROM usuarios WHERE nombre = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, usuario);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String hashGuardado = rs.getString("contrasena");

            if (Seguridad.verificarPassword(contrasenaTexto, hashGuardado)) {

                String nombreUsuario = rs.getString("nombre");
                String inicioSesion = rs.getString("inicio_sesion");
                String rol = rs.getString("rol");

                String mensaje = (inicioSesion != null && !inicioSesion.isEmpty())
                        ? "Bienvenido, " + inicioSesion + "\nRol: " + rol
                        : "Bienvenido, " + nombreUsuario + "\nRol: " + rol;

                JOptionPane.showMessageDialog(this, mensaje);

                if (conn != null && !conn.isClosed()) conn.close();

                ventanaPrincipal.loginExitoso(rol);

            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectas");
                contraseña.setText("");
                nombre.requestFocus();
            }

        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectas");
            contraseña.setText("");
            nombre.requestFocus();
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al validar: " + e.getMessage());
    }
}
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Label1;
    private javax.swing.JLabel Label2;
    private javax.swing.JLabel Label3;
    private javax.swing.JPasswordField contraseña;
    private javax.swing.JButton ingresar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField nombre;
    // End of variables declaration//GEN-END:variables
}
