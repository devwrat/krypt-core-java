package org.jruby.ext.krypt.provider;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.List;

import org.jruby.ext.krypt.provider.KryptMd;

public class NativeKryptProvider extends Structure
{
    protected List getFieldOrder(){
        return null;
    }
    
    String name;
    
    public interface init extends Callback
    {
        int invoke(KryptProvider provider, Pointer args);
        /* void * is replaced by pointer according to guidelines here: https://github.com/twall/jna/blob/master/www/Mappings.md */
    }
    

    public interface finalize extends Callback
    {
        int invoke(KryptProvider provider);
    }

    public interface md_new_oid extends Callback
    {
        KryptMd invoke(KryptProvider provider, String oid);
        /* const keyword in the c declaration is ignored for now */
    }

    public interface md_new_name extends Callback
    {
        KryptMd invoke(KryptProvider provider, String name);
        /* const keyword in the c declaration is ignored for now */
    }
   
    init initialize;
    
    finalize cleanup;
    
    md_new_oid NewDigestByOid;
    
    md_new_name NewDigestByName;

}
