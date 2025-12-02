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
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author rober
 */
public class FormularioAlmacenes extends javax.swing.JPanel {

    private PaginaPrincipal ventanaPrincipal;
    /**
     * Creates new form FormularioAlmacenes
     */
    public FormularioAlmacenes(PaginaPrincipal ventanaPrincipal) {
        initComponents();
        this.ventanaPrincipal = ventanaPrincipal;
        centrado();
        conectarBD();
        verificarColumnasHistorial();
        seleccionarAlmacenes();
        aplicarBordesRedondeados();

        jButton1.addActionListener(evt -> agregarAlmacen());
        jButton2.addActionListener(evt -> modificarAlmacen());
        jButton3.addActionListener(evt -> volverAlmacenes());
    }

    private void volverAlmacenes() {
        String rol = ventanaPrincipal.getRolUsuario();
        Almacenes almacenes = new Almacenes(rol, ventanaPrincipal);
        ventanaPrincipal.mostrarPanelPublico(almacenes);
    
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
                return new Insets(6, 6, 6, 6); // padding interno
            }
            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        };
        jTextField1.setBorder(borde8px);
        jTextField2.setBorder(borde8px);
    }
    
    private String obtenerFechaHoraActual() {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return ahora.format(formato);
    }
    
    private String obtenerNombreUsuario() {
         String rol = ventanaPrincipal.getRolUsuario();
        String sql = "SELECT nombre FROM usuarios WHERE rol = ? LIMIT 1";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rol);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("nombre");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener nombre de usuario: " + e.getMessage());
        }
        return rol;
    }
    
    private void registrarCambio(int id, String tipoModificacion) {
        String sql = "UPDATE almacenes SET ultima_modificacion = ?, usuario_modificacion = ?, tipo_modificacion = ? WHERE id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, obtenerFechaHoraActual());
            ps.setString(2, obtenerNombreUsuario());
            ps.setString(3, tipoModificacion);
            ps.setInt(4, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al registrar cambio: " + e.getMessage());
        }
    }
    
    private void centrado() {
        
        // Cambiar layout del jPanel1 para centrar contenido
        jPanel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Panel superior (jPanel2) - logos
        gbc.gridy = 0;
        gbc.weighty = 0;
        jPanel1.add(jPanel2, gbc);

        // Panel central con tabla y formulario
        javax.swing.JPanel panelCentral = new javax.swing.JPanel();
        panelCentral.setBackground(new java.awt.Color(255, 255, 255));
        panelCentral.setLayout(new GridBagLayout());
        GridBagConstraints gbcCentral = new GridBagConstraints();
        gbcCentral.gridx = 0;
        gbcCentral.insets = new Insets(10, 0, 10, 0);
        jScrollPane1.setVisible(false);

        // Panel de formulario
        javax.swing.JPanel formPanel = new javax.swing.JPanel(new GridBagLayout());
        formPanel.setBackground(new java.awt.Color(255, 255, 255));
        GridBagConstraints gbcForm = new GridBagConstraints();
        gbcForm.insets = new Insets(5, 5, 5, 5);
        gbcForm.anchor = GridBagConstraints.WEST;

        // ID
        gbcForm.gridx = 0;
        gbcForm.gridy = 0;
        formPanel.add(jLabel2, gbcForm);
        gbcForm.gridx = 1;
        jTextField2.setPreferredSize(new java.awt.Dimension(200, 25));
        formPanel.add(jTextField2, gbcForm);

        // Nombre
        gbcForm.gridx = 0;
        gbcForm.gridy = 1;
        formPanel.add(jLabel1, gbcForm);
        gbcForm.gridx = 1;
        jTextField1.setPreferredSize(new java.awt.Dimension(200, 25));
        formPanel.add(jTextField1, gbcForm);

        gbcCentral.gridy = 1;
        panelCentral.add(formPanel, gbcCentral);

        // Botones
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel(new java.awt.FlowLayout());
        buttonPanel.setBackground(new java.awt.Color(255, 255, 255));
        buttonPanel.add(jButton1);
        buttonPanel.add(jButton2);
        buttonPanel.add(jButton3);

        gbcCentral.gridy = 2;
        panelCentral.add(buttonPanel, gbcCentral);

        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        jPanel1.add(panelCentral, gbc);

        // Panel inferior (jPanel7) - banner centrado
        jPanel7.setLayout(new GridBagLayout());
        GridBagConstraints gbcPanel7 = new GridBagConstraints();
        gbcPanel7.gridx = 0;
        gbcPanel7.weightx = 1.0;
        gbcPanel7.fill = GridBagConstraints.HORIZONTAL;

        // Panel amarillo arriba
        gbcPanel7.gridy = 0;
        gbcPanel7.weighty = 0;
        jPanel8.setPreferredSize(new java.awt.Dimension(800, 48));
        jPanel7.add(jPanel8, gbcPanel7);

        // Panel para centrar el banner
        javax.swing.JPanel panelBannerCentrado = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
        panelBannerCentrado.setBackground(new java.awt.Color(0, 82, 158));
        panelBannerCentrado.add(jLabel8);

        gbcPanel7.gridy = 1;
        gbcPanel7.weighty = 1.0;
        gbcPanel7.fill = GridBagConstraints.BOTH;
        jPanel7.add(panelBannerCentrado, gbcPanel7);

        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jPanel1.add(jPanel7, gbc);
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
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaAlmacenes = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("id");

        jTextField2.setBackground(new java.awt.Color(204, 204, 255));
        jTextField2.setForeground(new java.awt.Color(0, 0, 0));
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jTextField1.setBackground(new java.awt.Color(204, 204, 255));

        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("nombre");

        jButton1.setBackground(new java.awt.Color(0, 82, 158));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Agregar");

        jButton2.setBackground(new java.awt.Color(0, 82, 158));
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Modificar");

        jPanel2.setBackground(new java.awt.Color(0, 82, 158));

        jLabel11.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenuni.png"))); // NOI18N
        jLabel11.setMaximumSize(new java.awt.Dimension(2048, 707));
        jLabel11.setMinimumSize(new java.awt.Dimension(2048, 707));
        jLabel11.setPreferredSize(new java.awt.Dimension(2048, 707));

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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addGap(2, 2, 2)
                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addContainerGap())))
        );

        jPanel7.setBackground(new java.awt.Color(0, 82, 158));

        jPanel8.setBackground(new java.awt.Color(248, 187, 0));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 835, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 42, Short.MAX_VALUE)
        );

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenban.png"))); // NOI18N
        jLabel8.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 679, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jScrollPane1.setBackground(new java.awt.Color(0, 82, 158));

        tablaAlmacenes.setBackground(new java.awt.Color(204, 204, 255));
        tablaAlmacenes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Id", "Nombre"
            }
        ));
        jScrollPane1.setViewportView(tablaAlmacenes);

        jButton3.setBackground(new java.awt.Color(0, 82, 158));
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Volver");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(266, 266, 266)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(231, 231, 231))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(213, 213, 213))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 94, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton1)
                    .addComponent(jButton3))
                .addGap(35, 35, 35)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed
    private Connection conn;

    private void conectarBD() {
        try {
            conn = DatabaseManager.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
            "Error al conectar: " + e.getMessage());
        }
    }
    
    private void verificarColumnasHistorial() {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("PRAGMA table_info(almacenes)")) {
            boolean tieneUltima = false, tieneUsuario = false, tieneTipo = false;

            while (rs.next()) {
                String col = rs.getString("name");
                if ("ultima_modificacion".equals(col)) tieneUltima = true;
                if ("usuario_modificacion".equals(col)) tieneUsuario = true;
                if ("tipo_modificacion".equals(col)) tieneTipo = true;
            }

            if (!tieneUltima) stmt.execute("ALTER TABLE almacenes ADD COLUMN ultima_modificacion TEXT");
            if (!tieneUsuario) stmt.execute("ALTER TABLE almacenes ADD COLUMN usuario_modificacion TEXT");
            if (!tieneTipo) stmt.execute("ALTER TABLE almacenes ADD COLUMN tipo_modificacion TEXT");
        } catch (SQLException e) {
            System.err.println("Error al verificar columnas: " + e.getMessage());
        }
    }

    private void seleccionarAlmacenes() {
        String sql = "SELECT * FROM almacenes";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            DefaultTableModel modelo = (DefaultTableModel) tablaAlmacenes.getModel();
            modelo.setRowCount(0);

            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id"),
                    rs.getString("nombre"),};
                modelo.addRow(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar almacenes: " + e.getMessage());
        }
    }

    private void agregarAlmacen() {
            String sql = "INSERT INTO almacenes (id, nombre) VALUES (?, ?)";
            PreparedStatement ps = null;
        try {
            int id = Integer.parseInt(jTextField2.getText().trim());
            String nombre = jTextField1.getText().trim();
            
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío");
                return;
            }
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setString(2, nombre);
            ps.executeUpdate();
            
            registrarCambio(id, "AGREGADO");

            JOptionPane.showMessageDialog(this, "Almacén agregado correctamente.");
            seleccionarAlmacenes();
            jTextField1.setText("");
            jTextField2.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID debe ser un número válido");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar almacén: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void modificarAlmacen() {
            String sql = "UPDATE almacenes SET nombre=? WHERE id=?";
        PreparedStatement ps = null;
        try {
            int id = Integer.parseInt(jTextField2.getText().trim());
            String nombre = jTextField1.getText().trim();
            
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío");
                return;
            }
            
            ps = conn.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setInt(2, id);
            
            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                registrarCambio(id, "MODIFICADO");
                
                JOptionPane.showMessageDialog(this, "Almacén modificado correctamente");
                seleccionarAlmacenes();
                
                jTextField1.setText("");
                jTextField2.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró ningún almacén con ese ID");
            }
        
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al modificar almacén: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El ID debe ser un número válido");
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTable tablaAlmacenes;
    // End of variables declaration//GEN-END:variables
}
