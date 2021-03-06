/*
 * Copyright 2011 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.codegen.meta.impl.java;

import org.jboss.errai.codegen.meta.AnnotationParser;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassMember;
import org.jboss.errai.codegen.meta.MetaParameter;

import java.lang.annotation.Annotation;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class JavaReflectionParameter extends MetaParameter {
  private final static AtomicInteger paramNameCounter = new AtomicInteger();
  
  private final String name;
  private final MetaClass type;
  private final Annotation[] annotations;
  private final MetaClassMember declaredBy;

  public JavaReflectionParameter(final MetaClass type,
                                 final Annotation[] annotations,
                                 final MetaClassMember declaredBy) {
    
    // Java Reflection doesn't provide parameter names, so we have to make one up to satisfy the Parameter interface.
    this.name = "jp" + paramNameCounter.getAndIncrement();
    
    this.type = type;
    this.annotations = AnnotationParser.parseAnnotations(annotations);
    this.declaredBy = declaredBy;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public MetaClass getType() {
    return type;
  }

  @Override
  public Annotation[] getAnnotations() {
    return annotations == null ? new Annotation[0] : annotations;
  }

  @Override
  public final <A extends Annotation> A getAnnotation(final Class<A> annotation) {
    for (final Annotation a : getAnnotations()) {
      if (a.annotationType().equals(annotation)) return (A) a;
    }
    return null;
  }

  @Override
  public final boolean isAnnotationPresent(final Class<? extends Annotation> annotation) {
    return getAnnotation(annotation) != null;
  }


  @Override
  public MetaClassMember getDeclaringMember() {
    return declaredBy;
  }

  @Override
  public String toString() {
    return type.getFullyQualifiedName();
  }
}
