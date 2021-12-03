// Generated from lab7.g4 by ANTLR 4.9.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link lab7Parser}.
 */
public interface lab7Listener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link lab7Parser#compUnit}.
	 * @param ctx the parse tree
	 */
	void enterCompUnit(lab7Parser.CompUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#compUnit}.
	 * @param ctx the parse tree
	 */
	void exitCompUnit(lab7Parser.CompUnitContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#decl}.
	 * @param ctx the parse tree
	 */
	void enterDecl(lab7Parser.DeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#decl}.
	 * @param ctx the parse tree
	 */
	void exitDecl(lab7Parser.DeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#constDecl}.
	 * @param ctx the parse tree
	 */
	void enterConstDecl(lab7Parser.ConstDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#constDecl}.
	 * @param ctx the parse tree
	 */
	void exitConstDecl(lab7Parser.ConstDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#bType}.
	 * @param ctx the parse tree
	 */
	void enterBType(lab7Parser.BTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#bType}.
	 * @param ctx the parse tree
	 */
	void exitBType(lab7Parser.BTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#constDef}.
	 * @param ctx the parse tree
	 */
	void enterConstDef(lab7Parser.ConstDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#constDef}.
	 * @param ctx the parse tree
	 */
	void exitConstDef(lab7Parser.ConstDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#constInitVal}.
	 * @param ctx the parse tree
	 */
	void enterConstInitVal(lab7Parser.ConstInitValContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#constInitVal}.
	 * @param ctx the parse tree
	 */
	void exitConstInitVal(lab7Parser.ConstInitValContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#constExp}.
	 * @param ctx the parse tree
	 */
	void enterConstExp(lab7Parser.ConstExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#constExp}.
	 * @param ctx the parse tree
	 */
	void exitConstExp(lab7Parser.ConstExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#varDecl}.
	 * @param ctx the parse tree
	 */
	void enterVarDecl(lab7Parser.VarDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#varDecl}.
	 * @param ctx the parse tree
	 */
	void exitVarDecl(lab7Parser.VarDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#varDef}.
	 * @param ctx the parse tree
	 */
	void enterVarDef(lab7Parser.VarDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#varDef}.
	 * @param ctx the parse tree
	 */
	void exitVarDef(lab7Parser.VarDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#initVal}.
	 * @param ctx the parse tree
	 */
	void enterInitVal(lab7Parser.InitValContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#initVal}.
	 * @param ctx the parse tree
	 */
	void exitInitVal(lab7Parser.InitValContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#funcDef}.
	 * @param ctx the parse tree
	 */
	void enterFuncDef(lab7Parser.FuncDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#funcDef}.
	 * @param ctx the parse tree
	 */
	void exitFuncDef(lab7Parser.FuncDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#funcType}.
	 * @param ctx the parse tree
	 */
	void enterFuncType(lab7Parser.FuncTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#funcType}.
	 * @param ctx the parse tree
	 */
	void exitFuncType(lab7Parser.FuncTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(lab7Parser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(lab7Parser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#blockItem}.
	 * @param ctx the parse tree
	 */
	void enterBlockItem(lab7Parser.BlockItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#blockItem}.
	 * @param ctx the parse tree
	 */
	void exitBlockItem(lab7Parser.BlockItemContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmt(lab7Parser.StmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmt(lab7Parser.StmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#exp}.
	 * @param ctx the parse tree
	 */
	void enterExp(lab7Parser.ExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#exp}.
	 * @param ctx the parse tree
	 */
	void exitExp(lab7Parser.ExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#cond}.
	 * @param ctx the parse tree
	 */
	void enterCond(lab7Parser.CondContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#cond}.
	 * @param ctx the parse tree
	 */
	void exitCond(lab7Parser.CondContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#lVal}.
	 * @param ctx the parse tree
	 */
	void enterLVal(lab7Parser.LValContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#lVal}.
	 * @param ctx the parse tree
	 */
	void exitLVal(lab7Parser.LValContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#primaryExp}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryExp(lab7Parser.PrimaryExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#primaryExp}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryExp(lab7Parser.PrimaryExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#unaryExp}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExp(lab7Parser.UnaryExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#unaryExp}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExp(lab7Parser.UnaryExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#unaryOp}.
	 * @param ctx the parse tree
	 */
	void enterUnaryOp(lab7Parser.UnaryOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#unaryOp}.
	 * @param ctx the parse tree
	 */
	void exitUnaryOp(lab7Parser.UnaryOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#funcRParams}.
	 * @param ctx the parse tree
	 */
	void enterFuncRParams(lab7Parser.FuncRParamsContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#funcRParams}.
	 * @param ctx the parse tree
	 */
	void exitFuncRParams(lab7Parser.FuncRParamsContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#mulExp}.
	 * @param ctx the parse tree
	 */
	void enterMulExp(lab7Parser.MulExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#mulExp}.
	 * @param ctx the parse tree
	 */
	void exitMulExp(lab7Parser.MulExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#addExp}.
	 * @param ctx the parse tree
	 */
	void enterAddExp(lab7Parser.AddExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#addExp}.
	 * @param ctx the parse tree
	 */
	void exitAddExp(lab7Parser.AddExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#relExp}.
	 * @param ctx the parse tree
	 */
	void enterRelExp(lab7Parser.RelExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#relExp}.
	 * @param ctx the parse tree
	 */
	void exitRelExp(lab7Parser.RelExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#eqExp}.
	 * @param ctx the parse tree
	 */
	void enterEqExp(lab7Parser.EqExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#eqExp}.
	 * @param ctx the parse tree
	 */
	void exitEqExp(lab7Parser.EqExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#lAndExp}.
	 * @param ctx the parse tree
	 */
	void enterLAndExp(lab7Parser.LAndExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#lAndExp}.
	 * @param ctx the parse tree
	 */
	void exitLAndExp(lab7Parser.LAndExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#lOrExp}.
	 * @param ctx the parse tree
	 */
	void enterLOrExp(lab7Parser.LOrExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#lOrExp}.
	 * @param ctx the parse tree
	 */
	void exitLOrExp(lab7Parser.LOrExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(lab7Parser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(lab7Parser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link lab7Parser#ident}.
	 * @param ctx the parse tree
	 */
	void enterIdent(lab7Parser.IdentContext ctx);
	/**
	 * Exit a parse tree produced by {@link lab7Parser#ident}.
	 * @param ctx the parse tree
	 */
	void exitIdent(lab7Parser.IdentContext ctx);
}