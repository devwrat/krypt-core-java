/***** BEGIN LICENSE BLOCK *****
* Version: CPL 1.0/GPL 2.0/LGPL 2.1
*
* The contents of this file are subject to the Common Public
* License Version 1.0 (the "License"); you may not use this file
* except in compliance with the License. You may obtain a copy of
* the License at http://www.eclipse.org/legal/cpl-v10.html
*
* Software distributed under the License is distributed on an "AS
* IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
* implied. See the License for the specific language governing
* rights and limitations under the License.
*
* Copyright (C) 2011 
* Hiroshi Nakamura <nahi@ruby-lang.org>
* Martin Bosslet <Martin.Bosslet@googlemail.com>
*
* Alternatively, the contents of this file may be used under the terms of
* either of the GNU General Public License Version 2 or later (the "GPL"),
* or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
* in which case the provisions of the GPL or the LGPL are applicable instead
* of those above. If you wish to allow use of your version of this file only
* under the terms of either the GPL or the LGPL, and not to allow others to
* use your version of this file under the terms of the CPL, indicate your
* decision by deleting the provisions above and replace them with the notice
* and other provisions required by the GPL or the LGPL. If you do not delete
* the provisions above, a recipient may use your version of this file under
* the terms of any one of the CPL, the GPL or the LGPL.
 */
package org.jruby.ext.krypt.asn1;

import impl.krypt.asn1.Asn1Object;
import impl.krypt.asn1.TagClass;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyFixnum;
import org.jruby.anno.JRubyMethod;
import org.jruby.ext.krypt.Errors;
import org.jruby.ext.krypt.asn1.Asn1.Asn1Constructive;
import org.jruby.ext.krypt.asn1.Asn1.Asn1Data;
import org.jruby.ext.krypt.asn1.Asn1.Asn1Primitive;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * 
 * @author <a href="mailto:Martin.Bosslet@googlemail.com">Martin Bosslet</a>
 */
public class Asn1DataClasses {
    
    private Asn1DataClasses() {}
    
    private static class TagAndClass {
        private final IRubyObject tag;
        private final IRubyObject tagClass;

        public TagAndClass(IRubyObject tag, IRubyObject tagClass) {
            this.tag = tag;
            this.tagClass = tagClass;
        }
    }
    
    private static TagAndClass validateArgs(Ruby runtime, IRubyObject[] args, int defaultTag) {
        IRubyObject tag, tagClass;
        
        switch (args.length) {
        case 3:
            if (!args[2].isNil() && args[1].isNil()) {
                throw Errors.newASN1Error(runtime, "Tag must be specified if tag class is");
            }
            tag = args[1];
            tagClass = args[2];
            break;
        case 2:
            tag = args[1];
            tagClass = runtime.newSymbol(TagClass.CONTEXT_SPECIFIC.name());
            break;
        case 1:
            tag = runtime.newFixnum(defaultTag);
            tagClass = runtime.newSymbol(TagClass.UNIVERSAL.name());
            break;
        /*case 0: #currently not possible by design
            tag = runtime.newFixnum(defaultTag);
            tagClass = runtime.newSymbol(TagClass.UNIVERSAL.name());
            break;*/
        default:
            throw new IllegalStateException("caller must restrict number of args <= 3");
        }
        
        return new TagAndClass(tag, tagClass);
    }
    
    private static IRubyObject init(Asn1Data data, ThreadContext ctx, IRubyObject[] args, int defaultTag) {
        Ruby runtime = ctx.getRuntime();
        IRubyObject value = args[0];
        IRubyObject tag, tagClass;
        
        TagAndClass tac = validateArgs(runtime, args, defaultTag);
        tag = tac.tag;
        tagClass = tac.tagClass;
        
        Asn1.defaultInitialize(runtime,
                               data,
                               value, 
                               tag, 
                               tagClass);
        
        /* override default behavior to support Primitives with tag classes
         * other than UNIVERSAL */
        data.setCodec(Asn1.codecFor(defaultTag, TagClass.UNIVERSAL));
        return data;
    }
    
    public static class Asn1EndOfContents extends Asn1Primitive {
        
