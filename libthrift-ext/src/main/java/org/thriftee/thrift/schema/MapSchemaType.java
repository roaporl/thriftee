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

public final class MapSchemaType extends ContainerSchemaType {

  private static final long serialVersionUID = -5613803424652950927L;

  private final SchemaType keyType;

  MapSchemaType(
      final SchemaType _keyType,
      final SchemaType _valueType) throws SchemaBuilderException {
    super(ThriftProtocolType.MAP, _valueType);
    this.keyType = _keyType;
  }

  public SchemaType getKeyType() {
    return this.keyType;
  }

  @Override
  public String toNamespacedIDL(String namespace) {
    return "map<" +
      getKeyType().toNamespacedIDL(namespace) + ", " +
      getValueType().toNamespacedIDL(namespace) +
    ">";
  }

}
