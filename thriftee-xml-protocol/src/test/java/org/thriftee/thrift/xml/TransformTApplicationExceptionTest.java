package org.thriftee.thrift.xml;

import static org.junit.Assert.*;
import static org.thriftee.thrift.xml.Transforms.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.protocol.TType;
import org.junit.Test;
import org.thriftee.thrift.xml.protocol.TestProtocol;

public class TransformTApplicationExceptionTest extends BaseThriftXMLTest {

  @Test
  public void testApplicationException() throws 
      TException, IOException, TransformerException {

    final File verbose   = new File(testMethodDir, "verbose.xml");
    final File simple    = new File(testMethodDir, "simple.xml");
    final File streaming = new File(testMethodDir, "streaming.xml");

    final TestProtocol protocol = createOutProtocol();
    final TApplicationException x = new TApplicationException(
      TApplicationException.INTERNAL_ERROR, "An internal error occured."
    );
    protocol.writeMessageBegin(new TMessage("woah", TMessageType.EXCEPTION, 9));
    x.write(protocol);
    protocol.writeMessageEnd();
    protocol.getTransport().flush();
    Transforms.formatXml(protocol.getStringOutput(), new StreamResult(verbose));

    System.out.println("\nexception:\n-----");
    System.out.println(readFileAsString(verbose));

    try (FileWriter w = new FileWriter(simple)) {
      final Transformer trans = Transforms.newStreamingToSimpleTransformer();
      addFormatting(trans);
      trans.setParameter("service_name", "Universe");
      trans.setParameter("schema", urlToModelFor("everything").toString());
      trans.transform(new StreamSource(verbose), new StreamResult(simple));
      w.flush();
    }

    System.out.println("simple output:\n-----");
    System.out.println(readFileAsString(simple));

    final Transformer trans = Transforms.newSimpleToStreamingTransformer();
    addFormatting(trans);
    trans.setParameter("service_name", "Universe");
    trans.setParameter("schema", urlToModelFor("everything").toString());
    trans.transform(new StreamSource(simple), new StreamResult(streaming));
    System.out.println("\nstreaming output:\n----------------");
    System.out.println(readFileAsString(streaming));

    assertEquals("final message should equal original",
      readFileAsString(verbose), readFileAsString(streaming)
    );

    final TestProtocol test = createOutProtocol(readFileAsString(streaming));
    final TMessage msg = test.readMessageBegin();
    assertNotNull(msg);
    assertEquals(TMessageType.EXCEPTION, msg.type);
    assertEquals(9, msg.seqid);
    final TStruct struct = test.readStructBegin();
    assertNotNull(struct);
    
    final TField messageField = test.readFieldBegin();
    assertNotNull(messageField);
    assertEquals(1, messageField.id);
    assertEquals(TType.STRING, messageField.type);
    final String message = test.readString();
    assertEquals(message, "An internal error occured.");
    test.readFieldEnd();

    final TField typeField = test.readFieldBegin();
    assertNotNull(typeField);
    assertEquals(2, typeField.id);
    assertEquals(TType.I32, typeField.type);
    final int type = test.readI32();
    assertEquals(TApplicationException.INTERNAL_ERROR, type);
    test.readFieldEnd();

    test.readStructEnd();
    test.readMessageEnd();

  }

}
