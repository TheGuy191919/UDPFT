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
import java.util.AbstractQueue;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author evan__000
 */
public class UDPSender implements AbstractProtocolSender {

    private int numberOfSockets = -1;
    private InetAddress address;
    private int port = 58394;
    private MulticastSocket socket;
    private AbstractCrypto crypto;
    private List<Sender> listOfSender = new LinkedList<>();

    public void start() {

    }

    public void stop() {

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
        synchronized (this) {
            for (Sender sender : this.listOfSender) {
                sender.send(bytearray);
            }
            this.notifyAll();
        }
    }

    @Override
    public void send(Protocol protocol) {
        this.send(protocol.returnByteArray());
    }

}

class Sender implements Runnable {

    private AbstractQueue<byte[]> que = new PriorityBlockingQueue();
    private Thread thread;
    private boolean running;
    private InetAddress address;
    private int threadNumber;
    private int port = 58394;
    private MulticastSocket socket;
    private AbstractCrypto crypto;
    private UDPSender sender;

    public Sender(InetAddress address, UDPSender sender, int number, int port, AbstractCrypto crypto) {
        try {
            this.sender = sender;
            this.address = address;
            this.threadNumber = number;
            this.port = port;
            this.crypto = crypto;
            this.socket = new MulticastSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void start() {
        try {
            this.thread = new Thread(this, "Sender at " + address.getHostAddress() + " with port " + this.port + "#" + this.threadNumber);
            this.running = true;
            this.socket.joinGroup(address);
            this.thread.start();
        } catch (IOException ex) {
            Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while (this.running) {
            try {
                synchronized (sender) {
                    while (!this.que.isEmpty()) {
                        byte[] bytearray = this.que.poll();
                        bytearray = this.crypto.decrypt(bytearray);
                        try {
                            this.socket.send(new DatagramPacket(bytearray, bytearray.length, this.address, this.port));
                        } catch (IOException ex) {
                            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    this.wait();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void stop() {
        this.running = false;
        this.thread.interrupt();
        socket.close();
        thread = null;
    }

    public void send(byte[] array) {
        this.que.add(array);
    }

}
