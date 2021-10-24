#include<stdio.h>
#include<string.h>
#include <ctype.h>

char c;
char strbuf[200];
char all[10000];
/*
1.int
2.main
3.(
4.)
5.{
6.}
7.return
8.number
*/
int flag=0,f=0,k=0;
int sum=0;
int count=0;
//int getsym(){
//	int i=0;
//	memset(strbuf, 0, sizeof strbuf);
//	c=all[k];k++;
//	while(c==' '&&c=='\t'&&c=='\n'&&c=='\r'){
//		c=all[k];k++;
//	}
//	while(c!=' '&&c!='\t'&&c!='\n'&&c!='\r'){
//		if(isdigit(c)||isalpha(c))
//		{
//			strbuf[i]=c;i++;
//		}
//		if(c=='('||c==')'||c=='{'||c=='}'||c==';')
//		{
//			if(i==0){
//				strbuf[i]=c;break;
//			}else{
//				k--;break;
//			}
//		}
//		c=all[k];k++;
//	}
//	printf(" %s-",strbuf);	
//	return 0;
//}

int get_true(char *s){
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
}
int Primary(){
	if(all[k]=='('){
		get_true("(");
		Exp();
		get_true(")");
	}else{
		Number();
	}
}
int UnaryOp(){
	if(all[k]=='+'){
		get_true("+");
	}else if(all[k]=='-'){
		get_true("-");
		count++;
	}
}
int UnaryExp(){
	if(all[k]=='('||isdigit(all[k])){
		Primary();
	}else if(all[k]=='+'||all[k]=='-'){
		UnaryOp();
		UnaryExp();
	}
}
int MulExp(){
	UnaryExp();
}
int AddExp(){
	MulExp();
}
int Exp(){
	AddExp();
}

int Stmt(){
	get_true("return"); 
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
	printf("define dso_local i32 @main(){\n");
	if(count%2==1){
		sum=-sum;
	}
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
