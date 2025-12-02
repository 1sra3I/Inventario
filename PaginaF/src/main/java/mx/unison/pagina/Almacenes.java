/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package mx.unison.pagina;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
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
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author rober
 */
public class Almacenes extends javax.swing.JPanel {

    private String rolUsuario;
    private PaginaPrincipal ventanaPrincipal;

    /**
     * Creates new form FormularioAlmacen
     */
    public Almacenes(String rol, PaginaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        this.rolUsuario = rol;
        initComponents();
        centrado();
        conectarBD();
        configurarBotones();
        seleccionarAlmacenes();
        verificarColumnasHistorial();
        configurarFiltros();
        cargarUltimoHistorial();
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
                return new Insets(6, 6, 6, 6); // padding interno
            }
            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        };
        infoId.setBorder(borde8px);
        infoNombre.setBorder(borde8px);
    }

    private void configurarFiltros() {
        java.awt.event.KeyAdapter filtroListener = new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filtrarAlmacenes();
            }
        };
        infoId.addKeyListener(filtroListener);
        infoNombre.addKeyListener(filtroListener);
    }

    private void filtrarAlmacenes() {
        String filtroId = infoId.getText().trim();
        String filtroNombre = infoNombre.getText().trim();

        StringBuilder sql = new StringBuilder("SELECT * FROM almacenes WHERE id != -1");

        if (!filtroId.isEmpty()) {
            sql.append(" AND CAST(id AS TEXT) LIKE '%").append(filtroId).append("%'");
        }
        if (!filtroNombre.isEmpty()) {
            sql.append(" AND nombre LIKE '%").append(filtroNombre).append("%'");
        }

        ejecutarConsulta(sql.toString());
    }

    private void ejecutarConsulta(String sql) {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            DefaultTableModel modelo = (DefaultTableModel) tablaAlmacenes.getModel();
            modelo.setRowCount(0);

            while (rs.next()) {
                modelo.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nombre")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al filtrar almacenes: " + e.getMessage());
        }
    }

    private void crearPanelHistorial() {
        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 10));
        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.removeAll(); // Limpiar cualquier componente previo

        // Etiqueta de tipo
        JLabel lblTipo = new JLabel("Tipo:");
        lblTipo.setFont(new Font("Dialog", Font.BOLD, 11));
        lblTipo.setForeground(new java.awt.Color(0, 0, 0));
        jPanel3.add(lblTipo);

        tipoModificacion = new JLabel("Sin cambios");
        tipoModificacion.setForeground(new java.awt.Color(60, 60, 60));
        jPanel3.add(tipoModificacion);

        // Etiqueta de fecha
        JLabel lblFecha = new JLabel("Fecha y Hora:");
        lblFecha.setFont(new Font("Dialog", Font.BOLD, 11));
        lblFecha.setForeground(new java.awt.Color(0, 0, 0));
        jPanel3.add(lblFecha);

        ultimaModificacion = new JLabel("--/--/---- --:--:--");
        ultimaModificacion.setForeground(new java.awt.Color(60, 60, 60));
        jPanel3.add(ultimaModificacion);

        // Etiqueta de usuario
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Dialog", Font.BOLD, 11));
        lblUsuario.setForeground(new java.awt.Color(0, 0, 0));
        jPanel3.add(lblUsuario);

        usuarioModificacion = new JLabel("Ninguno");
        usuarioModificacion.setForeground(new java.awt.Color(60, 60, 60));
        jPanel3.add(usuarioModificacion);
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

        // Panel central con tabla y formulario
        javax.swing.JPanel panelCentral = new javax.swing.JPanel();
        panelCentral.setBackground(new java.awt.Color(255, 255, 255));
        panelCentral.setLayout(new GridBagLayout());
        GridBagConstraints gbcCentral = new GridBagConstraints();
        gbcCentral.gridx = 0;
        gbcCentral.insets = new Insets(10, 0, 10, 0);

        //Filtros
        jPanel4.setBackground(new java.awt.Color(240, 248, 255));

        gbcCentral.gridy = 0;
        panelCentral.add(jPanel4, gbcCentral);

        // Tabla
        gbcCentral.gridy = 1;
        jScrollPane1.setPreferredSize(new java.awt.Dimension(500, 100));
        panelCentral.add(jScrollPane1, gbcCentral);

        // Panel de formulario
        javax.swing.JPanel formPanel = new javax.swing.JPanel(new GridBagLayout());
        formPanel.setBackground(new java.awt.Color(255, 255, 255));
        GridBagConstraints gbcForm = new GridBagConstraints();
        gbcForm.insets = new Insets(5, 5, 5, 5);
        gbcForm.anchor = GridBagConstraints.WEST;

        gbcCentral.gridy = 1;
        panelCentral.add(formPanel, gbcCentral);

        // Botones
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel(new java.awt.FlowLayout());
        buttonPanel.setBackground(new java.awt.Color(255, 255, 255));
        buttonPanel.add(jButton2);
        buttonPanel.add(jButton1);

        gbcCentral.gridy = 2;
        panelCentral.add(buttonPanel, gbcCentral);

        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        jPanel1.add(panelCentral, gbc);

        // Panel de historial - DESPUÉS del panel central
        crearPanelHistorial();
        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 0, 5, 0);
        jPanel3.setPreferredSize(new java.awt.Dimension(800, 40));
        jPanel1.add(jPanel3, gbc);

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
        gbcPanel7.weighty = 0;
        gbcPanel7.fill = GridBagConstraints.BOTH;
        jPanel7.add(panelBannerCentrado, gbcPanel7);

        gbc.gridy = 3;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        jPanel1.add(jPanel7, gbc);
    }

    private void configurarBotones() {
        if ("ALMACENES".equals(rolUsuario) || "ADMIN".equals(rolUsuario)) {
            // Tiene permisos para modificar almacenes
            jButton2.setEnabled(true);
            jButton1.setEnabled(true);
            jButton1.setText("Agregar/Modificar");

            // Agregar listeners
            jButton2.addActionListener(e -> eliminarAlmacen());
            jButton1.addActionListener(e -> abrirFormulario());

        } else {
            // Rol PRODUCTOS: Solo puede ver, no modificar
            jButton2.setEnabled(false);
            jButton1.setEnabled(false);
            jButton1.setText("Sin Acceso");

        }
    }

    private void abrirFormulario() {
        if ("ALMACENES".equals(rolUsuario) || "ADMIN".equals(rolUsuario)) {
            FormularioAlmacenes formulario = new FormularioAlmacenes(ventanaPrincipal);
            ventanaPrincipal.mostrarPanelPublico(formulario);
        } else {
            JOptionPane.showMessageDialog(this, "No tienes permisos para modificar almacenes.", "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
        }
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaAlmacenes = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        ultimaModificacion = new javax.swing.JLabel();
        usuarioModificacion = new javax.swing.JLabel();
        tipoModificacion = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        infoId = new javax.swing.JTextField();
        infoNombre = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

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
        tablaAlmacenes.setFocusable(false);
        jScrollPane1.setViewportView(tablaAlmacenes);

        jButton2.setBackground(new java.awt.Color(0, 82, 158));
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Eliminar");

        jPanel2.setBackground(new java.awt.Color(0, 82, 158));

        jLabel11.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenuni.png"))); // NOI18N
        jLabel11.setMaximumSize(new java.awt.Dimension(2048, 707));
        jLabel11.setMinimumSize(new java.awt.Dimension(2048, 707));
        jLabel11.setPreferredSize(new java.awt.Dimension(2048, 707));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/unisonv.jpg"))); // NOI18N

        jTextField3.setEditable(false);
        jTextField3.setBackground(new java.awt.Color(0, 82, 158));
        jTextField3.setForeground(new java.awt.Color(255, 255, 255));
        jTextField3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField3.setText("Blvd. Luis Encinas J, Calle Av. Rosales &, Centro, 83000 Hermosillo, Son.");
        jTextField3.setBorder(null);
        jTextField3.setFocusable(false);

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
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        jPanel7.setBackground(new java.awt.Color(0, 82, 158));

        jPanel8.setBackground(new java.awt.Color(248, 187, 0));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 828, Short.MAX_VALUE)
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

        jButton1.setBackground(new java.awt.Color(0, 82, 158));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("AgregarModificar");

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        ultimaModificacion.setForeground(new java.awt.Color(0, 0, 0));

        usuarioModificacion.setForeground(new java.awt.Color(0, 0, 0));

        tipoModificacion.setForeground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(165, 165, 165)
                .addComponent(ultimaModificacion)
                .addGap(133, 133, 133)
                .addComponent(usuarioModificacion)
                .addGap(106, 106, 106)
                .addComponent(tipoModificacion)
                .addContainerGap(58, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(33, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ultimaModificacion)
                    .addComponent(usuarioModificacion)
                    .addComponent(tipoModificacion))
                .addGap(28, 28, 28))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        infoId.setBackground(new java.awt.Color(255, 255, 255));

        infoNombre.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Id:");

        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Nombre:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(infoId, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(infoNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(infoId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(infoNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(320, 320, 320)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(166, 166, 166))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(224, 224, 224))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private Connection conn;

    private void conectarBD() {
        try {
        conn = DatabaseManager.getConnection();
        System.out.println(" Conectado a la base de datos (Almacenes)");
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Error al conectar con la BD: " + e.getMessage());
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

    public void cargarUltimoHistorial() {
        String sql = "SELECT ultima_modificacion, usuario_modificacion, tipo_modificacion FROM almacenes " +
                "WHERE ultima_modificacion IS NOT NULL ORDER BY ultima_modificacion DESC LIMIT 1";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                ultimaModificacion.setText(rs.getString("ultima_modificacion"));
                usuarioModificacion.setText(rs.getString("usuario_modificacion"));
                String tipo = rs.getString("tipo_modificacion");
                tipoModificacion.setText(tipo);

                Color color = "AGREGADO".equals(tipo) ? new Color(0, 128, 0) :
                              "MODIFICADO".equals(tipo) ? new Color(255, 140, 0) :
                              "ELIMINADO".equals(tipo) ? new Color(178, 34, 34) : Color.GRAY;
                        tipoModificacion.setForeground(color);
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar historial: " + e.getMessage());
        }
    }

    private void seleccionarAlmacenes() {
        String sql = "SELECT * FROM almacenes";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            DefaultTableModel modelo = (DefaultTableModel) tablaAlmacenes.getModel();
            modelo.setRowCount(0);

            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id"),
                    rs.getString("nombre")
                };
                modelo.addRow(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar almacenes: " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void eliminarAlmacen() {
        int filaSeleccionada = tablaAlmacenes.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un almacén para eliminar.");
            return;
        }
        int id = (int) tablaAlmacenes.getValueAt(filaSeleccionada, 0);
        String nombre = (String) tablaAlmacenes.getValueAt(filaSeleccionada, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Quieres eliminar este almacén?", "Confirmar eliminar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            PreparedStatement pstmt = null;
            PreparedStatement pstmtOtro = null;
            try {
                String fechaHora = java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                String usuario = obtenerNombreUsuario();

                // Primero eliminar el registro
                String sqlDelete = "DELETE FROM almacenes WHERE id = ?";
                pstmt = conn.prepareStatement(sqlDelete);
                pstmt.setInt(1, id);
                pstmt.executeUpdate();

                // Buscar otro registro existente para guardar el historial
                String sqlBuscar = "SELECT id FROM almacenes LIMIT 1";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sqlBuscar);

                if (rs.next()) {
                    // Si hay otro almacén, guardar el historial ahí
                    int idOtro = rs.getInt("id");
                    String sqlHistorial = "UPDATE almacenes SET ultima_modificacion = ?, "
                            + "usuario_modificacion = ?, tipo_modificacion = ? WHERE id = ?";
                    pstmtOtro = conn.prepareStatement(sqlHistorial);
                    pstmtOtro.setString(1, fechaHora);
                    pstmtOtro.setString(2, usuario);
                    pstmtOtro.setString(3, "ELIMINADO");
                    pstmtOtro.setInt(4, idOtro);
                    pstmtOtro.executeUpdate();
                } else {
                    // Si no hay más almacenes, insertar un registro temporal solo para el historial
                    String sqlInsert = "INSERT INTO almacenes (id, nombre, ultima_modificacion, usuario_modificacion, tipo_modificacion) VALUES (-1, 'HISTORIAL_TEMP', ?, ?, ?)";
                    pstmtOtro = conn.prepareStatement(sqlInsert);
                    pstmtOtro.setString(1, fechaHora);
                    pstmtOtro.setString(2, usuario);
                    pstmtOtro.setString(3, "ELIMINADO");
                    pstmtOtro.executeUpdate();
                }
                
                rs.close();
                stmt.close();

                JOptionPane.showMessageDialog(this, "Almacén eliminado correctamente.");
                seleccionarAlmacenes();
                cargarUltimoHistorial();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar almacén: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    if (pstmtOtro != null) {
                        pstmtOtro.close();
                    }
                    if (pstmt != null) {
                        pstmt.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String obtenerNombreUsuario() {
        String sql = "SELECT nombre FROM usuarios WHERE rol = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rolUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("nombre");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener nombre de usuario: " + e.getMessage());
        }
        return rolUsuario;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField infoId;
    private javax.swing.JTextField infoNombre;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTable tablaAlmacenes;
    private javax.swing.JLabel tipoModificacion;
    private javax.swing.JLabel ultimaModificacion;
    private javax.swing.JLabel usuarioModificacion;
    // End of variables declaration//GEN-END:variables
}
