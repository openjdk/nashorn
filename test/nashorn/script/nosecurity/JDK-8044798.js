/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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

/**
 * JDK-8044798: API for debugging Nashorn
 *
 * @test
 * @option -Dnashorn.mirror.always=false
 * @fork
 */

// basic API exercise checks

var Arrays = Java.type("java.util.Arrays");
var CharArray = Java.type("char[]");
var Reflector = Java.type("org.openjdk.nashorn.test.models.Reflector");
var DebuggerSupport = Java.type("org.openjdk.nashorn.internal.runtime.DebuggerSupport");
var DebuggerValueDesc = Java.type("org.openjdk.nashorn.internal.runtime.DebuggerSupport.DebuggerValueDesc");

var valueDescFields = DebuggerValueDesc.class.declaredFields;
Arrays.sort(valueDescFields, function(f1, f2) f1.name.compareTo(f2.name));
for each (var f in valueDescFields) {
    Reflector.setAccessible(f);
}

var debuggerSupportMethods = DebuggerSupport.class.declaredMethods;

// methods of DebuggerSupport that we use
var evalMethod, valueInfoMethod, valueInfosMethod;
var getSourceInfoMethod, valueAsStringMethod;

for each (var m in debuggerSupportMethods) {
    Reflector.setAccessible(m);
    switch (m.name) {
        case "eval":
            evalMethod = m;
            break;
        case "valueInfo":
            if (m.parameterCount == 3) {
                valueInfoMethod = m;
            }
            break;
        case "valueInfos":
            valueInfosMethod = m;
            break;
        case "valueAsString":
            valueAsStringMethod = m;
            break;
        case "getSourceInfo":
            getSourceInfoMethod = m;
            break;
    }
}

// eval
var value = evalMethod.invoke(null, null, null, "33 + 55", false);
print(value);

// valueInfo
var info = valueInfoMethod.invoke(null, "apply", Function, true);
for each (var f in valueDescFields) {
    print(f.name, "=", f.get(info));
}

// valueInfo - user defined object
var info = valueInfoMethod.invoke(null, "foo", { foo: 343 }, true);
for each (var f in valueDescFields) {
    print(f.name, "=", f.get(info));
}

// valueInfos
var infos = valueInfosMethod.invoke(null, Object, true);
for each (var info in infos) {
    for each (var f in valueDescFields) {
        print(f.name, "=", f.get(info));
    }
}

// valueInfos - user defined object
var infos = valueInfosMethod.invoke(null, { foo: 34, bar: "hello" }, true);
for each (var info in infos) {
    for each (var f in valueDescFields) {
        print(f.name, "=", f.get(info));
    }
}

// valueAsString
function printValue(value) {
    print(valueAsStringMethod.invoke(null, value));
}

printValue(undefined);
printValue(null);
printValue("hello");
printValue(Math.PI);
printValue(this);

// The below are not part of DebuggerSupport. But we need these to
// test DebuggerSupport.getSourceInfo etc. which need compiled script class

var Source = Java.type("org.openjdk.nashorn.internal.runtime.Source");
var Context = Java.type("org.openjdk.nashorn.internal.runtime.Context");
var ThrowErrorManager = Java.type("org.openjdk.nashorn.internal.runtime.Context.ThrowErrorManager");
var contextCls = java.lang.Class.forName("org.openjdk.nashorn.internal.runtime.Context");
var sourceCls = Source.class;
var errorMgrCls = Java.type("org.openjdk.nashorn.internal.runtime.ErrorManager").class;
var booleanCls = Java.type("java.lang.Boolean").TYPE;
var stringCls = Java.type("java.lang.String").class;

// private compile method of Context class
var compileMethod = contextCls.getDeclaredMethod("compile",
                sourceCls, errorMgrCls, booleanCls, booleanCls);
Reflector.setAccessible(compileMethod);

var getContextMethod = contextCls.getMethod("getContext");
Reflector.setAccessible(getContextMethod);

var sourceForMethod = sourceCls.getMethod("sourceFor", stringCls, stringCls);
var scriptCls = compileMethod.invoke(getContextMethod.invoke(null),
    sourceForMethod.invoke(null, "test", "print('hello')"),
    ThrowErrorManager.class.newInstance(), false, false);

var SCRIPT_CLASS_NAME_PREFIX = "org.openjdk.nashorn.internal.scripts.Script$";
print("script class name pattern satisfied? " +
    scriptCls.name.startsWith(SCRIPT_CLASS_NAME_PREFIX));

var srcInfo = getSourceInfoMethod.invoke(null, scriptCls);
var srcInfoFields = srcInfo.class.declaredFields;
Arrays.sort(srcInfoFields, function(f1, f2) f1.name.compareTo(f2.name));

print("Source info");
for each (var f in srcInfoFields) {
    Reflector.setAccessible(f);
    var fieldValue = f.get(srcInfo);
    if (fieldValue instanceof CharArray) {
        fieldValue = new java.lang.String(fieldValue);
    }

    print(f.name, "=", fieldValue);
}
