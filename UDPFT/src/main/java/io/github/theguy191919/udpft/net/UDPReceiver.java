/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.theguy191919.udpft.net;

import io.github.theguy191919.udpft.encryption.AbstractCrypto;
import io.github.theguy191919.udpft.protocol.Protocol;
import io.github.theguy191919.udpft.protocol.ProtocolEventListener;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author evan__000
 */
public class UDPReceiver implements AbstractProtocolReceiver, Runnable{

    Map<ProtocolEventListener, Integer> mapOfListener = new ConcurrentHashMap();
    
    private List<Thread> thread = new LinkedList<>();
    private boolean running = false;
    private List<InetAddress> address = new LinkedList<>();
    private int port = 58394;
    private MulticastSocket socket;
    private AbstractCrypto crypto;
    
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
    
    public void start(){
        for(int a = 0; a < this.address.size(); a++){
                this
            }
    }

    @Override
    public void run() {
        //listen for an arry of things, delete repeted messages, bases on hash, rence and repeat
        while(running){
            
            
        }
    }
    
    public void stop(){
        
    }

    @Override
    public void setCrypto(AbstractCrypto crypto) {
        this.crypto = crypto;
    }

    @Override
    public AbstractCrypto getCrypto() {
        return this.crypto;
    }
    
    private void messageGotten(Protocol protocol){
        List<ProtocolEventListener> arrayOfListener = this.getForValue(protocol.getProtocolNumber());
        arrayOfListener.addAll(this.getForValue(-1));
        for(ProtocolEventListener listener : arrayOfListener){
            listener.gotEvent(protocol);
        }
    }
    
    private List<ProtocolEventListener> getForValue(int value){
        List<ProtocolEventListener> arrayOfMatch = new LinkedList<>();
        Iterator it = this.mapOfListener.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pairs = (Map.Entry)it.next();
            if(((Integer)pairs.getValue()).equals(value)){
                arrayOfMatch.add((ProtocolEventListener)pairs.getKey());
            }
        }
        return arrayOfMatch;
    }
    
}
