// main.c
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>

FILE *fp;
int size = 200;
int judgeif(char str[], int i)
{
    if (str[i + 1] == 'f' && (str[i + 2] == '(' || str[i + 2] == ' '||str[i + 2] == '\t'||str[i + 2] == '\r'||str[i + 2] == '\n'))
    {
        return 1;
    }
    else
    {
        return 0;
    }
}

int judgeelse(char str[], int i)
{
    if (str[i + 1] == 'l' && str[i + 2] == 's' && str[i + 3] == 'e' && (str[i + 4] == '(' || str[i + 4] == ' ' || str[i + 4] == '\n' || str[i + 4] == '\0'))
    {
        return 1;
    }
    else
    {
        return 0;
    }
}

int judgewhile(char str[], int i)
{
    if (str[i + 1] == 'h' && str[i + 2] == 'i' && str[i + 3] == 'l' && str[i + 4] == 'e' && (str[i + 5] == '(' || str[i + 5] == ' ' || str[i + 5] == '\n' || str[i + 5] == '\0'))
    {
        return 1;
    }
    else
    {
        return 0;
    }
}

int judgebreak(char str[], int i)
{
    if (str[i + 1] == 'r' && str[i + 2] == 'e' && str[i + 3] == 'a' && str[i + 4] == 'k' && (str[i + 5] == '(' || str[i + 5] == ' ' || str[i + 5] == '\n' || str[i + 5] == '\0' || str[i + 5] == ';'))
    {
        return 1;
    }
    else
    {
        return 0;
    }
}

int judgecontinue(char str[], int i)
{
    if (str[i + 1] == 'o' && str[i + 2] == 'n' && str[i + 3] == 't' && str[i + 4] == 'i' && str[i + 5] == 'n' && str[i + 6] == 'u' && str[i + 7] == 'e' && (str[i + 8] == '(' || str[i + 8] == ' ' || str[i + 8] == '\n' || str[i + 8] == '\0' || str[i + 8] == ';'))
    {
        return 1;
    }
    else
    {
        return 0;
    }
}

