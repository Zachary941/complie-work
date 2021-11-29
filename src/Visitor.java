import java.util.ArrayList;

public class Visitor extends lab4BaseVisitor<Void> {
    int index = 1;
    ArrayList<Symbol> symbolsstack = new ArrayList<>();
    int nownumber = 0;
    String nowidentName = "";
    String nowIRName = "";
    int is_global_variable = 0;
    int gobal_num = 0;
    int return_unaryop = 0;

    public void is_def_in_symbolsstack() {
        for (Symbol symbol : this.symbolsstack) {
            if (this.nowidentName.equals(symbol.old_name)) {
                System.out.println("符号栈中已有符号" + nowidentName);
                System.exit(1);
            }
        }
    }

    @Override
    public Void visitConstDecl(lab4Parser.ConstDeclContext ctx) {
        return super.visitConstDecl(ctx);
    }

    @Override
    public Void visitConstDef(lab4Parser.ConstDefContext ctx) {
        if (is_global_variable == 0) {
            visit(ctx.ident());
            if (ctx.children.size() == 3) {
                visit(ctx.constInitVal());
                System.out.println("@" + this.nowidentName + " = dso_local global i32 " + this.nownumber);
                is_def_in_symbolsstack();
                Symbol symbol = new Symbol(nowidentName, "%" + (index - 1), is_global_variable);
                symbol.num = this.nownumber;
                symbol.isconst = true;
                symbolsstack.add(symbol);
            }
        } else {
            String returnindex = index + "";
            System.out.println("    %" + (index++) + " = alloca i32");
            visit(ctx.ident());
            is_def_in_symbolsstack();
            Symbol symbol = new Symbol(nowidentName, "%" + (index - 1), is_global_variable);
            symbol.num = this.nownumber;
            symbol.isconst = true;
            symbolsstack.add(symbol);
            visit(ctx.constInitVal());
            System.out.println("    store i32 %" + (index - 1) + ", i32* %" + returnindex);
        }

        return null;
    }


    @Override
    public Void visitVarDef(lab4Parser.VarDefContext ctx) {
        if (is_global_variable == 0) {
            visit(ctx.ident());
            Symbol symbol = new Symbol(nowidentName, "%" + (index - 1), is_global_variable);
            if (ctx.children.size() == 3) {
                visit(ctx.initVal());
                is_def_in_symbolsstack();
                System.out.println("@" + this.nowidentName + " = dso_local global i32 " + this.nownumber);

            }
            symbolsstack.add(symbol);
        } else {
            String returnindex = index + "";
            System.out.println("    %" + (index++) + " = alloca i32");
            if (ctx.children.size() == 1) {
                visit(ctx.ident());
                is_def_in_symbolsstack();
                Symbol symbol = new Symbol(nowidentName, "%" + (index - 1), is_global_variable);
                symbolsstack.add(symbol);
            } else if (ctx.children.size() == 3) {
                visit(ctx.ident());
                is_def_in_symbolsstack();
                Symbol symbol = new Symbol(nowidentName, "%" + (index - 1), is_global_variable);
                symbolsstack.add(symbol);
                visit(ctx.initVal());
                System.out.println("    store i32 %" + (index - 1) + ", i32* %" + returnindex);
            } else {
                System.out.println("vardef error");
                System.exit(1);
            }
        }

        return null;
    }

    @Override
    public Void visitFuncDef(lab4Parser.FuncDefContext ctx) {
        if (ctx.children.size() == 5) {
            System.out.println("define dso_local i32 @main()");
            is_global_variable = 1;
            return super.visitFuncDef(ctx);
        } else {
            System.out.println("funcdef error");
            System.exit(1);
        }

        return null;
    }

    @Override
    public Void visitBlock(lab4Parser.BlockContext ctx) {
        System.out.println("{");
        if (ctx.children.size() >= 2) {
            for (int i = 0; i < ctx.children.size() - 2; i++) {
                visit(ctx.blockItem(i));
            }
            System.out.println("}");
        } else {
            System.out.println("block error");
            System.exit(1);
        }
        return null;
    }

    @Override
    public Void visitBlockItem(lab4Parser.BlockItemContext ctx) {
        return super.visitBlockItem(ctx);
    }

    @Override
    public Void visitStmt(lab4Parser.StmtContext ctx) {
        if (ctx.children.size() == 4) {
            String lval, exp;
            visit(ctx.lVal());
            //将查到的变量返回回来
            //返回到this.nowIRName
            lval = this.nowIRName;
            for (Symbol symbol : symbolsstack) {
                if (symbol.new_name.equals(lval)) {
                    if (symbol.isconst) {
                        System.out.println("不可改变常量值，该常量为" +symbol.old_name);
                        System.exit(1);
                    }
                }
            }
            visit(ctx.exp());
            //将运算完的变量返回回来
            exp = this.nowIRName;

            //输出store
            System.out.println("    store i32 " + exp + ", i32* " + lval);
        } else if (ctx.children.size() == 2) {
            visit(ctx.exp());
        } else if (ctx.children.size() == 3) {
            visit(ctx.exp());
            System.out.println("    ret i32 " + this.nowIRName);
        }
        return null;
    }

    @Override
    public Void visitLVal(lab4Parser.LValContext ctx) {
        visit(ctx.ident());
        int flag1 = 0;
        for (Symbol symbol : this.symbolsstack) {
            if (symbol.old_name.equals(this.nowidentName)) {
                this.nowIRName = symbol.new_name;
                flag1 = 1;
            }
        }
        if (flag1 == 0) {
            System.out.println("字符表中不存在字符" + this.nowidentName);
            System.exit(1);
        }

        return null;
    }

