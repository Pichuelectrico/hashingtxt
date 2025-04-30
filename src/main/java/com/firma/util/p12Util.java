package com.firma.util;

import java.io.FileInputStream;

import com.firma.model.P12Entry;

import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;

import java.math.BigInteger;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.security.SecureRandom;

public class p12Util {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void crearArchivoP12(String nombre, String password, String archivoSalida, int tries) throws Exception {
        // Generar el par de claves
        KeyPair keyPair = generarParDeClaves();
        PrivateKey privateKey = keyPair.getPrivate();

        // Crear un KeyStore tipo PKCS12
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(null, null); // inicializar
        for (int i = 0; i < tries; i++) {
            generarParDeClaves();
        }
        // Generar un certificado autofirmado usando la clave pública del par generado
        X509Certificate cert = generateSelfSignedCertificate(keyPair, "CN=Test User, O=MyCompany, C=EC" + nombre, 365);

        // Agregar la clave privada con un alias (el nombre)
        ks.setKeyEntry(nombre, privateKey, password.toCharArray(), new Certificate[]{cert});

        // Guardar el KeyStore en el archivo .p12
        try (FileOutputStream fos = new FileOutputStream(archivoSalida)) {
            ks.store(fos, password.toCharArray());
        }

        System.out.println("Archivo .p12 creado correctamente en: " + archivoSalida);
    }

    public static KeyPair generarParDeClaves() throws Exception {
        // Crear un generador de pares de claves para RSA
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");

        // Utilizar una semilla aleatoria basada en el tiempo actual
        SecureRandom random = new SecureRandom();
        keyGen.initialize(2048, random); // Tamaño de clave de 2048 bits

        // Generar el par de claves
        KeyPair pair = keyGen.generateKeyPair();

        return pair;
    }

    public static X509Certificate generateSelfSignedCertificate(KeyPair keyPair, String dn, int days) throws Exception {
        // Fechas de validez
        long now = System.currentTimeMillis();
        Date notBefore = new Date(now);
        Date notAfter = new Date(now + days * 86400000L);

        // Nombre del sujeto/emisor (es el mismo para autofirmado)
        X500Name issuer = new X500Name(dn);

        // Número de serie aleatorio
        BigInteger serial = BigInteger.valueOf(now);

        // Construcción del certificado
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuer,
                serial,
                notBefore,
                notAfter,
                issuer,
                keyPair.getPublic()
        );

        // Firma
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .setProvider("BC")
                .build(keyPair.getPrivate());

        // Convertir a X509Certificate (tipo estándar Java)
        X509Certificate cert = new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(certBuilder.build(signer));

        cert.verify(keyPair.getPublic()); // Verificar la firma

        return cert;
    }

    public static List<P12Entry> mostrarContenidoP12(String archivoP12, String password) throws Exception {
        List<P12Entry> entries = new ArrayList<>();
    
        // Load the KeyStore from the .p12 file
        KeyStore ks = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(archivoP12)) {
            ks.load(fis, password.toCharArray());
        }
    
        Enumeration<String> aliases = ks.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
    
            if (ks.isKeyEntry(alias)) {
                KeyStore.Entry entry = ks.getEntry(alias, new KeyStore.PasswordProtection(password.toCharArray()));
    
                if (entry instanceof KeyStore.PrivateKeyEntry privateKeyEntry) {
                    PrivateKey privateKey = privateKeyEntry.getPrivateKey();
                    Certificate[] chain = ks.getCertificateChain(alias);
                    X509Certificate certificate = (X509Certificate) chain[0]; // Assuming the first certificate is the signed certificate
                    entries.add(new P12Entry(alias, privateKey, certificate));
                }
            }
        }
        return entries;
    }

    public static void main(String[] args) throws Exception {
        // Datos de ejemplo
        String nombre = "usuario1";
        String hashBase64 = Base64.getEncoder().encodeToString("valorSecreto".getBytes());
        String password = "123456";
        String archivoSalida = "c:\\Users\\alijo\\Downloads\\usuario4.p12";

        System.out.println("Hash (Base64): " + hashBase64 + " Nombre: " + nombre);

        // Crear el archivo.p12
        crearArchivoP12(nombre, password, archivoSalida, 6);

        List<P12Entry> contenidos = mostrarContenidoP12(archivoSalida, password);
        System.out.println("Contenido del archivo.p12:");
        for (P12Entry entry : contenidos) {
            System.out.println(entry.toString());
        }
    }
}
