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
 * Common interface for all nodes in an abstract syntax tree.
 *
 * <p><b>WARNING:</b> This interface and its sub-interfaces are
 * subject to change as the ECMAScript  programming language evolves.
 *
 * @since 9
 */
public interface Tree {

    /**
     * Enumerates all kinds of trees.
     */
    public enum Kind {
        /**
         * Used for instances of {@link ArrayAccessTree}.
         */
        ARRAY_ACCESS(ArrayAccessTree.class),

        /**
         * Used for instances of {@link ArrayLiteralTree}.
         */
        ARRAY_LITERAL(ArrayLiteralTree.class),

        /**
         * Used for instances of {@link AssignmentTree}.
         */
        ASSIGNMENT(AssignmentTree.class),

        /**
         * Used for instances of {@link BlockTree}.
         */
        BLOCK(BlockTree.class),

        /**
         * Used for instances of {@link BreakTree}.
         */
        BREAK(BreakTree.class),

        /**
         * Used for instances of {@link ClassDeclarationTree}.
         */
        CLASS(ClassDeclarationTree.class),

        /**
         * Used for instances of {@link ClassExpressionTree}.
         */
        CLASS_EXPRESSION(ClassExpressionTree.class),

        /**
         * Used for instances of {@link CaseTree}.
         */
        CASE(CaseTree.class),

        /**
         * Used for instances of {@link CatchTree}.
         */
        CATCH(CatchTree.class),

        /**
         * Used for instances of {@link CompilationUnitTree}.
         */
        COMPILATION_UNIT(CompilationUnitTree.class),

        /**
         * Used for instances of {@link ConditionalExpressionTree}.
         */
        CONDITIONAL_EXPRESSION(ConditionalExpressionTree.class),

        /**
         * Used for instances of {@link ContinueTree}.
         */
        CONTINUE(ContinueTree.class),

        /**
         * Used for instances of {@link DoWhileLoopTree}.
         */
        DO_WHILE_LOOP(DoWhileLoopTree.class),

        /**
         * Used for instances of {@link DebuggerTree}.
         */
        DEBUGGER(DebuggerTree.class),

        /**
         * Used for instances of {@link ForInLoopTree}.
         */
        FOR_IN_LOOP(ForInLoopTree.class),

        /**
         * Used for instances of {@link FunctionExpressionTree}.
         */
        FUNCTION_EXPRESSION(FunctionExpressionTree.class),

        /**
         * Used for instances of {@link ErroneousTree}.
         */
        ERROR(ErroneousTree.class),

        /**
         * Used for instances of {@link ExpressionStatementTree}.
         */
        EXPRESSION_STATEMENT(ExpressionStatementTree.class),

        /**
         * Used for instances of {@link MemberSelectTree}.
         */
        MEMBER_SELECT(MemberSelectTree.class),

        /**
         * Used for instances of {@link ForLoopTree}.
         */
        FOR_LOOP(ForLoopTree.class),

        /**
         * Used for instances of {@link IdentifierTree}.
         */
        IDENTIFIER(IdentifierTree.class),

        /**
         * Used for instances of {@link IfTree}.
         */
        IF(IfTree.class),

        /**
         * Used for instances of {@link InstanceOfTree}.
         */
        INSTANCE_OF(InstanceOfTree.class),

        /**
         * Used for instances of {@link LabeledStatementTree}.
         */
        LABELED_STATEMENT(LabeledStatementTree.class),

        /**
         * Used for instances of {@link ModuleTree}.
         */
        MODULE(ModuleTree.class),

        /**
         * Used for instances of {@link ExportEntryTree}.
         */
        EXPORT_ENTRY(ExportEntryTree.class),

        /**
         * Used for instances of {@link ImportEntryTree}.
         */
        IMPORT_ENTRY(ImportEntryTree.class),

        /**
         * Used for instances of {@link FunctionDeclarationTree}.
         */
        FUNCTION(FunctionDeclarationTree.class),

        /**
         * Used for instances of {@link FunctionCallTree}.
         */
        FUNCTION_INVOCATION(FunctionCallTree.class),

        /**
         * Used for instances of {@link NewTree}.
         */
        NEW(NewTree.class),

        /**
         * Used for instances of {@link ObjectLiteralTree}.
         */
        OBJECT_LITERAL(ObjectLiteralTree.class),

        /**
         * Used for instances of {@link ParenthesizedTree}.
         */
        PARENTHESIZED(ParenthesizedTree.class),

        /**
         * Used for instances of {@link PropertyTree}.
         */
        PROPERTY(PropertyTree.class),

