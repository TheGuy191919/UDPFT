/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package io.github.theguy191919.udpft2.net;

import io.github.theguy191919.udpft2.net.*;
import io.github.theguy191919.udpft.encryption.AbstractCrypto;
import io.github.theguy191919.udpft2.protocol.ProtocolEventListener;

/**
 *
 * @author Yiwen Dong
 */
public interface AbstractProtocolReceiver {
    void addListener(ProtocolEventListener listener);
    //void addListener(ProtocolEventListener listener, int listenFor);
    void start();
    void stop();
    void removeListener(ProtocolEventListener listener);
    void setCrypto(AbstractCrypto crypto);
    AbstractCrypto getCrypto();
}
