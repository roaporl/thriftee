package org.thriftee.compiler.schema;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.thriftee.util.New;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;

@ThriftStruct
public final class ServiceSchema extends BaseSchema<ModuleSchema, ServiceSchema> {
    
    public static final int THRIFT_INDEX_NAME = 1;
    
    public static final int THRIFT_INDEX_ANNOTATIONS = THRIFT_INDEX_NAME + 1;
    
    public static final int THRIFT_INDEX_PARENT_SERVICE = THRIFT_INDEX_ANNOTATIONS + 1;
    
    public static final int THRIFT_INDEX_METHODS = THRIFT_INDEX_PARENT_SERVICE + 1;
    
    private static final long serialVersionUID = 419978455931497309L;

    private final Map<String, MethodSchema> methods;
    
    private final String parentService;
    
    private ServiceSchema(
            ModuleSchema module, 
            String _name,
            Collection<ThriftAnnotation> _annotations,
            String parentService, 
            Collection<MethodSchema.Builder> _methods
        ) throws SchemaBuilderException {
        super(ModuleSchema.class, ServiceSchema.class, module, _name, _annotations);
        this.parentService = parentService;
        this.methods = toMap(this, _methods);
    }
    
    @ThriftField(THRIFT_INDEX_NAME)
    public String getName() {
        return super.getName();
    }
    
    @ThriftField(THRIFT_INDEX_ANNOTATIONS)
    public Map<String, ThriftAnnotation> getAnnotations() {
        return super.getAnnotations();
    }

    @ThriftField(THRIFT_INDEX_PARENT_SERVICE)
    public String getParentService() {
        return this.parentService;
    }
    
    @ThriftField(THRIFT_INDEX_METHODS)
    public Map<String, MethodSchema> getMethods() {
        return methods;
    }
    
    public ServiceSchema getParentServiceSchema() {
        if (parentService == null) {
            return null;
        } else {
            throw new UnsupportedOperationException();
        }
    }
    
    public static class Builder extends AbstractSchemaBuilder<ModuleSchema, ServiceSchema, ModuleSchema.Builder, ServiceSchema.Builder> {

        private String parentService;
        
        private List<MethodSchema.Builder> methods = New.linkedList();
        
        Builder(final ModuleSchema.Builder parentBuilder) {
            super(parentBuilder, ServiceSchema.Builder.class);
        }
        
        public MethodSchema.Builder addMethod(final String _name) {
            MethodSchema.Builder result = new MethodSchema.Builder(this);
            methods.add(result);
            return result.name(_name);
        }
        
        public Builder parentService(String parentService) {
            this.parentService = parentService;
            return this;
        }

        @Override
        protected ServiceSchema _build(final ModuleSchema _parent) throws SchemaBuilderException {
            super._validate();
            final ServiceSchema result = new ServiceSchema(
                _parent, 
                getName(), 
                getAnnotations(), 
                this.parentService, 
                methods
            );
            return result;
        }

        @Override
        protected String[] toStringFields() {
            return new String[] { "name", "annotations", "parentService", "methods", };
        }
        
    }
}
