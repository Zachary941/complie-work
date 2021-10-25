#include<stdio.h>
#include<string.h>
#include <ctype.h>

char c;
char strbuf[200];
char all[10000];
char exp[2000]={0};
int flag=0,f=0,k=0;
int sum=0;
int m=0,count=0;

int Exp();
int expression_value();//读入表达式，返回其值
int factor_value();//读入一个因子，并且返回其值
int term_value();//读入一项，返回其值
int expression_value(){//求一个表达式的值
    int result=term_value();//求第一项的值
    bool more=true;//有没有新的项
    while(more){
        char op=exp[m];//看一个字符，不取走。cin会从输入流里面拿走
        if(op=='+'||op=='-'){
            m++;
            int value=term_value();
            if(op=='+')
                result+=value;
            else result-=value;
        }
        else more=false;//如果是右括号，说明输入流结束。
    }
    return result;
}
int term_value(){//求一个项的值
    int result=factor_value();
    while(true){
        char op=exp[m];
        if(op=='*'||op=='/'){//如果有乘除，说明还有后续因子
            m++;//从输入中取走一个字符
            int value=factor_value();
            if(op=='*')
                result*=value;
            else result/=value;
        }
        else break;//没有因子了
    }
    return result;
}
int factor_value(){//求一个因子的值
    int result=0;
    char c=exp[m];
    if(c=='('){//因子是由左右括号和表达式组成
        m++;//把左括号扔掉
        result=expression_value();//处理表达式，
        m++;
       }
    else{//因子是一个数
        int i=0;
	    int sum=0,flag=0;
	    memset(strbuf,0,sizeof(strbuf));
	    while(isdigit(exp[m])||isalpha(exp[m])){
			strbuf[i]=exp[m];
			i++;m++;
		}
		if (strbuf[0]=='0'&&(strbuf[1]!='x'&&strbuf[1]!='X'))
		{
			//8->10
			int i=1;
			sum=0;
			for (i = 1; i<strlen(strbuf); i++)
			{
				if ('0'>strbuf[i]||strbuf[i]>'7')
				{
					flag=1;return 0;
				}
				sum=sum*8+ (strbuf[i]-'0');
			}
		}else if ((strbuf[0]=='0'&&strbuf[1]=='x')||(strbuf[0]=='0'&&strbuf[1]=='X'))
		{
			//16->10
			int i=2;
			sum=0;
			for (i = 2; i<strlen(strbuf); i++)
			{
				if (isdigit(strbuf[i]))
				{
					sum=sum*16+ (strbuf[i]-'0');
				}else if(isalpha(strbuf[i])){
					sum=sum*16+ (toupper(strbuf[i])-'A'+10);
				}			
			}
		}else{
			int i;
			sum=0;
			for (i = 0; i<strlen(strbuf); i++)
			{
				if (!isdigit(strbuf[i]))
				{
					flag=1;return 0;
				}
				sum=sum*10+ (strbuf[i]-'0');
			}
		}
		result=sum;
    }
    return result;
}
int get_true(const char *s){
	int i=0;
	while(all[k]==' '||all[k]=='\t'||all[k]=='\n'||all[k]=='\r'){
		k++;
	}
	for(i=0;i<strlen(s);i++,k++){
		if(all[k]!=s[i]){
			flag=1;
		}
	}
	while(all[k]==' '||all[k]=='\t'||all[k]=='\n'||all[k]=='\r'){
		k++;
	}
	return 0;
}

