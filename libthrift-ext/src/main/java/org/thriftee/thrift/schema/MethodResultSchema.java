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
package org.thriftee.thrift.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MethodResultSchema extends AbstractStructSchema<
    MethodSchema, 
    MethodResultSchema, 
    MethodResultFieldSchema, 
    MethodResultFieldSchema.Builder> {

  private static final long serialVersionUID = 9173725847653740446L;

  private final SchemaType _returnType;

  private final Map<String, MethodThrowsSchema> _exceptions;

  private MethodResultSchema(
        final MethodSchema parent,
        final String name,
        final Collection<MethodResultFieldSchema.Builder> fields
      ) throws SchemaBuilderException {
    super(
      MethodSchema.class, 
      MethodResultSchema.class,
      parent, 
      name,
      fields,
      "",
      null
    );
    MethodReturnsSchema returns = null;
    final Map<String, MethodThrowsSchema> exceptions = new LinkedHashMap<>();
    for (final MethodResultFieldSchema field : getFields().values()) {
      if (field instanceof MethodThrowsSchema) {
        exceptions.put(field.getName(), (MethodThrowsSchema) field);
      } else if (field instanceof MethodReturnsSchema) {
        if (returns != null) {
          throw new SchemaBuilderException("more than one return type?");
        } else {
          returns = (MethodReturnsSchema) field;
        }
      } else {
        throw new SchemaBuilderException("unknown field type: " + field);
      }
    }
    if (returns == null) {
      throw new SchemaBuilderException("no return type?");
    }
    this._returnType = returns.getType();
    this._exceptions = Collections.unmodifiableMap(exceptions);
  }

  public SchemaType getReturnType() {
    return this._returnType;
  }

  public Map<String, MethodThrowsSchema> getExceptions() {
    return this._exceptions;
  }

  @Override
  public String getModuleName() {
    return getParent().getParent().getParent().getName();
  }

  @Override
  public String getTypeName() {
    return getParent().getParent().getName() + "." + getName();
  }


  public static final class Builder extends 
    AbstractStructSchema.AbstractStructSchemaBuilder<
      MethodSchema, 
      MethodResultSchema, 
      MethodSchema.Builder, 
      MethodResultFieldSchema.Builder, 
      MethodResultSchema.Builder
    > {

    private SchemaType _returnType = PrimitiveTypeSchema.VOID;

    protected Builder(MethodSchema.Builder parentBuilder) {
      super(parentBuilder, Builder.class);
    }

    public Builder returnType(SchemaType type) {
      this._returnType = type;
      return this;
    }

    @Override
    protected MethodThrowsSchema.Builder _createFieldBuilder() {
      return new MethodThrowsSchema.Builder(this);
    }

    @Override
    protected MethodResultSchema _createStruct(MethodSchema _parent) 
        throws SchemaBuilderException {
      final List<MethodResultFieldSchema.Builder> fields = 
          new ArrayList<>(this._getFields().size() + 1);
      fields.add(new MethodReturnsSchema.Builder(this).type(_returnType));
      fields.addAll(this._getFields());
      return new MethodResultSchema(_parent, getName(), fields);
    }

    public MethodThrowsSchema.Builder addThrows(String _name) {
      return (MethodThrowsSchema.Builder) addField(_name);
    }

  }

}
