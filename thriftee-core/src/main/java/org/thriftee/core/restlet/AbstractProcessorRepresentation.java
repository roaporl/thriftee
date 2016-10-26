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
package org.thriftee.core.restlet;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thriftee.core.exceptions.ThriftMessage;
import org.thriftee.core.exceptions.ThriftRuntimeException;

public abstract class AbstractProcessorRepresentation
        extends OutputRepresentation {

  protected final Logger LOG = LoggerFactory.getLogger(getClass());

  private final TProtocolFactory inProtocolFactory;

  private final TProtocolFactory outProtocolFactory;

  private final TProcessor processor;

  public AbstractProcessorRepresentation(
      final MediaType mediaType,
      final TProtocolFactory inProtocolFactory,
      final TProtocolFactory outProtocolFactory,
      final TProcessor processor
    ) {
    super(null);
    this.inProtocolFactory = inProtocolFactory;
    this.outProtocolFactory = outProtocolFactory;
    this.processor = processor;
  }

  protected TProtocolFactory getInFactory() {
    return inProtocolFactory;
  }

  protected TProtocolFactory getOutFactory() {
    return outProtocolFactory;
  }

  protected TProcessor getProcessor() {
    return processor;
  }

  final void process(
        final InputStream in,
        final OutputStream out
      ) throws IOException {
    if (out == null) {
      throw new IllegalArgumentException("output stream cannot be null.");
    }
    final TTransport transport = new TIOStreamTransport(
      requireNonNull(in, "input stream cannot be null"),
      requireNonNull(out, "output stream cannot be null")
    );
    try {
      final TProtocol inProtocol = getInFactory().getProtocol(transport);
      final TProtocol outProtocol = getOutFactory().getProtocol(transport);
      if (getProcessor().process(inProtocol, outProtocol)) {
        transport.flush();
        out.flush();
      } else {
        throw new IOException("TProcessor.process() returned false");
      }
    } catch (TException|RuntimeException e) {
      LOG.error(Messages.PROCESSOR_001.getMessage(), e);
      throw new ThriftRuntimeException(e, Messages.PROCESSOR_001);
    } finally {
      transport.close();
    }
  }

  public static enum Messages implements ThriftMessage {

    PROCESSOR_001("An error processing a thrift service call."),
    ;

    private final String msg;

    private Messages(String msg) {
      this.msg = msg;
    }

    @Override
    public String getCode() {
      return name();
    }

    @Override
    public String getMessage() {
      return msg;
    }

  }
/*
  @Override
  public final void write(final OutputStream out) throws IOException {
    if (out == null) {
      throw new IllegalArgumentException("output stream cannot be null.");
    }
    final InputStream in = getInputStream();
    final TTransport transport = new TIOStreamTransport(in, out);
    try {
      final TProtocol inProtocol = getInFactory().getProtocol(transport);
      final TProtocol outProtocol = getOutFactory().getProtocol(transport);
      if (getProcessor().process(inProtocol, outProtocol)) {
        transport.flush();
        out.flush();
      } else {
        throw new IOException("TProcessor.process() returned false");
      }
    } catch (TException|RuntimeException e) {
      LOG.error(Messages.PROCESSOR_001.getMessage(), e);
      throw new ThriftRuntimeException(e, Messages.PROCESSOR_001);
    } finally {
      transport.close();
    }
  }
*/

}
