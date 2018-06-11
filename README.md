# Reverse proxy :octocat:

An implementation of https proxy server based on Java NIO via Netty.

That's a **simple** idea of how it works:

![alt text](https://github.com/wisner23/reverse-proxy/blob/master/reverse_proxy_flow.jpg)


To test it via browser, we will need setup a virtual hostname like this:

**127.0.0.1 test.localdomain**

So it will able the browser send the SNI to our netty server.

To test it via openssl, just use this command:
`openssl s_client -connect localhost:80 -servername test.localdomain 2>&1`

So you will be able to see if the protocol is matching with the SNI that we are sending.

**To have sure that our certificate is valid on google chrome and another web browsers, we need to add the P12 cert to root certifications settings from browser.**

  The P12 cert is located on src/main/resources 

To make a reverse proxy to a web server application in backend i've created a flask application that will run on **0.0.0.0:8030**
This is a simple application that will return a hello world.
This application can be cloned at:
https://github.com/wisner23/simple-flask