    @Override
    public Void visitAddExp(lab4Parser.AddExpContext ctx) {
        if (ctx.children.size() == 1) {// addexp->mulexp
            visit(ctx.mulExp());
        } else if (ctx.children.size() == 3)// addExp ('+' | '-') mulExp
        {
            if (is_global_variable == 0) {
                int lhs = 0, rhs = 0, result = 0;
                visit(ctx.addExp());
                lhs = this.nownumber;
                visit(ctx.mulExp());
                rhs = this.nownumber;
                if (ctx.getChild(1).toString().equals("+")) {
                    result = lhs + rhs;
                } else {
                    result = lhs - rhs;
                }
                this.nownumber = result;
            } else {
                String lhs = "", rhs = "";

                visit(ctx.addExp());
                lhs = nowIRName;

                visit(ctx.mulExp());
                rhs = nowIRName;

                if (ctx.getChild(1).toString().equals("+")) {
//                result = lhs + rhs;
                    System.out.println("    %" + (index++) + " = add i32 " + lhs + ", " + rhs);
                } else {
//                result = lhs - rhs;
                    System.out.println("    %" + (index++) + " = sub i32 " + lhs + ", " + rhs);
                }
                nowIRName = "%" + (index - 1);
            }

        } else {
            System.out.println("add exp wrong");
            System.exit(1);
        }
        return null;
    }

    @Override
    public Void visitUnaryExp(lab4Parser.UnaryExpContext ctx) {
        if (ctx.children.size() == 1) {
            visit(ctx.primaryExp());
        } else if (ctx.children.size() == 2) {
            visit(ctx.unaryExp());
            //"+"没有改变，故而不再转换
            if (ctx.unaryOp().getText().equals("-")) {
                System.out.println("    %" + (index) + " = sub i32 " + 0 + ", %" + (index - 1));
                index++;
            }
            this.nowIRName = "%" + (index - 1);

        }
        return null;
    }


    @Override
    public Void visitPrimaryExp(lab4Parser.PrimaryExpContext ctx) {
        if (ctx.children.size() == 1) {
            if (ctx.number() != null) {
                this.nownumber = 0;
                String strnum = ctx.number().getText();
//                System.out.println(strnum);
                if (strnum.startsWith("0x") || strnum.startsWith("0X")) {
                    for (int i = 2; i < strnum.length(); i++) {
                        if (Character.isDigit(strnum.charAt(i))) {
                            this.nownumber = this.nownumber * 16 + strnum.charAt(i) - '0';
                        } else if (Character.isAlphabetic(strnum.charAt(i))) {
                            this.nownumber = this.nownumber * 16 + strnum.toUpperCase().charAt(i) - 'A' + 10;
                        }
                    }
                } else if (strnum.startsWith("0")) {
                    for (int i = 1; i < strnum.length(); i++) {
                        this.nownumber = this.nownumber * 8 + strnum.charAt(i) - '0';
                    }
                } else {
                    for (int i = 0; i < strnum.length(); i++) {
                        this.nownumber = this.nownumber * 10 + strnum.charAt(i) - '0';
                    }
                }
                if (is_global_variable != 0) {
                    System.out.println("    %" + (index++) + "= add i32 0," + this.nownumber);
                    this.nowIRName = "%" + (index - 1);
                }

            } else {
                visit(ctx.lVal());
                System.out.println("    %" + (index++) + " = load i32, i32* " + this.nowIRName);
                this.nowIRName = "%" + (index - 1);
            }
        } else if (ctx.children.size() == 3) {
            visit(ctx.exp());
        } else {
            System.out.println("primaryexp error");
            System.exit(1);
        }
        return null;
    }

    @Override
    public Void visitNumber(lab4Parser.NumberContext ctx) {

        return null;
    }

    @Override
    public Void visitMulExp(lab4Parser.MulExpContext ctx) {
        if (ctx.children.size() == 1) {
            visit(ctx.unaryExp());
        } else if (ctx.children.size() == 3) {
            if (is_global_variable == 0) {
                int lhs = 0, rhs = 0, result = 0;
                visit(ctx.mulExp());
                lhs = this.nownumber;
                visit(ctx.unaryExp());
                rhs = this.nownumber;
                if (ctx.getChild(1).toString().equals("*")) {
                    result = lhs * rhs;
                } else if (ctx.getChild(1).toString().equals("/")) {
                    result = lhs / rhs;
                } else {
                    result = lhs % rhs;
                }
                this.nownumber = result;
            } else {
                // mulExp       :  mulExp ('*' | '/' | '%') unaryExp;
                String lhs = "";
                String rhs = "";

                visit(ctx.mulExp());
                lhs = nowIRName;

                visit(ctx.unaryExp());
                rhs = nowIRName;

                if (ctx.getChild(1).toString().equals("*")) {
//                result = lhs * rhs;
                    System.out.println("    %" + (index++) + " = mul i32 " + lhs + ", " + rhs);
                } else if (ctx.getChild(1).toString().equals("/")) {
//                result = lhs / rhs;
                    System.out.println("    %" + (index++) + " = sdiv i32 " + lhs + ", " + rhs);
                } else {
//                result = lhs % rhs;
                    System.out.println("    %" + (index++) + " = srem i32 " + lhs + ", " + rhs);
                }
                nowIRName = "%" + (index - 1);
            }

        } else {
            System.out.println("MulExp wrong");
            System.exit(1);
        }
        return null;
    }

    @Override
    public Void visitIdent(lab4Parser.IdentContext ctx) {
        this.nowidentName = "";
        for (int i = 0; i < ctx.getChildCount(); i++) {
            this.nowidentName = this.nowidentName + ctx.getChild(i).getText();
        }

        return super.visitIdent(ctx);
    }
}

