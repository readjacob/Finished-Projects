#include <stdio.h>
#include <iostream>
#include "Sorting.h"
#include <ctime>

int Sorting::getMaxValues(){
        return maxValues;
}
void Sorting::doubleMaxValues(){
        maxValues = maxValues * 2;
}
//selection sort with count
void Sorting::selectionSort(int array[], int numValues){
        int current, index, minIndex;

        for(current = 0; current<numValues-1; current++){
                minIndex = current;
                //looks for the minimum value and sets its index
                for(index = current+1; index< numValues; index++){
                        if(array[index] < array[minIndex]){
                                minIndex = index;
                        }
                        //counts the number of comparisons
                        count++;
                }
                swap(array[minIndex], array[current]);
        }
        cout << "\n\t#Selection-sort comparisons: " << count << "\n";
        printArray(array, numValues);
}

//merge sort with count
void Sorting::mergeSortCount(int array[], int numValues){
        mergeSort(array, 0, numValues-1);
        cout << "\n\t#Merge-sort comparisons: " << count << "\n";
        printArray(array, numValues);
}
//recursive merge sort
void Sorting::mergeSort(int array[], int first, int last){
        if(first < last){
                int middle = (first + last)/2;
                mergeSort(array, first, middle);
                mergeSort(array, middle+1, last);

                Merge(array, first, middle, middle+1, last);
        }
}
//merge split up arrays
void Sorting::Merge(int array[], int leftFirst, int leftLast, int rightFirst, int rightLast){
        int tempArray[maxValues];
        int index = leftFirst;
        int saveFirst = leftFirst;
        while((leftFirst <= leftLast) && (rightFirst <= rightLast)){
                if(array[leftFirst] < array[rightFirst]){
                        tempArray[index] = array[leftFirst];
                        leftFirst++;
                }
                else{
                        tempArray[index] = array[rightFirst];
                        rightFirst++;
                }
                index++;
                //counts the number of comparisons
                count++;
        }
        //copy left
        while(leftFirst <= leftLast){
                tempArray[index] = array[leftFirst];
                leftFirst++;
                index++;
        }
        //copy right
        while(rightFirst <= rightLast){
                tempArray[index] = array[rightFirst];
                rightFirst++;
                index++;
        }
        //put in the original array
        for(index = saveFirst; index <= rightLast; index++){
                array[index] = tempArray[index];
        }

}
//heap sort
void Sorting::heapSort(int array[], int numValues){
        int index;
        //convert to a heap
        for(index = numValues/2 - 1; index >=0; index--){
                ReheapDown(array, index, numValues-1);
        }

        //sort the array
        for(index = numValues-1; index >=1; index--){
                swap(array[0], array[index]);
                ReheapDown(array, 0, index-1);
        }
        cout << "\n\t#Heap-sort comparisons: " << count << "\n";
        printArray(array, numValues);
}
//recursivly reheaps
void Sorting::ReheapDown(int array[], int root, int bottom){
        int maxChild = root;
        int leftChild;
        int rightChild;

        leftChild = root*2 + 1;
        rightChild = root*2 + 2;

        if(leftChild <= bottom){
                if(leftChild == bottom){
                        maxChild = leftChild;
                }
                else{
                        count++;
                        if(array[leftChild] <= array[rightChild]){
                                maxChild = rightChild;
                        }
                        else{
                                maxChild = leftChild;
                        }
                }
                count++;
                if(array[root] < array[maxChild]){
                        swap(array[root], array[maxChild]);
                        ReheapDown(array, maxChild, bottom);
                }
        }
}
//counts the number of comparisons for quickSort first element or random as pivot
void Sorting::quickSortCount(int array[], int numValues, bool random){
        quickSort(array, 0, numValues-1, random);
        cout << "\n\t#Quick-sort comparisons: " << count << "\n";
        printArray(array, numValues);
}
//quickSort with first element as pivot
void Sorting::quickSort(int array[], int first,  int last, bool random){
        if(first < last){
                int splitPoint = Split(array, first, last, random);

                quickSort(array, first, splitPoint-1, random);
                quickSort(array, splitPoint+1, last, random);
        }
}
//split method for quickSort
int Sorting::Split(int array[], int first, int last, bool random){

        int pivot;
        if(random){
                srand(time(NULL));
                pivot = first + rand()%(last - first);
                swap(array[pivot], array[last]);
        }
        else{
                pivot = first;
                swap(array[pivot], array[last]);
        }

        int index = first -1;
        int key = array[last];

        for(int i = first; i< last; i++){
                count++;
                                if(array[i] <= key){
                        index++;
                        swap(array[index], array[i]);
                }
        }
        index++;
        swap(array[index], array[last]);

        return index;
}

void Sorting::printArray(int array[], int numValues){

        for(int i=0; i<numValues; i++){
                cout << array[i];
                cout << " ";
        }
        cout << "\n";
}