int judgereturn(char str[], int i)
{
    if (str[i + 1] == 'e' && str[i + 2] == 't' && str[i + 3] == 'u' && str[i + 4] == 'r' && str[i + 5] == 'n' && (str[i + 6] == '(' || str[i + 6] == ' ' || str[i + 6] == '\n' || str[i + 6] == '\0' || str[i + 6] == ';'|| str[i + 6] == '\t'|| str[i + 6] == '\r'))
    {
        return 1;
    }
    else
    {
        return 0;
    }
}
int main(int argc, char *argv[])
{
    int count = 0;
    int flag=0;
    // printf("%s",argv[1]);
    fp = fopen(argv[1], "r");
    char strbuf[size];
    char identbuf[size];
    memset(strbuf, 0, sizeof(strbuf));
    memset(identbuf, 0, sizeof(identbuf));
    while (1)
    {
        if (fgets(strbuf, sizeof(strbuf), fp) == 0||flag==2)
            break;
        int length = strlen(strbuf);
        int i = 0;
        while (strbuf[i] !='\0'&&i<length)
        {
            flag = 0;
            switch (strbuf[i])
            {
            case 'i':
                if (judgeif(strbuf, i))
                {
                    printf("If\n");
                    i += 2;
                    flag = 1;
                }
                // printf("%d if", i);
                break;
            case 'e':
                if (judgeelse(strbuf, i))
                {
                    printf("Else\n");
                    i += 4;
                    flag = 1;
                }
                break;
            case ' ':
                i++;
                flag = 1;
                break;
            case 'w':
                if (judgewhile(strbuf, i))
                {
                    printf("While\n");
                    i += 5;
                    flag = 1;
                }
                break;
            case 'b':
                if (judgebreak(strbuf, i))
                {
                    printf("Break\n");
                    i += 5;
                    flag = 1;
                }
                break;
            case 'c':
                if (judgecontinue(strbuf, i))
                {
                    printf("Continue\n");
                    i += 8;
                    flag = 1;
                }
                break;
            case 'r':
                if (judgereturn(strbuf, i))
                {
                    /* code */
                    printf("Return\n");
                    i += 6;
                    flag = 1;
                }
                break;
            case '=':
                if (strbuf[i + 1] == '=')
                {
                    printf("Eq\n");
                    i += 2;
                    flag = 1;
                }
                else if (strbuf[i + 1] != '=')
                {
                    printf("Assign\n");
                    i += 1;
                    flag = 1;
                }
                break;
            case ';':
                printf("Semicolon\n");
                i += 1;
                flag = 1;
                break;
            case '(':
                printf("LPar\n");
                i += 1;
                flag = 1;
                break;
            case ')':
                printf("RPar\n");
                i += 1;
                flag = 1;
                break;

            case '{':
                printf("LBrace\n");
                i += 1;
                flag = 1;
                break;
            case '}':
                printf("RBrace\n");
                i += 1;
                flag = 1;
                break;
            case '+':
                printf("Plus\n");
                i += 1;
                flag = 1;
                break;
            case '*':
                printf("Mult\n");
                i += 1;
                flag = 1;
                break;
            case '/':
                printf("Div\n");
                i += 1;
                flag = 1;
                break;
            case '<':
                printf("Lt\n");
                i += 1;
                flag = 1;
                break;
            case '>':
                printf("Gt\n");
                i += 1;
                flag = 1;
                break;   
            case '\n':
                flag=1;i++;break; 
            case '\t':
                flag=1;i++;break;
            case '\r':
                flag=1;i++;break;
            default:
                // printf("%d de", i);
                break;
            }
            if (flag == 0 && i + 1 < length)
            {
                memset(identbuf, 0, sizeof(identbuf));
                int j = 0;
                do
                {
                    identbuf[j] = strbuf[i];
                    i++;
                    j++;
                }while (strbuf[i] != ' ' && strbuf[i] != '\n'&& strbuf[i] != '\t'&& strbuf[i] != '\r'&&strbuf[i] != ';'&&strbuf[i] != ')'&&strbuf[i] != '='&&strbuf[i] != '('&&strbuf[i] != '<'&&strbuf[i] != '>'&&strbuf[i] != '}'&&strbuf[i] != '{'&&strbuf[i] != '+'&&strbuf[i] != '-'&&strbuf[i] != '*'&&strbuf[i] != '/');
                // printf("%s identbuf\n",identbuf);
                if ('0' <= identbuf[0] && '9' >= identbuf[0])
                {
                    // printf("Number(%s)\n", identbuf);flag=1;
                    int flag2=0;
                    for (int k = 0; k < strlen(identbuf); k++)
                    {
                        if ('0' > identbuf[k] || '9' < identbuf[k])
                        {
                            flag2=k;break;
                        }
                    }
                    if (flag2==0)
                    {
                        printf("Number(%s)\n", identbuf);flag=1;
                    }else{

                        printf("Number(");flag=1;
                        for (int k = 0; k < flag2; k++)
                        {
                            printf("%c",identbuf[k]);
                        }
                        printf(")\n");
                        printf("Ident(");
                        for (int k = flag2; k < strlen(identbuf); k++)
                        {
                            printf("%c",identbuf[k]);
                        }
                        printf(")\n");
                        flag=1;
                    }
                }
                else if (('a' <= identbuf[0] && 'z' >= identbuf[0])||('A' <= identbuf[0] && 'Z' >= identbuf[0])||identbuf[0]=='_')
                {
                    printf("Ident(%s)\n", identbuf);flag=1;
                    // printf("%d %d",i,length);
                }
            }
            if (flag==0)
            {
                printf("Err\n");
                flag=2;break;
            }
        }
    }
}

