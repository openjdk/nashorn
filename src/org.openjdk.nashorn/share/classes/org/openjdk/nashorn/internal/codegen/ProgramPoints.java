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
package org.openjdk.nashorn.internal.codegen;

import static org.openjdk.nashorn.internal.runtime.UnwarrantedOptimismException.FIRST_PROGRAM_POINT;
import static org.openjdk.nashorn.internal.runtime.linker.NashornCallSiteDescriptor.MAX_PROGRAM_POINT_VALUE;

import java.util.HashSet;
import java.util.Set;
import org.openjdk.nashorn.internal.IntDeque;
import org.openjdk.nashorn.internal.ir.AccessNode;
import org.openjdk.nashorn.internal.ir.BinaryNode;
import org.openjdk.nashorn.internal.ir.CallNode;
import org.openjdk.nashorn.internal.ir.Expression;
import org.openjdk.nashorn.internal.ir.FunctionNode;
import org.openjdk.nashorn.internal.ir.IdentNode;
import org.openjdk.nashorn.internal.ir.IndexNode;
import org.openjdk.nashorn.internal.ir.Node;
import org.openjdk.nashorn.internal.ir.Optimistic;
import org.openjdk.nashorn.internal.ir.UnaryNode;
import org.openjdk.nashorn.internal.ir.VarNode;
import org.openjdk.nashorn.internal.ir.visitor.SimpleNodeVisitor;

/**
 * Find program points in the code that are needed for optimistic assumptions
 */
class ProgramPoints extends SimpleNodeVisitor {

    private final IntDeque nextProgramPoint = new IntDeque();
    private final Set<Node> noProgramPoint = new HashSet<>();

    private int next() {
        final int next = nextProgramPoint.getAndIncrement();
        if(next > MAX_PROGRAM_POINT_VALUE) {
            throw new AssertionError("Function has more than " + MAX_PROGRAM_POINT_VALUE + " program points");
        }
        return next;
    }

    @Override
    public boolean enterFunctionNode(final FunctionNode functionNode) {
        nextProgramPoint.push(FIRST_PROGRAM_POINT);
        return true;
    }

    @Override
    public Node leaveFunctionNode(final FunctionNode functionNode) {
        nextProgramPoint.pop();
        return functionNode;
    }

    private Expression setProgramPoint(final Optimistic optimistic) {
        if (noProgramPoint.contains(optimistic)) {
            return (Expression)optimistic;
        }
        return (Expression)(optimistic.canBeOptimistic() ? optimistic.setProgramPoint(next()) : optimistic);
    }

    @Override
    public boolean enterVarNode(final VarNode varNode) {
        noProgramPoint.add(varNode.getName());
        return true;
    }

    @Override
    public boolean enterIdentNode(final IdentNode identNode) {
        if (identNode.isInternal()) {
            noProgramPoint.add(identNode);
        }
        return true;
    }

    @Override
    public Node leaveIdentNode(final IdentNode identNode) {
        if(identNode.isPropertyName()) {
            return identNode;
        }
        return setProgramPoint(identNode);
    }

    @Override
    public Node leaveCallNode(final CallNode callNode) {
        return setProgramPoint(callNode);
    }

    @Override
    public Node leaveAccessNode(final AccessNode accessNode) {
        return setProgramPoint(accessNode);
    }

    @Override
    public Node leaveIndexNode(final IndexNode indexNode) {
        return setProgramPoint(indexNode);
    }

    @Override
    public Node leaveBinaryNode(final BinaryNode binaryNode) {
        return setProgramPoint(binaryNode);
    }

    @Override
    public Node leaveUnaryNode(final UnaryNode unaryNode) {
        return setProgramPoint(unaryNode);
    }
}
