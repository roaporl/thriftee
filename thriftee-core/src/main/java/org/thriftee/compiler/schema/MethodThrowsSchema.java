/*
 * Copyright (C) 2013-2016 Benjamin Gould, and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thriftee.compiler.schema;

import java.util.Collection;

import org.thriftee.compiler.schema.MethodThrowsSchema.Builder;

import com.facebook.swift.codec.ThriftConstructor;
import com.facebook.swift.codec.ThriftStruct;

@ThriftStruct(builder=Builder.class)
public final class MethodThrowsSchema extends MethodResultFieldSchema {

  private static final long serialVersionUID = -1297636271308306276L;

  public MethodThrowsSchema(
        MethodResultSchema _parent,
        String _name,
        Collection<ThriftAnnotation> _annotations,
        ISchemaType _type,
        Requiredness _required,
        Short _identifier
      ) throws SchemaBuilderException {
    super(_parent, _name, _annotations, _type, _required, _identifier);
  }

  public static class Builder extends MethodResultFieldSchema.Builder {

    public Builder() throws SchemaBuilderException {
      throw new NoArgConstructorOnlyExistsForSwiftValidationException();
    }

    protected Builder(MethodResultSchema.Builder parentBuilder) {
      super(parentBuilder);
    }

    @Override
    protected MethodThrowsSchema _buildInstance(MethodResultSchema _parent)
        throws SchemaBuilderException {
      return new MethodThrowsSchema(
        _parent, 
        getName(), 
        getAnnotations(), 
        getType(), 
        getRequiredness(), 
        getIdentifier()
      );
    }

    @Override
    protected String _fieldTypeName() {
      return "thrown exception";
    }

    @Override
    @ThriftConstructor
    public MethodThrowsSchema build() throws SchemaBuilderException {
      throw new NoArgConstructorOnlyExistsForSwiftValidationException();
    }

  }

}
