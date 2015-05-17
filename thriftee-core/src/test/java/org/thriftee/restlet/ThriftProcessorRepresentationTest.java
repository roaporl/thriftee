package org.thriftee.restlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TType;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.junit.Assert;
import org.junit.Test;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.representation.Representation;
import org.thriftee.compiler.schema.MethodArgumentSchema;
import org.thriftee.compiler.schema.MethodSchema;
import org.thriftee.compiler.schema.ModuleSchema;
import org.thriftee.compiler.schema.ServiceSchema;
import org.thriftee.examples.usergroup.domain.User;
import org.thriftee.tests.AbstractThriftEETest;
import org.thriftee.util.New;

import com.facebook.swift.codec.ThriftCodec;
import com.facebook.swift.codec.ThriftCodecManager;

public class ThriftProcessorRepresentationTest extends AbstractThriftEETest {

  @Test
  public void testBinaryProcessor() throws Exception{
    TProtocolFactory factory = new TBinaryProtocol.Factory();
    testProcessor(factory);
  }

  @Test
  public void testJsonProcessor() throws Exception {
    TProtocolFactory factory = new TJSONProtocol.Factory();
    testProcessor(factory);
  }

  @Test
  public void testCompactProcessor() throws Exception {
    TProtocolFactory factory = new TCompactProtocol.Factory();
    testProcessor(factory);
  }

  @Test
  public void testTupleProcessor() throws Exception {
    TProtocolFactory factory = new TTupleProtocol.Factory();
    testProcessor(factory);
  }

  void testProcessor(TProtocolFactory factory) throws Exception {

    final boolean txt = factory.getClass().equals(TJSONProtocol.Factory.class);
    LOG.debug("testing protocol: " + factory.getProtocol(null).getClass());

    final String modName = USERGROUP_SERVICES_MODULE;
    final ThriftCodecManager mgr = thrift().codecManager();
    final ModuleSchema module = thrift().schema().getModules().get(modName);
    final ServiceSchema service = module.getServices().get("UserService");
    final MethodSchema method = service.getMethods().get("find");

    final Map<String, Object> args = New.map();
    args.put("uid", "aaardvark");
    final byte[] serviceCall = createServiceCall(mgr, factory, method, args);
    String callStr;
    if (txt) {
      callStr = new String(serviceCall);
    } else {
      callStr = new BigInteger(1, serviceCall).toString(16);
    }
    LOG.debug("service call: {}", callStr);

    final Representation in = new ByteArrayRepresentation(serviceCall);
    final ThriftProcessorRepresentation r = new ThriftProcessorRepresentation(
      in, factory, factory, thrift().processorFor(service)
    );
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    r.write(out);
    String resultStr;
    if (txt) {
      resultStr = new String(out.toByteArray());
    } else {
      resultStr = new BigInteger(1, out.toByteArray()).toString(16);
    }
    LOG.debug("service result: {}", resultStr);

    final InputStream bais = new ByteArrayInputStream(out.toByteArray());
    final TTransport transport = new TIOStreamTransport(bais, null);
    final TProtocol protocol = factory.getProtocol(transport);

    final ThriftCodec<User> codec = mgr.getCodec(User.class);
    final TMessage msg = protocol.readMessageBegin();
    protocol.readStructBegin();
    final TField field = protocol.readFieldBegin();
    final User result = codec.read(protocol);
    protocol.readFieldEnd();
    protocol.readStructEnd();
    protocol.readMessageEnd();

    Assert.assertEquals(TMessageType.REPLY, msg.type);
    Assert.assertEquals("find", msg.name);
    Assert.assertEquals(0, msg.seqid);

    Assert.assertEquals(TType.STRUCT, field.type);
    Assert.assertEquals(0, field.id);

    Assert.assertEquals("aaardvark", result.getUid());
    Assert.assertEquals("Alan", result.getFirstName());
    Assert.assertEquals("Aardvark", result.getLastName());
    Assert.assertEquals("Aardvark, Alan", result.getDisplayName());
    Assert.assertEquals("alanaardvark@example.com", result.getEmail());

  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static byte[] createServiceCall(
      ThriftCodecManager mgr,
      TProtocolFactory factory,
      MethodSchema method, 
      Map<String, Object> args
    ) throws TException {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    TTransport transport = new TIOStreamTransport(baos);
    TProtocol protocol = factory.getProtocol(transport);
    TMessage msg = new TMessage(method.getName(), TMessageType.CALL, 0);
    protocol.writeMessageBegin(msg);
    protocol.writeStructBegin(new TStruct(""));
    if (args != null) {
      for (Entry<String, Object> entry : args.entrySet()) {
        final String key = entry.getKey();
        final Object arg = entry.getValue();
        final MethodArgumentSchema argSchema = method.getArguments().get(key);
        if (arg == null) {
          throw new IllegalArgumentException("argument cannot be null.");
        }
        if (argSchema == null) {
          throw new IllegalArgumentException(
            "no schema for arg: " + key + " : " + 
            method.getArguments().keySet()
          );
        }
        final ThriftCodec codec = mgr.getCodec(arg.getClass());
        if (codec == null) {
          throw new IllegalArgumentException("no codec for " + arg.getClass());
        }
        byte codecType = codec.getType().getProtocolType().getType();
        byte schemaType = argSchema.getType().getProtocolType().getType();
        if (codecType != schemaType) {
          throw new IllegalArgumentException(
            "codec and schema types do not match for arg: " + key);
        }
        final TField field = new TField(
          argSchema.getName(), 
          argSchema.getType().getProtocolType().getType(), 
          argSchema.getIdentifier().shortValue()
        );
        protocol.writeFieldBegin(field);
        try {
          codec.write(arg, protocol);
        } catch (Exception e) {
          throw new TException(e);
        }
        protocol.writeFieldEnd();
      }
    }
    protocol.writeFieldStop();
    protocol.writeStructEnd();
    protocol.writeMessageEnd();

    final byte[] bytes = baos.toByteArray();
    return bytes;

  }
}