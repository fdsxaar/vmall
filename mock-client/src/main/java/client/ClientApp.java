package client;

public class ClientApp {

	public static void main(String []args) {
		//SubmitOrder submitOrder = new SubmitOrder();
		//for(int i=10000;i>0;i--){
		//submitOrder.postOrder();
		//}
		ClientMultiThreadedExecution exec = new ClientMultiThreadedExecution();
		exec.postOrder();
	}
}
