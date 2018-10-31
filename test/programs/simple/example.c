extern void __VERIFIER_error(void);

int main(void){
	int a=1;
	int b=1;
	int c=__VERIFIER_nondet();

	int i;
	int j;

	int (* func)(int);

	i=0;

	while(i<1){
		a=a+b;
		if(a<b){
		ERROR:	__VERIFIER_error();
			goto ERROR;
		}
		i = incOne(i);
	}

	if (c>0){
		func = incOne;
	}else{
		func = incTwo;
	}

	j=0;
	while(j<1){
		a = a - 1;
		j = func(j);
	}

	if(a<b){
		errorFn();
	}

	return 0;
}

int incOne(int a){
	if (a==1){
		return incOne2(a);
	}else{
		return a + 1;
	}
}

int incOne2(int a){
	return a + a;
}

int incTwo(int a){
	return a + 2;
}

void errorFn(void){
ERROR:	__VERIFIER_error();
	goto ERROR;	
}
