/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
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

package org.openjdk.nashorn.internal.runtime.linker;

import java.security.ProtectionDomain;
import java.security.SecureClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import jdk.dynalink.beans.StaticClass;
import org.openjdk.nashorn.internal.codegen.DumpBytecode;
import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;
import org.openjdk.nashorn.internal.runtime.ScriptObject;

/**
 * This class encapsulates the bytecode of the adapter class and can be used to load it into the JVM as an actual Class.
 * It can be invoked repeatedly to create multiple adapter classes from the same bytecode; adapter classes that have
 * class-level overrides must be re-created for every set of such overrides. Note that while this class is named
 * "class loader", it does not, in fact, extend {@code ClassLoader}, but rather uses them internally. Instances of this
 * class are normally created by {@code JavaAdapterBytecodeGenerator}.
 */
final class JavaAdapterClassLoader {
    private static final Module NASHORN_MODULE = Context.class.getModule();

    private static final Collection<String> VISIBLE_INTERNAL_CLASS_NAMES = Collections.unmodifiableCollection(new HashSet<>(
            Arrays.asList(JavaAdapterServices.class.getName(), ScriptObject.class.getName(), ScriptFunction.class.getName(), JSType.class.getName())));

    private final String className;
    private final byte[] classBytes;

    JavaAdapterClassLoader(final String className, final byte[] classBytes) {
        this.className = className.replace('/', '.');
        this.classBytes = classBytes;
    }

    /**
     * Loads the generated adapter class into the JVM.
     * @param parentLoader the parent class loader for the generated class loader
     * @param protectionDomain the protection domain for the generated class
     * @return the generated adapter class
     */
    StaticClass generateClass(final ClassLoader parentLoader, final ProtectionDomain protectionDomain) {
        assert protectionDomain != null;
        try {
            return StaticClass.forClass(Class.forName(className, true, createClassLoader(parentLoader, protectionDomain)));
        } catch (final ClassNotFoundException e) {
            throw new AssertionError(e); // cannot happen
        }
    }

    // Note that the adapter class is created in the protection domain of the class/interface being
    // extended/implemented, and only the privileged global setter action class is generated in the protection domain
    // of Nashorn itself. Also note that the creation and loading of the global setter is deferred until it is
    // required by JVM linker, which will only happen on first invocation of any of the adapted method. We could defer
    // it even more by separating its invocation into a separate static method on the adapter class, but then someone
    // with ability to introspect on the class and use setAccessible(true) on it could invoke the method. It's a
    // security tradeoff...
    private ClassLoader createClassLoader(final ClassLoader parentLoader, final ProtectionDomain protectionDomain) {
        return new SecureClassLoader(parentLoader) {
            private final ClassLoader myLoader = getClass().getClassLoader();

            // the unnamed module into which adapter is loaded!
            private final Module adapterModule = getUnnamedModule();

            {
                // specific exports from nashorn to the new adapter module
                NASHORN_MODULE.addExports("org.openjdk.nashorn.internal.runtime", adapterModule);
                NASHORN_MODULE.addExports("org.openjdk.nashorn.internal.runtime.linker", adapterModule);

                // nashorn should be be able to read methods of classes loaded in adapter module
                NASHORN_MODULE.addReads(adapterModule);
            }

            @Override
            public Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
                try {
                    return super.loadClass(name, resolve);
                } catch (final SecurityException se) {
                    // we may be implementing an interface or extending a class that was
                    // loaded by a loader that prevents package.access. If so, it'd throw
                    // SecurityException for nashorn's classes!. For adapter's to work, we
                    // should be able to refer to the few classes it needs in its implementation.
                    if(VISIBLE_INTERNAL_CLASS_NAMES.contains(name)) {
                        return myLoader != null? myLoader.loadClass(name) : Class.forName(name, false, null);
                    }
                    throw se;
                }
            }

            @Override
            protected Class<?> findClass(final String name) throws ClassNotFoundException {
                if(name.equals(className)) {
                    assert classBytes != null : "what? already cleared .class bytes!!";

                    final Context ctx = Context.getContext();
                    DumpBytecode.dumpBytecode(ctx.getEnv(), ctx.getLogger(org.openjdk.nashorn.internal.codegen.Compiler.class), classBytes, name);
                    return defineClass(name, classBytes, 0, classBytes.length, protectionDomain);
                }
                throw new ClassNotFoundException(name);
            }
        };
    }
}
