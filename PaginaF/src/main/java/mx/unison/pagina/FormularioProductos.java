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
public class FormularioProductos extends javax.swing.JPanel {

    private PaginaPrincipal ventanaPrincipal;

    /**
     * Creates new form FormularioProductos
     */
    public FormularioProductos(PaginaPrincipal ventanaPrincipal) {
        initComponents();
        this.ventanaPrincipal = ventanaPrincipal;
        centrado();
        conectarBD();
        verificarColumnasHistorial();
        seleccionarProductos();
        aplicarBordesRedondeados();

        jButton1.addActionListener(e -> agregarProducto());
        jButton2.addActionListener(e -> modificarProducto());
        jButton3.addActionListener(evt -> volverProducto());
    }

    private void volverProducto() {
        String rol = ventanaPrincipal.getRolUsuario();

        Productos almacenes = new Productos(rol, ventanaPrincipal);
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
        jTextField3.setBorder(borde8px);
        jTextField4.setBorder(borde8px);
        jTextField5.setBorder(borde8px);
        jTextField6.setBorder(borde8px);
    }

    private String obtenerFechaHoraActual() {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return ahora.format(formato);
    }

    private String obtenerNombreUsuario() {
        String rol = ventanaPrincipal.getRolUsuario();
        String nombreUsuario = rol; 

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT nombre FROM usuarios WHERE rol = ? LIMIT 1";
            ps = conn.prepareStatement(sql);
            ps.setString(1, rol);
            rs = ps.executeQuery();

            if (rs.next()) {
                nombreUsuario = rs.getString("nombre");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener nombre de usuario: " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return nombreUsuario;
    }


    private void registrarCambio(int id, String tipoModificacion) {
        PreparedStatement pstmt = null;
        try {
            String fechaHora = obtenerFechaHoraActual();
            String usuario = obtenerNombreUsuario(); 

            String sql = "UPDATE productos SET ultima_modificacion = ?, "
                    + "usuario_modificacion = ?, tipo_modificacion = ? WHERE id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, fechaHora);
            pstmt.setString(2, usuario);
            pstmt.setString(3, tipoModificacion);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al registrar cambio: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
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

        // Panel de formulario con 2 columnas
        javax.swing.JPanel formPanel = new javax.swing.JPanel(new GridBagLayout());
        formPanel.setBackground(new java.awt.Color(255, 255, 255));
        GridBagConstraints gbcForm = new GridBagConstraints();
        gbcForm.insets = new Insets(5, 5, 5, 5);
        gbcForm.anchor = GridBagConstraints.WEST;

        // Columna 1 - ID
        gbcForm.gridx = 0;
        gbcForm.gridy = 0;
        formPanel.add(jLabel1, gbcForm);
        gbcForm.gridx = 1;
        jTextField2.setPreferredSize(new java.awt.Dimension(150, 25));
        formPanel.add(jTextField2, gbcForm);

        // Columna 2 - Nombre
        gbcForm.gridx = 2;
        gbcForm.gridy = 0;
        formPanel.add(jLabel2, gbcForm);
        gbcForm.gridx = 3;
        jTextField3.setPreferredSize(new java.awt.Dimension(150, 25));
        formPanel.add(jTextField3, gbcForm);

        // Columna 1 - Precio
        gbcForm.gridx = 0;
        gbcForm.gridy = 1;
        formPanel.add(jLabel3, gbcForm);
        gbcForm.gridx = 1;
        jTextField4.setPreferredSize(new java.awt.Dimension(150, 25));
        formPanel.add(jTextField4, gbcForm);

        // Columna 2 - Cantidad
        gbcForm.gridx = 2;
        gbcForm.gridy = 1;
        formPanel.add(jLabel4, gbcForm);
        gbcForm.gridx = 3;
        jTextField6.setPreferredSize(new java.awt.Dimension(150, 25));
        formPanel.add(jTextField6, gbcForm);

        // Columna 1 - Departamento
        gbcForm.gridx = 0;
        gbcForm.gridy = 2;
        formPanel.add(jLabel5, gbcForm);
        gbcForm.gridx = 1;
        jTextField1.setPreferredSize(new java.awt.Dimension(150, 25));
        formPanel.add(jTextField1, gbcForm);

        // Columna 2 - Almacen
        gbcForm.gridx = 2;
        gbcForm.gridy = 2;
        formPanel.add(jLabel6, gbcForm);
        gbcForm.gridx = 3;
        jTextField5.setPreferredSize(new java.awt.Dimension(150, 25));
        formPanel.add(jTextField5, gbcForm);

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
        jPanel8.setPreferredSize(new java.awt.Dimension(800, 41));
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
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField1 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
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
        tablaProductos = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("precio");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("almacen");

        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("cantidad");

        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("id");

        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("nombre");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("departamento");

        jTextField5.setBackground(new java.awt.Color(204, 204, 255));

        jTextField2.setBackground(new java.awt.Color(204, 204, 255));
        jTextField2.setForeground(new java.awt.Color(0, 0, 0));

        jTextField3.setBackground(new java.awt.Color(204, 204, 255));

        jTextField6.setBackground(new java.awt.Color(204, 204, 255));
        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        jTextField1.setBackground(new java.awt.Color(204, 204, 255));
        jTextField1.setForeground(new java.awt.Color(0, 0, 0));

        jTextField4.setBackground(new java.awt.Color(204, 204, 255));
        jTextField4.setForeground(new java.awt.Color(0, 0, 0));

        jButton1.setBackground(new java.awt.Color(0, 82, 158));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Agregar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

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
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
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
                        .addComponent(jLabel7)
                        .addGap(20, 20, 20))))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)))
                .addContainerGap())
        );

        jPanel7.setBackground(new java.awt.Color(0, 82, 158));

        jPanel8.setBackground(new java.awt.Color(248, 187, 0));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 851, Short.MAX_VALUE)
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 697, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(63, 63, 63))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 118, Short.MAX_VALUE)
                .addGap(6, 6, 6))
        );

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
        jScrollPane1.setViewportView(tablaProductos);

        jButton3.setBackground(new java.awt.Color(0, 82, 158));
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Volver");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(297, 297, 297)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                                .addComponent(jTextField3, javax.swing.GroupLayout.Alignment.LEADING))
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(234, 234, 234))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 663, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(67, 67, 67))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton1)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

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
                stmt.execute("ALTER TABLE almacenes ADD COLUMN ultima_modificacion TEXT");
            }
            if (!tieneUsuario) {
                stmt.execute("ALTER TABLE almacenes ADD COLUMN usuario_modificacion TEXT");
            }
            if (!tieneTipo) {
                stmt.execute("ALTER TABLE almacenes ADD COLUMN tipo_modificacion TEXT");
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar columnas: " + e.getMessage());
        }
    }

    private void seleccionarProductos() {
        String sql = "SELECT p.id, p.nombre, p.precio, p.cantidad, p.departamento, a.nombre AS nombre_almacen "
                + "FROM productos p LEFT JOIN almacenes a ON p.almacen = a.id";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            DefaultTableModel modelo = (DefaultTableModel) tablaProductos.getModel();
            modelo.setRowCount(0);

            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getInt("cantidad"),
                    rs.getString("departamento"),
                    rs.getString("nombre_almacen")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage());
        }
    }

    private void agregarProducto() {
        PreparedStatement psAlmacen = null;
        PreparedStatement psInsertAlmacen = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

    try {
        int id = Integer.parseInt(jTextField2.getText().trim());
        String nombre = jTextField3.getText().trim();
        double precio = Double.parseDouble(jTextField4.getText().trim());
        int cantidad = Integer.parseInt(jTextField6.getText().trim());
        String departamento = jTextField1.getText().trim();
        String nombreAlmacen = jTextField5.getText().trim();

        if (nombre.isEmpty() || departamento.isEmpty() || nombreAlmacen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos");
            return;
        }

        // 1. Buscar ID de almacén
        String sqlAlmacen = "SELECT id FROM almacenes WHERE nombre = ?";
        psAlmacen = conn.prepareStatement(sqlAlmacen);
        psAlmacen.setString(1, nombreAlmacen);
        rs = psAlmacen.executeQuery();

        int idAlmacen;

        // 2. si no existe crealo
        if (!rs.next()) {
            String sqlNuevo = "INSERT INTO almacenes (nombre) VALUES (?)";
            psInsertAlmacen = conn.prepareStatement(sqlNuevo, Statement.RETURN_GENERATED_KEYS);
            psInsertAlmacen.setString(1, nombreAlmacen);
            psInsertAlmacen.executeUpdate();

            ResultSet rsNuevo = psInsertAlmacen.getGeneratedKeys();
            rsNuevo.next();
            idAlmacen = rsNuevo.getInt(1);
        } else {
            idAlmacen = rs.getInt("id");
        }

        // 3. Insertar producto
        String sql = "INSERT INTO productos (id, nombre, precio, cantidad, departamento, almacen) VALUES (?, ?, ?, ?, ?, ?)";
        ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.setString(2, nombre);
        ps.setDouble(3, precio);
        ps.setInt(4, cantidad);
        ps.setString(5, departamento);
        ps.setInt(6, idAlmacen);
        ps.executeUpdate();

        registrarCambio(id, "AGREGADO");

        JOptionPane.showMessageDialog(this, "Producto agregado correctamente");
        seleccionarProductos();

        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField6.setText("");
        jTextField1.setText("");
        jTextField5.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingresa valores válidos.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar: " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (psAlmacen != null) {
                    psAlmacen.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void modificarProducto() {
        PreparedStatement psAlmacen = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            int id = Integer.parseInt(jTextField2.getText().trim());
            String nombre = jTextField3.getText().trim();
            double precio = Double.parseDouble(jTextField4.getText().trim());
            int cantidad = Integer.parseInt(jTextField6.getText().trim());
            String departamento = jTextField1.getText().trim();
            String nombreAlmacen = jTextField5.getText().trim();

            if (nombre.isEmpty() || departamento.isEmpty() || nombreAlmacen.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Completa todos los campos");
                return;
            }

            // Obtener id del almacén
            String sqlAlmacen = "SELECT id FROM almacenes WHERE nombre = ?";
            psAlmacen = conn.prepareStatement(sqlAlmacen);
            psAlmacen.setString(1, nombreAlmacen);
            rs = psAlmacen.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "No existe un almacén con ese nombre");
                return;
            }

            int idAlmacen = rs.getInt("id");

            String sql = "UPDATE productos SET nombre=?, precio=?, cantidad=?, departamento=?, almacen=? WHERE id=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setDouble(2, precio);
            ps.setInt(3, cantidad);
            ps.setString(4, departamento);
            ps.setInt(5, idAlmacen);
            ps.setInt(6, id);

            int filas = ps.executeUpdate();

            if (filas > 0) {
                
                registrarCambio(id, "MODIFICADO");

                JOptionPane.showMessageDialog(this, "Producto modificado correctamente");
                seleccionarProductos();

                jTextField2.setText("");
                jTextField3.setText("");
                jTextField4.setText("");
                jTextField6.setText("");
                jTextField1.setText("");
                jTextField5.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró un producto con ese ID");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingresa valores válidos");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al modificar: " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (psAlmacen != null) {
                    psAlmacen.close();
                }
                if (ps != null) {
                    ps.close();
                }
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
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
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTable tablaProductos;
    // End of variables declaration//GEN-END:variables
}
