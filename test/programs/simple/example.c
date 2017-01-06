int main(void){
	int a;
	int b;

	if(b>a){
		while(b>a){
			b = sub(b);
		}
	}else{
		while(b<=a){
			b = b+1;
		}
	}

	if(a!=b){
	ERROR:
		goto ERROR;
	}

	return 0;
}

int sub(int p){
	return p-1;
}
