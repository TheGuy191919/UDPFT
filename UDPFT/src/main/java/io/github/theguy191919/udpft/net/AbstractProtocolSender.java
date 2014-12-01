/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package io.github.theguy191919.udpft.net;

import io.github.theguy191919.udpft.encryption.AbstractCrypto;
import io.github.theguy191919.udpft.protocol.Protocol;

/**
 *
 * @author Yiwen Dong
 */
public interface AbstractProtocolSender {
    @Deprecated
    void send(byte[] bytearray);
    void send(Protocol protocol);
    void setCrypto(AbstractCrypto crypto);
    AbstractCrypto getCrypto();
    //void addListener(ProtocolEventListener listener);
    //void addListener(ProtocolEventListener listener, int listenFor);
}
