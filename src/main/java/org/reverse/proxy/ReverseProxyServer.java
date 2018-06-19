package org.reverse.proxy;

import io.undertow.Undertow;
import io.undertow.server.handlers.proxy.LoadBalancingProxyClient;
import io.undertow.server.handlers.proxy.ProxyHandler;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.security.*;
import java.security.cert.CertificateException;

public class ReverseProxyServer {

    public static void main(String[] args) {

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");

            KeyStore keyStore = ReverseProxyServer.getKeyStore();

            sslContext.init(ReverseProxyServer.getKeyManager(keyStore),
                    null, null);

            LoadBalancingProxyClient loadBalancer  = new LoadBalancingProxyClient();
            loadBalancer.addHost(new URI("http://localhost:8030"));
            loadBalancer.setConnectionsPerThread(20);

            Undertow server = Undertow
                    .builder()
                    .addHttpsListener(80, "0.0.0.0", sslContext)
                    .setHandler(ProxyHandler.builder().setProxyClient(loadBalancer).build())
                    .build();

            server.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static KeyStore getKeyStore() throws KeyStoreException, IOException,
            NoSuchAlgorithmException, CertificateException{
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream("src/main/resources/xkeystore.jks"),
                "123456".toCharArray());

        return keyStore;
    }

    private static KeyManager[] getKeyManager(KeyStore keyStore) throws KeyStoreException, NoSuchAlgorithmException,
            UnrecoverableKeyException {

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, "123456".toCharArray());

        X509ExtendedKeyManager extendedKeyManager = null;
        for(KeyManager km : keyManagerFactory.getKeyManagers()){
            if(km instanceof X509ExtendedKeyManager){
                extendedKeyManager = (X509ExtendedKeyManager)km;
            }
        }

        XKeyManager xKeyManager = new XKeyManager(extendedKeyManager);

        return new KeyManager[]{ xKeyManager};
    }
}
