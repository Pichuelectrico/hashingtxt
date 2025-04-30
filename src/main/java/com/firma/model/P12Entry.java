package com.firma.model;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class P12Entry {
    private String alias;
    private PrivateKey key;
    private X509Certificate certificate;

    public P12Entry(String alias, PrivateKey key, X509Certificate certificate) {
        this.alias = alias;
        this.key = key;
        this.certificate = certificate;
    }

    public String getAlias() {
        return alias;
    }

    public PrivateKey getKey() {
        return key;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    @Override
    public String toString() {
        return "Alias: " + alias + ", Hash (Base64): " + key.getEncoded() + ", Certificate: " + certificate.toString();
    }
}