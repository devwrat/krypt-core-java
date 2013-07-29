package org.jruby.ext.krypt.provider;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.List;

public class NativeKryptProvider extends Structure
{
    protected List getFieldOrder(){
        return null;
    }
    
    String name;
    
    public static interface init extends Callback
    {
        int invoke(KryptProvider provider, Pointer args);
        /* void * is replaced by pointer according to guidelines here: https://github.com/twall/jna/blob/master/www/Mappings.md */
    }
    

    public static interface finalize extends Callback
    {
        int invoke(KryptProvider provider);
    }

    public static interface md_new_oid extends Callback
    {
        Digest invoke(KryptProvider provider, String oid);
        /* const keyword in the c declaration is ignored for now */
    }

    public static interface md_new_name extends Callback
    {
        Digest invoke(KryptProvider provider, String name);
        /* const keyword in the c declaration is ignored for now */
    }
   
    init initialize;
    
    finalize cleanup;
    
    md_new_oid NewDigestByOid;
    
    md_new_name NewDigestByName;


}
