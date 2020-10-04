/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
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
 * Test to check that nashorn "internal" classes in codegen, parser, ir
 * packages cannot * be accessed from sandbox scripts.
 *
 * @test
 * @run
 * @security
 */

function checkClass(name) {
    try {
        Java.type(name);
        fail("should have thrown exception for: " + name);
    } catch (e) {
        if (! (e instanceof java.lang.SecurityException)) {
            fail("Expected SecurityException, but got " + e);
        }
    }
}

// Not exhaustive - but a representative list of classes
checkClass("org.openjdk.nashorn.internal.codegen.Compiler");
checkClass("org.openjdk.nashorn.internal.codegen.types.Type");
checkClass("org.openjdk.nashorn.internal.ir.Node");
checkClass("org.openjdk.nashorn.internal.ir.FunctionNode");
checkClass("org.openjdk.nashorn.internal.ir.debug.JSONWriter");
checkClass("org.openjdk.nashorn.internal.ir.visitor.NodeVisitor");
checkClass("org.openjdk.nashorn.internal.lookup.MethodHandleFactory");
checkClass("org.openjdk.nashorn.internal.objects.Global");
checkClass("org.openjdk.nashorn.internal.parser.AbstractParser");
checkClass("org.openjdk.nashorn.internal.parser.Parser");
checkClass("org.openjdk.nashorn.internal.parser.JSONParser");
checkClass("org.openjdk.nashorn.internal.parser.Lexer");
checkClass("org.openjdk.nashorn.internal.parser.Scanner");
checkClass("org.openjdk.nashorn.internal.runtime.Context");
checkClass("org.openjdk.nashorn.internal.runtime.arrays.ArrayData");
checkClass("org.openjdk.nashorn.internal.runtime.linker.Bootstrap");
checkClass("org.openjdk.nashorn.internal.runtime.options.Option");
checkClass("org.openjdk.nashorn.internal.runtime.regexp.RegExp");
checkClass("org.openjdk.nashorn.internal.scripts.JO");
checkClass("org.openjdk.nashorn.tools.Shell");
