package org.jruby.ext.krypt.provider;

import com.sun.jna.Structure;

import java.util.List;

import org.jruby.ext.krypt.provider.ProviderInterface;
import org.jruby.ext.krypt.provider.DigestInterface;

public class KryptMd extends Structure{
    protected List getFieldOrder(){
        return null;
    }

    ProviderInterface provider;
    DigestInterface digest;
    /* or
     * Digest methods;
     */
    
    KrypMd(KryptProvider provider, Digest digest){
        this.provider = provider;
        this.digest = digest;
    }
}