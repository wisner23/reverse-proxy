package org.reverse.proxy;

import javax.net.ssl.*;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class XKeyManager extends X509ExtendedKeyManager {

    public X509ExtendedKeyManager keyManager;

    public XKeyManager(X509ExtendedKeyManager keyManager){
        this.keyManager = keyManager;
    }

    @Override
    public String chooseEngineServerAlias(String s, Principal[] principals, SSLEngine sslEngine) {
        ExtendedSSLSession session = (ExtendedSSLSession) sslEngine.getHandshakeSession();

        String hostname = "";
        for (SNIServerName sniName : session.getRequestedServerNames()){
            if(sniName.getType() == StandardConstants.SNI_HOST_NAME){
                hostname = ((SNIHostName)sniName).getAsciiName();
                break;
            }
        }

        if (hostname != null && (getCertificateChain(hostname) != null && getPrivateKey(hostname) != null))
            return hostname;

        return "test.example.com";
    }

    @Override
    public String[] getClientAliases(String s, Principal[] principals) {
        return new String[0];
    }

    @Override
    public String chooseClientAlias(String[] strings, Principal[] principals, Socket socket) {
        return null;
    }

    @Override
    public String[] getServerAliases(String s, Principal[] principals) {
        return this.keyManager.getServerAliases(s, principals);
    }

    @Override
    public String chooseServerAlias(String s, Principal[] principals, Socket socket) {
        return null;
    }

    @Override
    public X509Certificate[] getCertificateChain(String s) {
        return keyManager.getCertificateChain(s);
    }

    @Override
    public PrivateKey getPrivateKey(String s) {
        return keyManager.getPrivateKey(s);
    }
}
