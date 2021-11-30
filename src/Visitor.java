import java.util.ArrayList;

public class Visitor extends lab4BaseVisitor<Void> {
    int index = 1;
    ArrayList<Symbol> symbolsstack = new ArrayList<>();
    int nownumber = 0;
    String nowidentName = "";
    String nowIRName = "";
    int is_global_variable = 0;
    public static ArrayList<String> ir_code = new ArrayList<>();
    int[] fun_decl = {0, 0, 0, 0, 0, 0, 0, 0};
    public void is_def_in_symbolsstack() {
        for (Symbol symbol : this.symbolsstack) {
            if (this.nowidentName.equals(symbol.old_name)) {
                ir_code.add("符号栈中已有符号" + nowidentName + "\n");
//                System.out.println("符号栈中已有符号" + nowidentName);
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
                ir_code.add("@" + this.nowidentName + " = dso_local global i32 " + this.nownumber + "\n");
//                System.out.println("@" + this.nowidentName + " = dso_local global i32 " + this.nownumber);
                is_def_in_symbolsstack();
                Symbol symbol = new Symbol(nowidentName, "%x" + (index - 1), is_global_variable);
                symbol.num = this.nownumber;
                symbol.isconst = true;
                symbolsstack.add(symbol);
            }
        } else {
            String returnindex = index + "";
            ir_code.add("    %x" + (index++) + " = alloca i32\n");
            this.nowIRName="%x"+(index-1);
//            System.out.println("    %" + (index++) + " = alloca i32");
            visit(ctx.ident());
            is_def_in_symbolsstack();
            Symbol symbol = new Symbol(nowidentName, "%x" + (index - 1), is_global_variable);
            symbol.num = this.nownumber;
            symbol.isconst = true;
            symbolsstack.add(symbol);
            visit(ctx.constInitVal());
            ir_code.add("    store i32 %x" + (index - 1) + ", i32* %x" + returnindex + "\n");
//            System.out.println("    store i32 %" + (index - 1) + ", i32* %" + returnindex);
        }

        return null;
    }


