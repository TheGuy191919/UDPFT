/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package io.github.theguy191919.udpft.protocol;

import io.github.theguy191919.udpft.net.AbstractProtocolSender;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
//import seachat.net.Sender;

/**
 *
 * @author Yiwen Dong
 */
public abstract class Protocol {
    
    protected static final String VERSION = "01";
    private static final Map<Integer, Class<? extends Protocol>> mapOfProtocol;
    static{
        mapOfProtocol = new HashMap<>();
        regesterProtocol(0, Protocol0.class);
        regesterProtocol(1, Protocol1.class);
        regesterProtocol(2, Protocol2.class);
        regesterProtocol(3, Protocol3.class);
        regesterProtocol(4, Protocol4.class);
    }
    
    private static ProtocolEventHandler eventHandler = new ProtocolEventHandler();
    
    protected int ProtocolNumber = -1;
    protected String Sender = "";
    protected String Recipient = "";
    
    //char0-1 is version
    //char2-4 is protocol number
    //char5-19 is length info
    //char20-29 is username
    //char30-39 is recipient - not used by all
    //chat40-49 is time stamp (Year Month Date Hour Minet Second MicroSecond)
    //char50-499 is message
    //char length is 500, no less no more
    public Protocol(){
        
    }
    
    public Protocol(String message){
        this.setContent(message);
    }
    
    public Protocol(Protocol protocol){
        this.ProtocolNumber = protocol.ProtocolNumber;
        this.Recipient = protocol.Recipient;
        this.Sender = protocol.Sender;
        this.setContent(protocol.getContent());
    }
    
    @Deprecated
    public static Protocol getProtocol(String message) throws IllegalAccessException, InstantiationException{
        if(correctVersion(message)){
            int protocolNumber = parseForProtocolNumber(message);
            Protocol protocol = mapOfProtocol.get(protocolNumber).newInstance();
            protocol.setProtocolNumber(protocolNumber);
            protocol.setSender(parseForSender(message));
            protocol.setContent(parseForContent(message, parseForLength(message)));
            return protocol;
        }
        return null;
    }
    
    public static Protocol getProtocol(byte[] message) throws IllegalAccessException, InstantiationException{
        if(correctVersion(message)){
            int protocolNumber = parseForProtocolNumber(message);
            Protocol protocol = mapOfProtocol.get(protocolNumber).newInstance();
            protocol.setProtocolNumber(protocolNumber);
            protocol.setSender(parseForSender(message));
            protocol.setRecipient(parseForRecipient(message));
            protocol.setContent(parseForContent(message, parseForLength(message)));
            protocol.invoked();
            Protocol.eventHandler.messageGotten(protocol);
            return protocol;
        }
        return null;
    }
    
    public static Protocol getProtocol(byte[] message, boolean runEvent) throws IllegalAccessException, InstantiationException{
        if(correctVersion(message)){
            int protocolNumber = parseForProtocolNumber(message);
            Protocol protocol = mapOfProtocol.get(protocolNumber).newInstance();
            protocol.setProtocolNumber(protocolNumber);
            protocol.setSender(parseForSender(message));
            protocol.setRecipient(parseForRecipient(message));
            protocol.setContent(parseForContent(message, parseForLength(message)));
            if(runEvent){
                protocol.invoked();
            }
            return protocol;
        }
        return null;
    }
    
    @Deprecated
    private static int parseForProtocolNumber(String message){
        return Integer.valueOf(message.substring(2, 4));
    }
    
    private static int parseForProtocolNumber(byte[] message){
        return Integer.parseInt((new String(Arrays.copyOfRange(message, 2, 5))).trim());
    }
    
    @Deprecated
    private static int parseForLength(String message){
        return Integer.valueOf(message.substring(5, 19));
    }
    
    private static int parseForLength(byte[] message){
        return Integer.valueOf(new String(Arrays.copyOfRange(message, 5, 20)).trim());
    }
    
    @Deprecated
    private static String parseForSender(String message){
        return message.substring(20, 24);
    }
    
    private static String parseForSender(byte[] message){
        return new String(Arrays.copyOfRange(message, 20, 30)).trim();
    }
    
    private static String parseForRecipient(byte[] message){
        return new String(Arrays.copyOfRange(message, 30, 40)).trim();
    }
    
    @Deprecated
    private static String parseForContent(String message, int Length){
        if(Length == 0){
            return "";
        }
        return message.substring(50, 49 + Length);
    }
    
    private static String parseForContent(byte[] message, int Length){
        if(Length == 0){
            return "";
        }
        return new String(Arrays.copyOfRange(message, 50, 50 + Length)).trim();
    }
    
    @Deprecated
    private static boolean correctVersion(String message){
        return VERSION.equals(message.substring(0, 1));
    }
    
    private static boolean correctVersion(byte[] message){
        return VERSION.equals(new String(Arrays.copyOfRange(message, 0, 2)));
    }
    
    protected static String parseData(byte[] message, int starting, int ending){
    if(ending == starting){
            return "";
        }
        return (new String(Arrays.copyOfRange(message, starting, ending))).trim();
    }
    
    protected static byte[] addDataL(byte[] message, int starting, byte[] data){
        for(int a = starting; a < data.length + starting; a++){
                message[a] = data[a - starting];
            }
        return message;
    }
    
    protected static byte[] addDataR(byte[] message, int ending, byte[] data){
        int dataLength = data.length;
            int lengthStarting = ending + 1 - dataLength;
            int lengthLocation = ending + 1 - dataLength;
            while(lengthLocation <= ending){
                message[lengthLocation] = data[lengthLocation - lengthStarting];
                lengthLocation++;
            }
        return message;
    }
    
    public void setProtocolNumber(int Number){
        this.ProtocolNumber = Number;
    }
    
    public int getProtocolNumber(){
        return this.ProtocolNumber;
    }
    
    public void setSender(String sender){
        if(sender.length() > 10){
            sender = sender.substring(0, 10);
        }
        this.Sender = sender;
    }
    
    public String getSender(){
        return this.Sender;
    }
    
    public void setRecipient(String Recipient){
        this.Recipient = Recipient;
    }
    
    public String getRecipient(){
        return this.Recipient;
    }
    
    @Deprecated
    public static Protocol createProtocol(int Number) throws InstantiationException, IllegalAccessException{
        return mapOfProtocol.get(Number).newInstance();
    }
    
    protected static void regesterProtocol(int number, Class<? extends Protocol> classThing){
        Protocol.mapOfProtocol.put(number, classThing);
    }
    
    public static ProtocolEventHandler getProtocolEventHandler(){
        return Protocol.eventHandler;
    }
    
    @Override
    public abstract String toString();
    public abstract void invoked();
    public abstract void sendMessage(AbstractProtocolSender sender);
    public abstract byte[] returnByteArray();
    public abstract void setContent(String content);
    public abstract String getContent();
}