        /**
         * Used for instances of {@link RegExpLiteralTree}.
         */
        REGEXP_LITERAL(RegExpLiteralTree.class),

        /**
         * Used for instances of {@link TemplateLiteralTree}.
         */
        TEMPLATE_LITERAL(TemplateLiteralTree.class),

        /**
         * Used for instances of {@link ReturnTree}.
         */
        RETURN(ReturnTree.class),

        /**
         * Used for instances of {@link EmptyStatementTree}.
         */
        EMPTY_STATEMENT(EmptyStatementTree.class),

        /**
         * Used for instances of {@link SwitchTree}.
         */
        SWITCH(SwitchTree.class),

        /**
         * Used for instances of {@link ThrowTree}.
         */
        THROW(ThrowTree.class),

        /**
         * Used for instances of {@link TryTree}.
         */
        TRY(TryTree.class),

        /**
         * Used for instances of {@link VariableTree}.
         */
        VARIABLE(VariableTree.class),

        /**
         * Used for instances of {@link WhileLoopTree}.
         */
        WHILE_LOOP(WhileLoopTree.class),

        /**
         * Used for instances of {@link WithTree}.
         */
        WITH(WithTree.class),

        /**
         * Used for instances of {@link UnaryTree} representing postfix
         * increment operator {@code ++}.
         */
        POSTFIX_INCREMENT(UnaryTree.class),

        /**
         * Used for instances of {@link UnaryTree} representing postfix
         * decrement operator {@code --}.
         */
        POSTFIX_DECREMENT(UnaryTree.class),

        /**
         * Used for instances of {@link UnaryTree} representing prefix
         * increment operator {@code ++}.
         */
        PREFIX_INCREMENT(UnaryTree.class),

        /**
         * Used for instances of {@link UnaryTree} representing prefix
         * decrement operator {@code --}.
         */
        PREFIX_DECREMENT(UnaryTree.class),

        /**
         * Used for instances of {@link UnaryTree} representing unary plus
         * operator {@code +}.
         */
        UNARY_PLUS(UnaryTree.class),

        /**
         * Used for instances of {@link UnaryTree} representing unary minus
         * operator {@code -}.
         */
        UNARY_MINUS(UnaryTree.class),

        /**
         * Used for instances of {@link UnaryTree} representing bitwise
         * complement operator {@code ~}.
         */
        BITWISE_COMPLEMENT(UnaryTree.class),

        /**
         * Used for instances of {@link UnaryTree} representing logical
         * complement operator {@code !}.
         */
        LOGICAL_COMPLEMENT(UnaryTree.class),

        /**
         * Used for instances of {@link UnaryTree} representing logical
         * delete operator {@code delete}.
         */
        DELETE(UnaryTree.class),

        /**
         * Used for instances of {@link UnaryTree} representing logical
         * typeof operator {@code typeof}.
         */
        TYPEOF(UnaryTree.class),

