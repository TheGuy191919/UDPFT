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
import java.util.AbstractQueue;
import java.util.Arrays;
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

    private List<InetAddress> address = new LinkedList<>();
    private int port = 58394;
    private MulticastSocket socket;
    private AbstractCrypto crypto;
    private List<Sender> listOfSender = new LinkedList<>();

    public UDPSender() {
        try {
            this.address = new LinkedList<>(Arrays.asList(InetAddress.getAllByName("234.235.236.237")));
        } catch (UnknownHostException ex) {
            Logger.getLogger(UDPReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public UDPSender(String ip, int port) {
        try {
            this.address = new LinkedList<>(Arrays.asList(InetAddress.getAllByName(ip)));
            this.port = port;
        } catch (UnknownHostException ex) {
            Logger.getLogger(UDPReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void start() {
        for (int a = 0; a < this.address.size(); a++) {
            Sender sender = new Sender(this.address.get(a), this, a, this.port, this.crypto);
            this.listOfSender.add(sender);
            sender.start();
        }
    }

    @Override
    public void stop() {
        for (int a = 0; a < this.listOfSender.size(); a++) {
            this.listOfSender.get(a).stop();
            this.listOfSender.remove(a);
        }
    }

    @Override
    public void setCrypto(AbstractCrypto crypto) {
        this.crypto = crypto;
        for (Sender sender : this.listOfSender) {
            sender.setCrypto(crypto);
        }
    }

    public synchronized byte[] encrypt(byte[] input) {
        return this.crypto.encrypt(input);
    }

    @Override
    public AbstractCrypto getCrypto() {
        return this.crypto;
    }

    @Override
    public void send(byte[] bytearray) {
        for (Sender sender : this.listOfSender) {
            sender.send(bytearray);
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
        int sleepFor = 1000;
        while (this.running) {
            sleepFor += 50;
                while (!this.que.isEmpty()) {
                    sleepFor -= 100;
                    byte[] bytearray = this.que.poll();
                    bytearray = sender.encrypt(bytearray);
                    try {
                        this.socket.send(new DatagramPacket(bytearray, bytearray.length, this.address, this.port));
                    } catch (IOException ex) {
                        Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if(sleepFor < 0){
                    sleepFor = 0;
                }
            try {
                Thread.sleep(sleepFor);
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

    public void setCrypto(AbstractCrypto crypto) {
        this.crypto = crypto;
    }

    public void send(byte[] array) {
        this.que.add(array);
    }

}
