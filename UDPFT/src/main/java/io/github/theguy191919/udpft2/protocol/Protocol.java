/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.theguy191919.udpft2.protocol;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author root
 */
public class Protocol {
    
    private String ownerName;
    private ProtocolGoal goal;
    private String data;
    
    public Protocol(String ownerName, ProtocolGoal goal, String data){
        this.ownerName = ownerName;
        this.goal = goal;
        this.data = data;
    }
    
    public Protocol(String ownerName, String goal, String data){
        this(ownerName, ProtocolGoal.valueOf(goal), data);
    }
    
    public Protocol(String protocolString){
        //used for udp
        List<Integer> arrayOfLocation = new LinkedList<>();
        protocolString = protocolString.trim();
        List<String> arrayOfResults = new LinkedList<>();
        arrayOfLocation.add(-1);
        for(int a = 0; a < protocolString.length(); a++){
            if(protocolString.charAt(a) == '|'){
                arrayOfLocation.add(a);
            }
        }
        arrayOfLocation.add(protocolString.length());
        for(int a = 0; a < arrayOfLocation.size(); a++){
            arrayOfResults.add(protocolString.substring(arrayOfLocation.get(a) + 1, arrayOfLocation.get(a + 1)));
        }
        this.ownerName = arrayOfResults.get(0);
        this.goal = ProtocolGoal.valueOf(arrayOfResults.get(1));
        this.data = arrayOfResults.get(2);
    }

    /**
     * @return the ownerName
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * @return the goal
     */
    public ProtocolGoal getGoal() {
        return goal;
    }

    /**
     * @return the data
     */
    public String getData() {
        return data;
    }
    
    
    public String toString(){
        //used for udp
        return this.getOwnerName() + "|" + this.getGoal().toString() + "|" + this.getData();
    }
}
