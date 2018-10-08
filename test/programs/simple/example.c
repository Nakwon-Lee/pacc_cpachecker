extern void __VERIFIER_error(void);

int main(void){
	int a=1;
	int b=1;

	int i;
	int j;

	i=0;

	while(i<1){
		a=a+b;
		if(a<b){
		ERROR:	__VERIFIER_error();
			goto ERROR;
		}
		i = i + 1;
	}

	j=0;
	while(j<1){
		a = a - 1;
		j = incOne(j);
	}

	if(a<b){
		errorFn();
	}

	return 0;
}

int incOne(int a){
	return a + 1;
}

void errorFn(void){
ERROR:	__VERIFIER_error();
	goto ERROR;	
}