int Number(){
	//getsym();
	int i=0;
	while(all[k]==' '||all[k]=='\t'||all[k]=='\n'||all[k]=='\r'){
		k++;
	}
	while(isdigit(all[k])||isalpha(all[k])){
		strbuf[i]=all[k];
		i++;k++;
	}
	if (strbuf[0]=='0'&&(strbuf[1]!='x'&&strbuf[1]!='X'))
	{
		//8->10
		int i=1;
		sum=0;
		for (i = 1; i<strlen(strbuf); i++)
		{
			if ('0'>strbuf[i]||strbuf[i]>'7')
			{
				flag=1;return 0;
			}
			sum=sum*8+ (strbuf[i]-'0');
		}
	}else if ((strbuf[0]=='0'&&strbuf[1]=='x')||(strbuf[0]=='0'&&strbuf[1]=='X'))
	{
		//16->10
		int i=2;
		sum=0;
		for (i = 2; i<strlen(strbuf); i++)
		{
			if (isdigit(strbuf[i]))
			{
				sum=sum*16+ (strbuf[i]-'0');
			}else if(isalpha(strbuf[i])){
				sum=sum*16+ (toupper(strbuf[i])-'A'+10);
			}			
		}
	}else{
		int i;
		sum=0;
		for (i = 0; i<strlen(strbuf); i++)
		{
			if (!isdigit(strbuf[i]))
			{
				flag=1;return 0;
			}
			sum=sum*10+ (strbuf[i]-'0');
		}
	}
	while(all[k]==' '||all[k]=='\t'||all[k]=='\n'||all[k]=='\r'){
		k++;
	}
}
int Primary(){
	if(all[k]=='('){
		get_true("(");
		Exp();
		get_true(")");
	}else if(isdigit(all[k])){
		Number();
	}else{
		flag=1;return 0;
	}
}
int UnaryOp(){
	if(all[k]=='+'){
		get_true("+");
	}else if(all[k]=='-'){
		get_true("-");
	}else{
		flag=1;return 0;
	}
}
int UnaryExp(){
	if(all[k]=='('||isdigit(all[k])){
		Primary();
	}else if(all[k]=='+'||all[k]=='-'){
		UnaryOp();
		UnaryExp();
	}else{
		flag=1;return 0;
	}
}
int MulExp(){
	UnaryExp();
	while(all[k]=='*'||all[k]=='/'||all[k]=='%'){
		if(all[k]=='*'){
			get_true("*");
		}else if(all[k]=='/'){
			get_true("/");
		}else{
			get_true("%");
		}
		UnaryExp();
	}
	
}
int AddExp(){
	MulExp();
	while(all[k]=='+'||all[k]=='-'){
		if(all[k]=='+'){
			get_true("+");
		}else{
			get_true("-");
		}
		MulExp();
	}
	
}
int Exp(){
	AddExp();
}

int Stmt(){
	get_true("return"); 
	int l=k;
	int m=0;
	while(all[l]!=';'&&all[l]!='\n'){
			if(all[l]==' ') {
				l++;continue;
			}else if(all[l]=='+'||all[l]=='-'){
				count=0;
				while(all[l]=='-'||all[l]=='+'){
					if(all[l]=='-'){
						count++;
					}
					l++;
				}
				if(count%2==1){
					exp[m]='-';m++;
				}else{
					exp[m]='+';m++;
				}
			}else{
				exp[m]=all[l];
				l++;m++;
			}
		}
	Exp(); 

	get_true(";");
	return 0;
}

int Block(){
	get_true("{");
	Stmt();
	get_true("}");
	return 0;
}

int Ident(){
	get_true("main");
	return 0;
}

int FuncType(){
	get_true("int");
	return 0;
}


int FuncDef(){
	FuncType();
	Ident();
	get_true("(");
	get_true(")");
	Block();
	return 0;
}

int CompUnit(){
	FuncDef();
	return 0;
}

int out(){
	sum=expression_value();
	printf("define dso_local i32 @main(){\n");
    printf("\tret i32 %d\n",sum);
	printf("}\n");
	return 0;
}
int main(){
	int k=0,j=0,p=0;
	char first[100000];
	while((c=getchar())!=EOF){
		first[k]=c;k++;
	}
	for(j=0;j<strlen(first);j++,p++){
		
		if(first[j]=='/'&&first[j+1]=='/'){
			j++;j++;
			while(first[j]!='\n'){
				j++;
			}
			j++;
		}else if(first[j]=='/'&&first[j+1]=='*'){
			j++;j++;
			while(!(first[j]=='*'&&first[j+1]=='/')){
				j++;
				if(j>strlen(first)){
					return 1;
				}
			}
			j++;j++;
		}
		all[p]=first[j];
	}
	k=0;
	CompUnit();
	if (flag==1)
	{
		return 1;
	}
	out();
	return 0;
}
