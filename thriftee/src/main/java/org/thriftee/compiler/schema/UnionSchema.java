package org.thriftee.compiler.schema;

import java.util.Collection;

import com.facebook.swift.codec.ThriftUnion;

@ThriftUnion
public final class UnionSchema extends AbstractStructSchema<ModuleSchema, UnionSchema, UnionFieldSchema, UnionFieldSchema.Builder> {
    
    private static final long serialVersionUID = 9173725847653740446L;
    
    private UnionSchema(
            ModuleSchema parent, 
            String _name, 
            Collection<UnionFieldSchema.Builder> _fields, 
            Collection<ThriftAnnotation> _annotations
        ) throws SchemaBuilderException {
        super(
            ModuleSchema.class, 
            UnionSchema.class,
            parent, 
            _name,
            _fields,
            _annotations
        );
    }
    
    static final class Builder extends AbstractStructSchema.AbstractStructSchemaBuilder<
        ModuleSchema, 
        UnionSchema, 
        ModuleSchema.Builder, 
        UnionFieldSchema.Builder, 
        UnionSchema.Builder> {

        protected Builder(ModuleSchema.Builder parentBuilder) {
            super(parentBuilder, Builder.class);
        }

        @Override
        protected UnionFieldSchema.Builder _createFieldBuilder() {
            return new UnionFieldSchema.Builder(this);
        }

        @Override
        protected UnionSchema _createStruct(ModuleSchema _parent) throws SchemaBuilderException {
            return new UnionSchema(_parent, getName(), _getFields(), getAnnotations());
        }

    }
    
}
