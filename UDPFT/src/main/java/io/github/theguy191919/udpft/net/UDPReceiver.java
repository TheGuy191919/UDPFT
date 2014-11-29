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
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author evan__000
 */
public class UDPReceiver implements AbstractProtocolReceiver {

    Map<ProtocolEventListener, Integer> mapOfListener = new ConcurrentHashMap();

    private List<Receiver> receivers = new LinkedList<>();
    private List<InetAddress> address = new LinkedList<>();
    private int port = 58394;
    private MulticastSocket socket;
    private AbstractCrypto crypto;

    public UDPReceiver() {
        try {
            this.address = new LinkedList<InetAddress>(Arrays.asList(InetAddress.getAllByName("234.235.236.237")));
        } catch (UnknownHostException ex) {
            Logger.getLogger(UDPReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public UDPReceiver(String ip, int port) {
        try {
            this.address = new LinkedList<InetAddress>(Arrays.asList(InetAddress.getAllByName(ip)));
            this.port = port;
        } catch (UnknownHostException ex) {
            Logger.getLogger(UDPReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setInetAddress(String ip) {
        try {
            this.address = new LinkedList<InetAddress>(Arrays.asList(InetAddress.getAllByName(ip)));
        } catch (UnknownHostException ex) {
            Logger.getLogger(UDPReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getInetAddress() {
        return this.address.get(0).getHostAddress();
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return this.port;
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

    @Override
    public void start() {
        for (int a = 0; a < this.address.size(); a++) {
            Receiver rec = new Receiver(this.address.get(a), this, a, this.port, this.crypto);
            this.receivers.add(rec);
            rec.start();
        }
    }

    @Override
    public void stop() {
        for (int a = 0; a < this.receivers.size(); a++) {
            this.receivers.get(a).stop();
            this.receivers.remove(a);
        }
    }

    @Override
    public void setCrypto(AbstractCrypto crypto) {
        this.crypto = crypto;
    }

    @Override
    public AbstractCrypto getCrypto() {
        return this.crypto;
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

class Receiver implements Runnable {

    private UDPReceiver parent;
    private InetAddress address;
    private Thread thread;
    private int threadNumber;
    private int port = 58394;
    private boolean running = false;
    private MulticastSocket socket;
    private AbstractCrypto crypto;

    public Receiver(InetAddress address, UDPReceiver rec, int number, int port, AbstractCrypto crypto) {
        try {
            this.address = address;
            this.parent = rec;
            this.crypto = crypto;
            this.port = port;
            this.threadNumber = number;
            this.socket = new MulticastSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void start() {
        try {
            this.thread = new Thread(this, "Receiver at " + address.getHostAddress() + " with port " + this.port + "#" + this.threadNumber);
            this.running = true;
            this.socket.joinGroup(address);
            this.thread.start();
        } catch (IOException ex) {
            Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        //listen for an arry of things, delete repeted messages, bases on hash, rence and repeat
        while (running) {

            try {
                byte[] buffer = new byte[500];
                socket.receive(new DatagramPacket(buffer, buffer.length, this.address, this.port));
                this.parent.messageGotten(buffer);
            } catch (Exception ex) {
                Logger.getLogger(ByteReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void stop() {
        this.running = false;
        this.thread.interrupt();
        socket.close();
        thread = null;
    }
}
