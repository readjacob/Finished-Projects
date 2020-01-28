#ifndef SORTING_H
#define SORTING_H

using namespace std;


class Sorting
{
        private:
                int maxValues = 10000;
                long count = 0;
                void ReheapDown(int array[], int root, int bottom);
                void Merge(int array[], int leftFirst, int leftLast, int rightFirst, int rightLast);
                int Split(int array[], int first, int last, bool random);
        public:
                int getMaxValues();
                void doubleMaxValues();
                void selectionSort(int array[], int numValues);
                void mergeSortCount(int array[], int numValues);
                void mergeSort(int array[], int first, int last);
                void heapSort(int array[], int numValues);
                void quickSortCount(int array[], int numValues, bool random);
                void quickSort(int array[], int first,  int last, bool random);
                void printArray(int array[], int numValues);
};


#endif