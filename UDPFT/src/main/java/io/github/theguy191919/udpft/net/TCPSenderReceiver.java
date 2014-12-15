/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.theguy191919.udpft.net;

import io.github.theguy191919.udpft.encryption.AbstractCrypto;
import io.github.theguy191919.udpft.protocol.Protocol;
import io.github.theguy191919.udpft.protocol.ProtocolEventListener;
import java.io.IOException;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author evan__000
 */
public class TCPSenderReceiver implements AbstractProtocolSender, AbstractProtocolReceiver, Runnable{

    private Map<ProtocolEventListener, Integer> mapOfListener = new ConcurrentHashMap();
    private AbstractQueue<SentContent> que = new LinkedBlockingDeque();
    private AbstractCrypto crypto;
    private Thread thread;
    private boolean running = false;
    private String defaultURL;
    CloseableHttpClient client = HttpClients.createSystem();
    
    public TCPSenderReceiver(AbstractCrypto crypto){
        this.crypto = crypto;
        this.defaultURL = "localhost";
    }
    
    public TCPSenderReceiver(AbstractCrypto crypto, String defaultURL){
        this.defaultURL = defaultURL;
        this.crypto = crypto;
    }
    
    @Override
    public void send(byte[] bytearray) {
        this.que.add(new SentContent(this.defaultURL, bytearray));
    }
    
    public void send(byte[] bytearray, String url){
        this.que.add(new SentContent(url, bytearray));
    }

    @Override
    public void send(Protocol protocol) {
        this.send(protocol.returnByteArray());
    }
    
    public void send(Protocol protocol, String url){
        this.send(protocol.returnByteArray(), url);
    }

    @Override
    public void start() {
        this.running = true;
        this.thread = new Thread(this, "TCP Sender Receiver to url:\"" + this.defaultURL + "\"");
        thread.start();
    }
    
    @Override
    public void run() {
                int sleepFor = 100;
        while (this.running) {
            sleepFor += 10;
            while (!this.que.isEmpty()) {
                sleepFor -= 50;
                SentContent thing = this.que.poll();
                if(thing == null){
                    break;
                }
                byte[] bytearray = thing.getMessage();
                String url = thing.getUrl();
                this.crypto.encrypt(bytearray);
                
                HttpPost post = new HttpPost(url);
                //List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                //nvps.add(new BasicNameValuePair("data", ));
                post.setEntity(new ByteArrayEntity(bytearray));
                try {
                    HttpResponse response = client.execute(post);
                    byte[] reply = EntityUtils.toByteArray(response.getEntity());
                    this.messageGotten(reply);
                } catch (IOException ex) {
                    Logger.getLogger(TCPSenderReceiver.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (sleepFor < 0) {
                sleepFor = 0;
            } else if (sleepFor > 500) {
                sleepFor = 1000;
            }
            try {
                Thread.sleep(sleepFor);
            } catch (InterruptedException ex) {
                Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    @Override
    public void stop() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCrypto(AbstractCrypto crypto) {
        this.crypto = crypto;
    }

    @Override
    public AbstractCrypto getCrypto() {
        return this.crypto;
    }

    @Override
    public void addListener(ProtocolEventListener listener) {
        this.mapOfListener.put(listener, -1);
    }

    @Override
    public void addListener(ProtocolEventListener listener, int listenFor) {
        this.mapOfListener.put(listener, listenFor);
    }
    
    @Override
    public void removeListener(ProtocolEventListener listener) {
        this.mapOfListener.remove(listener);
    }
    
    protected void messageGotten(byte[] array) {
        try {
            Protocol protocol = Protocol.getProtocol(this.crypto.decrypt(array));
            List<ProtocolEventListener> arrayOfListener = this.getForValue(protocol.getProtocolNumber());
            arrayOfListener.addAll(this.getForValue(-1));
            for (ProtocolEventListener listener : arrayOfListener) {
                listener.gotEvent(protocol);
            }
        } catch (IllegalAccessException | InstantiationException ex) {
            Logger.getLogger(UDPReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<ProtocolEventListener> getForValue(int value) {
        List<ProtocolEventListener> arrayOfMatch = new LinkedList<>();
        Iterator it = this.mapOfListener.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            if (((Integer) pairs.getValue()).equals(value)) {
                arrayOfMatch.add((ProtocolEventListener) pairs.getKey());
            }
        }
        return arrayOfMatch;
    }
}

class SentContent{
    
    private String url = "";
    private byte[] message;
    
    public SentContent(String url, byte[] message){
        this.url = url;
        this.message = message;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the message
     */
    public byte[] getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(byte[] message) {
        this.message = message;
    }
}