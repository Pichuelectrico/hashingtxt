package com.firma.util;

import java.io.File;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.AccessPermissions;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.IExternalDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.SignerProperties;
import com.firma.model.P12Entry;

public class Firmador {
    public static void main(String[] args) {
        try {
            // Ruta al archivo PDF que se va a firmar
            String filePath = "C:\\Users\\alijo\\Downloads\\test.pdf";
            // Nombre del firmante
            String signerName = "Alejo";
            String psswrd = "123456";
            String certificatePath = "C:\\Users\\alijo\\Downloads\\usuario4.p12";

            P12Entry entry = p12Util.mostrarContenidoP12(certificatePath, psswrd).get(0);

            PrivateKey privateKey = entry.getKey();
            X509Certificate certificate = entry.getCertificate();

            if (privateKey != null && certificate != null) {
                // Firmar el documento
                firmar(filePath, signerName, privateKey, certificate);
                System.out.println("Documento firmado exitosamente.");

                // Verificar la firma
                String signedFileName = filePath.replace(".pdf", "_firmado.pdf");
                String signatureInfo = obtenerFirma(signedFileName);
                System.out.println("Información de la firma:");
                System.out.println(signatureInfo);
            } else {
                System.out.println("No se pudo obtener la clave privada o el certificado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void firmar(String filePath, String signerName, PrivateKey pk, X509Certificate cert) throws Exception {
        // Obtener el directorio del archivo original
        File originalFile = new File(filePath);
        String parentDir = originalFile.getParent();
        String originalFileName = originalFile.getName();
        
        // Crear el nombre del archivo firmado
        String signedFileName = originalFileName.replace(".pdf", "_firmado.pdf");
        String dest = new File(parentDir, signedFileName).getAbsolutePath();
    
        // Crear el lector y escritor de PDF
        PdfReader reader = new PdfReader(originalFile);
    
        // Configurar el firmante
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties());
    
        // Crear la firma
        IExternalSignature pks = new PrivateKeySignature(pk, "SHA-256", null);
        IExternalDigest digest = new BouncyCastleDigest();
    
        // Añadir información adicional a la firma
        signer.setSignerProperties(
                new SignerProperties()
                .setCertificationLevel(AccessPermissions.UNSPECIFIED)
                .setFieldName(signerName)
                .setReason("Firmado por " + signerName)
                .setLocation("Fecha y hora: " + new java.util.Date().toString())
                );
    
        // Añadir el certificado al proceso de firma
        Certificate[] chain = new Certificate[]{cert};
        signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);
    }

    public static String obtenerFirma(String filePath) throws IOException {
        PdfReader reader = new PdfReader(new File(filePath));
        PdfDocument pdfDoc = new PdfDocument(reader);
        SignatureUtil signUtil = new SignatureUtil(pdfDoc);
        
        String signatureInfo = "";
        
        if (signUtil.getSignatureNames().size() > 0) {
            // Get the first signature if multiple exist
            String sigName = signUtil.getSignatureNames().get(0);
            PdfPKCS7 pkcs7 = signUtil.readSignatureData(sigName);
            
            if (pkcs7 != null) {
                try {
                    signatureInfo = "Signature Name: " + sigName + "\n" +
                            "Signer: " + pkcs7.getSigningCertificate().getSubjectX500Principal() + "\n" +
                            "Timestamp: " + pkcs7.getSignDate().getTime() + "\n" +
                            "Signature Valid: " + pkcs7.verifySignatureIntegrityAndAuthenticity();
                } catch (GeneralSecurityException ex) {
                    System.err.println(ex);
                }
            }
        } else {
            signatureInfo = "No digital signature found in the document.";
        }
        
        pdfDoc.close();
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return signatureInfo;
    }
}