    @Override
    public Void visitVarDef(lab4Parser.VarDefContext ctx) {
        if (is_global_variable == 0) {
            visit(ctx.ident());
            Symbol symbol = new Symbol(nowidentName, "%x" + (index - 1), is_global_variable);
            if (ctx.children.size() == 3) {
                visit(ctx.initVal());
                is_def_in_symbolsstack();
                ir_code.add("@" + this.nowidentName + " = dso_local global i32 " + this.nownumber + "\n");
//                System.out.println("@" + this.nowidentName + " = dso_local global i32 " + this.nownumber);

            }
            symbolsstack.add(symbol);
        } else {
            String returnindex = index + "";
            ir_code.add("    %x" + (index++) + " = alloca i32\n");
            this.nowIRName="%x"+(index-1);
//            System.out.println("    %" + (index++) + " = alloca i32");
            if (ctx.children.size() == 1) {
                visit(ctx.ident());
                is_def_in_symbolsstack();
                Symbol symbol = new Symbol(nowidentName, "%x" + (index - 1), is_global_variable);
                symbolsstack.add(symbol);
            } else if (ctx.children.size() == 3) {
                visit(ctx.ident());
                is_def_in_symbolsstack();
                Symbol symbol = new Symbol(nowidentName, "%x" + (index - 1), is_global_variable);
                symbolsstack.add(symbol);
                visit(ctx.initVal());
                ir_code.add("    store i32 %x" + (index - 1) + ", i32* %x" + returnindex + "\n");
//                System.out.println("    store i32 %" + (index - 1) + ", i32* %" + returnindex);
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
            ir_code.add("define dso_local i32 @main()\n");
//            System.out.println("define dso_local i32 @main()");
            is_global_variable = 1;
            ir_code.add("{\n");
            visit(ctx.block());
            ir_code.add("}\n");
            return null;
        } else {
            ir_code.add("funcdef error\n");
//            System.out.println("funcdef error");
            System.exit(1);
        }

        return null;
    }

    @Override
    public Void visitBlock(lab4Parser.BlockContext ctx) {
//        System.out.println("{");

        if (ctx.children.size() >= 2) {
            for (int i = 0; i < ctx.children.size() - 2; i++) {
                visit(ctx.blockItem(i));
            }
//            System.out.println("}");

        } else {

//            System.out.println("block error");
            ir_code.add("block error");
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
            //stmt         : lVal '=' exp ';'
            String lval, exp;
            visit(ctx.lVal());
            //将查到的变量返回回来
            //返回到this.nowIRName
            lval = this.nowIRName;
            for (Symbol symbol : symbolsstack) {
                if (symbol.new_name.equals(lval)) {
                    if (symbol.isconst) {
                        ir_code.add("不可改变常量值，该常量为" + symbol.old_name + "\n");
//                        System.out.println("不可改变常量值，该常量为" +symbol.old_name);
                        System.out.println("stmt error");
                        System.exit(1);
                    }
                }
            }
            visit(ctx.exp());
            //将运算完的变量返回回来
            exp = this.nowIRName;
            //输出store
//            System.out.println("    store i32 " + exp + ", i32* " + lval);
            ir_code.add("    store i32 " + exp + ", i32* " + lval + "\n");
        }else if (ctx.children.size()==1){
            visit(ctx.block());
        }else if (ctx.children.size() == 2) {
            visit(ctx.exp());
        } else if (ctx.children.size() == 3) {
            visit(ctx.exp());
//            System.out.println("    ret i32 " + this.nowIRName);
            ir_code.add("    ret i32 " + this.nowIRName + "\n");
        } else if (ctx.children.size() >= 5) {
//            System.out.println(ctx.children.get(0).getText());
            if (ctx.children.get(0).getText().equals("if")){
                int cond=0,label1=0,label2=0,label3=0;
                visit(ctx.cond());
                //等待返回的
                cond=index-1;
                label1=index;
                label2=index+1;
                index+=2;
//                System.out.println("    br i1 %7,label %8, label %10");
                ir_code.add("    br i1 %x"+cond+",label %x"+label1+", label %x"+label2);
                if (ctx.children.size()==5){
                    ir_code.add("\nx"+label1+":\n");
                    visit(ctx.stmt(0));
                    ir_code.add("    br label %x"+label2+"\n");
                    ir_code.add("\nx"+label2+":\n");
                }else {
                    label3=index++;
                    ir_code.add("\nx"+label1+":\n");
                    visit(ctx.stmt(0));
                    ir_code.add("    br label %x"+label3+"\n");
                    ir_code.add("\nx"+label2+":\n");
                    visit(ctx.stmt(1));
                    ir_code.add("    br label %x"+(label3)+"\n");
                    ir_code.add("\nx"+label3+":\n");
                }
                this.nowIRName="%x"+(index-1);
            }else if (ctx.children.get(0).getText().equals("while")){
                visit(ctx.cond());
            }
        }
        return null;
    }

    @Override
    public Void visitRelExp(lab4Parser.RelExpContext ctx) {
        if (ctx.children.size()==1){
            visit(ctx.addExp());
        }else if (ctx.children.size()==3){
            String left="",right="";
            visit(ctx.relExp());
            left=this.nowIRName;
            visit(ctx.addExp());
            right=this.nowIRName;
            if (ctx.getChild(1).getText().equals(">")){
                ir_code.add("    %x"+(index++)+" = icmp sgt i32 "+left+", "+right+"\n");
            }else if (ctx.getChild(1).getText().equals(">=")){
                ir_code.add("    %x"+(index++)+" = icmp sge i32 "+left+", "+right+"\n");
            }else if (ctx.getChild(1).getText().equals("<")){
                ir_code.add("    %x"+(index++)+" = icmp slt i32 "+left+", "+right+"\n");
            }else if (ctx.getChild(1).getText().equals("<=")){
                ir_code.add("    %x"+(index++)+" = icmp sle i32 "+left+", "+right+"\n");
            }
            this.nowIRName="%x"+(index-1);
        }else {
            System.out.println("relexp");
            System.exit(1);
        }
        return null;
    }

    @Override
    public Void visitEqExp(lab4Parser.EqExpContext ctx) {
        if (ctx.children.size()==1){
            visit(ctx.relExp());
        }else if (ctx.children.size()==3){
            String left="",right="";
            visit(ctx.eqExp());
            left=this.nowIRName;
            visit(ctx.relExp());
            right=this.nowIRName;
            if (ctx.getChild(1).getText().equals("==")){
                ir_code.add("    %x"+(index++)+" = icmp eq i32 "+left+", "+right+"\n");
            }else if (ctx.getChild(1).getText().equals("!=")){
                ir_code.add("    %x"+(index++)+" = icmp ne i32 "+left+", "+right+"\n");
            }
            this.nowIRName="%x"+(index-1);
        }else {
            System.out.println("eqexp error");
            System.exit(1);
        }
        return null;
    }

    @Override
    public Void visitLAndExp(lab4Parser.LAndExpContext ctx) {
        if (ctx.children.size()==1){
            visit(ctx.eqExp());
        }else if (ctx.children.size()==3){
            String left="",right="";
            visit(ctx.lAndExp());
            left=this.nowIRName;
            visit(ctx.eqExp());
            right=this.nowIRName;
            ir_code.add("    %x" +(index)+"= and i1 "+left+","+right+";\n");
            index++;
            this.nowIRName="%x"+(index-1);
        }else {
            System.out.println("landexp error");
            System.exit(1);
        }
        return null;
    }

    @Override
    public Void visitLOrExp(lab4Parser.LOrExpContext ctx) {
        if (ctx.children.size()==1){
            visit(ctx.lAndExp());
        }else if (ctx.children.size()==3){
            String left="",right="";
            visit(ctx.lOrExp());
            left=this.nowIRName;
            visit(ctx.lAndExp());
            right=this.nowIRName;
            ir_code.add("    %x" +(index)+"= or i1 "+left+","+right+";\n");
            index++;
            this.nowIRName="%x"+(index-1);
        }else {
            System.out.println("lorexp error");
            System.exit(1);
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
//            System.out.println("字符表中不存在字符" + this.nowidentName);
            ir_code.add("字符表中不存在字符" + this.nowidentName + "\n");
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
//                    System.out.println("    %" + (index++) + " = add i32 " + lhs + ", " + rhs);
                    ir_code.add("    %x" + (index++) + " = add i32 " + lhs + ", " + rhs + "\n");
                } else {
//                result = lhs - rhs;
//                    System.out.println("    %" + (index++) + " = sub i32 " + lhs + ", " + rhs);
                    ir_code.add("    %x" + (index++) + " = sub i32 " + lhs + ", " + rhs + "\n");
                }
                nowIRName = "%x" + (index - 1);
            }

        } else {
//            System.out.println("add exp wrong");
            ir_code.add("add exp wrong\n");
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
//                System.out.println("    %" + (index) + " = sub i32 " + 0 + ", %" + (index - 1));
                ir_code.add("    %x" + (index) + " = sub i32 " + 0 + ", %x" + (index - 1) + "\n");
                index++;
            }
            this.nowIRName = "%x" + (index - 1);

        } else if (ctx.children.size() >= 3) {
            visit(ctx.ident());
            //返回函数名
            String fun_name = this.nowidentName;
            if (fun_name.equals("getint")) {
                if (fun_decl[0] == 0) {
                    ir_code.add(0, "declare i32 @getint()\n");
                    fun_decl[0] = 1;
                }
                ir_code.add("    %x" + index + " = call i32 @getint()\n");
                index++;
                this.nowIRName="%x"+(index-1);
            } else if (fun_name.equals("putint")) {
                visit(ctx.funcRParams());
                if (fun_decl[1] == 0) {
                    ir_code.add(0, "declare void @putint(i32)\n");
                    fun_decl[1] = 1;
                }
                ir_code.add("    call void @putint(i32 " + this.nowIRName + ")\n");
            }else if (fun_name.equals("getch")){
                if (fun_decl[2]==0){
                    ir_code.add(0,"declare i32 @getch()");
                    fun_decl[2]=1;
                }
                ir_code.add("    %x" + index + " = call i32 @getch()\n");
                index++;
                this.nowIRName="%x"+(index-1);
            }else if (fun_name.equals("putch")){
                visit(ctx.funcRParams());
                if (fun_decl[3] == 0) {
                    ir_code.add(0, "declare void @putch(i32)\n");
                    fun_decl[3] = 1;
                }
                ir_code.add("    call void @putch(i32 " + this.nowIRName + ")\n");
            }else if (fun_name.equals("getarray")){
                visit(ctx.funcRParams());
                if (fun_decl[4]==0){
                    ir_code.add(0,"declare i32 @getarray(i32*)");
                    fun_decl[4]=1;
                }
                //getarray函数尚未完成

            }else if (fun_name.equals("putarray")){
                visit(ctx.funcRParams());
                if (fun_decl[4]==0){
                    ir_code.add("declare void @putarray(i32,i32*)");
                    fun_decl[4]=1;
                }
                //putarr函数尚未完成

            }
        } else {
            ir_code.add("unaryexp error");
            System.exit(1);
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
//                    System.out.println("    %" + (index++) + "= add i32 0," + this.nownumber);
                    ir_code.add("    %x" + (index++) + "= add i32 0," + this.nownumber + "\n");
                    this.nowIRName = "%x" + (index - 1);
                }

            } else {
                visit(ctx.lVal());
//                System.out.println("    %" + (index++) + " = load i32, i32* " + this.nowIRName);
                ir_code.add("    %x" + (index++) + " = load i32, i32* " + this.nowIRName + "\n");
                this.nowIRName = "%x" + (index - 1);
            }
        } else if (ctx.children.size() == 3) {
            visit(ctx.exp());
        } else {
//            System.out.println("primaryexp error");
            ir_code.add("primaryexp error\n");
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
//                    System.out.println("    %" + (index++) + " = mul i32 " + lhs + ", " + rhs);
                    ir_code.add("    %x" + (index++) + " = mul i32 " + lhs + ", " + rhs + "\n");
                } else if (ctx.getChild(1).toString().equals("/")) {
//                result = lhs / rhs;
//                    System.out.println("    %" + (index++) + " = sdiv i32 " + lhs + ", " + rhs);
                    ir_code.add("    %x" + (index++) + " = sdiv i32 " + lhs + ", " + rhs + "\n");
                } else {
//                result = lhs % rhs;
//                    System.out.println("    %" + (index++) + " = srem i32 " + lhs + ", " + rhs);
                    ir_code.add("    %x" + (index++) + " = srem i32 " + lhs + ", " + rhs + "\n");
                }
                nowIRName = "%x" + (index - 1);
            }

        } else {
//            System.out.println("MulExp wrong");
            ir_code.add("MulExp wrong\n");
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