        static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass type) {
                return new Asn1EndOfContents(runtime, type);
            }
        };
        
        private Asn1EndOfContents(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }
        
        public Asn1EndOfContents(Ruby runtime, RubyClass type, Asn1Object object) {
            super(runtime, type, object);
        }
        
        @JRubyMethod(required=0, optional=1)
        public IRubyObject initialize(ThreadContext ctx, IRubyObject[] args) {
            Ruby runtime = ctx.getRuntime();
            
            if (args.length == 1) {
                IRubyObject value = args[0];
                if (!value.isNil())
                    throw runtime.newArgumentError("Value must be nil for END_OF_CONTENTS");
            }
            
            Asn1.defaultInitialize(runtime,
                                   this,
                                   runtime.getNil(), 
                                   runtime.newFixnum(Asn1Tags.END_OF_CONTENTS), 
                                   runtime.newSymbol(TagClass.UNIVERSAL.name()));
            return this;
        }
    }
    
    public static class Asn1Boolean extends Asn1Primitive {
        
        static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass type) {
                return new Asn1Boolean(runtime, type);
            }
        };
        
        private Asn1Boolean(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }
        
        public Asn1Boolean(Ruby runtime, RubyClass type, Asn1Object object) {
            super(runtime, type, object);
        }
        
        @JRubyMethod(required=1, optional=2)
        public IRubyObject initialize(ThreadContext ctx, IRubyObject[] args) {
            return init(this, ctx, args, Asn1Tags.BOOLEAN);
        }
    }
    
    public static class Asn1Integer extends Asn1Primitive {
        
        static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass type) {
                return new Asn1Integer(runtime, type);
            }
        };
        
        private Asn1Integer(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }
        
        public Asn1Integer(Ruby runtime, RubyClass type, Asn1Object object) {
            super(runtime, type, object);
        }
        
        @JRubyMethod(required=1, optional=2)
        public IRubyObject initialize(ThreadContext ctx, IRubyObject[] args) {
            return init(this, ctx, args, Asn1Tags.INTEGER);
        }
    }
    
    public static class Asn1BitString extends Asn1Primitive {
        
        static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass type) {
                return new Asn1BitString(runtime, type);
            }
        };
        
        private Asn1BitString(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }
        
        public Asn1BitString(Ruby runtime, RubyClass type, Asn1Object object) {
            super(runtime, type, object);
        }
        
        @JRubyMethod(required=1, optional=2)
        public IRubyObject initialize(ThreadContext ctx, IRubyObject[] args) {
            IRubyObject val = init(this, ctx, args, Asn1Tags.BIT_STRING);
            val.getInstanceVariables().setInstanceVariable("unused_bits", RubyFixnum.zero(ctx.getRuntime()));
            return val;
        }
        
        @JRubyMethod(name={"unused_bits="})
        public IRubyObject set_unused_bits(ThreadContext ctx, IRubyObject value) {
            getInstanceVariables().setInstanceVariable("unused_bits", value);
            return value;
        }
        
        @JRubyMethod
        public IRubyObject unused_bits(ThreadContext ctx) {
            if (!isDecoded()) {
                decodeValue(ctx);
            }
            return getInstanceVariables().getInstanceVariable("unused_bits");
        }
    }
    
    public static class Asn1OctetString extends Asn1Primitive {
        
        static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass type) {
                return new Asn1OctetString(runtime, type);
            }
        };
        
        private Asn1OctetString(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }
        
        public Asn1OctetString(Ruby runtime, RubyClass type, Asn1Object object) {
            super(runtime, type, object);
        }
        
        @JRubyMethod(required=1, optional=2)
        public IRubyObject initialize(ThreadContext ctx, IRubyObject[] args) {
            return init(this, ctx, args, Asn1Tags.OCTET_STRING);
        }
    }
    
    public static class Asn1Null extends Asn1Primitive {
        
        static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass type) {
                return new Asn1Null(runtime, type);
            }
        };
        
        private Asn1Null(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }
        
        public Asn1Null(Ruby runtime, RubyClass type, Asn1Object object) {
            super(runtime, type, object);
        }
        
        @JRubyMethod(required=0, optional=3)
        public IRubyObject initialize(ThreadContext ctx, IRubyObject[] args) {
            Ruby runtime = ctx.getRuntime();
            if (args.length == 0) {
                Asn1.defaultInitialize(runtime,
                                       this, 
                                       runtime.getNil(), 
                                       runtime.newFixnum(Asn1Tags.NULL), 
                                       runtime.newSymbol(TagClass.UNIVERSAL.name()));
                return this;
            } else {
                if (!args[0].isNil())
                    throw runtime.newArgumentError("Value for ASN.1 NULL must be nil");
                return init(this, ctx, args, Asn1Tags.NULL);
            }
        }
    }
    
    public static class Asn1ObjectId extends Asn1Primitive {
        
        static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass type) {
                return new Asn1ObjectId(runtime, type);
            }
        };
        
        private Asn1ObjectId(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }
        
        public Asn1ObjectId(Ruby runtime, RubyClass type, Asn1Object object) {
            super(runtime, type, object);
        }
        
        @JRubyMethod(required=1, optional=2)
        public IRubyObject initialize(ThreadContext ctx, IRubyObject[] args) {
            return init(this, ctx, args, Asn1Tags.OBJECT_ID);
        }
    }
    
    public static class Asn1Enumerated extends Asn1Primitive {
        
        static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass type) {
                return new Asn1Enumerated(runtime, type);
            }
        };
        
        private Asn1Enumerated(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }
        
        public Asn1Enumerated(Ruby runtime, RubyClass type, Asn1Object object) {
            super(runtime, type, object);
        }
        
        @JRubyMethod(required=1, optional=2)
        public IRubyObject initialize(ThreadContext ctx, IRubyObject[] args) {
            return init(this, ctx, args, Asn1Tags.ENUMERATED);
        }
    }
    
    public static class Asn1Utf8String extends Asn1Primitive {
        
        static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass type) {
                return new Asn1Utf8String(runtime, type);
            }
        };
        
        private Asn1Utf8String(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }
        
        public Asn1Utf8String(Ruby runtime, RubyClass type, Asn1Object object) {
            super(runtime, type, object);
        }
        
        @JRubyMethod(required=1, optional=2)
        public IRubyObject initialize(ThreadContext ctx, IRubyObject[] args) {
            return init(this, ctx, args, Asn1Tags.UTF8_STRING);
        }
    }
    
    public static class Asn1Sequence extends Asn1Constructive {
        
        static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass type) {
                return new Asn1Sequence(runtime, type);
            }
        };
        
        private Asn1Sequence(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }
        
        public Asn1Sequence(Ruby runtime, RubyClass type, Asn1Object object) {
            super(runtime, type, object);
        }
        
        @JRubyMethod(required=1, optional=2)
        public IRubyObject initialize(ThreadContext ctx, IRubyObject[] args) {
            return init(this, ctx, args, Asn1Tags.SEQUENCE);
        }
    }
    
    public static class Asn1Set extends Asn1Constructive {
        
        static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass type) {
                return new Asn1Set(runtime, type);
            }
        };
        
        private Asn1Set(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }
        
        public Asn1Set(Ruby runtime, RubyClass type, Asn1Object object) {
            super(runtime, type, object);
        }
        
        @JRubyMethod(required=1, optional=2)
        public IRubyObject initialize(ThreadContext ctx, IRubyObject[] args) {
            return init(this, ctx, args, Asn1Tags.SET);
        }
    }
    
    public static class Asn1NumericString extends Asn1Primitive {
        
        static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass type) {
                return new Asn1NumericString(runtime, type);
            }
        };
        
        private Asn1NumericString(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }
        
        public Asn1NumericString(Ruby runtime, RubyClass type, Asn1Object object) {
            super(runtime, type, object);
        }
        
        @JRubyMethod(required=1, optional=2)
        public IRubyObject initialize(ThreadContext ctx, IRubyObject[] args) {
            return init(this, ctx, args, Asn1Tags.NUMERIC_STRING);
        }
    }
    
    public static class Asn1PrintableString extends Asn1Primitive {
        
        static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass type) {
                return new Asn1PrintableString(runtime, type);
            }
        };
        
        private Asn1PrintableString(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }
        
        public Asn1PrintableString(Ruby runtime, RubyClass type, Asn1Object object) {
            super(runtime, type, object);
        }
        
        @JRubyMethod(required=1, optional=2)
        public IRubyObject initialize(ThreadContext ctx, IRubyObject[] args) {
            return init(this, ctx, args, Asn1Tags.PRINTABLE_STRING);
        }
    }
    
    public static class Asn1T61String extends Asn1Primitive {
        
        static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass type) {
                return new Asn1T61String(runtime, type);
            }
        };
        
        private Asn1T61String(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }
        
        public Asn1T61String(Ruby runtime, RubyClass type, Asn1Object object) {
            super(runtime, type, object);
        }
        
        @JRubyMethod(required=1, optional=2)
        public IRubyObject initialize(ThreadContext ctx, IRubyObject[] args) {
            return init(this, ctx, args, Asn1Tags.T61_STRING);
        }
    }
    
    public static class Asn1VideotexString extends Asn1Primitive {
        
        static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass type) {
                return new Asn1VideotexString(runtime, type);
            }
        };
        
        private Asn1VideotexString(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }
        
        public Asn1VideotexString(Ruby runtime, RubyClass type, Asn1Object object) {
            super(runtime, type, object);
        }
        
        @JRubyMethod(required=1, optional=2)
        public IRubyObject initialize(ThreadContext ctx, IRubyObject[] args) {
            return init(this, ctx, args, Asn1Tags.VIDEOTEX_STRING);
        }
    }
    
    public static class Asn1Ia5String extends Asn1Primitive {
        
        static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass type) {
                return new Asn1Ia5String(runtime, type);
            }
        };
        
        private Asn1Ia5String(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }
        
        public Asn1Ia5String(Ruby runtime, RubyClass type, Asn1Object object) {
            super(runtime, type, object);
        }
        
        @JRubyMethod(required=1, optional=2)
        public IRubyObject initialize(ThreadContext ctx, IRubyObject[] args) {
            return init(this, ctx, args, Asn1Tags.IA5_STRING);
        }
    }
    
    public static class Asn1UtcTime extends Asn1Primitive {
        
        static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass type) {
                return new Asn1UtcTime(runtime, type);
            }
        };
        
        private Asn1UtcTime(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }
        
        public Asn1UtcTime(Ruby runtime, RubyClass type, Asn1Object object) {
            super(runtime, type, object);
        }
        
        @JRubyMethod(required=1, optional=2)
        public IRubyObject initialize(ThreadContext ctx, IRubyObject[] args) {
            return init(this, ctx, args, Asn1Tags.UTC_TIME);
        }
    }
    
    public static class Asn1GeneralizedTime extends Asn1Primitive {
        
        static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass type) {
                return new Asn1GeneralizedTime(runtime, type);
            }
        };
        
        private Asn1GeneralizedTime(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }
        
        public Asn1GeneralizedTime(Ruby runtime, RubyClass type, Asn1Object object) {
            super(runtime, type, object);
        }
        
        @JRubyMethod(required=1, optional=2)
        public IRubyObject initialize(ThreadContext ctx, IRubyObject[] args) {
            return init(this, ctx, args, Asn1Tags.GENERALIZED_TIME);
        }
    }
    
    public static class Asn1GraphicString extends Asn1Primitive {
        
        static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass type) {
                return new Asn1GraphicString(runtime, type);
            }
        };
        
        private Asn1GraphicString(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }
        
        public Asn1GraphicString(Ruby runtime, RubyClass type, Asn1Object object) {
            super(runtime, type, object);
        }
        
        @JRubyMethod(required=1, optional=2)
        public IRubyObject initialize(ThreadContext ctx, IRubyObject[] args) {
            return init(this, ctx, args, Asn1Tags.GRAPHIC_STRING);
        }
    }
    
    public static class Asn1Iso64String extends Asn1Primitive {
        
        static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass type) {
                return new Asn1Iso64String(runtime, type);
            }
        };
        
        private Asn1Iso64String(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }
        
        public Asn1Iso64String(Ruby runtime, RubyClass type, Asn1Object object) {
            super(runtime, type, object);
        }
        
        @JRubyMethod(required=1, optional=2)
        public IRubyObject initialize(ThreadContext ctx, IRubyObject[] args) {
            return init(this, ctx, args, Asn1Tags.ISO64_STRING);
        }
    }
    
    public static class Asn1GeneralString extends Asn1Primitive {
        
        static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass type) {
                return new Asn1GeneralString(runtime, type);
            }
        };
        
        private Asn1GeneralString(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }
        
        public Asn1GeneralString(Ruby runtime, RubyClass type, Asn1Object object) {
            super(runtime, type, object);
        }
        
        @JRubyMethod(required=1, optional=2)
        public IRubyObject initialize(ThreadContext ctx, IRubyObject[] args) {
            return init(this, ctx, args, Asn1Tags.GENERAL_STRING);
        }
    }
    
    public static class Asn1UniversalString extends Asn1Primitive {
        
        static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass type) {
                return new Asn1UniversalString(runtime, type);
            }
        };
        
        private Asn1UniversalString(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }
        
        public Asn1UniversalString(Ruby runtime, RubyClass type, Asn1Object object) {
            super(runtime, type, object);
        }
        
        @JRubyMethod(required=1, optional=2)
        public IRubyObject initialize(ThreadContext ctx, IRubyObject[] args) {
            return init(this, ctx, args, Asn1Tags.UNIVERSAL_STRING);
        }
    }
    
    public static class Asn1BmpString extends Asn1Primitive {
        
        static ObjectAllocator ALLOCATOR = new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass type) {
                return new Asn1BmpString(runtime, type);
            }
        };
        
        private Asn1BmpString(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }
        
        public Asn1BmpString(Ruby runtime, RubyClass type, Asn1Object object) {
            super(runtime, type, object);
        }
        
        @JRubyMethod(required=1, optional=2)
        public IRubyObject initialize(ThreadContext ctx, IRubyObject[] args) {
            return init(this, ctx, args, Asn1Tags.BMP_STRING);
        }
    }
}
