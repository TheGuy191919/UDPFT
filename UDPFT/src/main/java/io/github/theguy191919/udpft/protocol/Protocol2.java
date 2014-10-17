/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.theguy191919.udpft.protocol;

import io.github.theguy191919.udpft.net.AbstractProtocolSender;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This protocol is for ping, sent out to room every 30 second. 
 * If not received for 2 minuet, user is considered gone.
 * @author evan__000
 */
public class Protocol2 extends Protocol{
    
    static{
        Protocol.regesterProtocol(2, Protocol2.class);
    }
    
    private String message = "";
    
    public Protocol2(){
        super.ProtocolNumber = 2;
    }

    @Override
    public String toString() {
        return Protocol.VERSION + this.ProtocolNumber + this.Sender + this.Recipient + this.message;
    }

    @Override
    public void invoked() {
        //seachat.SEAChat.log(this.toString());
    }

    @Override
    public void sendMessage(AbstractProtocolSender sender) {
        sender.send(this.returnByteArray());
    }

    @Override
    public byte[] returnByteArray() {
        byte[] byteArray = new byte[500];
        Protocol.addDataL(byteArray, 0, Protocol.VERSION.getBytes());
        Protocol.addDataR(byteArray, 4, (this.ProtocolNumber + "").getBytes());
        Protocol.addDataR(byteArray, 19, (this.message.length() + "").getBytes());
        Protocol.addDataL(byteArray, 20, this.Sender.getBytes());
        Protocol.addDataL(byteArray, 25, this.Recipient.getBytes());
        Protocol.addDataL(byteArray, 50, this.message.getBytes());
        return byteArray;
    }

    @Override
    public void setContent(String content) {
        this.message = content;
    }

    @Override
    public String getContent() {
        return this.message;
    }
    
}
