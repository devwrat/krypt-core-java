package org.jruby.ext.krypt.provider;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;

import java.util.List;
import org.jruby.ext.krypt.provider.KryptMd;


/* useful reference: http://twall.github.io/jna/4.0/javadoc/ Primitive Types section */

public class DigestInterface extends Structure
{
    protected List getFieldOrder(){
        return null;
    }
    
    
    public interface md_reset extends Callback{
        int invoke(KryptMd md);
    }
    
    public interface md_update extends Callback{
        int invoke(KryptMd md, Pointer data, int len);
    }

    public interface md_final extends Callback{
        int invoke(KryptMd md, PointerByReference data, Pointer len);
    }

    public interface md_digest extends Callback{
        int invoke(KryptMd md, Pointer data, int len, PointerByReference digest, Pointer digest_length);
    }

    public interface md_digest_lenght extends Callback{
        int invoke(KryptMd md, Pointer len);
    }
    
    public interface md_block_length extends Callback{
        int invoke(KryptMd md, Pointer block_len);
    }

    public interface md_name extends Callback{
        int invoke(KryptMd md, String[] name);
    }
   
    public interface mark extends Callback{
        int invoke(KryptMd md);
    }

    public interface free extends Callback{
        int invoke(KryptMd md);
    }
    
    md_reset reset;
    md_update update;
    md_final finalize;
    md_digest digestf;
    md_digest_lenght digest_lenght;
    md_block_length block_length;
    md_name name;
    mark markf;
    free freef;
    

    /* constructor */
    DigestInterface(DigestInterface d){
            this.reset = d.reset;
            this.update = d.update;
            this.finalize = d.finalize;
            this.digestf = d.digestf;
            this.digest_lenght = d.digest_lenght;
            this.block_length = d.block_length;
            this.name = d.name;
            this.markf = d.markf;
            this.freef = d.freef;
    };
}
