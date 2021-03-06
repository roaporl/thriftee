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
package org.thriftee.thrift.protocol;

import java.nio.ByteBuffer;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.TSet;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.protocol.TType;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractContextProtocol extends TProtocol {

  protected final Logger LOG = LoggerFactory.getLogger(getClass());

  /*--------------------------- Write Methods ------------------------------*/
  @Override
  public void writeMessageBegin(TMessage msg) throws TException {
    final MessageContext ctx = writectx.base().newMessage();
    ctx.set(msg);
    ctx.push();
    ctx.writeStart();
  }

  @Override
  public void writeMessageEnd() throws TException {
    writectx.peek(MessageContext.class).writeEnd().pop();
  }

  @Override
  public void writeStructBegin(TStruct struct) throws TException {
    final StructContext structctx;
    final Context ctx = writectx.peek();
    if (ctx instanceof ValueHolderContext) {
      structctx = ((ValueHolderContext)ctx).newStruct();
    } else if (ctx instanceof MessageContext) {
      structctx = ((MessageContext)ctx).newStruct();
    } else if (ctx instanceof BaseContext) {
      structctx = ((BaseContext)ctx).newStruct();
    } else {
      throw new IllegalStateException();
    }
    structctx.set(struct);
    structctx.push();
    structctx.writeStart();
  }

  @Override
  public void writeStructEnd() throws TException {
    writectx.peek(StructContext.class).writeEnd().pop();
  }

  @Override
  public void writeFieldBegin(TField field) throws TException {
    final FieldContext ctx = writectx.peek(StructContext.class).newField();
    ctx.set(field);
    ctx.push();
    ctx.writeStart();
  }

  @Override
  public void writeFieldEnd() throws TException {
    writectx.peek(FieldContext.class).writeEnd().pop();
  }

  @Override
  public void writeFieldStop() throws TException {
    writectx.peek(StructContext.class).writeFieldStop();
  }

  @Override
  public void writeListBegin(TList list) throws TException {
    final ListContext ctx = writectx.peek(ValueHolderContext.class).newList();
    ctx.set(list);
    ctx.push();
    ctx.writeStart();
  }

  @Override
  public void writeListEnd() throws TException {
    writectx.peek(ListContext.class).writeEnd().pop();
  }

  @Override
  public void writeMapBegin(TMap map) throws TException {
    final MapContext ctx = writectx.peek(ValueHolderContext.class).newMap();
    ctx.set(map);
    ctx.push();
    ctx.writeStart();
  }

  @Override
  public void writeMapEnd() throws TException {
    writectx.peek(MapContext.class).writeEnd().pop();
  }

  @Override
  public void writeSetBegin(TSet set) throws TException {
    final SetContext ctx = writectx.peek(ValueHolderContext.class).newSet();
    ctx.set(set);
    ctx.push();
    ctx.writeStart();
  }

  @Override
  public void writeSetEnd() throws TException {
    writectx.peek(SetContext.class).writeEnd().pop();
  }

  @Override
  public void writeBinary(ByteBuffer buffer) throws TException {
    writectx.peek(ValueHolderContext.class).writeBinary(buffer);
  }

  @Override
  public void writeBool(boolean bool) throws TException {
    writectx.peek(ValueHolderContext.class).writeBool(bool);
  }

  @Override
  public void writeByte(byte bite) throws TException {
    writectx.peek(ValueHolderContext.class).writeByte(bite);
  }

  @Override
  public void writeDouble(double dbl) throws TException {
    writectx.peek(ValueHolderContext.class).writeDouble(dbl);
  }

  @Override
  public void writeI16(short n) throws TException {
    writectx.peek(ValueHolderContext.class).writeI16(n);
  }

  @Override
  public void writeI32(int n) throws TException {
    writectx.peek(ValueHolderContext.class).writeI32(n);
  }

  @Override
  public void writeI64(long n) throws TException {
    writectx.peek(ValueHolderContext.class).writeI64(n);
  }

  @Override
  public void writeString(String str) throws TException {
    writectx.peek(ValueHolderContext.class).writeString(str);
  }

  /*--------------------------- Read Methods -------------------------------*/
  @Override
  public TMessage readMessageBegin() throws TException {
    final MessageContext ctx = readctx.peek(BaseContext.class).newMessage();
    ctx.readStart().push();
    return ctx.emit();
  }

  @Override
  public void readMessageEnd() throws TException {
    readctx.peek().readEnd().pop();
  }

  @Override
  public TStruct readStructBegin() throws TException {
    final Context ctx = readctx.peek();
    final StructContext struct;
    if (ctx instanceof ValueHolderContext) {
      struct = ((ValueHolderContext)ctx).newStruct();
    } else if (ctx instanceof MessageContext) {
      struct = ((MessageContext)ctx).newStruct();
    } else if (ctx instanceof BaseContext) {
      struct = ((BaseContext)ctx).newStruct();
    } else {
      throw new IllegalStateException();
    }
    struct.push().readStart();
    return struct.emit();
  }

  @Override
  public void readStructEnd() throws TException {
    readctx.peek(StructContext.class).readEnd().pop();
  }

  @Override
  public TField readFieldBegin() throws TException {
    final FieldContext ctx = readctx.peek(StructContext.class).newField();
    if (ctx == null) { // subclasses can return null for stop field
      return new TField();
    }
    ctx.readStart().push();
    if (ctx.fieldType() == TType.STOP) {
      ctx.readEnd().pop();
      return new TField();
    } else {
      return ctx.emit();
    }
  }

  @Override
  public void readFieldEnd() throws TException {
    readctx.peek(FieldContext.class).readEnd().pop();
  }

  @Override
  public TList readListBegin() throws TException {
    final ListContext ctx = readctx.peek(ValueHolderContext.class).newList();
    ctx.readStart().push();
    return ctx.emit();
  }

  @Override
  public void readListEnd() throws TException {
    readctx.peek(ListContext.class).readEnd().pop();
  }

  @Override
  public TMap readMapBegin() throws TException {
    final MapContext ctx = readctx.peek(ValueHolderContext.class).newMap();
    ctx.readStart().push();
    return ctx.emit();
  }

  @Override
  public void readMapEnd() throws TException {
    readctx.peek(MapContext.class).readEnd().pop();
  }

  @Override
  public TSet readSetBegin() throws TException {
    final SetContext ctx = readctx.peek(ValueHolderContext.class).newSet();
    ctx.readStart().push();
    return ctx.emit();
  }

  @Override
  public void readSetEnd() throws TException {
    readctx.peek(SetContext.class).readEnd().pop();
  }

  @Override
  public ByteBuffer readBinary() throws TException {
    return readctx.peek(ValueHolderContext.class).readBinary();
  }

  @Override
  public boolean readBool() throws TException {
    return readctx.peek(ValueHolderContext.class).readBool();
  }

  @Override
  public byte readByte() throws TException {
    return readctx.peek(ValueHolderContext.class).readByte();
  }

  @Override
  public double readDouble() throws TException {
    return readctx.peek(ValueHolderContext.class).readDouble();
  }

  @Override
  public String readString() throws TException {
    return readctx.peek(ValueHolderContext.class).readString();
  }

  @Override
  public short readI16() throws TException {
    return readctx.peek(ValueHolderContext.class).readI16();
  }

  @Override
  public int readI32() throws TException {
    return readctx.peek(ValueHolderContext.class).readI32();
  }

  @Override
  public long readI64() throws TException {
    return readctx.peek(ValueHolderContext.class).readI64();
  }

  /*--------------------------- Context API -------------------------------*/
  public static enum ContextType {
    READ,
    WRITE;
  }

  public static enum ContainerType {
    LIST(TType.LIST),
    SET(TType.SET),
    MAP(TType.MAP);
    private final byte byteval;
    ContainerType(byte byteval) {
      this.byteval = byteval;
    }
    public String strval() {
      return name().toLowerCase();
    }
    public byte byteval() {
      return byteval;
    }
  }

  public interface Context {
    Context parent();
    BaseContext base();
    ContextType type();
    Context writeStart() throws TException;
    Context readStart() throws TException;
    Context writeEnd() throws TException;
    Context readEnd() throws TException;
    Context peek() throws TException;
    Context push() throws TException;
    Context pop() throws TException;
    void pushed() throws TException;
    void popped() throws TException;
    <T extends Context> T peek(Class<T> type) throws TException;
    <T extends Context> T pop(Class<T> type) throws TException;
    void debug(String prefix);
  }

  public interface TypedContext<T> extends Context {
    T emit() throws TException;
    void set(T obj) throws TException;
  }

  public interface ValueHolder {

    void writeBinary(ByteBuffer buffer) throws TException;
    void writeBool(boolean bool) throws TException;
    void writeByte(byte bite) throws TException;
    void writeDouble(double dbl) throws TException;
    void writeI16(short arg0) throws TException;
    void writeI32(int arg0) throws TException;
    void writeI64(long arg0) throws TException;
    void writeString(String str) throws TException;

    ByteBuffer readBinary() throws TException;
    boolean readBool() throws TException;
    byte readByte() throws TException;
    double readDouble() throws TException;
    short readI16() throws TException;
    int readI32() throws TException;
    long readI64() throws TException;
    String readString() throws TException;

  }

  public interface ValueHolderContext extends ValueHolder, Context {

    StructContext newStruct() throws TException;
    ListContext newList() throws TException;
    SetContext newSet() throws TException;
    MapContext newMap() throws TException;

  }

  public interface MessageContext extends TypedContext<TMessage> {
    StructContext newStruct() throws TException;
  }

  public interface StructContext extends TypedContext<TStruct> {
    FieldContext newField() throws TException;
    StructContext writeFieldStop() throws TException;
  }

  public interface FieldContext extends ValueHolderContext, TypedContext<TField> {
    byte fieldType();
  }

  public interface ContainerContext<T> extends ValueHolderContext, TypedContext<T> {
    ContainerType containerType();
  }

  public interface ListContext extends ContainerContext<TList> {}

  public interface SetContext extends ContainerContext<TSet> {}

  public interface MapContext extends ContainerContext<TMap> {}

  public static abstract class AbstractContext implements Context {
    private final Context parent;
    private final BaseContext base;
    AbstractContext(Context parent) {
      this.parent = parent;
      if (this instanceof BaseContext) {
        this.base = (BaseContext) this;
      } else {
        this.base = this.parent.base();
      }
    }
    @Override
    public final Context parent() {
      return this.parent;
    }
    @Override
    public final BaseContext base() {
      return base;
    }
    @Override
    public final ContextType type() {
      return base().type;
    }
    public final boolean isReading() {
      return base().type.equals(ContextType.READ);
    }
    public final boolean isWriting() {
      return base().type.equals(ContextType.WRITE);
    }
    @Override
    public Context peek() {
      return base().peek();
    }
    @Override
    public Context pop() throws TException {
      return base().pop();
    }
    @Override
    public Context push() throws TException {
      return base().push(this);
    }
    @Override
    public <T extends Context> T peek(Class<T> type) {
      return _ensure(type, peek());
    }
    @Override
    public <T extends Context> T pop(Class<T> type) throws TException {
      return _ensure(type, pop());
    }
    @Override
    public void pushed() throws TException { } // debug("push: "); }
    @Override
    public void popped() throws TException { } // debug("pop:  "); }
    @Override
    public final void debug(String op) {
      final StringBuilder sb = new StringBuilder();
      sb.append(type().name().toLowerCase());
      if (op != null) {
        sb.append(' ').append(op).append(": ");
      }
      for (Context top = peek().parent(); top != null; top = top.parent()) {
        sb.append("  ");
      }
      sb.append(toString());
      System.out.println(sb.toString());
    }
  }

  public static abstract class BaseContext extends AbstractContext {
    private Context head = this;
    private final ContextType type;
    BaseContext(ContextType type) {
      super(null);
      if (type == null) {
        throw new IllegalArgumentException("type cannot be null.");
      }
      this.type = type;
    }
    @Override
    public final BaseContext writeStart() throws TException { throw up(); }
    @Override
    public final BaseContext readStart() throws TException { throw up(); }
    @Override
    public final BaseContext writeEnd() throws TException { throw up(); }
    @Override
    public final BaseContext readEnd() throws TException { throw up(); }
    @Override
    public final Context peek() {
      return head;
    }
    <T extends Context> T push(final T context) throws TException {
      if (context.parent() != head) {
        throw new IllegalArgumentException(
          "new context's parent must match the current top of stack");
      }
      final Context oldhead = this.head;
      this.head = context;
      oldhead.pushed();
//      context.debug("push: ");
      return context;
    }
    @Override
    public Context pop() throws TException {
      if (this.head == this) {
        throw new IllegalStateException("Cannot pop the base context.");
      }
      final Context oldhead = this.head;
//      oldhead.debug(" pop: ");
      this.head = oldhead.parent();
      this.head.popped();
      return oldhead;
    }
    abstract StructContext newStruct() throws TException;
    abstract MessageContext newMessage() throws TException;
  }

  public static abstract class AbstractStructContext
      extends AbstractContext implements StructContext {

    String name;

    AbstractStructContext(Context parent) {
      super(parent);
    }

    @Override
    public void set(TStruct obj) {
      this.name = obj.name;
    }

    @Override
    public TStruct emit() {
      return new TStruct(name);
    }

    @Override
    public String toString() {
      return "<TStruct name:'" + name + "'>";
    }

    @Override
    public abstract FieldContext newField() throws TException;

  }

  private static <T extends Context> T _ensure(Class<T> type, Context ctx) {
    if (type.isAssignableFrom(ctx.getClass())) {
      return type.cast(ctx);
    }
    throw new IllegalArgumentException(
      "Expected " + type.getSimpleName() +
      " but was actually " + ctx.getClass().getSimpleName()
    );
  }

  protected abstract BaseContext createBaseContext(ContextType type);

  protected final BaseContext readctx;

  protected final BaseContext writectx;

  protected AbstractContextProtocol(TTransport trans) {
    super(trans);
    this.writectx = createBaseContext(ContextType.WRITE);
    this.readctx = createBaseContext(ContextType.READ);
  }

  protected static final UnsupportedOperationException up() {
    throw new UnsupportedOperationException();
  }

  protected static TException ex(String msg) {
    return new TProtocolException(msg);
  }

  protected static TException ex(Throwable t) {
    for (Throwable x = t; x != null; x = x.getCause())
      if (x instanceof TException)
        return (TException) x;
    return new TProtocolException(t);
  }

  protected static TException ex(String msg, Throwable t) {
//    LOG.error("An error occurred during TXMLProtocol processing: " + msg, t);
    if (msg == null) {
      return ex(t);
    } else if (t == null) {
      return ex(msg);
    } else {
      return new TProtocolException(msg, t);
    }
  }

}