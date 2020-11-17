/*
 * Copyright (c) 2016, Oracle and/or its affiliates. All rights reserved.
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

package jdk.nashorn.api.tree;

/**
 *  A tree node for <a href="http://www.ecma-international.org/ecma-262/6.0/#sec-generator-function-definitions">yield expressions</a> used in generator functions.
 *
 * For example:
 * <pre>
 * <em>function*</em> id(){
 *     var index = 0;
 *     while(index &lt; 10)
 *         <em>yield index++;</em>
 * }
 * </pre>
 *
 * @since 9
 */
public interface YieldTree extends ExpressionTree {
    /**
     * Returns the expression that is yielded.
     *
     * @return The expression that is yielded.
     */
    ExpressionTree getExpression();

    /**
     * Is this a yield * expression in a generator function?
     *
     * For example:
     * <pre>
     * function* id(){
     *     yield 1;
     *     <em>yield * anotherGeneratorFunc();</em>
     *     yield 10;
     * }
     * </pre>
     *
     *
     * @return true if this is a yield * expression
     */
    boolean isStar();
}
