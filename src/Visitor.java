import java.util.ArrayList;

public class Visitor extends lab7BaseVisitor<Void> {
    int index = 1;
    public static ArrayList<Symbol> symbolsstack = new ArrayList<>();
    int nownumber = 0;
    String nowidentName = "";
    String nowIRName = "";
    String nowType = "";
    int layer = 0;
    int if_alone = 0;
    public static ArrayList<String> ir_code = new ArrayList<>();
    int[] fun_decl = {0, 0, 0, 0, 0, 0, 0, 0};
    int num_of_initval = 0;
    int array1 = 0;
    int array2 = 0;
    int array_start = 0;
    int second_i32 = 0;
    ArrayList<String> func_param=new ArrayList<>();
    int for_array_addr=0;
    ArrayList<Integer> func_param_add=new ArrayList<>();
    Symbol func_symbol;
    int no_load;
    int no_add;
    ArrayList<String> reparm=new ArrayList<>();
    //宏观定义和当前变量定义
    public void is_def_in_symbolsstack() {
        for (Symbol symbol : symbolsstack) {
            if (this.nowidentName.equals(symbol.old_name) && (symbol.layer == this.layer)) {
                ir_code.add("符号栈中已有符号" + nowidentName + "\n");
                System.out.println("符号栈中已有符号" + nowidentName + symbol.layer);
                System.exit(1);
            }
        }
    }

    @Override
    public Void visitConstDecl(lab7Parser.ConstDeclContext ctx) {
        return super.visitConstDecl(ctx);
    }

    @Override
    public Void visitConstDef(lab7Parser.ConstDefContext ctx) {
        if (ctx.children.size() <= 3) {
            if (layer == 0) {
                visit(ctx.ident());
                if (ctx.children.size() == 3) {
                    is_def_in_symbolsstack();
                    visit(ctx.constInitVal());
                    ir_code.add("@" + this.nowidentName + " = dso_local global i32 " + this.nownumber + "\n");
//                System.out.println("@" + this.nowidentName + " = dso_local global i32 " + this.nownumber);
                    Symbol symbol = new Symbol(nowidentName, "@" + nowidentName, layer);
                    symbol.num = this.nownumber;
                    symbol.isconst = true;
                    symbolsstack.add(symbol);
                }
            } else {
                String returnindex = index + "";
                ir_code.add("    %x" + (index++) + " = alloca i32\n");
                this.nowIRName = "%x" + (index - 1);
//            System.out.println("    %" + (index++) + " = alloca i32");
                visit(ctx.ident());
                is_def_in_symbolsstack();
                Symbol symbol = new Symbol(nowidentName, "%x" + (index - 1), layer);
                symbol.num = this.nownumber;
                symbol.isconst = true;
                symbolsstack.add(symbol);
                visit(ctx.constInitVal());
                ir_code.add("    store i32 %x" + (index - 1) + ", i32* %x" + returnindex + "\n");
//            System.out.println("    store i32 %" + (index - 1) + ", i32* %" + returnindex);
            }
        } else {

            num_of_initval = 0;
            array1 = 0;
            array2 = 0;
            visit(ctx.ident());
            String name = this.nowidentName;
            if (layer == 0) {
                if (ctx.children.size() == 6) {
                    //一维数组初始化
                    visit(ctx.constExp(0));
                    array1 = this.nownumber;
                    ir_code.add("@" + name + " = dso_local constant [" + array1 + " x i32] ");
                    visit(ctx.constInitVal());
                    ir_code.add("\n");
                    //入栈
                    Symbol symbol = new Symbol(name, "@" + name, layer);
                    symbol.type = "one_array";
                    symbol.isconst = true;
                    symbol.array1 = array1;
                    symbolsstack.add(symbol);
                } else {
                    //二维数组初始化
                    visit(ctx.constExp(0));
                    array1 = this.nownumber;
                    visit(ctx.constExp(1));
                    array2 = this.nownumber;
                    //@c = dso_local constant [2 x [1 x i32]] [[1 x i32] [i32 1], [1 x i32] [i32 3]]
                    ir_code.add("@" + name + " = dso_local constant [" + array1 + " x [" + array2 + " x i32]] ");
                    visit(ctx.constInitVal());
                    ir_code.add("\n");
                    //入栈
                    Symbol symbol = new Symbol(name, "@" + name, layer);
                    symbol.type = "two_array";
                    symbol.isconst = true;
                    symbol.array1 = array1;
                    symbol.array2 = array2;
                    symbolsstack.add(symbol);
                }
            } else {
                //int main 内的数组初始化
                array_start = 0;
                second_i32 = 0;
                if (ctx.children.size() == 6) {
                    //一维数组初始化
                    visit(ctx.constExp(0));
                    array1 = this.nownumber;
                    ir_code.add("    %x" + index + " = alloca [" + array1 + " x i32]\n");
                    array_start = index;
                    index++;
                    ir_code.add("    %x" + index + " = getelementptr [" + array1 + " x i32], [" + array1 + " x i32]* %x" + (index - 1) + ", i32 0, i32 0\n");
                    index++;
                    ir_code.add("    call void @memset(i32* %x" + (index - 1) + ", i32 0, i32 " + array1 * 4 + ")\n");
                    if (fun_decl[6] == 0) {
                        ir_code.add(0, "declare void @memset(i32*, i32, i32)\n");
                        fun_decl[6] = 1;
                    }
                    //入栈
                    Symbol symbol = new Symbol(name, "@" + name, layer);
                    symbol.type = "one_array";
                    symbol.array1 = array1;
                    symbolsstack.add(symbol);
                    visit(ctx.constInitVal());
                } else {
                    //二维数组初始化
                    visit(ctx.constExp(0));
                    array1 = this.nownumber;
                    visit(ctx.constExp(1));
                    array2 = this.nownumber;
                    //    %1 = alloca [2 x [2 x i32]]
                    ir_code.add("    %x" + index + " = alloca [" + array1 + " x [" + array2 + " x i32]]\n");
                    array_start = index;
                    index++;
                    //%3 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* %2, i32 0, i32 0
                    ir_code.add("    %x" + index + " = getelementptr [" + array1 + " x [" + array2 + " x i32]], [" + array1 + " x [" + array2 + " x i32]]* %x" + (index - 1) + ", i32 0, i32 0,i32 0\n");

                    index++;
                    ir_code.add("    call void @memset(i32* %x" + (index - 1) + ", i32 0, i32 " + array1 * array2 * 4 + ")\n");
                    if (fun_decl[6] == 0) {
                        ir_code.add(0, "declare void @memset(i32*, i32, i32)\n");
                        fun_decl[6] = 1;
                    }
                    //入栈
                    Symbol symbol = new Symbol(name, "@" + name, layer);
                    symbol.type = "two_array";
                    symbol.array1 = array1;
                    symbol.array2 = array2;
                    symbolsstack.add(symbol);
                    visit(ctx.constInitVal());
                }
            }
        }

        return null;
    }

