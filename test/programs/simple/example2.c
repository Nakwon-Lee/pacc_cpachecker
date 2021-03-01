extern void reach_error();

int main(void){
	int a=__VERIFIER_nondet_int();
	int b=__VERIFIER_nondet_int();
	int i;

	i=0;
	while(a<=b){
		a = a + 1;
		if(a>b){
			errorFn(a-b);
		}
		i = incOne(i);
	}
	return 0;
}

int incOne(int a){
	return incOne2(a);
}

int incOne2(int a){
	return a+1;
}

void errorFn(int k){
	if(k<0){
		errorFn2();	
	}
}

void errorFn2(void){
	reach_error();
}
