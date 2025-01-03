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

package org.openjdk.nashorn.internal.runtime;

import static org.openjdk.nashorn.internal.lookup.Lookup.MH;
import static org.openjdk.nashorn.internal.runtime.ECMAErrors.typeError;

import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import jdk.dynalink.CallSiteDescriptor;
import jdk.dynalink.NamedOperation;
import jdk.dynalink.linker.GuardedInvocation;
import jdk.dynalink.linker.support.Guards;
import org.openjdk.nashorn.internal.runtime.linker.NashornCallSiteDescriptor;

/**
 * Unique instance of this class is used to represent JavaScript undefined.
 */
public final class Undefined extends DefaultPropertyAccess implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean isEmpty;

    private Undefined(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }

    private static final Undefined UNDEFINED = new Undefined(false);
    private static final Undefined EMPTY     = new Undefined(true);

    // Guard used for indexed property access/set on the Undefined instance
    private static final MethodHandle UNDEFINED_GUARD = Guards.getIdentityGuard(UNDEFINED);

    /**
     * Get the value of {@code undefined}, this is represented as a global singleton
     * instance of this class. It can always be reference compared
     *
     * @return the undefined object
     */
    public static Undefined getUndefined() {
        return UNDEFINED;
    }

    /**
     * Get the value of {@code empty}. This is represented as a global singleton
     * instanceof this class. It can always be reference compared.
     * <p>
     * We need empty to differentiate behavior in things like array iterators
     * <p>
     * @return the empty object
     */
    public static Undefined getEmpty() {
        return EMPTY;
    }

    /**
     * Get the class name of Undefined
     * @return "Undefined"
     */
    @SuppressWarnings("static-method")
    public String getClassName() {
        return "Undefined";
    }

    @Override
    public String toString() {
        return "undefined";
    }

    /**
     * Lookup the appropriate method for an invoke dynamic call.
     * @param desc The invoke dynamic callsite descriptor.
     * @return GuardedInvocation to be invoked at call site.
     */
    public static GuardedInvocation lookup(final CallSiteDescriptor desc) {
        switch (NashornCallSiteDescriptor.getStandardOperation(desc)) {
        case CALL:
        case NEW:
            final String name = NashornCallSiteDescriptor.getOperand(desc);
            final String msg = name != null? "not.a.function" : "cant.call.undefined";
            throw typeError(msg, name);
        case GET:
            // NOTE: we support GET:ELEMENT and SET:ELEMENT as JavaScript doesn't distinguish items from properties. Nashorn itself
            // emits "GET:PROPERTY|ELEMENT|METHOD:identifier" for "<expr>.<identifier>" and "GET:ELEMENT|PROPERTY|METHOD" for "<expr>[<expr>]", but we are
            // more flexible here and dispatch not on operation name (getProp vs. getElem), but rather on whether the
            // operation has an associated name or not.
            if (!(desc.getOperation() instanceof NamedOperation)) {
                return findGetIndexMethod(desc);
            }
            return findGetMethod(desc);
        case SET:
            if (!(desc.getOperation() instanceof NamedOperation)) {
                return findSetIndexMethod(desc);
            }
            return findSetMethod(desc);
        case REMOVE:
            if (!(desc.getOperation() instanceof NamedOperation)) {
                return findDeleteIndexMethod(desc);
            }
            return findDeleteMethod(desc);
        default:
        }
        return null;
    }

    private static ECMAException lookupTypeError(final String msg, final CallSiteDescriptor desc) {
        final String name = NashornCallSiteDescriptor.getOperand(desc);
        return typeError(msg, name != null && !name.isEmpty()? name : null);
    }

    private static final MethodHandle GET_METHOD = findOwnMH("get", Object.class, Object.class);
    private static final MethodHandle SET_METHOD = MH.insertArguments(findOwnMH("set", void.class, Object.class, Object.class, int.class), 3, NashornCallSiteDescriptor.CALLSITE_STRICT);
    private static final MethodHandle DELETE_METHOD = MH.insertArguments(findOwnMH("delete", boolean.class, Object.class, boolean.class), 2, false);

    private static GuardedInvocation findGetMethod(final CallSiteDescriptor desc) {
        return new GuardedInvocation(MH.insertArguments(GET_METHOD, 1, NashornCallSiteDescriptor.getOperand(desc)), UNDEFINED_GUARD).asType(desc);
    }

    private static GuardedInvocation findGetIndexMethod(final CallSiteDescriptor desc) {
        return new GuardedInvocation(GET_METHOD, UNDEFINED_GUARD).asType(desc);
    }

    private static GuardedInvocation findSetMethod(final CallSiteDescriptor desc) {
        return new GuardedInvocation(MH.insertArguments(SET_METHOD, 1, NashornCallSiteDescriptor.getOperand(desc)), UNDEFINED_GUARD).asType(desc);
    }

    private static GuardedInvocation findSetIndexMethod(final CallSiteDescriptor desc) {
        return new GuardedInvocation(SET_METHOD, UNDEFINED_GUARD).asType(desc);
    }

    private static GuardedInvocation findDeleteMethod(final CallSiteDescriptor desc) {
        return new GuardedInvocation(MH.insertArguments(DELETE_METHOD, 1, NashornCallSiteDescriptor.getOperand(desc)), UNDEFINED_GUARD).asType(desc);
    }

    private static GuardedInvocation findDeleteIndexMethod(final CallSiteDescriptor desc) {
        return new GuardedInvocation(DELETE_METHOD, UNDEFINED_GUARD).asType(desc);
    }


    @Override
    public Object get(final Object key) {
        throw typeError("cant.read.property.of.undefined", ScriptRuntime.safeToString(key));
    }

    @Override
    public void set(final Object key, final Object value, final int flags) {
        throw typeError("cant.set.property.of.undefined", ScriptRuntime.safeToString(key));
    }

    @Override
    public boolean delete(final Object key, final boolean strict) {
        throw typeError("cant.delete.property.of.undefined", ScriptRuntime.safeToString(key));
    }

    @Override
    public boolean has(final Object key) {
        return false;
    }

    @Override
    public boolean hasOwnProperty(final Object key) {
        return false;
    }

    private static MethodHandle findOwnMH(final String name, final Class<?> rtype, final Class<?>... types) {
        return MH.findVirtual(MethodHandles.lookup(), Undefined.class, name, MH.type(rtype, types));
    }

    private Object readResolve() {
        return isEmpty ? EMPTY : UNDEFINED;
    }
}
