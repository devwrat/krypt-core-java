/*
 * krypt-core API - Java version
 *
 * Copyright (c) 2011-2013
 * Hiroshi Nakamura <nahi@ruby-lang.org>
 * Martin Bosslet <martin.bosslet@gmail.com>
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.jruby.ext.krypt.provider;

import org.jruby.ext.krypt.provider.KryptMd;
import org.jruby.ext.krypt.provider.ProviderInterface;
import org.jruby.ext.krypt.provider.DigestInterface;
        
import com.sun.jna.Callback;
import com.sun.jna.Pointer;

/**
 * 
 * @author <a href="mailto:Martin.Bosslet@gmail.com">Martin Bosslet</a>
 */

public class Digest{
    
    NativeHandle handle;
    
    Digest(ProviderInterface provider, String nameoroid){
        /* write this in a try catch block */
        this.handle = interface_for_name(provider, nameoroid);
        
        /* if error in above line */
        this.handle = interface_for_oid(provider, nameoroid);
    }
    
    public int reset(){
        return handle.inter.reset(handle.container);
    }
    
    public int update(byte[] data, int off, int len){
        byte[] temp = new byte[len-off + 1];
        int i;
        for(i = off; i < off+len; i++)
            temp[i-off] = data[i];
        
        return handle.inter.update(handle.container, temp, len);
    }
    
    /* all the private methods and classes */
    private class NativeHandle{
        KryptMd container;
        DigestInterface inter;
        
        NativeHandle(KryptMd container, DigestInterface inter){
            this.container = container;
            this.inter = inter;
        }
        
    }
    
    private NativeHandle interface_for_name(ProviderInterface provider, String name){
         KryptMd temp = provider.NewDigestByName.invoke(provider, name);
         
         KryptMd container = new KryptMd(temp);
         DigestInterface inter = new DigestInterface(container.methods);
         
         return new NativeHandle(container, inter);
         
    }
    
    private NativeHandle interface_for_oid(ProviderInterface provider, String oid){
        KryptMd temp = provider.NewDigestByOid.invoke(provider, oid);
        KryptMd container = new KryptMd(temp);
        DigestInterface inter = new DigestInterface(container.methods);
         
        return new NativeHandle(container, inter);
         
        
    }
}

/* original code */
/*
public interface Digest {
    
    public void update(byte[] data, int off, int len);
    public byte[] digest();
    public byte[] digest(byte[] data);
    public void reset();
    public String getName();
    public int getDigestLength();
    public int getBlockLength();
    
}
*/

