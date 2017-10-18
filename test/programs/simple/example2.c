extern void __VERIFIER_error() __attribute__ ((__noreturn__));

int main(void){
	int a;
	int b;

	if (b>a){
		while (b>a){
			b = sub(b);
		}
	}else{
		while (b<=a){
			b = b + 1;
		}
	}

	if (a!=b){
		ERROR: __VERIFIER_error();
	}

	return 0;
}

int sub(int p){
	return p - 1;
}