    @Override
    public Void visitConstInitVal(lab7Parser.ConstInitValContext ctx) {
        if (ctx.children.size() == 1) {
            visit(ctx.constExp());
        } else if (ctx.children.size() >= 3) {
            num_of_initval++;
            if (layer == 0) {
                if (array2 == 0) {
                    //一维数组
                    ir_code.add("[");
                    for (int i = 0; i < (ctx.children.size() - 2) / 2 + 1; i++) {
                        System.out.println(num_of_initval);
                        visit(ctx.constInitVal(i));
                        if (i == array1 - 1) {
                            ir_code.add("i32 " + this.nownumber);
                        } else {
                            ir_code.add("i32 " + this.nownumber + ",");
                        }
                    }
                    for (int i = (ctx.children.size() - 2) / 2 + 1; i < array1; i++) {
                        if (i == array1 - 1) {
                            ir_code.add("i32 " + 0);
                        } else {
                            ir_code.add("i32 " + 0 + ",");
                        }
                    }
                    ir_code.add("]");
                } else {
                    //二维数组
                    ir_code.add("[");
                    if (num_of_initval == 1) {
                        for (int i = 0; i < (ctx.children.size() - 2) / 2 + 1; i++) {
                            ir_code.add("[" + array2 + " x i32] ");
                            visit(ctx.constInitVal(i));
                            if (i == array1 - 1) {
                                ir_code.add("");
                            } else {
                                ir_code.add(",");
                            }
                        }
                        for (int i = (ctx.children.size() - 2) / 2 + 1; i < array1; i++) {
                            ir_code.add("[" + array2 + " x i32] ");
                            if (i == array1 - 1) {
                                ir_code.add("zeroinitializer");
                            } else {
                                ir_code.add("zeroinitializer,");
                            }
                        }
                    } else {
                        for (int i = 0; i < (ctx.children.size() - 2) / 2 + 1; i++) {
                            visit(ctx.constInitVal(i));
                            if (i == array2 - 1) {
                                ir_code.add("i32 " + this.nownumber);
                            } else {
                                ir_code.add("i32 " + this.nownumber + ",");
                            }
                        }
                        for (int i = (ctx.children.size() - 2) / 2 + 1; i < array2; i++) {
                            if (i == array2 - 1) {
                                ir_code.add("i32 " + 0);
                            } else {
                                ir_code.add("i32 " + 0 + ",");
                            }
                        }
                    }
                    //[1 x i32] [i32 1], [1 x i32] [i32 3]
                    ir_code.add("]");
                }
            } else {
                if (array2 == 0) {
                    //一维数组int内初始化存值
                    int locate = array_start + 1;
                    for (int i = 0; i < (ctx.children.size() - 2) / 2 + 1; i++) {
                        if (i != 0) {
                            ir_code.add("    %" + index + " = getelementptr i32, i32* %x" + array_start + ", i32 " + i + "\n");
                            locate = index;
                            index++;
                        }
                        visit(ctx.constInitVal(i));
                        ir_code.add("    store i32 %x" + (index - 1) + ", i32* %x" + locate + "\n");
                    }
                } else {
                    //二维数组int内初始化存值
                    int locate = array_start + 1;

                    if (num_of_initval == 1) {
                        for (int i = 0; i < (ctx.children.size() - 2) / 2 + 1; i++) {
                            second_i32 = i;
                            visit(ctx.constInitVal(i));
                        }
                    } else {
                        for (int i = 0; i < (ctx.children.size() - 2) / 2 + 1; i++) {
                            if (i != 0 || second_i32 > 0) {
                                ir_code.add("    %x" + index + " = getelementptr [" + array1 + " x [" + array2 + " x i32]], [" + array1 + " x [" + array2 + " x i32]]* %x" + (index - 1) + ", i32 0, i32 " + second_i32 + ", i32 " + i + " ;\n");
                                locate = index;
                                index++;
                            }
                            visit(ctx.constInitVal(i));
                            ir_code.add("    store i32 %x" + (index - 1) + ", i32* %x" + locate + "\n");
                            index++;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Void visitVarDef(lab7Parser.VarDefContext ctx) {
        if (ctx.children.size() == 1 || (ctx.children.size() == 3 && !ctx.getChild(2).getText().equals("["))) {
            if (layer == 0) {
                String var = "";
                visit(ctx.ident());
                var = "@" + this.nowidentName;
                Symbol symbol = new Symbol(nowidentName, "@" + nowidentName, layer);
                if (ctx.children.size() == 3) {
                    is_def_in_symbolsstack();
                    visit(ctx.initVal());
                    ir_code.add(var + " = dso_local global i32 " + this.nownumber + "\n");
//                System.out.println("@" + this.nowidentName + " = dso_local global i32 " + this.nownumber);
                } else {
                    is_def_in_symbolsstack();
                    ir_code.add(var + " = dso_local global i32 " + 0 + "\n");
                }
                symbolsstack.add(symbol);
//            for (int i = 0; i < symbolsstack.size(); i++) {
//                System.out.print(symbolsstack.get(i).old_name+"  "+symbolsstack.get(i).isconst+"\n");
//            }
//            System.out.println();
            } else {
                String returnindex = index + "";
                ir_code.add("    %x" + (index++) + " = alloca i32\n");
                this.nowIRName = "%x" + (index - 1);
//            System.out.println("    %" + (index++) + " = alloca i32");
                if (ctx.children.size() == 1) {
                    visit(ctx.ident());
                    is_def_in_symbolsstack();
                    Symbol symbol = new Symbol(nowidentName, "%x" + (index - 1), layer);
                    symbolsstack.add(symbol);
                } else if (ctx.children.size() == 3) {
                    visit(ctx.ident());
                    is_def_in_symbolsstack();
                    Symbol symbol = new Symbol(nowidentName, "%x" + (index - 1), layer);
                    symbolsstack.add(symbol);
                    visit(ctx.initVal());
                    ir_code.add("    store i32 %x" + (index - 1) + ", i32* %x" + returnindex + "\n");
//                System.out.println("    store i32 %" + (index - 1) + ", i32* %" + returnindex);
                } else {
                    System.out.println("vardef error");
                    System.exit(1);
                }

            }
        } else {
            num_of_initval = 0;
            array1 = 0;
            array2 = 0;
            visit(ctx.ident());
            String name = this.nowidentName;
            if (layer == 0) {
                if (ctx.children.size() == 4) {
                    visit(ctx.constExp(0));
                    array1 = this.nownumber;
                    ir_code.add("@" + name + " = dso_local global [" + array1 + " x i32] zeroinitializer");
                    ir_code.add("\n");
                    //入栈
                    Symbol symbol = new Symbol(name, "@" + name, layer);
                    symbol.type = "one_array";
                    symbol.isconst = true;
                    symbol.array1 = array1;
                    symbolsstack.add(symbol);
                } else if (ctx.children.size() == 7) {
                    visit(ctx.constExp(0));
                    array1 = this.nownumber;
                    visit(ctx.constExp(1));
                    array2 = this.nownumber;
                    //@c = dso_local constant [2 x [1 x i32]] [[1 x i32] [i32 1], [1 x i32] [i32 3]]
                    ir_code.add("@" + name + " = dso_local global [" + array1 + " x [" + array2 + " x i32]] zeroinitializer");
                    ir_code.add("\n");
                    //入栈
                    Symbol symbol = new Symbol(name, "@" + name, layer);
                    symbol.type = "two_array";
                    symbol.isconst = true;
                    symbol.array1 = array1;
                    symbol.array2 = array2;
                    symbolsstack.add(symbol);
                } else if (ctx.children.size() == 6) {
                    //一维数组初始化
                    visit(ctx.constExp(0));
                    array1 = this.nownumber;
                    //只处理了这部分其他尚未处理
                    if (array1 < 0) {
                        System.out.println("数组长度不能为负数");
                        System.exit(1);
                    }
                    ir_code.add("@" + name + " = dso_local global [" + array1 + " x i32] ");
                    visit(ctx.initVal());
                    ir_code.add("\n");
                    //入栈
                    Symbol symbol = new Symbol(name, "@" + name, layer);
                    symbol.type = "one_array";
                    symbol.isconst = true;
                    symbol.array1 = array1;
                    symbolsstack.add(symbol);
                } else if (ctx.children.size() == 9) {
                    //二维数组初始化
                    visit(ctx.constExp(0));
                    array1 = this.nownumber;
                    visit(ctx.constExp(1));
                    array2 = this.nownumber;
                    //@c = dso_local constant [2 x [1 x i32]] [[1 x i32] [i32 1], [1 x i32] [i32 3]]
                    ir_code.add("@" + name + " = dso_local global [" + array1 + " x [" + array2 + " x i32]] ");
                    visit(ctx.initVal());
                    ir_code.add("\n");
                    //入栈
                    Symbol symbol = new Symbol(name, "@" + name, layer);
                    symbol.type = "two_array";
                    symbol.isconst = true;
                    symbol.array1 = array1;
                    symbol.array2 = array2;
                    symbolsstack.add(symbol);
                }
            } else {
                //int main 内的数组初始化
                array_start = 0;
                second_i32 = 0;
                if (ctx.children.size() == 6) {
                    //一维数组初始化
                    visit(ctx.constExp(0));
                    array1 = this.nownumber;
                    if (array1 < 0) {
                        System.out.println("数组长度不能为负数");
                        System.exit(1);
                    }
                    ir_code.add("    %x" + index + " = alloca [" + array1 + " x i32]\n");
                    array_start = index;
                    index++;
                    ir_code.add("    %x" + index + " = getelementptr [" + array1 + " x i32], [" + array1 + " x i32]* %x" + (index - 1) + ", i32 0, i32 0\n");

                    index++;
                    ir_code.add("    call void @memset(i32* %x" + (index - 1) + ", i32 0, i32 " + array1 * 4 + ")\n");
                    if (fun_decl[6] == 0) {
                        ir_code.add(0, "declare void @memset(i32*, i32, i32)\n");
                        fun_decl[6] = 1;
                    }
                    //入栈
                    Symbol symbol = new Symbol(name, "%x" + array_start, layer);
                    symbol.type = "one_array";
                    symbol.array1 = array1;
                    symbolsstack.add(symbol);
                    visit(ctx.initVal());
                } else if (ctx.children.size() == 9) {
                    //二维数组初始化
                    visit(ctx.constExp(0));
                    array1 = this.nownumber;
                    visit(ctx.constExp(1));
                    array2 = this.nownumber;
                    //    %1 = alloca [2 x [2 x i32]]
                    ir_code.add("    %x" + index + " = alloca [" + array1 + " x [" + array2 + " x i32]]\n");
                    array_start = index;
                    index++;
                    //%3 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* %2, i32 0, i32 0
                    ir_code.add("    %x" + index + " = getelementptr [" + array1 + " x [" + array2 + " x i32]], [" + array1 + " x [" + array2 + " x i32]]* %x" + (index - 1) + ", i32 0, i32 0,i32 0\n");

                    index++;
                    ir_code.add("    call void @memset(i32* %x" + (index - 1) + ", i32 0, i32 " + array1 * array2 * 4 + ")\n");
                    if (fun_decl[6] == 0) {
                        ir_code.add(0, "declare void @memset(i32*, i32, i32)\n");
                        fun_decl[6] = 1;
                    }
                    //入栈
                    Symbol symbol = new Symbol(name, "%x" + array_start, layer);
                    symbol.type = "two_array";
                    symbol.array1 = array1;
                    symbol.array2 = array2;
                    symbolsstack.add(symbol);
                    visit(ctx.initVal());
                } else if (ctx.children.size() == 4) {
                    visit(ctx.constExp(0));
                    array1 = this.nownumber;
                    ir_code.add("    %x" + index + " = alloca [" + array1 + " x i32]\n");
                    array_start = index;
                    index++;
                    ir_code.add("    %x" + index + " = getelementptr [" + array1 + " x i32], [" + array1 + " x i32]* %x" + (index - 1) + ", i32 0, i32 0\n");
                    index++;
                    ir_code.add("    call void @memset(i32* %x" + (index - 1) + ", i32 0, i32 " + array1 * 4 + ")\n");
                    if (fun_decl[6] == 0) {
                        ir_code.add(0, "declare void @memset(i32*, i32, i32)\n");
                        fun_decl[6] = 1;
                    }
                    //入栈
                    Symbol symbol = new Symbol(name, "%x" + array_start, layer);
                    symbol.type = "one_array";
                    symbol.array1 = array1;
                    symbolsstack.add(symbol);
                } else if (ctx.children.size() == 7) {
                    visit(ctx.constExp(0));
                    array1 = this.nownumber;
                    visit(ctx.constExp(1));
                    array2 = this.nownumber;
                    //    %1 = alloca [2 x [2 x i32]]
                    ir_code.add("    %x" + index + " = alloca [" + array1 + " x [" + array2 + " x i32]]\n");
                    array_start = index;
                    index++;
                    //%3 = getelementptr [2 x [2 x i32]], [2 x [2 x i32]]* %2, i32 0, i32 0
                    ir_code.add("    %x" + index + " = getelementptr [" + array1 + " x [" + array2 + " x i32]], [" + array1 + " x [" + array2 + " x i32]]* %x" + (index - 1) + ", i32 0, i32 0,i32 0\n");

                    index++;
                    ir_code.add("    call void @memset(i32* %x" + (index - 1) + ", i32 0, i32 " + array1 * array2 * 4 + ")\n");
                    if (fun_decl[6] == 0) {
                        ir_code.add(0, "declare void @memset(i32*, i32, i32)\n");
                        fun_decl[6] = 1;
                    }
                    //入栈
                    Symbol symbol = new Symbol(name, "%x" + array_start, layer);
                    symbol.type = "two_array";
                    symbol.array1 = array1;
                    symbol.array2 = array2;
                    symbolsstack.add(symbol);
                }
            }
        }
        return null;
    }

    @Override
    public Void visitInitVal(lab7Parser.InitValContext ctx) {
        if (ctx.children.size() == 1) {
            visit(ctx.exp());
        }else if (ctx.children.size()==2&&layer==0){
            ir_code.add("zeroinitializer ");
        }else if (ctx.children.size() >= 3) {
            num_of_initval++;
            if (layer == 0) {
                if (array2 == 0) {
                    //一维数组
                    ir_code.add("[");
                    for (int i = 0; i < (ctx.children.size() - 2) / 2 + 1; i++) {
                        System.out.println(num_of_initval);
                        visit(ctx.initVal(i));
                        if (i == array1 - 1) {
                            ir_code.add("i32 " + this.nownumber);
                        } else {
                            ir_code.add("i32 " + this.nownumber + ",");
                        }
                    }
                    for (int i = (ctx.children.size() - 2) / 2 + 1; i < array1; i++) {
                        if (i == array1 - 1) {
                            ir_code.add("i32 " + 0);
                        } else {
                            ir_code.add("i32 " + 0 + ",");
                        }
                    }
                    ir_code.add("]");
                } else {
                    //二维数组
                    ir_code.add("[");
                    if (num_of_initval == 1) {
                        for (int i = 0; i < (ctx.children.size() - 2) / 2 + 1; i++) {
                            ir_code.add("[" + array2 + " x i32] ");
                            visit(ctx.initVal(i));
                            if (i == array1 - 1) {
                                ir_code.add("");
                            } else {
                                ir_code.add(",");
                            }
                        }
                        for (int i = (ctx.children.size() - 2) / 2 + 1; i < array1; i++) {
                            ir_code.add("[" + array2 + " x i32] ");
                            if (i == array1 - 1) {
                                ir_code.add("zeroinitializer");
                            } else {
                                ir_code.add("zeroinitializer,");
                            }
                        }
                    } else {
                        for (int i = 0; i < (ctx.children.size() - 2) / 2 + 1; i++) {
                            visit(ctx.initVal(i));
                            if (i == array2 - 1) {
                                ir_code.add("i32 " + this.nownumber);
                            } else {
                                ir_code.add("i32 " + this.nownumber + ",");
                            }
                        }
                        for (int i = (ctx.children.size() - 2) / 2 + 1; i < array2; i++) {
                            if (i == array2 - 1) {
                                ir_code.add("i32 " + 0);
                            } else {
                                ir_code.add("i32 " + 0 + ",");
                            }
                        }
                    }
                    //[1 x i32] [i32 1], [1 x i32] [i32 3]
                    ir_code.add("]");
                }
            } else {
                if (array2 == 0) {
                    //一维数组int内初始化存值
                    int locate = array_start + 1;
                    for (int i = 0; i < (ctx.children.size() - 2) / 2 + 1; i++) {
                        if (i != 0) {
                            //"    %x"+index+" = getelementptr ["+array1+" x i32], ["+array1+" x i32]* %x"+array_start+", i32 0, i32 "+i+"\n"
                            ir_code.add("    %x" + index + " = getelementptr [" + array1 + " x i32], [" + array1 + " x i32]* %x" + array_start + ", i32 0, i32 " + i + "\n");
                            locate = index;
                            index++;
                        }
                        visit(ctx.initVal(i));
                        ir_code.add("    store i32 %x" + (index - 1) + ", i32* %x" + locate + "\n");
                        index++;
                    }
                } else {
                    //二维数组int内初始化存值
                    int locate = array_start + 1;

                    if (num_of_initval == 1) {
                        for (int i = 0; i < (ctx.children.size() - 2) / 2 + 1; i++) {
                            second_i32 = i;
                            visit(ctx.initVal(i));
                        }
                    } else {
                        for (int i = 0; i < (ctx.children.size() - 2) / 2 + 1; i++) {
                            if (i != 0 || second_i32 > 0) {
                                ir_code.add("    %x" + index + " = getelementptr [" + array1 + " x [" + array2 + " x i32]], [" + array1 + " x [" + array2 + " x i32]]* %x" + array_start + ", i32 0, i32 " + second_i32 + ", i32 " + i + " ;\n");
                                locate = index;
                                index++;
                            }
                            visit(ctx.initVal(i));
                            ir_code.add("    store i32 %x" + (index - 1) + ", i32* %x" + locate + "\n");
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Void visitFuncDef(lab7Parser.FuncDefContext ctx) {
        layer=0;
        if (ctx.children.size() == 5) {
            String functype="";
            if (ctx.funcType().getText().equals("void")){
                functype="void";
            }else if (ctx.funcType().getText().equals("int")){
                functype="i32";
            }
            visit(ctx.ident());
            ir_code.add("define dso_local "+functype+" @"+this.nowidentName+"()\n");
            Symbol symbol=new Symbol(this.nowidentName,"@"+this.nowidentName,layer);
            symbol.type=functype+"_func";
            symbolsstack.add(symbol);
//            System.out.println("define dso_local i32 @main()");
            ir_code.add("{\n");
            layer++;
            visit(ctx.block());
            ir_code.add("}\n");
            return null;
        }else if (ctx.children.size()==6){
            String functype="";
            if (ctx.funcType().getText().equals("void")){
                functype="void";
            }else if (ctx.funcType().getText().equals("int")){
                functype="i32";
            }
            visit(ctx.ident());
            ir_code.add("define dso_local "+functype+" @"+this.nowidentName+"(");

            Symbol symbol=new Symbol(this.nowidentName,"@"+this.nowidentName,layer);
            symbol.type=functype+"_func";
            symbolsstack.add(symbol);
            func_symbol=symbolsstack.get(symbolsstack.size()-1);
            layer=1;
            visit(ctx.funcFParams());
            ir_code.add(")\n");
            ir_code.add("{\n");
            for (int i = 0; i < symbol.params.size(); i++) {
                if (symbol.params.get(i).type==0)
                {
                    ir_code.add("    %x"+index+"=alloca i32\n");
                    ir_code.add("    store i32 "+symbol.params.get(i).new_name+",i32* %x"+index+"\n");
                    index++;
                    Symbol symbol1=new Symbol(symbol.params.get(i).old_name,"%x"+(index-1),layer);
                    symbol1.type="func_var";
                    symbolsstack.add(symbol1);
                }else if (symbol.params.get(i).type==1){
                    //将参数进行重新声明方便赋值
                    ir_code.add("    %x"+index+"=alloca i32*\n");
                    ir_code.add("    store i32* "+symbol.params.get(i).new_name+",i32* * %x"+index+"\n");
                    index++;
                    Symbol symbol1=new Symbol(symbol.params.get(i).old_name,"%x"+(index-1),layer);
                    symbol1.type="func_one_array";
                    symbolsstack.add(symbol1);
                }else if (symbol.params.get(i).type==2){
                    //将参数进行重新声明方便赋值
                    ir_code.add("    %x"+index+"=alloca "+symbol.params.get(i).paramtype+"\n");
                    ir_code.add("    store "+symbol.params.get(i).paramtype+" "+symbol.params.get(i).new_name+","+symbol.params.get(i).paramtype+" * %x"+index+"\n");
                    index++;
                    Symbol symbol1=new Symbol(symbol.params.get(i).old_name,"%x"+(index-1),layer);
                    symbol1.type="func_two_array";
                    symbol1.array_long_for_two=this.nownumber;
                    symbolsstack.add(symbol1);
                }
            }
            visit(ctx.block());
            if (functype.equals("void")){
                ir_code.add("    ret void\n");
            }
            ir_code.add("}\n");
        }else {
            ir_code.add("funcdef error\n");
            System.out.println("funcdef error");
            System.exit(1);
        }

        return null;
    }

    @Override
    public Void visitFuncFParams(lab7Parser.FuncFParamsContext ctx) {
        for (int i = 0; i < ctx.children.size()/2+1; i++) {
            visit(ctx.funcFParam(i));
            if (i!=ctx.children.size()/2){
                ir_code.add(",");
            }
        }
        return null;
    }

    @Override
    public Void visitFuncFParam(lab7Parser.FuncFParamContext ctx) {
        if (ctx.children.size()==2){
            visit(ctx.ident());
            ir_code.add("i32 %x"+index);
            //将参数放入函数的symbol里去
            Funcfparam funcfparam=new Funcfparam(this.nowidentName,"%x"+index,"i32",0);
            func_symbol.params.add(funcfparam);
            index++;
        }else if (ctx.children.size()==4){
            visit(ctx.ident());
            ir_code.add("i32* %x"+index);
            Funcfparam funcfparam=new Funcfparam(this.nowidentName,"%x"+index,"i32*",1);
            func_symbol.params.add(funcfparam);
            index++;

        }else if (ctx.children.size()>=6){
            visit(ctx.ident());
            no_add=1;
            visit(ctx.exp(0));
            no_add=0;
            ir_code.add("["+this.nownumber+" x i32]* %x"+index);
            Funcfparam funcfparam=new Funcfparam(this.nowidentName,"%x"+index,"["+this.nownumber+" x i32]*",2);
            func_symbol.params.add(funcfparam);
            index++;
        }
        return null;
    }

    @Override
    public Void visitBlock(lab7Parser.BlockContext ctx) {
//        System.out.println("{");

        if (ctx.children.size() >= 2) {
            for (int i = 0; i < ctx.children.size() - 2; i++) {
                visit(ctx.blockItem(i));
            }
//            System.out.println("}");
        } else {

            System.out.println("block error");
            ir_code.add("block error");
            System.exit(1);
        }
        for (int i = symbolsstack.size() - 1; i >= 0; i--) {
            if (symbolsstack.get(i).layer == layer) {
//                System.out.println("已删除"+symbolsstack.get(i).old_name+"  "+layer);
                symbolsstack.remove(i);
            }
        }
        layer--;
        return null;
    }

    @Override
    public Void visitBlockItem(lab7Parser.BlockItemContext ctx) {
        return super.visitBlockItem(ctx);
    }

    @Override
    public Void visitStmt(lab7Parser.StmtContext ctx) {
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
        } else if (ctx.children.size() == 1 && !ctx.getChild(0).toString().equals(";")) {
            layer++;
            visit(ctx.block());
            System.out.println("use block");
        } else if (ctx.getChild(0).getText().equals("continue")) {
            ir_code.add("continue");
            System.out.println("use continue");
        } else if (ctx.getChild(0).getText().equals("break")) {
            ir_code.add("break");
            System.out.println("use break");
        } else if (ctx.children.size() == 2 && ctx.getChild(1).toString().equals(";")) {
            visit(ctx.exp());
            System.out.println("use exp");
        } else if (ctx.children.size() == 3) {
            visit(ctx.exp());
//            System.out.println("    ret i32 " + this.nowIRName);
            ir_code.add("    ret i32 " + this.nowIRName + "\n");
        } else if (ctx.children.size() >= 5) {
//            System.out.println(ctx.children.get(0).getText());
            if (ctx.children.get(0).getText().equals("if")) {
                if_alone = 0;
                int cond = 0, label1 = 0, label2 = 0, label3 = 0;
                this.nowType = "";
                visit(ctx.cond());
                //等待返回的
                cond = index - 1;
                //不能用trunc直接截取
//                if (this.nowType.equals("i32")){
//                    ir_code.add("    %x"+index+" = trunc i32 %x"+cond+" to i1\n");
//                    cond=index;
//                    index++;
//                }
                label1 = index;
                label2 = index + 1;
                index += 2;
//                System.out.println("    br i1 %7,label %8, label %10");
                ir_code.add("    br i1 %x" + cond + ",label %x" + label1 + ", label %x" + label2);
                if (ctx.children.size() == 5) {
                    ir_code.add("\nx" + label1 + ":\n");
                    visit(ctx.stmt(0));
                    ir_code.add("    br label %x" + label2 + "\n");
                    ir_code.add("\nx" + label2 + ":\n");
                } else {
                    label3 = index++;
                    ir_code.add("\nx" + label1 + ":\n");
                    visit(ctx.stmt(0));
                    ir_code.add("    br label %x" + label3 + "\n");
                    ir_code.add("\nx" + label2 + ":\n");
                    visit(ctx.stmt(1));
                    ir_code.add("    br label %x" + (label3) + "\n");
                    ir_code.add("\nx" + label3 + ":\n");
                }
                this.nowIRName = "%x" + (index - 1);
            } else if (ctx.children.get(0).getText().equals("while")) {
                System.out.println("use while");
                int cond = 0, label1 = 0, label2 = 0, start = 0, whilestart = 0, whileend = 0;
                whilestart = ir_code.size() - 1;
                ir_code.add("    br label %x" + (index) + "\n");
                ir_code.add("\nx" + index + ":\n");
                start = index;
                index++;
                visit(ctx.cond());
                cond = index - 1;
                label1 = index;
                label2 = index + 1;
                index += 2;
                ir_code.add("    br i1 %x" + cond + ",label %x" + label1 + ", label %x" + label2 + "\n");
                ir_code.add("\nx" + label1 + ":\n");
                visit(ctx.stmt(0));
                ir_code.add("    br label %x" + start + "\n");
                ir_code.add("\nx" + label2 + ":\n");
                whileend = ir_code.size() - 1;
                for (int i = whilestart; i <= whileend; i++) {
                    if (ir_code.get(i).equals("break")) {
                        ir_code.set(i, "    br label %x" + label2 + "\n");
                    } else if (ir_code.get(i).equals("continue")) {
                        ir_code.set(i, "    br label %x" + start + "\n");
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Void visitRelExp(lab7Parser.RelExpContext ctx) {
        if (ctx.children.size() == 1) {
            visit(ctx.addExp());
            if (if_alone == 0) {
                ir_code.add("    %x" + index + " = icmp ne i32 %x" + (index - 1) + ", 0\n");
                this.nowIRName = "%x" + index;
                index++;
            }
        } else if (ctx.children.size() == 3) {
            if_alone = 1;
            String left = "", right = "";
            visit(ctx.relExp());
            left = this.nowIRName;
            visit(ctx.addExp());
            right = this.nowIRName;
            if (ctx.getChild(1).getText().equals(">")) {
                ir_code.add("    %x" + (index++) + " = icmp sgt i32 " + left + ", " + right + "\n");
            } else if (ctx.getChild(1).getText().equals(">=")) {
                ir_code.add("    %x" + (index++) + " = icmp sge i32 " + left + ", " + right + "\n");
            } else if (ctx.getChild(1).getText().equals("<")) {
                ir_code.add("    %x" + (index++) + " = icmp slt i32 " + left + ", " + right + "\n");
            } else if (ctx.getChild(1).getText().equals("<=")) {
                ir_code.add("    %x" + (index++) + " = icmp sle i32 " + left + ", " + right + "\n");
            }
            this.nowIRName = "%x" + (index - 1);
            this.nowType = "i1";
        } else {
            System.out.println("relexp");
            System.exit(1);
        }
        return null;
    }

    @Override
    public Void visitEqExp(lab7Parser.EqExpContext ctx) {
        if (ctx.children.size() == 1) {
            visit(ctx.relExp());
        } else if (ctx.children.size() == 3) {
            if_alone = 1;
            String left = "", right = "";
            visit(ctx.eqExp());
            left = this.nowIRName;
            visit(ctx.relExp());
            right = this.nowIRName;
            if (ctx.getChild(1).getText().equals("==")) {
                ir_code.add("    %x" + (index++) + " = icmp eq i32 " + left + ", " + right + "\n");
            } else if (ctx.getChild(1).getText().equals("!=")) {
                ir_code.add("    %x" + (index++) + " = icmp ne i32 " + left + ", " + right + "\n");
            }
            this.nowIRName = "%x" + (index - 1);
            this.nowType = "i1";
        } else {
            System.out.println("eqexp error");
            System.exit(1);
        }
        return null;
    }

    @Override
    public Void visitLAndExp(lab7Parser.LAndExpContext ctx) {
        if (ctx.children.size() == 1) {
            visit(ctx.eqExp());
        } else if (ctx.children.size() == 3) {
            if_alone = 1;
            String left = "", right = "";
            visit(ctx.lAndExp());
            left = this.nowIRName;
            if_alone=0;
            visit(ctx.eqExp());
            right = this.nowIRName;
            ir_code.add("    %x" + (index) + "= and i1 " + left + "," + right + ";\n");
            index++;
            this.nowIRName = "%x" + (index - 1);
            this.nowType = "i1";
        } else {
            System.out.println("landexp error");
            System.exit(1);
        }
        return null;
    }

    @Override
    public Void visitLOrExp(lab7Parser.LOrExpContext ctx) {
        if (ctx.children.size() == 1) {
            if_alone = 0;
            visit(ctx.lAndExp());
        } else if (ctx.children.size() == 3) {
            if_alone = 1;
            String left = "", right = "";
            visit(ctx.lOrExp());
            left = this.nowIRName;
            if_alone = 0;
            visit(ctx.lAndExp());
            right = this.nowIRName;
            ir_code.add("    %x" + (index) + "= or i1 " + left + "," + right + ";\n");
            index++;
            this.nowIRName = "%x" + (index - 1);
            this.nowType = "i1";
        } else {
            System.out.println("lorexp error");
            System.exit(1);
        }
        return null;
    }

    @Override
    public Void visitLVal(lab7Parser.LValContext ctx) {
        visit(ctx.ident());
        int flag1 = 0;
        Symbol tmp_symbol=new Symbol();
        for (Symbol symbol : symbolsstack) {
            if (symbol.old_name.equals(this.nowidentName)) {
                this.nowIRName = symbol.new_name;
                tmp_symbol=symbol;
                flag1 = 1;
            }
        }
        if (flag1 == 0) {
            System.out.println("字符表中不存在字符" + this.nowidentName);
            ir_code.add("字符表中不存在字符" + this.nowidentName + "\n");
            System.exit(1);
        }
        System.out.println(for_array_addr+"hhh"+tmp_symbol.old_name+tmp_symbol.type);
        if (for_array_addr==2){
            //在int内使用函数的时候传参前的规范化
            if (tmp_symbol.type.equals("one_array")){
                ir_code.add("    %x"+index+" = getelementptr ["+tmp_symbol.array1+" x i32], ["+tmp_symbol.array1+" x i32]* "+tmp_symbol.new_name+", i32 0, i32 0\n");
                this.nowIRName="%x"+index;
                no_load=1;
                index++;
            }else if (tmp_symbol.type.equals("two_array")){
                visit(ctx.exp(0));
                //    %8 = getelementptr [2 x [3 x i32]], [2 x [3 x i32]]* %1, i32 0, i32 0
                ir_code.add("    %x"+index+" = getelementptr ["+tmp_symbol.array1+" x ["+tmp_symbol.array2+" x i32]], ["+tmp_symbol.array1+" x ["+tmp_symbol.array2+" x i32]]* "+tmp_symbol.new_name+", i32 0, i32 %x"+(index-1)+",i32 0\n");
                no_load=1;
                this.nowIRName="%x"+index;
                index++;
            }
        }else if (for_array_addr==3){
            if (tmp_symbol.type.equals("two_array")){
                ir_code.add("    %x" + index + " = getelementptr [" + tmp_symbol.array1 + " x [" + tmp_symbol.array2 + " x i32]], [" + tmp_symbol.array1 + " x [" + tmp_symbol.array2 + " x i32]]* " + tmp_symbol.new_name + ", i32 0, i32 0;\n");
                this.nowIRName="%x"+index;
                index++;
                no_load=1;
            }
        }else if (ctx.children.size() == 1&&(tmp_symbol.type.equals("var")||tmp_symbol.type.equals("func_var"))) {
            //处理变量
            if (layer == 0) {
                for (int i = 0; i < symbolsstack.size(); i++) {
                    if (!symbolsstack.get(i).isconst) {
                        System.out.println("宏观变量赋值时存在变量");
                        System.exit(1);
                    }
                }
            }
//            if (tmp_symbol.type.equals("func_var")){
//                no_load=1;
//            }
        } else {
            //处理数组
            if (ctx.children.size() == 4) {
                //处理一维数组
                Symbol symbol1 = new Symbol();
                int flag2 = 0, tmp_array1 = 0;
                for (Symbol symbol : symbolsstack) {
                    if (symbol.old_name.equals(this.nowidentName)) {
                        this.nowIRName = symbol.new_name;
                        symbol1 = symbol;
                        flag2 = 1;
                    }
                }
                if (flag2 == 0) {
                    System.out.println("字符表中不存在字符" + this.nowidentName);
                    ir_code.add("字符表中不存在字符" + this.nowidentName + "\n");
                    System.exit(1);
                }
                if (symbol1.array2 != 0) {
                    System.out.println(symbol1.old_name + "为二维数组");
                    System.exit(1);
                }
                visit(ctx.exp(0));
                if (symbol1.type.equals("func_one_array")){
                    //%5 = load i32* , i32* * %3
                    ir_code.add("    %x"+index+" = load i32* , i32* * "+symbol1.new_name+"\n");
                    index++;
                    ir_code.add("    %x"+index+" = getelementptr i32, i32* %x"+(index-1)+", i32 "+this.nowIRName+"\n");
                }else {
                    ir_code.add("    %x" + index + " = getelementptr [" + symbol1.array1 + " x i32], [" + symbol1.array1 + " x i32]* " + symbol1.new_name + ", i32 0, i32 %x" + (index - 1) + "\n");
                }
                index++;
                this.nowIRName = "%x" + (index - 1);
            } else if (ctx.children.size() == 7) {
                //处理二维数组
                Symbol symbol1 = new Symbol();
                int flag2 = 0, tmp_array1 = 0, tmp_array2 = 0;
                for (Symbol symbol : symbolsstack) {
                    if (symbol.old_name.equals(this.nowidentName)) {
                        this.nowIRName = symbol.new_name;
                        symbol1 = symbol;
                        flag2 = 1;
                    }
                }
                if (flag2 == 0) {
                    System.out.println("字符表中不存在字符" + this.nowidentName);
                    ir_code.add("字符表中不存在字符" + this.nowidentName + "\n");
                    System.exit(1);
                }
//                if (symbol1.array2 == 0) {
//                    System.out.println(symbol1.old_name + "为一维数组");
//                    System.exit(1);
//                }
                //%1 = getelementptr [5 x [4 x i32]], [5 x [4 x i32]]* @a, i32 0, i32 2, i32 3
                visit(ctx.exp(0));
                tmp_array1 = index - 1;
                visit(ctx.exp(1));
                tmp_array2 = index - 1;
                if (symbol1.type.equals("func_two_array")){
                    //    %18 = load [3 x i32]*, [3 x i32]* * %5
                    ir_code.add("    %x"+index+" = load ["+symbol1.array_long_for_two+" x i32]* , [ " + symbol1.array_long_for_two + " x i32]* * "+symbol1.new_name+"\n");
                    index++;
                    ir_code.add("    %x" + index + " = getelementptr [" + symbol1.array_long_for_two + " x i32], [" + symbol1.array_long_for_two + " x i32]* %x" + (index-1) + ", i32 %x" + tmp_array1 + ", i32 %x" + tmp_array2 + "\n");
                    index++;
                    this.nowIRName = "%x" + (index - 1);
                }else {

                    ir_code.add("    %x" + index + " = getelementptr [" + symbol1.array1 + " x [" + symbol1.array2 + " x i32]], [" + symbol1.array1 + " x [" + symbol1.array2 + " x i32]]* " + symbol1.new_name + ", i32 0, i32 %x" + tmp_array1 + ", i32 %x" + tmp_array2 + "\n");
                    index++;
                    this.nowIRName = "%x" + (index - 1);
                }
            }
        }


        return null;
    }

    @Override
    public Void visitAddExp(lab7Parser.AddExpContext ctx) {
        if (ctx.children.size() == 1) {// addexp->mulexp
            visit(ctx.mulExp());
        } else if (ctx.children.size() == 3)// addExp ('+' | '-') mulExp
        {
            if (layer == 0||no_add==1) {
                int lhs1 = 0, rhs1 = 0, result1 = 0;
                visit(ctx.addExp());
                lhs1 = this.nownumber;
                visit(ctx.mulExp());
                rhs1 = this.nownumber;
                if (ctx.getChild(1).toString().equals("+")) {
                    result1 = lhs1 + rhs1;
                } else {
                    result1 = lhs1 - rhs1;
                }
                this.nownumber = result1;
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
                this.nowType="i32";
            }
        } else {
            System.out.println("add exp wrong");
            ir_code.add("add exp wrong\n");
            System.exit(1);
        }
        return null;
    }

    @Override
    public Void visitUnaryExp(lab7Parser.UnaryExpContext ctx) {
        if (ctx.children.size() == 1) {
            visit(ctx.primaryExp());
        } else if (ctx.children.size() == 2) {
            visit(ctx.unaryExp());
            //"+"没有改变，故而不再转换
            if (ctx.unaryOp().getText().equals("-")) {
//                System.out.println("    %" + (index) + " = sub i32 " + 0 + ", %" + (index - 1));
                this.nownumber= -this.nownumber;
                if (layer!=0){
                    ir_code.add("    %x" + (index) + " = sub i32 " + 0 + ", %x" + (index - 1) + "\n");
                    index++;
                }

            } else if (ctx.unaryOp().getText().equals("!")) {
                ir_code.add("    %x" + (index) + " = icmp eq i32 %x" + (index - 1) + ", 0\n");
                index++;
                ir_code.add("    %x" + index + " = zext i1 %x" + (index - 1) + " to i32\n");
                index++;
            }
            this.nowIRName = "%x" + (index - 1);
            this.nowType = "i32";

        } else if (ctx.children.size() >= 3) {
            visit(ctx.ident());
            //返回函数名
            String fun_name = this.nowidentName;
            func_param_add.clear();
            if (fun_name.equals("getint")) {
                if (fun_decl[0] == 0) {
                    ir_code.add(0, "declare i32 @getint()\n");
                    fun_decl[0] = 1;
                }
                ir_code.add("    %x" + index + " = call i32 @getint()\n");
                index++;
                this.nowIRName = "%x" + (index - 1);
            } else if (fun_name.equals("putint")) {
                func_param_add.add(1);
                visit(ctx.funcRParams());
                if (fun_decl[1] == 0) {
                    ir_code.add(0, "declare void @putint(i32)\n");
                    fun_decl[1] = 1;
                }
                ir_code.add("    call void @putint(i32 " + this.nowIRName + ")\n");
            } else if (fun_name.equals("getch")) {
                if (fun_decl[2] == 0) {
                    ir_code.add(0, "declare i32 @getch()\n");
                    fun_decl[2] = 1;
                }
                ir_code.add("    %x" + index + " = call i32 @getch()\n");
                index++;
                this.nowIRName = "%x" + (index - 1);
            } else if (fun_name.equals("putch")) {
                func_param_add.add(1);
                visit(ctx.funcRParams());
                if (fun_decl[3] == 0) {
                    ir_code.add(0, "declare void @putch(i32)\n");
                    fun_decl[3] = 1;
                }
                ir_code.add("    call void @putch(i32 " + this.nowIRName + ")\n");
            } else if (fun_name.equals("getarray")) {
                func_param_add.add(2);
                visit(ctx.funcRParams());
                if (fun_decl[4] == 0) {
                    ir_code.add(0, "declare i32 @getarray(i32*)\n");
                    fun_decl[4] = 1;
                }
                //%13 = call i32 @getarray(i32* %12)
                ir_code.add("    %x" + index + " = call i32 @getarray(i32* "+this.nowIRName+")\n");
                index++;
                this.nowIRName = "%x" + (index - 1);
            } else if (fun_name.equals("putarray")) {
                func_param_add.add(1);
                func_param_add.add(2);
                visit(ctx.funcRParams());
                if (fun_decl[5] == 0) {
                    ir_code.add(0,"declare void @putarray(i32,i32*)\n");
                    fun_decl[5] = 1;
                }
                //putarr函数尚未完成
                ir_code.add("    call void @putarray(i32 "+func_param.get(0)+", i32* "+func_param.get(1)+")\n");

            }else {
                int flag2=0;
                for (Symbol symbol : symbolsstack) {
                    if (fun_name.equals(symbol.old_name)) {
                        for (int i = 0; i < symbol.params.size(); i++) {
                            func_param_add.add(symbol.params.get(i).type+1);
                            System.out.println(symbol.params.get(i).type);
                        }
                        System.out.println(symbol.old_name+"  "+symbol.type);
                        flag2=1;
                        if (symbol.type.equals("i32_func")&&symbol.params.size()==0){
                            //有返回值但无参数
                            //    %2 = call i32 @func1()
                            ir_code.add("    %x"+index+" = call i32 "+symbol.new_name+"()\n");
                            this.nowIRName="%x"+index;
                            index++;
                        }else if (symbol.type.equals("i32_func")){
                            //有返回值有参数
                            visit(ctx.funcRParams());
                            ir_code.add("    %x"+index+" = call i32 "+symbol.new_name+"(");
                            this.nowIRName="%x"+index;
                            index++;
                            if (symbol.params.size()!=func_param.size()){
                                System.out.println("函数参数使用出错，该函数为"+symbol.old_name);
                                System.exit(1);
                            }
                            for (int i = 0; i < func_param.size(); i++) {
                                ir_code.add(symbol.params.get(i).paramtype+" "+func_param.get(i));
                                if (i!=func_param.size()-1){
                                    ir_code.add(",");
                                }
                            }
                            ir_code.add(")\n");
                        }else if (symbol.type.equals("void_func")){
                            //无返回值但有参数
                            visit(ctx.funcRParams());
                            ir_code.add("    call void "+symbol.new_name+"(");
                            System.out.println(symbol.params.size()+"  "+func_param.size());
                            if (symbol.params.size()!=func_param.size()){
                                System.out.println("函数参数使用出错，该函数为"+symbol.old_name);
                                System.exit(1);
                            }
                            for (int i = 0; i < func_param.size(); i++) {
                                ir_code.add(symbol.params.get(i).paramtype+" "+func_param.get(i));
                                if (i!=func_param.size()-1){
                                    ir_code.add(",");
                                }
                            }
                            ir_code.add(")\n");
                        }else{
                            //无返回值无参数
                            ir_code.add("   call i32 "+symbol.new_name+"()\n");
                            this.nowIRName="%x"+index;
                            index++;
                        }
                    }
                }
                if (flag2==0){
                    System.out.println("不能调用现在尚未声明的函数");
                    System.exit(1);
                }
            }
        } else {
//            ir_code.add("unaryexp error");
            System.out.println("unaryexp error");
            System.exit(1);
        }
        return null;
    }

    @Override
    public Void visitFuncRParams(lab7Parser.FuncRParamsContext ctx) {
        func_param.clear();
        for (int i = 0; i < ctx.children.size()/2+1; i++) {
            for_array_addr=func_param_add.get(i);
            visit(ctx.exp(i));
            String tmp=this.nowIRName;
            func_param.add(tmp);
        }
        for_array_addr=0;
        return null;
    }

    @Override
    public Void visitPrimaryExp(lab7Parser.PrimaryExpContext ctx) {
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
                if (layer != 0&&no_add!=1) {
//                    System.out.println("    %" + (index++) + "= add i32 0," + this.nownumber);
                    ir_code.add("    %x" + (index++) + "= add i32 0," + this.nownumber + "\n");
                    this.nowIRName = "%x" + (index - 1);
                    this.nowType = "i32";
                }

            } else {
                visit(ctx.lVal());
//                System.out.println("    %" + (index++) + " = load i32, i32* " + this.nowIRName);
                //
                if (layer != 0&&no_load ==0) {
                    ir_code.add("    %x" + (index++) + " = load i32, i32* " + this.nowIRName + "\n");
                    this.nowIRName = "%x" + (index - 1);
                    this.nowType = "i32";
                }
                no_load=0;
            }
        } else if (ctx.children.size() == 3) {
            visit(ctx.exp());
        } else {
            System.out.println("primaryexp error");
            ir_code.add("primaryexp error\n");
            System.exit(1);
        }
        return null;
    }

    @Override
    public Void visitNumber(lab7Parser.NumberContext ctx) {

        return null;
    }

    @Override
    public Void visitMulExp(lab7Parser.MulExpContext ctx) {
        if (ctx.children.size() == 1) {
            visit(ctx.unaryExp());
        } else if (ctx.children.size() == 3) {
            if (layer == 0) {
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
                this.nowType = "i32";
            }

        } else {
            System.out.println("MulExp wrong");
            ir_code.add("MulExp wrong\n");
            System.exit(1);
        }
        return null;
    }

    @Override
    public Void visitIdent(lab7Parser.IdentContext ctx) {
        this.nowidentName = "";
        for (int i = 0; i < ctx.getChildCount(); i++) {
            this.nowidentName = this.nowidentName + ctx.getChild(i).getText();
        }
        return super.visitIdent(ctx);
    }
}

