package org.thriftee.provider.swift;

import static org.thriftee.compiler.schema.SchemaBuilderException.Messages.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thriftee.compiler.schema.SchemaBuilder;
import org.thriftee.compiler.schema.SchemaBuilderException;
import org.thriftee.compiler.schema.ThriftSchema;
import org.thriftee.compiler.schema.ThriftSchema.Builder;
import org.thriftee.framework.ThriftEE;
import org.thriftee.util.New;

import com.facebook.swift.parser.ThriftIdlParser;
import com.facebook.swift.parser.model.Document;

public class SwiftSchemaBuilder implements SchemaBuilder {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  
  public SwiftSchemaBuilder() {
  }
  
  @Override
  public ThriftSchema buildSchema(ThriftEE thrift) throws SchemaBuilderException {

    final Map<String, Document> documents = New.map();
    for (File idlFile : thrift.idlFiles()) {
      logger.trace("Parsing generated IDL: {}", idlFile.getName());
      FileReader reader = null;
      try {
        reader = new FileReader(idlFile);
        final Document document = ThriftIdlParser.parseThriftIdl(reader);
        documents.put(idlFile.getName(), document);
      } catch (IOException e) {
        throw new SchemaBuilderException(e, SCHEMA_103, e.getMessage());
      } finally {
        if (reader != null) try { reader.close(); } catch (IOException e) {}
      }
      logger.trace("Parsing {} complete.", idlFile.getName());
    }

    final Document global = documents.get("global.thrift");
    if (global == null) {
      throw new SchemaBuilderException(SCHEMA_100);
    }

    final Builder builder = new Builder().name("ThriftEE");
    for (String include : global.getHeader().getIncludes()) {
      final Document module = documents.get(include);
      if (module == null) {
        throw new SchemaBuilderException(SCHEMA_101, include);
      }
      final String moduleName = includeToModuleName(include);
      SwiftTranslator.translate(builder, moduleName, module);
    }

    return builder.build();
  }
  
  private String includeToModuleName(String include) {
    return include.substring(0, include.length() - 7);
  }
  
}
