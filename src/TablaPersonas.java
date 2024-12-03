/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author thoma
 */
public class TablaPersonas extends JFrame {
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtIdentificacion;
    private JTextField txtNombre;
    private JTextField txtCorreo;
    private JButton btnGuardarPlano, btnLeerPlano, btnGuardarXML, btnLeerXML, btnAgregar;
    private JButton btnGuardarJSON, btnLeerJSON;

    public TablaPersonas() {
        setTitle("Gestión de Personas");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Correo"}, 0);
        tabla = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tabla);
        add(scrollPane, BorderLayout.CENTER);

        
        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 10, 10));
        txtIdentificacion = new JTextField();
        txtNombre = new JTextField();
        txtCorreo = new JTextField();
        btnAgregar = new JButton("Agregar Persona");

        panelFormulario.add(new JLabel("ID:"));
        panelFormulario.add(txtIdentificacion);
        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(new JLabel("Correo:"));
        panelFormulario.add(txtCorreo);
        panelFormulario.add(btnAgregar);

        add(panelFormulario, BorderLayout.NORTH);

        
        JPanel panelBotones = new JPanel(new GridLayout(1, 6, 5, 5));
        btnGuardarPlano = new JButton("Guardar Plano");
        btnLeerPlano = new JButton("Leer Plano");
        btnGuardarXML = new JButton("Guardar XML");
        btnLeerXML = new JButton("Leer XML");
        btnGuardarJSON = new JButton("Guardar JSON");
        btnLeerJSON = new JButton("Leer JSON");

        panelBotones.add(btnGuardarPlano);
        panelBotones.add(btnLeerPlano);
        panelBotones.add(btnGuardarXML);
        panelBotones.add(btnLeerXML);
        panelBotones.add(btnGuardarJSON);
        panelBotones.add(btnLeerJSON);

        add(panelBotones, BorderLayout.SOUTH);

        
        btnAgregar.addActionListener(e -> agregarPersona());
        btnGuardarPlano.addActionListener(e -> guardarArchivoTXT());
        btnLeerPlano.addActionListener(e -> leerArchivoTXT());
        btnGuardarXML.addActionListener(e -> guardarXML());
        btnLeerXML.addActionListener(e -> leerXML());
        btnGuardarJSON.addActionListener(e -> guardarJSON());
        btnLeerJSON.addActionListener(e -> leerJSON());
    }

    private void agregarPersona() {
        String id = txtIdentificacion.getText();
        String nombre = txtNombre.getText();
        String correo = txtCorreo.getText();

        if (!id.isEmpty() && !nombre.isEmpty() && !correo.isEmpty()) {
            try {
                validacionCorreo(correo);
                modeloTabla.addRow(new Object[]{id, nombre, correo});
                limpiarFormulario();
            } catch (CorreoInvalidoException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        txtIdentificacion.setText("");
        txtNombre.setText("");
        txtCorreo.setText("");
    }

    
    private void guardarArchivoTXT() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("personas.txt"))) {
            int rowCount = modeloTabla.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                String ID = modeloTabla.getValueAt(i, 0).toString();
                String nombre = modeloTabla.getValueAt(i, 1).toString();
                String correo = modeloTabla.getValueAt(i, 2).toString();
                writer.write(ID + ";" + nombre + ";" + correo);
                writer.newLine();
            }
            JOptionPane.showMessageDialog(this, "Datos guardados en archivo plano correctamente.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

   
    private void leerArchivoTXT() {
        try (BufferedReader reader = new BufferedReader(new FileReader("personas.txt"))) {
            String linea;
            modeloTabla.setRowCount(0); 
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length == 3) {
                    modeloTabla.addRow(new Object[]{datos[0], datos[1], datos[2]});
                }
            }
            JOptionPane.showMessageDialog(this, "Datos cargados desde el archivo plano correctamente.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void guardarXML() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = builder.newDocument();
            Element rootElement = doc.createElement("Personas");
            doc.appendChild(rootElement);

            int rowCount = modeloTabla.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                Element persona = doc.createElement("Persona");

                Element id = doc.createElement("ID");
                id.appendChild(doc.createTextNode(modeloTabla.getValueAt(i, 0).toString()));
                persona.appendChild(id);

                Element nombre = doc.createElement("Nombre");
                nombre.appendChild(doc.createTextNode(modeloTabla.getValueAt(i, 1).toString()));
                persona.appendChild(nombre);

                Element correo = doc.createElement("Correo");
                correo.appendChild(doc.createTextNode(modeloTabla.getValueAt(i, 2).toString()));
                persona.appendChild(correo);

                rootElement.appendChild(persona);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("Personas.xml"));

            transformer.transform(source, result);

            JOptionPane.showMessageDialog(this, "Datos guardados exitosamente.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el archivo XML.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void leerXML() {
        try {
            File archivo = new File("Personas.xml");
            if (!archivo.exists()) {
                throw new FileNotFoundException();
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(archivo);

            NodeList listaPersonas = doc.getElementsByTagName("Persona");
            modeloTabla.setRowCount(0);

            for (int i = 0; i < listaPersonas.getLength(); i++) {
                Node nodo = listaPersonas.item(i);
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) nodo;

                    String ID = elemento.getElementsByTagName("ID").item(0).getTextContent();
                    String nombre = elemento.getElementsByTagName("Nombre").item(0).getTextContent();
                    String correo = elemento.getElementsByTagName("Correo").item(0).getTextContent();

                    modeloTabla.addRow(new Object[]{ID, nombre, correo});
                }
            }
            JOptionPane.showMessageDialog(this, "Datos cargados desde el archivo XML correctamente.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo XML.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void guardarJSON() {
    List<Persona> personas = new ArrayList<>();
    int rowCount = modeloTabla.getRowCount();
    for (int i = 0; i < rowCount; i++) {
        String ID = modeloTabla.getValueAt(i, 0).toString();
        String nombre = modeloTabla.getValueAt(i, 1).toString();
        String correo = modeloTabla.getValueAt(i, 2).toString();
        personas.add(new Persona(ID, nombre, correo));
    }

    try (Writer writer = new FileWriter("personas.json")) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
        gson.toJson(personas, writer);
        JOptionPane.showMessageDialog(this, "Datos guardados en archivo JSON correctamente con formato.");
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error al guardar el archivo JSON.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    
    private void leerJSON() {
    try (Reader reader = new FileReader("personas.json")) {
        Gson gson = new Gson();
        Persona[] personasArray = gson.fromJson(reader, Persona[].class); 

        modeloTabla.setRowCount(0); 
        for (Persona persona : personasArray) {
            modeloTabla.addRow(new Object[]{persona.getID(), persona.getNombre(), persona.getCorreo()});
        }
        JOptionPane.showMessageDialog(this, "Datos desde el archivo JSON correctamente cargados.");
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error al leer el archivo JSON.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    public class CorreoInvalidoException extends Exception {
    public CorreoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
    
    private void validacionCorreo(String correo) throws CorreoInvalidoException {
    if (!correo.contains("@") || !correo.endsWith(".com")) {
        throw new CorreoInvalidoException("El correo es inválido. Debe contener '@' y '.com'.");
    }
}

  
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TablaPersonas tabla = new TablaPersonas();
            tabla.setVisible(true);
        });
    }
}
