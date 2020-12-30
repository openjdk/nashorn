/*
 * Copyright (c) 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.openjdk.dynalink.test;

import static jdk.dynalink.StandardNamespace.PROPERTY;
import static jdk.dynalink.StandardOperation.GET;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import jdk.dynalink.CallSiteDescriptor;
import jdk.dynalink.DynamicLinker;
import jdk.dynalink.DynamicLinkerFactory;
import jdk.dynalink.Operation;
import jdk.dynalink.support.SimpleRelinkableCallSite;
import org.openjdk.nashorn.api.scripting.AbstractJSObject;
import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings("javadoc")
public class DynamicLinkerFactoryTest {

    private static final Operation GET_PROPERTY = GET.withNamespace(PROPERTY);

    private static DynamicLinkerFactory newDynamicLinkerFactory(final boolean resetClassLoader) {
        final DynamicLinkerFactory factory = new DynamicLinkerFactory();
        if (resetClassLoader) {
            factory.setClassLoader(null);
        }
        return factory;
    }

    @Test
    public void nashornExportedLinkerJSObjectTest() {
        final DynamicLinkerFactory factory = newDynamicLinkerFactory(false);
        final DynamicLinker linker = factory.createLinker();

        final MethodType mt = MethodType.methodType(Object.class, Object.class);
        final Operation op = GET_PROPERTY.named("foo");
        final CallSite cs = linker.link(new SimpleRelinkableCallSite(new CallSiteDescriptor(
                MethodHandles.publicLookup(), op, mt)));
        final boolean[] reachedGetMember = new boolean[1];
        // check that the nashorn exported linker can be used for user defined JSObject
        final Object obj = new AbstractJSObject() {
                @Override
                public Object getMember(final String name) {
                    reachedGetMember[0] = true;
                    return name.equals("foo")? "bar" : "<unknown>";
                }
            };

        Object value = null;
        try {
            value = cs.getTarget().invoke(obj);
        } catch (final Throwable th) {
            throw new RuntimeException(th);
        }

        Assert.assertTrue(reachedGetMember[0]);
        Assert.assertEquals(value, "bar");
    }

    @Test
    public void nashornExportedLinkerScriptObjectMirrorTest() {
        final DynamicLinkerFactory factory = newDynamicLinkerFactory(false);
        final DynamicLinker linker = factory.createLinker();

        // check that the nashorn exported linker can be used for ScriptObjectMirror
        final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        final MethodType mt = MethodType.methodType(Object.class, Object.class);
        final Operation op = GET_PROPERTY.named("foo");
        final CallSite cs = linker.link(new SimpleRelinkableCallSite(new CallSiteDescriptor(
                MethodHandles.publicLookup(), op, mt)));
        Object value = null;
        try {
            final Object obj = engine.eval("({ foo: 'hello' })");
            value = cs.getTarget().invoke(obj);
        } catch (final Throwable th) {
            throw new RuntimeException(th);
        }
        Assert.assertEquals(value, "hello");
    }
}
