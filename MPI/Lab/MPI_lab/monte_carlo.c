#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <mpi.h>
#include <stdbool.h>
#include <math.h>

const int num_iter_per_proc = 10 * 1000 * 1000;

bool is_point_inside(double x, double y){

    double x2 = pow(x,2);
    double y2 = pow(y,2);

    if ( (sqrt(x2  + y2 ) <= 1) && (x2 + y2 <= 1) ) return true;

    return false;
}

int compute_sum(const int* buffer, int size){
    int sum = 0;
    for (int i = 0; i < size; i++){
        sum += buffer[i];
    }
    return sum;
}

int main() {
    MPI_Init(NULL, NULL);

    int rank;
    int num_procs;
    int sum;

    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &num_procs);

    srand(time(NULL) + rank);

    // TODO

    int local_count = 0;

    //Processes compute the number of points inside the circle

    for (int i = 0; i < num_iter_per_proc; i++){
        double x_gen = rand() / (double) RAND_MAX;
        double y_gen = rand() / (double) RAND_MAX;

        if (is_point_inside(x_gen,y_gen)){
            local_count++;
        }
    }

    int *gather_buffer = NULL;
    if (rank == 0) {
      gather_buffer = (int *) malloc(sizeof(int) * num_procs);
    }

    MPI_Gather(&local_count, 1, MPI_INT, gather_buffer, 1, MPI_INT, 0, MPI_COMM_WORLD);

    if (rank == 0) {
        sum = compute_sum(gather_buffer, num_procs);
        double pi = (4.0*sum) / (num_iter_per_proc*num_procs);
        printf("Pi = %f\n", pi);
    }

    // Clean up
    if (rank == 0) {
        free(gather_buffer);
    }

    MPI_Barrier(MPI_COMM_WORLD);

    MPI_Finalize();
    return 0;
}
