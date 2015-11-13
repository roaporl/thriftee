/*
 * Copyright (C) 2012 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.facebook.swift.generator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;

import com.facebook.swift.generator.template.TemplateContextGenerator;
import com.facebook.swift.parser.ThriftIdlParser;
import com.facebook.swift.parser.model.Document;
import com.google.common.base.Preconditions;

public class SwiftDocumentContext
{
  private final Document document;
  private final String namespace;
  private final SwiftGeneratorConfig generatorConfig;
  private final TypeRegistry typeRegistry;
  private final TypedefRegistry typedefRegistry;
  private final TypeToJavaConverter typeConverter;
  private final ConstantRenderer constantRenderer;
  private final URI thriftUri;

  public SwiftDocumentContext(final URI thriftUri,
                final String namespace,
                final SwiftGeneratorConfig generatorConfig,
                final TypeRegistry typeRegistry,
                final TypedefRegistry typedefRegistry) throws IOException
  {
    InputStream in = null;
    InputStreamReader reader = null;
    try {
      in = thriftUri.toURL().openStream();
      reader = new InputStreamReader(in, Charset.forName("UTF-8"));
      this.document = ThriftIdlParser.parseThriftIdl(reader);
    } catch (IOException e) {
      throw e;
    } finally {
      if (reader != null) { try  { reader.close(); } catch (IOException e2) {} }
      if (in != null) { try { in.close(); } catch (IOException e2) {} }
    }
    this.thriftUri = thriftUri;
    this.namespace = namespace;
    this.generatorConfig = generatorConfig;
    this.typeRegistry = typeRegistry;
    this.typedefRegistry = typedefRegistry;
    this.typeConverter = new TypeToJavaConverter(typeRegistry,
                           typedefRegistry,
                           namespace,
                           getJavaPackage());
    this.constantRenderer = new ConstantRenderer(
      typeConverter, 
      namespace, 
      typeRegistry, 
      typedefRegistry
    );
  }

  public String getNamespace()
  {
    return namespace;
  }

  public Document getDocument()
  {
    return document;
  }

  public TypeRegistry getTypeRegistry()
  {
    return typeRegistry;
  }

  public TypedefRegistry getTypedefRegistry()
  {
    return typedefRegistry;
  }

  public TypeToJavaConverter getTypeConverter()
  {
    return typeConverter;
  }

  public ConstantRenderer getConstantRenderer()
  {
    return constantRenderer;
  }

  public TemplateContextGenerator getTemplateContextGenerator()
  {
    return new TemplateContextGenerator(generatorConfig, typeRegistry, typeConverter, constantRenderer, namespace);
  }

  public String getJavaPackage()
  {
    String effectiveJavaNamespace = "java.swift";
    if (generatorConfig.containsTweak(SwiftGeneratorTweak.USE_PLAIN_JAVA_NAMESPACE)) {
      effectiveJavaNamespace = "java";
    }

    // Override takes precedence
    String javaPackage = generatorConfig.getOverridePackage();
    // Otherwise fallback on package specified in .thrift file
    if (javaPackage == null) {
      javaPackage = getDocument().getHeader().getNamespace(effectiveJavaNamespace);
    }
    // Or the default if we don't have an override package or a package in the .thrift file
    if (javaPackage == null) {
      javaPackage = generatorConfig.getDefaultPackage();
    }

    // If none of the above options get us a package to use, fail
    Preconditions.checkState(javaPackage != null, "thrift uri %s does not declare a '%s' namespace!", thriftUri, effectiveJavaNamespace);

    return javaPackage;
  }
}
