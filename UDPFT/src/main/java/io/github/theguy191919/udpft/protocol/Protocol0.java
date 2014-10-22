/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.theguy191919.udpft.protocol;

import io.github.theguy191919.udpft.net.AbstractProtocolSender;

/**
 * Message Protocol contains message.
 *
 * @author Yiwen Dong
 */
public class Protocol0 extends Protocol {
    
    static{
        Protocol.regesterProtocol(0, Protocol0.class);
    }
    
    private String message = "";

    public Protocol0() {
        super.ProtocolNumber = 0;
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
        Protocol.addDataL(byteArray, 30, this.Recipient.getBytes());
        Protocol.addDataL(byteArray, 50, this.message.getBytes());
        return byteArray;
    }

    @Override
    public void setContent(String content) {
        if(content.length() >= 450){
            content = content.substring(0, 445) + "...";
        }
        this.message = content;
    }

    @Override
    public String getContent() {
        return message;
    }

}
