import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Scanner;
/*
1.消除注释
2.if
*/
public class lab4_1 {
    public static void main(String[] args) throws FileNotFoundException {
        // String inputName="D:\\学习\\2021大三上学期\\编译原理\\编译原理实验\\lab4\\src\\input.txt";
        // String outputName="./output.txt";
       String inputName=args[0];
       String outputName=args[1];
       PrintStream ps = new PrintStream(outputName);
       System.setOut(ps);
        String input1 = "";
        StringBuilder input2= new StringBuilder();
        try (Scanner sc = new Scanner(new FileReader(inputName))) {
            while (sc.hasNextLine()) {
                String str=sc.nextLine()+"\n";
                input2.append(str);
            }
        }
//        input1.replace(0,input1.length(),"intmain(){//newline=10;intn;inti;intj;//m=1478;//intt;i=4;j=20;inttemp;temp=i;i=j;j=temp;putint(i);temp=10;putint(j);return0;}");
        for (int i = 0,j=0; i < input2.length(); i++,j++) {
            if (input2.charAt(i)=='/'&&input2.charAt(i+1)=='/'){
                i++;i++;
                while (input2.charAt(i)!='\n'){
                    i++;
                }
                i++;
            }else if (input2.charAt(i)=='/'&&input2.charAt(i+1)=='*'){
                i++;i++;
                while (!(input2.charAt(i)=='*'&&input2.charAt(i+1)=='/')){
                    i++;
                }
                i++;i++;
            }else if (input2.charAt(i)==' '||input2.charAt(i)=='\n'){
                continue;
            }else{
                input1+=input2.charAt(i)+"";
            }
        }
//        System.out.println(input1);
        CharStream inputStream = CharStreams.fromString(input1.toString());
        lab4Lexer lexer = new lab4Lexer(inputStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        lab4Parser parser = new lab4Parser(tokenStream);
        ParseTree tree = parser.compUnit();
        Visitor visitor = new Visitor();
        visitor.visit(tree);
        for (int i = 0; i < Visitor.ir_code.size(); i++) {
            System.out.print(Visitor.ir_code.get(i));
        }
//        System.out.println(tree.toStringTree(parser));
    }
}
