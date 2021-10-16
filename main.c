#include<stdio.h>
#include<string.h>
#include <ctype.h>

char c;
char strbuf[200];
char all[10000];
int main(){
	int k=0;
	while((c=getchar())!=EOF){
		all[k]=c;k++;
	}
	printf("%s",all);
	return 0;
}
