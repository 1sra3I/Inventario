/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package mx.unison.pagina;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author rober
 */
public class Productos extends javax.swing.JPanel {

    private String rolUsuario;
    private PaginaPrincipal ventanaPrincipal;

    /**
     * Creates new form FormularioProducto
     */
    public Productos(String rol, PaginaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        this.rolUsuario = rol;
        initComponents();
        centrado();
        conectarBD();
        configurarBotones();
        seleccionarProductos();
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
        precioMax.setBorder(borde8px);
        precioMin.setBorder(borde8px);
        cantidadMax.setBorder(borde8px);
        cantidadMin.setBorder(borde8px);
        infoAlmacen.setBorder(borde8px);
        infoDepartamento.setBorder(borde8px);
    }

    private void configurarFiltros() {
        java.awt.event.KeyAdapter filtroListener = new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filtrarProductos();
            }
        };
        infoDepartamento.addKeyListener(filtroListener);
        infoAlmacen.addKeyListener(filtroListener);
        precioMin.addKeyListener(filtroListener);
        precioMax.addKeyListener(filtroListener);
        cantidadMin.addKeyListener(filtroListener);
        cantidadMax.addKeyListener(filtroListener);
    }

    private void filtrarProductos() {
        String filtroDepartamento = infoDepartamento.getText().trim();
        String filtroAlmacen = infoAlmacen.getText().trim();
        String filtroPrecioMin = precioMin.getText().trim();
        String filtroPrecioMax = precioMax.getText().trim();
        String filtroCantidadMin = cantidadMin.getText().trim();
        String filtroCantidadMax = cantidadMax.getText().trim();

        StringBuilder sql = new StringBuilder(
                "SELECT p.id, p.nombre, p.precio, p.cantidad, p.departamento, a.nombre AS nombre_almacen "
                + "FROM productos p LEFT JOIN almacenes a ON p.almacen = a.id WHERE p.id != -1"
        );

        // Filtro por Departamento
        if (!filtroDepartamento.isEmpty()) {
            sql.append(" AND p.departamento LIKE '%").append(filtroDepartamento).append("%'");
        }

        // Filtro por Almacén
        if (!filtroAlmacen.isEmpty()) {
            sql.append(" AND a.nombre LIKE '%").append(filtroAlmacen).append("%'");
        }
        // Filtros por Precio
        if (!filtroPrecioMin.isEmpty()) {
            try {
                double min = Double.parseDouble(filtroPrecioMin);
                sql.append(" AND p.precio >= ").append(min);
            } catch (NumberFormatException e) {
                // Ignorar si no es un número válido
            }
        }
        if (!filtroPrecioMax.isEmpty()) {
            try {
                double max = Double.parseDouble(filtroPrecioMax);
                sql.append(" AND p.precio <= ").append(max);
            } catch (NumberFormatException e) {
                // Ignorar si no es un número válido
            }
        }
        // Filtros por Cantidad 
        if (!filtroCantidadMin.isEmpty()) {
            try {
                int min = Integer.parseInt(filtroCantidadMin);
                sql.append(" AND p.cantidad >= ").append(min);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Valor inválido en 'Cantidad mínima'");
                return;
            }
        }
        if (!filtroCantidadMax.isEmpty()) {
            try {
                int max = Integer.parseInt(filtroCantidadMax);
                sql.append(" AND p.cantidad <= ").append(max);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Valor inválido en 'Cantidad máxima'");
                return;
            }
        }
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql.toString());

            DefaultTableModel modelo = (DefaultTableModel) tablaProductos.getModel();
            modelo.setRowCount(0);

            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getInt("cantidad"),
                    rs.getString("departamento"),
                    rs.getString("nombre_almacen")
                };
                modelo.addRow(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al filtrar productos: " + e.getMessage());
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

    private void crearPanelHistorial() {
        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 10));
        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.removeAll();

        JLabel lblTipo = new JLabel("Tipo:");
        lblTipo.setFont(new Font("Dialog", Font.BOLD, 11));
        lblTipo.setForeground(new java.awt.Color(0, 0, 0));
        jPanel3.add(lblTipo);

        tipoModificacion = new JLabel("Sin cambios");
        tipoModificacion.setForeground(new java.awt.Color(60, 60, 60));
        jPanel3.add(tipoModificacion);

        JLabel lblFecha = new JLabel("Fecha y Hora:");
        lblFecha.setFont(new Font("Dialog", Font.BOLD, 11));
        lblFecha.setForeground(new java.awt.Color(0, 0, 0));
        jPanel3.add(lblFecha);

        ultimaModificacion = new JLabel("--/--/---- --:--:--");
        ultimaModificacion.setForeground(new java.awt.Color(60, 60, 60));
        jPanel3.add(ultimaModificacion);

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
        jScrollPane1.setPreferredSize(new java.awt.Dimension(700, 100));
        panelCentral.add(jScrollPane1, gbcCentral);

        // Panel de formulario con 2 columnas
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

        // Panel de historial
        crearPanelHistorial();
        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 0, 10, 0);
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
        jPanel8.setPreferredSize(new java.awt.Dimension(800, 41));
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
        if ("PRODUCTOS".equals(rolUsuario) || "ADMIN".equals(rolUsuario)) {
            jButton2.setEnabled(true);
            jButton1.setEnabled(true);
            jButton1.setText("Agregar/Modificar");

            jButton2.addActionListener(e -> eliminarProducto());
            jButton1.addActionListener(e -> abrirFormulario());
        } else {
            jButton2.setEnabled(false);
            jButton1.setEnabled(false);
            jButton1.setText("Sin Acceso");
        }
    }

    private void abrirFormulario() {
        if ("PRODUCTOS".equals(rolUsuario) || "ADMIN".equals(rolUsuario)) {
            FormularioProductos formulario = new FormularioProductos(ventanaPrincipal);
            ventanaPrincipal.mostrarPanelPublico(formulario);
        } else {
            JOptionPane.showMessageDialog(this, "No tienes permisos para modificar productos.", "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
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
        tablaProductos = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
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
        precioMax = new javax.swing.JTextField();
        precioMin = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cantidadMin = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cantidadMax = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        infoDepartamento = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        infoAlmacen = new javax.swing.JTextField();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane1.setBackground(new java.awt.Color(0, 82, 158));

        tablaProductos.setBackground(new java.awt.Color(204, 204, 255));
        tablaProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "id", "nombre", "precio", "cantidad", "departamento", "almacen"
            }
        ));
        tablaProductos.setFocusable(false);
        jScrollPane1.setViewportView(tablaProductos);

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
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
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
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        jPanel7.setBackground(new java.awt.Color(0, 82, 158));

        jPanel8.setBackground(new java.awt.Color(248, 187, 0));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 41, Short.MAX_VALUE)
        );

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenban.png"))); // NOI18N
        jLabel8.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(74, Short.MAX_VALUE)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 697, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 118, Short.MAX_VALUE)
                .addGap(6, 6, 6))
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
        jPanel4.setForeground(new java.awt.Color(0, 0, 0));

        precioMax.setBackground(new java.awt.Color(255, 255, 255));

        precioMin.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("PMIN");

        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("PMAX");

        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("CMIN");

        cantidadMin.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("CMAX");

        cantidadMax.setBackground(new java.awt.Color(255, 255, 255));

        jLabel5.setForeground(new java.awt.Color(51, 51, 51));
        jLabel5.setText("Departamento");

        infoDepartamento.setBackground(new java.awt.Color(255, 255, 255));

        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Almacen");

        infoAlmacen.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addComponent(jLabel1)
                .addGap(3, 3, 3)
                .addComponent(precioMin, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(jLabel2)
                .addGap(6, 6, 6)
                .addComponent(precioMax, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jLabel3)
                .addGap(3, 3, 3)
                .addComponent(cantidadMin, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(jLabel4)
                .addGap(3, 3, 3)
                .addComponent(cantidadMax, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(infoDepartamento, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addGap(4, 4, 4)
                .addComponent(infoAlmacen, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(precioMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(precioMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(cantidadMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(cantidadMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(infoDepartamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(infoAlmacen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(174, 174, 174))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 663, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jButton1)
                                    .addGap(223, 223, 223))))
                        .addGap(55, 55, 55))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(88, 88, 88)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton1))
                .addGap(26, 26, 26)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private Connection conn;

    private void conectarBD() {
        try {
        conn = DatabaseManager.getConnection();
        System.out.println(" Conectado a la base de datos (Productos)");
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Error al conectar con la BD: " + e.getMessage());
        }
    }

    private void verificarColumnasHistorial() {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("PRAGMA table_info(productos)")) {
            boolean tieneUltima = false, tieneUsuario = false, tieneTipo = false;

            while (rs.next()) {
                String col = rs.getString("name");
                if ("ultima_modificacion".equals(col)) {
                    tieneUltima = true;
                }
                if ("usuario_modificacion".equals(col)) {
                    tieneUsuario = true;
                }
                if ("tipo_modificacion".equals(col)) {
                    tieneTipo = true;
                }
            }

            if (!tieneUltima) {
                stmt.execute("ALTER TABLE productos ADD COLUMN ultima_modificacion TEXT");
            }
            if (!tieneUsuario) {
                stmt.execute("ALTER TABLE productos ADD COLUMN usuario_modificacion TEXT");
            }
            if (!tieneTipo) {
                stmt.execute("ALTER TABLE productos ADD COLUMN tipo_modificacion TEXT");
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar columnas: " + e.getMessage());
        }
    }

    public void cargarUltimoHistorial() {
        String sql = "SELECT ultima_modificacion, usuario_modificacion, tipo_modificacion FROM productos "
                + "WHERE ultima_modificacion IS NOT NULL ORDER BY ultima_modificacion DESC LIMIT 1";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                ultimaModificacion.setText(rs.getString("ultima_modificacion"));
                usuarioModificacion.setText(rs.getString("usuario_modificacion"));
                String tipo = rs.getString("tipo_modificacion");
                tipoModificacion.setText(tipo);

                Color color = "AGREGADO".equals(tipo) ? new Color(0, 128, 0)
                        : "MODIFICADO".equals(tipo) ? new Color(255, 140, 0)
                        : "ELIMINADO".equals(tipo) ? new Color(178, 34, 34) : Color.GRAY;
                tipoModificacion.setForeground(color);
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar historial: " + e.getMessage());
        }
    }

    private void seleccionarProductos() {
        String sql = "SELECT p.id, p.nombre, p.precio, p.cantidad, p.departamento, a.nombre AS nombre_almacen "
                + "FROM productos p LEFT JOIN almacenes a ON p.almacen = a.id WHERE p.id != -1";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            DefaultTableModel modelo = (DefaultTableModel) tablaProductos.getModel();
            modelo.setRowCount(0);

            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getInt("cantidad"),
                    rs.getString("departamento"),
                    rs.getString("nombre_almacen")
                };
                modelo.addRow(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage());
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

    private void eliminarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona primero una fila para eliminar.");
            return;
        }

        int id = (int) tablaProductos.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Quieres eliminar el producto?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            PreparedStatement pstmt = null;
            PreparedStatement pstmtOtro = null;
            try {
                String fechaHora = java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                String usuario = obtenerNombreUsuario();

                // Primero eliminar
                String sqlDelete = "DELETE FROM productos WHERE id = ?";
                pstmt = conn.prepareStatement(sqlDelete);
                pstmt.setInt(1, id);
                pstmt.executeUpdate();

                // Buscar otro producto para guardar historial
                String sqlBuscar = "SELECT id FROM productos WHERE id != -1 LIMIT 1";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sqlBuscar);

                if (rs.next()) {
                    int idOtro = rs.getInt("id");
                    String sqlHistorial = "UPDATE productos SET ultima_modificacion = ?, "
                            + "usuario_modificacion = ?, tipo_modificacion = ? WHERE id = ?";
                    pstmtOtro = conn.prepareStatement(sqlHistorial);
                    pstmtOtro.setString(1, fechaHora);
                    pstmtOtro.setString(2, usuario);
                    pstmtOtro.setString(3, "ELIMINADO");
                    pstmtOtro.setInt(4, idOtro);
                    pstmtOtro.executeUpdate();
                } else {
                    // Crear registro temporal
                    String sqlInsert = "INSERT INTO productos (id, nombre, precio, cantidad, departamento, almacen, ultima_modificacion, usuario_modificacion, tipo_modificacion) VALUES (-1, 'HISTORIAL_TEMP', 0, 0, 'TEMP', 1, ?, ?, ?)";
                    pstmtOtro = conn.prepareStatement(sqlInsert);
                    pstmtOtro.setString(1, fechaHora);
                    pstmtOtro.setString(2, usuario);
                    pstmtOtro.setString(3, "ELIMINADO");
                    pstmtOtro.executeUpdate();
                }
                rs.close();
                stmt.close();

                JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.");
                seleccionarProductos();
                cargarUltimoHistorial();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar producto: " + e.getMessage());
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
                if (rs.next()) {
                    return rs.getString("nombre");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener nombre de usuario: " + e.getMessage());
        }
        return rolUsuario;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField cantidadMax;
    private javax.swing.JTextField cantidadMin;
    private javax.swing.JTextField infoAlmacen;
    private javax.swing.JTextField infoDepartamento;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
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
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField precioMax;
    private javax.swing.JTextField precioMin;
    private javax.swing.JTable tablaProductos;
    private javax.swing.JLabel tipoModificacion;
    private javax.swing.JLabel ultimaModificacion;
    private javax.swing.JLabel usuarioModificacion;
    // End of variables declaration//GEN-END:variables
}
