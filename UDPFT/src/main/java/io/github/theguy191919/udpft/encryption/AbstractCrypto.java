/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.theguy191919.udpft.encryption;

import java.security.Key;

/**
 *
 * @author evan__000
 */
public interface AbstractCrypto {
    
    void setPublicKey(long key);
    void setPublicKey(Key key);
    void setPrivateKey(long key);
    void setPrivateKey(Key key);
    
    @Deprecated
    String encrypt(String input);
    byte[] encrypt(byte[] input);
    @Deprecated
    String decrypt(String input);
    byte[] decrypt(byte[] input);
}
