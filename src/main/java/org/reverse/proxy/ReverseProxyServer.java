package org.reverse.proxy;

import io.undertow.Undertow;
import io.undertow.util.Headers;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class ReverseProxyServer {

    public static void main(String[] args) {

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");

            KeyStore keyStore = ReverseProxyServer.getKeyStore();

            sslContext.init(ReverseProxyServer.getKeyManager(keyStore),
                    ReverseProxyServer.getTrustManagers(null), null);

            Undertow server = Undertow
                    .builder()
                    .addHttpsListener(9030,"0.0.0.0", sslContext)
                    .setHandler((exchange) -> {
                            exchange.getResponseHeaders().add(Headers.CONTENT_TYPE,"text/plain");
                            exchange.getResponseSender().send("ssl funcionando");
                        }
                    )
                    .build();

            server.start();
        }
        catch (NoSuchAlgorithmException ex){ ex.printStackTrace();}
        catch (KeyManagementException ex){ ex.printStackTrace();}
        catch(KeyStoreException ex){ex.printStackTrace();}
        catch(CertificateException ex){ex.printStackTrace();}
        catch(IOException ex){ex.printStackTrace();}
        catch(UnrecoverableKeyException ex){ex.printStackTrace();}
    }

    private static KeyStore getKeyStore() throws KeyStoreException, IOException,
            NoSuchAlgorithmException, CertificateException{
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream("src/main/resources/localdois"),
                "123456".toCharArray());

        return keyStore;
    }

    private static KeyManager[] getKeyManager(KeyStore keyStore) throws KeyStoreException, NoSuchAlgorithmException,
            UnrecoverableKeyException {

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, "123456".toCharArray());

        return keyManagerFactory.getKeyManagers();
    }

    public static TrustManager[] getTrustManagers(KeyStore keyStore) throws NoSuchAlgorithmException, KeyStoreException{

        TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        return trustManagerFactory.getTrustManagers();
    }
}
