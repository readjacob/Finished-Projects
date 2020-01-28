#include <iostream>
#include <fstream>
#include <stdio.h>
#include "Sorting.h"

using namespace std;
void addFromFile(string filename);
void print_Commands();
void useCommands(bool &hasQuit);

//list to be used
Sorting sort;
int* array = new int[sort.getMaxValues()];
int numValues = 0;

int main(int argc, char *argv[]){

        bool hasQuit = false;
        if(argv[1] != NULL){
                addFromFile(argv[1]);
        }
        //commands to be used
        print_Commands();

        //run program and use commands
        while(!hasQuit){
                hasQuit = true;
                useCommands(hasQuit);
        }
        delete array;
        return 0;
}

//adds values to list from file
void addFromFile(string filename){
        int input;
        ifstream fs (filename);
        if(fs.is_open()){
                fs >> input;
                while(!fs.eof()){
                        if (numValues >= sort.getMaxValues()) {
                                sort.doubleMaxValues();            // double the previous size
                                int* temp = new int[sort.getMaxValues()];       // create new bigger array.
                                for (int i=0; i<numValues; i++) {
                                        temp[i] = array[i];         // copy values to new array.
                                }
                                delete [] array;
                                array = temp;
                        }
                        array[numValues] = input;
                        numValues++;
                        fs >> input;
                }
                fs.close();
        }
        else{
                cout << "Failed to open the input file" << endl;
                exit(1);
        }
}
//prints commands
void print_Commands(){
        cout << "\nselection-sort (s)"
                << "  merge-sort (m)"
                << "  heap-sort (h)"
                << "  quick-sort-fp (q)"
                << "  quick-sort-rp (r)";
}
//uses commands, if q is entered then hasQuit is made true
void useCommands(bool &hasQuit){
        char command;

        cout << "\n\nEnter the algorithm: ";
        cin >> command;

        //selection sort
        if(command == 's'){
                //sort.printArray(array, numValues);
                sort.selectionSort(array, numValues);
        }
        //merge sort
        else if(command == 'm'){
                //sort.printArray(array, numValues);
                sort.mergeSortCount(array, numValues);
        }
        //heap sort
        else if(command == 'h'){
                //sort.printArray(array, numValues);
                sort.heapSort(array, numValues);
        }
        //quick sort first element
        else if(command == 'q'){
                //sort.printArray(array, numValues);
                sort.quickSortCount(array, numValues, 0);
        }
        //quick sort random element
        else if(command == 'r'){
                //sort.printArray(array, numValues);
                sort.quickSortCount(array, numValues, 1);
        }
        //on invalid command
        else{
                cin.clear();
                cin.ignore(100,'\n');
                cout << "Please enter a valid algorithm";
                hasQuit = false;
                print_Commands();
        }

}