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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yiwen Dong
 */
@Deprecated
public class ByteSender implements AbstractProtocolSender{
    
    private int numberOfSockets = -1;
    private InetAddress address;
    private int port = 58394;
    private MulticastSocket socket;
    private AbstractCrypto crypto;
    
    public ByteSender(){
        try {
            this.address = InetAddress.getByName("234.235.236.237");
            InetAddress[] address = InetAddress.getAllByName("234.235.236.237");
            socket = new MulticastSocket(port);
        } catch (UnknownHostException | SocketException ex) {
            Logger.getLogger(ByteSender.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ByteSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public ByteSender(int port){
        try {
            this.address = InetAddress.getByName("234.235.236.237");
            this.port = port;
            socket = new MulticastSocket(port);
        } catch (UnknownHostException | SocketException ex) {
            Logger.getLogger(ByteSender.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ByteSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public ByteSender(InetAddress address, int port){
        try {
            this.address = address;
            this.port = port;
            socket = new MulticastSocket(port);
        } catch (SocketException ex) {
            Logger.getLogger(ByteSender.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ByteSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void joinGroup(){
        try {
            socket.joinGroup(address);
        } catch (IOException ex) {
            Logger.getLogger(ByteSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void joinGroup(InetAddress address){
        try {
            this.address = address;
            socket.joinGroup(this.address);
        } catch (IOException ex) {
            Logger.getLogger(ByteSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void leaveGroup(){
        try {
            this.socket.leaveGroup(address);
        } catch (IOException ex) {
            Logger.getLogger(ByteSender.class.getName()).log(Level.SEVERE, null, ex);
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
    public void send(byte[] bytearray) {
        try {
            if(this.crypto != null){
                bytearray = crypto.encrypt(bytearray);
            }
            this.socket.send(new DatagramPacket(bytearray, bytearray.length, this.address, this.port));
        } catch (IOException ex) {
            Logger.getLogger(ByteSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    @Override
//    public void addListener(ProtocolEventListener listener) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public void addListener(ProtocolEventListener listener, int listenFor) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

    @Override
    public AbstractCrypto getCrypto() {
        return this.crypto;
    }

    @Override
    public void send(Protocol protocol) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
