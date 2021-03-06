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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.thriftee.thrift.schema.AbstractFieldSchema.Requiredness;
import org.thriftee.thrift.schema.idl.IdlSchema;

public class IdlSchemaBuilderTest extends BaseSchemaTest {

  @Test
  public void testExampleSchema() throws Exception {

    final File xmlModel = createXmlModel();
    final IdlSchema idlSchema = IdlXmlUtils.fromXml(new StreamSource(xmlModel));
    final ThriftSchema schema = new IdlSchemaBuilder().buildFrom(idlSchema);

    assertNotNull("schema must not be null", schema);
    assertNotNull("modules collection must not be null", schema.getModules());
    assertEquals("number of modules must be 3", 3, schema.getModules().size());
    assertTrue(schema.getRoot() instanceof ThriftSchema);

    final ModuleSchema thrift = schema.getModules().get("org.apache.thrift");
    assertNotNull("org.apache.thrift module must not be null", thrift);
    assertTrue(thrift.getExceptions().containsKey("TApplicationException"));
    assertNotNull(schema.applicationExceptionSchema());
    assertTrue(thrift.getRoot() instanceof ThriftSchema);

    final ModuleSchema module = schema.getModules().get("everything");
    assertNotNull("everything module must not be null", module);
    assertTrue(module.getDoc().startsWith("this is just"));
    assertEquals(1, module.getIncludes().size());
    assertTrue(module.getRoot() instanceof ThriftSchema);

    final ModuleSchema nothing = schema.getModules().get("nothing_all_at_once");
    assertNotNull("nothing_all_at_once module must not be null", nothing);
    assertEquals("", nothing.getDoc());
    assertEquals(0, nothing.getIncludes().size());
    assertTrue(nothing.getRoot() instanceof ThriftSchema);

    final ServiceSchema universe = module.getServices().get("Universe");
    assertNotNull("universe service should not be null");
    assertEquals("this service has some documentation", universe.getDoc());
    assertEquals("nothing_all_at_once.Metaverse", universe.getParentService());
    assertNotNull(universe.getParentServiceSchema());
    assertTrue(universe.getRoot() instanceof ThriftSchema);

    try {
      universe.findMethod("notreal");
      fail("should have thrown SchemaException for invalid method");
    } catch (final SchemaException e) {
      assertTrue(e.getMessage().startsWith("method not found"));
    }

    final MethodSchema grok = universe.findMethod("grok");
    assertNotNull(grok);
    assertEquals(PrimitiveTypeSchema.I32, grok.getReturnType());
    assertFalse(grok.isOneway());
    assertEquals(3, grok.getExceptions().size());
    assertEquals(4, grok.getResultStruct().getFields().size());
    assertTrue(grok.getRoot() instanceof ThriftSchema);

    final MethodReturnsSchema grokSuccess = (MethodReturnsSchema)
        grok.getResultStruct().getField((short)0);
    assertEquals(PrimitiveTypeSchema.I32, grokSuccess.getType());
    assertEquals(Requiredness.NONE, grokSuccess.getRequiredness());
    assertEquals("success", grokSuccess.getName());
    assertEquals("", grokSuccess.getDoc());
    assertEquals(0, grokSuccess.getAnnotations().size());
    assertTrue(grokSuccess.getRoot() instanceof ThriftSchema);

    final MethodSchema sendit = universe.findMethod("sendIt");
    assertNotNull(sendit);
    assertEquals(PrimitiveTypeSchema.VOID, sendit.getReturnType());
    assertEquals(0, sendit.getArguments().size());
    assertTrue(sendit.getRoot() instanceof ThriftSchema);

    final EnumSchema enumSchema = module.getEnums().get("Spinkle");
    assertNotNull(enumSchema);
    assertEquals(3, enumSchema.getEnumValues().size());
    assertTrue(enumSchema.getRoot() instanceof ThriftSchema);

    final StructSchema struct = module.getStructs().get("Everything");
    assertNotNull("Everything struct must not be null", struct);
    assertEquals("This struct has a bunch of fields", struct.getDoc());
    assertTrue(struct.getRoot() instanceof ThriftSchema);

    final UnionSchema union = module.getUnions().get("Sprat");
    assertNotNull(union);
    assertEquals("some union doc & \"stuff\"", union.getDoc());
    assertEquals(3, union.getFields().size());
    assertNotNull(union.getField((short)1).getDoc());
    assertNotNull("some wowzer doc", union.getField((short)2).getDoc());
    assertTrue(union.getRoot() instanceof ThriftSchema);
  }

}
