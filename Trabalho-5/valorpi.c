// Author: Wes Kendall
// Copyright 2011 www.mpitutorial.com
// This code is provided freely with the tutorials on mpitutorial.com. Feel
// free to modify it for your own use. Any distribution of the code must
// either provide a link to www.mpitutorial.com or keep this header in tact.
//
// MPI_Send, MPI_Recv example. Communicates the number -1 from process 0
// to processe 1.
//
#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_STRING 100

int main(int argc, char** argv) {

//  char greeting[MAX_STRING];
  // Initialize the MPI environment
  MPI_Init(NULL, NULL);
  // Find out rank, size
  int world_rank;
  MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);
  int world_size;
  MPI_Comm_size(MPI_COMM_WORLD, &world_size);

  int quantidadePontos  = 200;

  // We are assuming at least 2 processes for this task
  if (world_size < 2) {
    fprintf(stderr, "World size must be greater than 1 for %s\n", argv[0]);
    MPI_Abort(MPI_COMM_WORLD, 1); 
  }

  if(world_rank == 0){
  		int i;
	  	for(i = 1; i < world_size; i++){
		  	MPI_Send(&quantidadePontos, quantidadePontos, MPI_INT, 0, 0, MPI_COMM_WORLD);
  	}
  }



  //int number;
  if (world_rank != 0) {
    // If we are rank 0, set the number to -1 and send it to process 1
    //sprintf(greeting, "Greetings from process %d of %d!", world_rank, world_size);
    //MPI_Send(&greeting, strlen(greeting) + 1, MPI_CHAR, 0, 0, MPI_COMM_WORLD);
    MPI_Recv(&quantidadePontos, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
    printf("RECEBI: %d\n", quantidadePontos);
  } //else {
  	//	int i;
	  //	for(i = 1; i < world_size; i++){
		//  	MPI_Recv(&greeting, MAX_STRING, MPI_CHAR, i, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		//	printf("Process 0 received \'%s\' from process %d\n", greeting, i);
  	//}
  
 // }
  MPI_Finalize();
}
