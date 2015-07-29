/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.theguy191919.udpft2.net;

import io.github.theguy191919.udpft.encryption.AbstractCrypto;
import io.github.theguy191919.udpft2.protocol.Protocol;
import io.github.theguy191919.udpft2.protocol.ProtocolEventListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author root
 */
public class Protocol2SenderReceiver implements AbstractProtocolReceiver, AbstractProtocolSender, Runnable{
    
    private List<ProtocolEventListener> listOfListener = new LinkedList();
    private AbstractQueue<SentContent> que = new LinkedBlockingDeque();
    private AbstractCrypto crypto;
    private String defaultURL;
    private Thread thread;
    private boolean running = false;
    CloseableHttpClient client = HttpClients.createSystem();

    public Protocol2SenderReceiver(AbstractCrypto crypto){
        this.crypto = crypto;
        this.defaultURL = "localhost";
    }
    
    public Protocol2SenderReceiver(AbstractCrypto crypto, String defaultURL){
        this.crypto = crypto;
        this.defaultURL = defaultURL;
    }
    
    @Override
    public void addListener(ProtocolEventListener listener) {
        this.listOfListener.add(listener);
    }

//    @Override
//    public void addListener(ProtocolEventListener listener, int listenFor) {
//        this.listOfListener.put(listener, listenFor);
//    }

    @Override
    public void start() {
        this.running = true;
        this.thread = new Thread(this, "HTTP Sender Receiver to url:\"" + this.defaultURL + "\"");
        thread.start();
    }

    @Override
    public void stop() {
        this.running = false;
        this.thread.interrupt();
        this.thread = null;
    }

    @Override
    public void removeListener(ProtocolEventListener listener) {
        this.listOfListener.remove(listener);
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
    public void send(byte[] bytearray) {
        this.send(bytearray, defaultURL);
    }
    
    public void send(byte[] bytearray, String url){
        try {
            this.que.add(new SentContent(url, new String(bytearray, "ISO-8859-1")));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Protocol2SenderReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void send(Protocol protocol) {
        this.send(protocol, defaultURL);
    }
    
    public void send(Protocol protocol, String url){
        this.que.add(new SentContent(url, protocol));
    }

    @Override
    public void run() {
                    int sleepFor = 100;
        while (this.running) {
            sleepFor += 10;
            while (!this.que.isEmpty()) {
                sleepFor -= 50;
                Protocol2SenderReceiver.SentContent thing = this.que.poll();
                if(thing == null){
                    break;
                }
                String bytearray = thing.getMessage();
                String url = thing.getUrl();
                //this.crypto.encrypt(bytearray);
                
                //this.printArray("Sending message", bytearray, "End of message");
                
                HttpPost post = new HttpPost(url);
                
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                nvps.add(new BasicNameValuePair("username", thing.getUser()));
                nvps.add(new BasicNameValuePair("goal", thing.getGoal()));
                nvps.add(new BasicNameValuePair("data", thing.getMessage()));
//                try {
//                    nvps.add(new BasicNameValuePair("data", new String(bytearray, "ISO-8859-1")));
//                } catch (UnsupportedEncodingException ex) {
//                    Logger.getLogger(HttpSenderReceiver.class.getName()).log(Level.SEVERE, null, ex);
//                }
                try {
                    post.setEntity(new UrlEncodedFormEntity(nvps));
                    
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(Protocol2SenderReceiver.class.getName()).log(Level.SEVERE, null, ex);
                }
                //post.setEntity(new ByteArrayEntity(bytearray));
                try {
                    HttpResponse response = client.execute(post);
                    String reply = EntityUtils.toString(response.getEntity());
                    this.messageGotten(reply);
                } catch (IOException ex) {
                    Logger.getLogger(Protocol2SenderReceiver.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(SentContent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    protected void messageGotten(String message) {
        Protocol protocol = new Protocol(message);
        for(int a = 0; a < this.listOfListener.size(); a++){
            this.listOfListener.get(a).gotEvent(protocol);
        }
        //this.printArray("Recieved message", this.crypto.decrypt(array), "End of print");
//        try {
//            Protocol protocol = Protocol.getProtocol(this.crypto.decrypt(array));
//            List<ProtocolEventListener> arrayOfListener = this.getForValue(protocol.getProtocolNumber());
//            arrayOfListeer.addAll(this.getForValue(-1));
//            for (ProtocolEventListener listener : arrayOfListener) {
//                listener.gotEvent(protocol);
//            }
//        } catch (IllegalAccessException | InstantiationException ex) {
//            Logger.getLogger(UDPReceiver.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
    }

//    private List<ProtocolEventListener> getForValue(int value) {
//        List<ProtocolEventListener> arrayOfMatch = new LinkedList<>();
//        Iterator it = this.listOfListener.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pairs = (Map.Entry) it.next();
//            if (((Integer) pairs.getValue()).equals(value)) {
//                arrayOfMatch.add((ProtocolEventListener) pairs.getKey());
//            }
//        }
//        return arrayOfMatch;
//    }
    
    private void printArray(String ini, byte[] array, String end){
        System.out.println(ini);
        for(int a = 0; a < array.length; a++){
            System.out.println(a + ": " + array[a]);
        }
        System.out.println(end);
    }
    
    private class SentContent{
    
    private String url = "";
    private String message;
    private Protocol protocol;
    
    private boolean usingProtocol;
    
    public SentContent(String url, String message){
        this.url = url;
        this.message = message;
        this.usingProtocol = false;
    }
    
    public SentContent(String url, Protocol protocol){
        this.url = url;
        this.protocol = protocol;
        this.usingProtocol = true;
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
//    public void setUrl(String url) {
//        this.url = url;
//    }

    /**
     * @return the message
     */
    public String getMessage() {
        if(this.usingProtocol){
            return this.protocol.getData();
        }
        return message;
    }
    
    public String getUser(){
        if(this.usingProtocol){
            return this.protocol.getOwnerName();
        }
        return null;
    }
    
    public String getGoal(){
        if(this.usingProtocol){
            return this.protocol.getGoal().toString();
        }
        return null;
    }

    /**
     * @param message the message to set
     */
//    public void setMessage(String message) {
//        this.message = message;
//    }
}
}
