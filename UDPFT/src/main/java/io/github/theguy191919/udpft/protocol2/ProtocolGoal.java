/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.theguy191919.udpft.protocol2;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author root
 */
public enum ProtocolGoal {
    POST("post");
    
    private final String name;
    private final EnumSet<ProtocolGoal> setOfGoals = EnumSet.allOf(ProtocolGoal.class);
    
    
    ProtocolGoal(String name){
        this.name = name;
    }
    
    @Override
    public String toString(){
        return this.name;
    }
    
    public ProtocolGoal getGoalFromString(String goal){
        for(ProtocolGoal enumGoal : setOfGoals){
            if(goal.toString().equalsIgnoreCase(goal)){
                return enumGoal;
            }
        }
        return null;
    }
}
