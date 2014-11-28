/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.theguy191919.udpft.encryption;

import java.security.Key;
import java.util.Random;

/**
 * Need to be thread safe
 * @author evan__000
 */
public class SimpleCrypto implements AbstractCrypto{
    
    long key = -1L;
    public SimpleCrypto(){
        
    }
    
    public SimpleCrypto(long key){
        this.key = key;
    }

    @Override
    public void setPublicKey(long key) {
        this.key = key;
    }

    @Override
    public void setPublicKey(Key key) {
        this.key = new String(key.getEncoded()).hashCode();
    }

    @Override
    public void setPrivateKey(long key) {
        this.key = key;
    }

    @Override
    public void setPrivateKey(Key key) {
        this.key = new String(key.getEncoded()).hashCode();
    }

    @Override
    public synchronized String encrypt(String input) {
        Random generator = new Random(key);
        byte[] data = input.getBytes();
        int pos = 0;
        while(pos < data.length){
            data[pos] = (byte) (data[pos] + (byte)generator.nextInt(128));
            pos++;
        }
        return new String(data);
    }

    @Override
    public synchronized String decrypt(String input) {
        Random generator = new Random(key);
        byte[] data = input.getBytes();
        int pos = 0;
        while(pos < data.length){
            data[pos] = (byte) (data[pos] - (byte)generator.nextInt(128));
            pos++;
        }
        return new String(data);
    }

    @Override
    public synchronized byte[] encrypt(byte[] input) {
        Random generator = new Random(key);
        byte[] data = input;
        int pos = 0;
        while(pos < data.length){
            data[pos] = (byte) (data[pos] + (byte)generator.nextInt(128));
            pos++;
        }
        return data;
    }

    @Override
    public synchronized byte[] decrypt(byte[] input) {
        Random generator = new Random(key);
        byte[] data = input;
        int pos = 0;
        while(pos < data.length){
            data[pos] = (byte) (data[pos] - (byte)generator.nextInt(128));
            pos++;
        }
        return data;
    }
    
}
