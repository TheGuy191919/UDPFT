/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.theguy191919.udpft.net;

import io.github.theguy191919.udpft.encryption.AbstractCrypto;
import io.github.theguy191919.udpft.protocol.Protocol;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.AbstractQueue;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

/**
 *
 * @author evan__000
 */
public class UDPSender implements AbstractProtocolSender, Runnable{
    
    private int numberOfSockets = -1;
    private InetAddress address;
    private int port = 58394;
    private MulticastSocket socket;
    private AbstractCrypto crypto;
    private AbstractQueue<byte[]> que = new PriorityBlockingQueue();
    
    public void start() {
        
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void stop(){
        
    }

    @Override
    public void setCrypto(AbstractCrypto crypto) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AbstractCrypto getCrypto() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void send(byte[] bytearray) {
        this.que.add(bytearray);
    }

    @Override
    public void send(Protocol protocol) {
        this.que.add(protocol.returnByteArray());
    }
    
}
