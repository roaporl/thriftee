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

import org.thriftee.compiler.schema.UnionSchema.Builder;

import com.facebook.swift.codec.ThriftConstructor;
import com.facebook.swift.codec.ThriftStruct;

@ThriftStruct(builder=Builder.class)
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

  @Override
  public String getModuleName() {
    return getParent().getName();
  }

  @Override
  public String getTypeName() {
    return getName();
  }

  public static final class Builder extends AbstractStructSchema.AbstractStructSchemaBuilder<
    ModuleSchema, 
    UnionSchema, 
    ModuleSchema.Builder, 
    UnionFieldSchema.Builder, 
    UnionSchema.Builder> {

    public Builder() throws NoArgConstructorOnlyExistsForSwiftValidationException {
      this(null);
      throw new NoArgConstructorOnlyExistsForSwiftValidationException();
    }

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

    @Override
    @ThriftConstructor
    public UnionSchema build() throws SchemaBuilderException {
      throw new NoArgConstructorOnlyExistsForSwiftValidationException();
    }

  }

}
