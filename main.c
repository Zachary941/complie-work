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
    if (str[i + 1] == 'e' && str[i + 2] == 't' && str[i + 3] == 'u' && str[i + 4] == 'r' && str[i + 5] == 'n' && (str[i + 6] == '(' || str[i + 6] == ' ' || str[i + 6] == '\n' || str[i + 6] == '\0' || str[i + 6] == ';'))
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
        printf("%s\n",strbuf);
    }
}

