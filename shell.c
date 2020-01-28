#include <sys/wait.h>
#include <curses.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>

int main(int argc, char* argv[], char* envp[]){
        int homeFD = open(getenv("HOME"), O_DIRECTORY | O_RDONLY);

        pid_t pid;
        int* stat = NULL;

        char* clear[]={"clear", NULL};

        if((pid = fork()) <  0){
                printf("Fork Error\n");
                return -1;
        }
        else if(pid == 0){
                execvp(clear[0], clear);
        }
        else{
                wait(stat);
        }

        char* commandfile = strcat(getenv("HOME"), "/command.txt");

        int fd = creat(commandfile, 0666);
        close(fd);

        fchdir(homeFD);

        while(1){
                int n;

                char* shellTag = "1730sh:";
                if((write(STDOUT_FILENO, shellTag, 7)) == -1){
                        printf("Write Error 1\n");
                        return -1;
                }

                char dir[200];
                getcwd(dir, 200);

                char buffer[1];

                for(int k = 0; k < 200; k++){
                        buffer[0] = dir[k];

                        if(buffer[0] == 0){
                                break;
                        }

                        if((write(STDOUT_FILENO, buffer, 1)) == -1){
                                printf("Write Error 2\n");
                                return -1;
                        }
                }

                char* endTag = "$ ";
                if((write(STDOUT_FILENO, endTag, 2)) == -1){
                        printf("Write Error 3\n");
                        return -1;
                }

                char buff[1] = {'%'};

                fd = open(commandfile, O_RDWR | O_TRUNC, 0666);

                while(buff[0] != '\n'){
                        n = read(STDIN_FILENO, buff, 1);
                        if(buff[0] == '\n'){
                                break;
                        }
                                if((write(fd, buff, n)) == -1){
                                        printf("Write Error 4\n");
                                        return -1;
                                }
                        if(n == -1){
                                printf("Read Error\n");
                                return -1;
                        }
                }

                int count = 1;
                char buf[1];
                lseek(fd, 0, SEEK_SET);

                while((n = read(fd, buf, 1)) > 0){
                        if(buf[0] == ' '){
                                count++;
                        }
                }
                if(n == -1){
                        printf("Read Error\n");
                }

                char* command[count+1];

                int length = lseek(fd, 0, SEEK_END);

                char commandLine[length];

                lseek(fd, 0, SEEK_SET);

                read(fd, commandLine, length);

                char* pch;

                pch = strtok(commandLine, " ");

                command[0] = pch;
                int i = 1;

                while(pch != NULL){
                        command[i] = strtok(NULL, " ");
                        i++;
                }

                command[i] = NULL;

                close(fd);

                if(strcmp(command[0], "exit") == 0){
                        break;
                }

                if(strcmp(command[0], "cd") == 0){
                        if(command[1] == NULL){
                                fchdir(homeFD);
                        }
                        else{
                                int newFD = open(command[1], O_DIRECTORY | O_RDONLY);
                                if((fchdir(newFD)) == -1){
                                        printf("Error changing directories\n");
                                }
                                close(newFD);
                        }
                }

                if(strcmp(command[0], "export") == 0){
                        if(command[1] == NULL || command[2] != NULL){
                                printf("Invalid export syntax. Please use form: $ export NAME=VALUE\n");
                                return -1;
                        }
                        else{
                                char name[100];
                                char value[100];
                                int equalsFound = 0;
                                int q = 0;

                                for(int t = 0; t < 100; t++){
                                        if(command[1][t] == '\0'){
                                                break;
                                        }

                                        if(command[1][t] == '='){
                                                equalsFound = 1;
                                        }
                                        else if(equalsFound == 0){
                                                name[q] = command[1][t];
                                                q++;
                                        }
                                        else{
                                                value[t - (q+1)] = command[1][t];
                                        }
                                }
                                setenv(name, value, 1);
                        }
                }

                if((pid = fork()) < 0){
                        printf("Fork Error\n");
                        return -1;
                }
                else if(pid == 0){
                        execvp(command[0], command);
                }
                else{
                        wait(stat);
                }
        }
        return 1;
}