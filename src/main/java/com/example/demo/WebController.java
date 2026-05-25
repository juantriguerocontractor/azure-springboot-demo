package com.example.demo;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class WebController {

    @GetMapping("/datos-azure")
    public Map<String, String> obtenerDatosAzure() {
        Map<String, String> resultado = new HashMap<>();

        // 1. Leer Variable de Entorno de Azure Web App (Color de fondo)
        String colorFondo = System.getenv("COLOR_FONDO");
        if (colorFondo == null || colorFondo.trim().isEmpty()) {
            colorFondo = "#f0f2f5"; // Gris claro estético por defecto para local
        }
        resultado.put("color", colorFondo);

        // 2. Conectar a Azure Storage y traer un dato de texto
        String connectionString = System.getenv("AZURE_STORAGE_CONNECTION_STRING");
        
        if (connectionString != null && !connectionString.trim().isEmpty()) {
            try {
                // Inicializar cliente del servicio Azure Blob Storage
                BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                        .connectionString(connectionString)
                        .buildClient();

                // Busca el contenedor llamado 'textos' y el blob 'mensaje.txt'
                BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("textos");
                BlobClient blobClient = containerClient.getBlobClient("mensaje.txt");

                // Descargar el flujo de bytes del archivo
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                blobClient.downloadStream(outputStream);
                String textoStorage = outputStream.toString(StandardCharsets.UTF_8);

                resultado.put("texto", textoStorage);
                resultado.put("status", "Conectado a Azure exitosamente");
            } catch (Exception e) {
                resultado.put("texto", "No se pudo leer el archivo 'mensaje.txt' del contenedor 'textos'.");
                resultado.put("status", "Error de conexión: " + e.getMessage());
            }
        } else {
            resultado.put("texto", "Texto de prueba local (Falta configurar AZURE_STORAGE_CONNECTION_STRING)");
            resultado.put("status", "Modo de desarrollo local");
        }

        return resultado;
    }
}
