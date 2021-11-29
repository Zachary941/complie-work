import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Scanner;

public class lab4_1 {
    public static void main(String[] args) throws FileNotFoundException {
//        String inputName="D:\\学习\\2021大三上学期\\编译原理\\编译原理实验\\lab4\\src\\input.txt";
//        String outputName="./output.txt";
        String inputName=args[0];
        String outputName=args[1];
        PrintStream ps = new PrintStream(outputName);
        System.setOut(ps);
        StringBuilder input1= new StringBuilder();
        try (Scanner sc = new Scanner(new FileReader(inputName))) {
            while (sc.hasNext()) {
                String str = sc.next();
                input1.append(str);
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
//        System.out.println(tree.toStringTree(parser));
    }
}
