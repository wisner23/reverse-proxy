package org.reverse.proxy;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class NettySslEngine {


    public static SSLContext getSSLContext() throws Exception {

        KeyStore keyStore = getKeyStore();
        SSLContext sslContext = SSLContext.getInstance("TLS");

        KeyManager[] keyManagers = geKeyManagers(keyStore);
        sslContext.init(keyManagers, getTrustManagers(keyStore), null);

        return sslContext;
    }

    public static KeyStore getKeyStore() throws NoSuchAlgorithmException, CertificateException,
            KeyStoreException, IOException {

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream("src/main/resources/xkeystore.jks"), "123456".toCharArray());

        return keyStore;
    }

    public static KeyManager[] geKeyManagers(KeyStore keyStore) throws Exception {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, "123456".toCharArray());

        X509ExtendedKeyManager x509ExtendedKeyManager = null;
        for(KeyManager keyManager : keyManagerFactory.getKeyManagers()){
            if(keyManager instanceof X509ExtendedKeyManager){
                x509ExtendedKeyManager = (X509ExtendedKeyManager)keyManager;
            }
        }

        if (x509ExtendedKeyManager == null)
            throw new Exception("KeyManagerFactory did not create an X509ExtendedKeyManager");

        XKeyManager xKeyManager = new XKeyManager(x509ExtendedKeyManager);
        return new KeyManager[]{xKeyManager};
    }

    public static TrustManager[] getTrustManagers(KeyStore keyStore) throws NoSuchAlgorithmException,
            KeyStoreException{
        TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());

        trustManagerFactory.init(keyStore);

        X509ExtendedTrustManager x509ExtendedTrustManager = null;
        for(TrustManager trustManager : trustManagerFactory.getTrustManagers()){
            if(trustManager instanceof X509ExtendedTrustManager){
                x509ExtendedTrustManager = (X509ExtendedTrustManager)trustManager;
            }
        }

        XTrustManager xTrustManager = new XTrustManager(x509ExtendedTrustManager);
        return new TrustManager[]{ xTrustManager };
    }
}
