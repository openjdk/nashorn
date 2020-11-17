/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
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
package jdk.nashorn.internal.parser;

import java.util.List;
import jdk.nashorn.internal.ir.Statement;

/**
 * Used for keeping state when needed in the parser.
 */
interface ParserContextNode {
    /**
     * @return The flags for this node
     */
    public int getFlags();

    /**
     * @param flag The flag to set
     * @return All current flags after update
     */
    public int setFlag(final int flag);

    /**
     * @return The list of statements that belongs to this node
     */
    public List<Statement> getStatements();

    /**
     * @param statements The statement list
     */
    public void setStatements(final List<Statement> statements);

    /**
     * Adds a statement at the end of the statement list
     * @param statement The statement to add
     */
    public void appendStatement(final Statement statement);

    /**
     * Adds a statement at the beginning of the statement list
     * @param statement The statement to add
     */
    public void prependStatement(final Statement statement);

}
