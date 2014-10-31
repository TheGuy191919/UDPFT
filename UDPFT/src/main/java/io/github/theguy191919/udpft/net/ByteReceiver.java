/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package io.github.theguy191919.udpft.net;

import io.github.theguy191919.udpft.encryption.AbstractCrypto;
import io.github.theguy191919.udpft.protocol.Protocol;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yiwen Dong
 */
public class ByteReceiver implements AbstractProtocolReceiver, Runnable{
    
    private Thread thread;
    private boolean running = false;
    private InetAddress address;
    private int port = 58394;
    private MulticastSocket socket;
    private AbstractCrypto crypto;
    
    public ByteReceiver(){
        try {
            this.address = InetAddress.getByName("234.235.236.237");
            socket = new MulticastSocket(this.port);
        } catch (UnknownHostException ex) {
            Logger.getLogger(ByteReceiver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ByteReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ByteReceiver(int port){
        try {
            this.port = port;
            this.address = InetAddress.getByName("234.235.236.237");
            socket = new MulticastSocket(this.port);
        } catch (UnknownHostException ex) {
            Logger.getLogger(ByteReceiver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ByteReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ByteReceiver(InetAddress address, int port){
        try {
            this.address = address;
            this.port = port;
            socket = new MulticastSocket(this.port);
        } catch (IOException ex) {
            Logger.getLogger(ByteReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void startReceiver(){
        try {
            this.thread = new Thread(this, "Reveiver at " + this.address.getHostAddress() + " at port " + port);
            socket.joinGroup(address);
            this.running = true;
            this.thread.start();
        } catch (IOException ex) {
            Logger.getLogger(ByteReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void startReceiver(InetAddress address){
        try {
            this.address = address;
            this.thread = new Thread(this, "Reveiver at " + this.address.getHostAddress() + " at port " + port);
            socket.joinGroup(address);
            this.running = true;
            this.thread.start();
        } catch (IOException ex) {
            Logger.getLogger(ByteReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setPort(int port){
        this.port = port;
    }
    
    public int getPort(){
        return this.port;
    }
    
    public void setInetAddress(InetAddress address){
        this.address = address;
    }
    
    public InetAddress getInetAddress(){
        return this.address;
    }
    
    public void setCrypto(AbstractCrypto crypto){
        this.crypto = crypto;
    }
    
    @Override
    public void run() {
        while(this.running){
            try {
                byte[] buffer = new byte[500];
                socket.receive(new DatagramPacket(buffer, buffer.length, this.address, this.port));
                if(this.crypto != null){
                    buffer = crypto.decrypt(buffer);
                }
                Protocol.getProtocol(buffer);
            } catch (IOException | IllegalAccessException | InstantiationException ex) {
                Logger.getLogger(ByteReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void stopReveiver(){
        this.running = false;
        socket.close();
        thread = null;
    }
    
}
