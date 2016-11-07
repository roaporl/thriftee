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

public class SchemaException extends Exception {

  private static final long serialVersionUID = 8615124125530306117L;

  public SchemaException(String msg) {
      super(msg);
  }

  public SchemaException(String msg, Throwable cause) {
      super(msg, cause);
  }

  public static final SchemaException methodNotFound(MethodIdentifier id) {
    return new SchemaException(String.format(
      "method not found on %s.%s: %s",
      id.getModuleName(), id.getServiceName(), id.getMethodName()
    ));
  }

}