/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.theguy191919.udpft.protocol2;

/**
 *
 * @author root
 */
public class Protocol {
    
    private String ownerName;
    private ProtocolGoal goal;
    private String data;
    
    Protocol(String ownerName, ProtocolGoal goal, String data){
        this.ownerName = ownerName;
        this.goal = goal;
        this.data = data;
    }
    
    Protocol(String ownerName, String goal, String data){
        this(ownerName, ProtocolGoal.valueOf(goal), data);
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
    
}
