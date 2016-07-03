// Generated from C:/Users/Merlin May/Projects/megalib/checker/megalib.checker/src/org/java/megalib/antlr/Megalib.g4 by ANTLR 4.5.3

package org.java.megalib.antlr;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MegalibParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MegalibVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MegalibParser#declartation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclartation(MegalibParser.DeclartationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MegalibParser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(MegalibParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MegalibParser#entity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEntity(MegalibParser.EntityContext ctx);
	/**
	 * Visit a parse tree produced by {@link MegalibParser#object}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObject(MegalibParser.ObjectContext ctx);
}