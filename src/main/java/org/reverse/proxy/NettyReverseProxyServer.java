package org.reverse.proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SniHandler;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class NettyReverseProxyServer {

    public static void main(String[] args) {
        try{
            SSLEngine engine = NettyReverseProxyServer.getSSLContext().createSSLEngine();
            engine.setUseClientMode(false);


            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(new NioEventLoopGroup());
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) {
                    ch.pipeline().addLast(
                            new SslHandler(engine),
                            new NettyChannelHandler()
                    );
                }
            });
            bootstrap.bind(80);
        }
        catch (NoSuchAlgorithmException ex){ ex.printStackTrace();}
        catch (KeyManagementException ex){ ex.printStackTrace();}
        catch(KeyStoreException ex){ex.printStackTrace();}
        catch(CertificateException ex){ex.printStackTrace();}
        catch(IOException ex){ex.printStackTrace();}
        catch(UnrecoverableKeyException ex){ex.printStackTrace();}
    }

    public static SSLContext getSSLContext() throws NoSuchAlgorithmException,
            KeyStoreException, UnrecoverableKeyException, KeyManagementException, CertificateException, IOException {

        KeyStore keyStore = NettyReverseProxyServer.getKeyStore();

        SSLContext sslContext = SSLContext.getInstance("TLS");

        sslContext.init(NettyReverseProxyServer.geKeyManagers(keyStore),
                NettyReverseProxyServer.getTrustManagers(keyStore), null);

        return sslContext;
    }

    public static KeyStore getKeyStore() throws NoSuchAlgorithmException, CertificateException,
            KeyStoreException, IOException {

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream("src/main/resources/localdois"), "123456".toCharArray());

        return keyStore;
    }

    public static KeyManager[] geKeyManagers(KeyStore keyStore) throws NoSuchAlgorithmException,
            UnrecoverableKeyException, KeyStoreException {
        KeyManagerFactory keyManager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManager.init(keyStore, "123456".toCharArray());

        return keyManager.getKeyManagers();
    }

    public static TrustManager[] getTrustManagers(KeyStore keyStore) throws NoSuchAlgorithmException,
            KeyStoreException{
        TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());

        trustManagerFactory.init(keyStore);
        return trustManagerFactory.getTrustManagers();
    }

}