        /**
         * Used for instances of {@link UnaryTree} representing logical
         * void operator {@code void}.
         */
        VOID(UnaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * comma {@code ,}.
         */
        COMMA(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * multiplication {@code *}.
         */
        MULTIPLY(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * division {@code /}.
         */
        DIVIDE(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * remainder {@code %}.
         */
        REMAINDER(BinaryTree.class),

         /**
         * Used for instances of {@link BinaryTree} representing
         * addition or string concatenation {@code +}.
         */
        PLUS(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * subtraction {@code -}.
         */
        MINUS(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * left shift {@code <<}.
         */
        LEFT_SHIFT(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * right shift {@code >>}.
         */
        RIGHT_SHIFT(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * unsigned right shift {@code >>>}.
         */
        UNSIGNED_RIGHT_SHIFT(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * less-than {@code <}.
         */
        LESS_THAN(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * greater-than {@code >}.
         */
        GREATER_THAN(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * less-than-equal {@code <=}.
         */
        LESS_THAN_EQUAL(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * greater-than-equal {@code >=}.
         */
        GREATER_THAN_EQUAL(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * in operator {@code in}.
         */
        IN(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * equal-to {@code ==}.
         */
        EQUAL_TO(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * not-equal-to {@code !=}.
         */
        NOT_EQUAL_TO(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * equal-to {@code ===}.
         */
        STRICT_EQUAL_TO(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * not-equal-to {@code !==}.
         */
        STRICT_NOT_EQUAL_TO(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * bitwise and logical "and" {@code &}.
         */
        AND(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * bitwise and logical "xor" {@code ^}.
         */
        XOR(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * bitwise and logical "or" {@code |}.
         */
        OR(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * conditional-and {@code &&}.
         */
        CONDITIONAL_AND(BinaryTree.class),

        /**
         * Used for instances of {@link BinaryTree} representing
         * conditional-or {@code ||}.
         */
        CONDITIONAL_OR(BinaryTree.class),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * multiplication assignment {@code *=}.
         */
        MULTIPLY_ASSIGNMENT(CompoundAssignmentTree.class),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * division assignment {@code /=}.
         */
        DIVIDE_ASSIGNMENT(CompoundAssignmentTree.class),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * remainder assignment {@code %=}.
         */
        REMAINDER_ASSIGNMENT(CompoundAssignmentTree.class),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * addition or string concatenation assignment {@code +=}.
         */
        PLUS_ASSIGNMENT(CompoundAssignmentTree.class),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * subtraction assignment {@code -=}.
         */
        MINUS_ASSIGNMENT(CompoundAssignmentTree.class),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * left shift assignment {@code <<=}.
         */
        LEFT_SHIFT_ASSIGNMENT(CompoundAssignmentTree.class),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * right shift assignment {@code >>=}.
         */
        RIGHT_SHIFT_ASSIGNMENT(CompoundAssignmentTree.class),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * unsigned right shift assignment {@code >>>=}.
         */
        UNSIGNED_RIGHT_SHIFT_ASSIGNMENT(CompoundAssignmentTree.class),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * bitwise and logical "and" assignment {@code &=}.
         */
        AND_ASSIGNMENT(CompoundAssignmentTree.class),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * bitwise and logical "xor" assignment {@code ^=}.
         */
        XOR_ASSIGNMENT(CompoundAssignmentTree.class),

        /**
         * Used for instances of {@link CompoundAssignmentTree} representing
         * bitwise and logical "or" assignment {@code |=}.
         */
        OR_ASSIGNMENT(CompoundAssignmentTree.class),

        /**
         * Used for instances of {@link SpreadTree} representing
         * spread "operator" for arrays and function call arguments.
         */
        SPREAD(SpreadTree.class),

        /**
         * Used for instances of {@link YieldTree} representing (generator)
         * yield expression {@code yield expr}.
         */
        YIELD(YieldTree.class),

        /**
         * Used for instances of {@link LiteralTree} representing
         * a number literal expression of type {@code double}.
         */
        NUMBER_LITERAL(LiteralTree.class),

        /**
         * Used for instances of {@link LiteralTree} representing
         * a boolean literal expression of type {@code boolean}.
         */
        BOOLEAN_LITERAL(LiteralTree.class),

        /**
         * Used for instances of {@link LiteralTree} representing
         * a string literal expression of type {@link String}.
         */
        STRING_LITERAL(LiteralTree.class),

        /**
         * Used for instances of {@link LiteralTree} representing
         * the use of {@code null}.
         */
        NULL_LITERAL(LiteralTree.class),

        /**
         * An implementation-reserved node. This is the not the node
         * you are looking for.
         */
        OTHER(null);

        Kind(final Class<? extends Tree> intf) {
            associatedInterface = intf;
        }

       /**
        * Returns the associated interface type that uses this kind.
        * @return the associated interface
        */
        public Class<? extends Tree> asInterface() {
            return associatedInterface;
        }

        /**
         * Returns if this is a literal tree kind or not.
         *
         * @return true if this is a literal tree kind, false otherwise
         */
        public boolean isLiteral() {
            return associatedInterface == LiteralTree.class;
        }

        /**
         * Returns if this is an expression tree kind or not.
         *
         * @return true if this is an expression tree kind, false otherwise
         */
        public boolean isExpression() {
            return ExpressionTree.class.isAssignableFrom(associatedInterface);
        }

        /**
         * Returns if this is a statement tree kind or not.
         *
         * @return true if this is a statement tree kind, false otherwise
         */
        public boolean isStatement() {
            return StatementTree.class.isAssignableFrom(associatedInterface);
        }

        private final Class<? extends Tree> associatedInterface;
    }

    /**
     * Start character offset of this Tree within the source.
     *
     * @return the position
     */
    long getStartPosition();

    /**
     * End character offset of this Tree within the source.
     *
     * @return the position
     */
    long getEndPosition();

    /**
     * Gets the kind of this tree.
     *
     * @return the kind of this tree.
     */
    Kind getKind();

    /**
     * Accept method used to implement the visitor pattern.  The
     * visitor pattern is used to implement operations on trees.
     *
     * @param <R> result type of this operation.
     * @param <D> type of additional data.
     * @param visitor tree visitor
     * @param data additional data passed to visitor methods
     * @return the value from visitor's visit methods
     */
    <R,D> R accept(TreeVisitor<R,D> visitor, D data);
}
