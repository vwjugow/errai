/**
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.errai.security.test.style.client.local;

import static org.jboss.errai.enterprise.client.cdi.api.CDI.addPostInitTask;

import java.util.HashSet;
import java.util.Set;

import org.jboss.errai.enterprise.client.cdi.AbstractErraiCDITest;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.client.local.api.SecurityContext;
import org.jboss.errai.security.client.local.context.SecurityContextImpl;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.annotation.RestrictedAccess;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.jboss.errai.security.test.style.client.local.res.TemplatedStyleWidget;
import org.junit.Test;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Widget;

public class SecurityStyleTest extends AbstractErraiCDITest {

  @Override
  public String getModuleName() {
    return "org.jboss.errai.security.test.style.StyleTest";
  }

  private final User regularUser;
  private final User adminUser;

  private final RoleImpl userRole = new RoleImpl("user");
  private final RoleImpl adminRole = new RoleImpl("admin");

  private SyncBeanManager bm;
  private SecurityContext securityContext;

  public SecurityStyleTest() {
    final Set<Role> regularUserRoles = new HashSet<Role>();
    regularUserRoles.add(userRole);
    regularUser = new UserImpl("testuser", regularUserRoles);

    final Set<Role> adminUserRoles = new HashSet<Role>();
    adminUserRoles.add(userRole);
    adminUserRoles.add(adminRole);
    adminUser = new UserImpl("testadmin", adminUserRoles);
  }

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    addPostInitTask(new Runnable() {

      @Override
      public void run() {
        bm = IOC.getBeanManager();
        securityContext = bm.lookupBean(SecurityContextImpl.class).getInstance();
      }
    });
  }

  /**
   * Regression test for ERRAI-644.
   */
  @Test
  public void testTemplatedElementsStyleWhenNotLoggedIn() throws Exception {
    asyncTest();
    addPostInitTask(new Runnable() {

      @Override
      public void run() {
        final TemplatedStyleWidget widget = bm.lookupBean(TemplatedStyleWidget.class).getInstance();
        // Make sure we are not logged in as anyone.
        securityContext.setCachedUser(User.ANONYMOUS);
        
        assertFalse(hasStyle(RestrictedAccess.CSS_CLASS_NAME, widget.getControl()));
        assertTrue(hasStyle(RestrictedAccess.CSS_CLASS_NAME, widget.getAuthenticatedAnchor()));
        assertTrue(hasStyle(RestrictedAccess.CSS_CLASS_NAME, widget.getUserAnchor()));
        assertTrue(hasStyle(RestrictedAccess.CSS_CLASS_NAME, widget.getUserAdminAnchor()));
        assertTrue(hasStyle(RestrictedAccess.CSS_CLASS_NAME, widget.getAdminAnchor()));

        finishTest();
      }
    });
  }

  @Test
  public void testTemplatedElementsStyleWithSomeRoles() throws Exception {
    asyncTest();
    addPostInitTask(new Runnable() {

      @Override
      public void run() {
        final TemplatedStyleWidget widget = bm.lookupBean(TemplatedStyleWidget.class).getInstance();

        securityContext.setCachedUser(regularUser);
        
        assertFalse(hasStyle(RestrictedAccess.CSS_CLASS_NAME, widget.getControl()));
        assertFalse(hasStyle(RestrictedAccess.CSS_CLASS_NAME, widget.getAuthenticatedAnchor()));
        assertFalse(hasStyle(RestrictedAccess.CSS_CLASS_NAME, widget.getUserAnchor()));
        assertTrue(hasStyle(RestrictedAccess.CSS_CLASS_NAME, widget.getUserAdminAnchor()));
        assertTrue(hasStyle(RestrictedAccess.CSS_CLASS_NAME, widget.getAdminAnchor()));

        finishTest();
      }
    });
  }

  @Test
  public void testTemplatedElementsStyleFullyAuthorized() throws Exception {
    asyncTest();
    addPostInitTask(new Runnable() {

      @Override
      public void run() {
        final TemplatedStyleWidget widget = bm.lookupBean(TemplatedStyleWidget.class).getInstance();

        securityContext.setCachedUser(adminUser);
        
        assertFalse(hasStyle(RestrictedAccess.CSS_CLASS_NAME, widget.getControl()));
        assertFalse(hasStyle(RestrictedAccess.CSS_CLASS_NAME, widget.getAuthenticatedAnchor()));
        assertFalse(hasStyle(RestrictedAccess.CSS_CLASS_NAME, widget.getUserAnchor()));
        assertFalse(hasStyle(RestrictedAccess.CSS_CLASS_NAME, widget.getUserAdminAnchor()));
        assertFalse(hasStyle(RestrictedAccess.CSS_CLASS_NAME, widget.getAdminAnchor()));

        finishTest();
      }
    });
  }
  
  @Test
  public void testAdditionalStyleBindingApplied() throws Exception {
    asyncTest();
    addPostInitTask(new Runnable() {
      
      @Override
      public void run() {
        // Make sure we are not logged in as anyone.
        securityContext.setCachedUser(User.ANONYMOUS);

        final TemplatedStyleWidget widget = bm.lookupBean(TemplatedStyleWidget.class).getInstance();

        Anchor customStyledUserAnchor = widget.getCustomStyledUserAnchor();
        String color = customStyledUserAnchor.getElement().getStyle().getColor();
        String bgColor = customStyledUserAnchor.getElement().getStyle().getBackgroundColor();
        
        assertEquals("Custom style binding not applied", "red", color);
        assertEquals("Custom style binding not applied", "blue", bgColor);
        assertTrue(hasStyle(RestrictedAccess.CSS_CLASS_NAME, customStyledUserAnchor));
        
        securityContext.setCachedUser(adminUser);
        assertEquals("Custom style binding not applied", "red", color);
        assertEquals("Custom style binding not applied", "blue", bgColor);
        assertFalse(hasStyle(RestrictedAccess.CSS_CLASS_NAME, customStyledUserAnchor));
        
        finishTest();
      }
    });
  }
  
  private boolean hasStyle(final String name, final Widget widget) {
    String cssClasses = widget.getElement().getAttribute("class");
    return cssClasses != null && cssClasses.contains(name);
  }
}
