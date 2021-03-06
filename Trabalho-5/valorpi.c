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
#include <math.h>

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

	int numeroLinhas;
	int numeroColunas;
	int numeroMax;
	int fatorCalculo; 
	char fatorPGM[10];
	int tamanhoVetor;
	int quantidadePartes;


	FILE *file;
	int *vetorImagem;
	int *vetorImagemResultante;
	int *bufferRecv;
	int i;
	int j;


	int quantidadePontos  = 20;


	if(world_rank == 0) {
		fatorCalculo = atoi(argv[1]);
		file = fopen(argv[2],"r");
		fscanf(file, "%[^\n]", fatorPGM);
		fgetc(file);
		fscanf(file, "%d", &numeroColunas);
		fscanf(file, "%d", &numeroLinhas);
		fscanf(file, "%d", &numeroMax);
    		printf("Leitura da Imagem \n");
    		printf("Colunas: %d\n", numeroColunas);
    		printf("Linhas: %d\n", numeroLinhas);
    		printf("Max: %d\n", numeroMax);
    		printf("Fator de Calculo: %d\n", fatorCalculo);

//-------
		tamanhoVetor = numeroLinhas * numeroColunas;
    		printf("Tamanho do Vetor: %d\n", tamanhoVetor);

		quantidadePartes = abs((tamanhoVetor/world_size));

		vetorImagem = (int *) malloc (tamanhoVetor * sizeof(int));
		vetorImagemResultante = (int *) malloc (tamanhoVetor * sizeof(int));

    		printf("Quantidade de Partes: %d\n", quantidadePartes);

		vetorImagemResultante[0]=0;


		//Preenche Vetor Imagem
		for(i = 0; i < tamanhoVetor; i++) {
			fscanf(file, "%d", &vetorImagem[i]);
		}
		fclose(file);


    		printf("GRANDE VETOR \n");
		for (i = 0; i < tamanhoVetor; i++){
			printf("%d\n ", vetorImagem[i]);
		}
    		printf("GRANDE VETOR FIM\n");
	}




	// We are assuming at least 2 processes for this task
  	if (world_size < 2) {
    		fprintf(stderr, "World size must be greater than 1 for %s\n", argv[0]);
    		MPI_Abort(MPI_COMM_WORLD, 1); 
  	}


	MPI_Bcast(&quantidadePontos, 1, MPI_INT, 0, MPI_COMM_WORLD);
	MPI_Bcast(&numeroColunas, 1, MPI_INT, 0, MPI_COMM_WORLD);
	MPI_Bcast(&numeroLinhas, 1, MPI_INT, 0, MPI_COMM_WORLD);
	MPI_Bcast(&numeroMax, 1, MPI_INT, 0, MPI_COMM_WORLD);
	MPI_Bcast(&fatorCalculo, 1, MPI_INT, 0, MPI_COMM_WORLD);
	MPI_Bcast(&tamanhoVetor, 1, MPI_INT, 0, MPI_COMM_WORLD);
	MPI_Bcast(&quantidadePartes, 1, MPI_INT, 0, MPI_COMM_WORLD);

	bufferRecv = (int *) malloc ((quantidadePartes)* sizeof(int));
	//MPI_Scatter(&sendbuf,sendcnt,sendtype,&recvbuf,recvcnt,recvtype,source,comm);
	MPI_Scatter(vetorImagem, quantidadePartes, MPI_INT, bufferRecv, quantidadePartes,MPI_INT,0,MPI_COMM_WORLD);

  	




	//int number;
  	if (1) {
    		printf("world_rank %d \n",world_rank);
    		printf("Colunas: %d\n", numeroColunas);
    		printf("Linhas: %d\n", numeroLinhas);
    		printf("Max: %d\n", numeroMax);
    		printf("Fator de Calculo: %d\n", fatorCalculo);
    		printf("Tamanho do Vetor: %d\n", tamanhoVetor);
    		printf("Quantidade de Partes: %d\n", quantidadePartes);
		printf("To no world  ");
		for(i=0; i < quantidadePartes-1;i++) {
			if (abs((bufferRecv[i] - bufferRecv[i+1])) < fatorCalculo ) {
				bufferRecv[i] = numeroMax;
			}
			else {
				bufferRecv[i] = 0;
			} 
		}
  	}
 
	//MPI_Gather(&sendbuf,sendcnt,sendtype,&recvbuf,recvcount,recvtype,dest,comm);
	MPI_Gather(bufferRecv,quantidadePartes,MPI_INT,vetorImagem,quantidadePartes,MPI_INT, 0, MPI_COMM_WORLD);

	if (world_rank == 0) {
		printf("GRANDE VETOR \n");
		j=0;
		int k=0;
		for (i = 0; i < numeroLinhas; i++){
			for(j=0; j< numeroColunas;j++){
				printf(" %d ", vetorImagem[k]);
				k++;
			}
			printf("\n");
		}
    		printf("\nGRANDE VETOR FIM\n");

  	
	}
  	MPI_Finalize();
}
