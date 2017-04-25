package connectivity;

import java.io.IOException;
import java.io.InputStream;

public class RemoteTerminalUnit extends Station {
	private ServerConnection server;
	public static void main(String[] args) {
		RemoteTerminalUnit rt = new RemoteTerminalUnit();
		rt.service.submit(new Runnable() {
			@Override
			public void run() {
				rt.launch();
			}
		});
	}
	@Override
	public void launch() {
		super.launch();
		int C = 0;
		while (true) {
			switch (getContext()) {
			case DISCONNECTED:
				System.out.println("DISCONNECTED: " + C++);
				while (getContext() == FSMState.DISCONNECTED) {
					try {
						setConnection(server.accept());
						setContext(FSMState.CONNECTED);
					} catch (IOException | NullPointerException e) {
					}
				}
				break;
			case CONNECTED:
				System.out.println("CONNECTED: " + C++);
				while (getContext() == FSMState.CONNECTED) {
					try {
						check();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				break;
			case STARTDT:
				System.out.println("STARTDT: " + C++);
				while (getContext() == FSMState.STARTDT) {
				}
				break;
			case RECEIVING_I:
				System.out.println("RECEIVING_I: " + C++);
				while (getContext() == FSMState.RECEIVING_I) {
				}
				break;
			case SENDING_S:
				System.out.println("SENDING_S: " + C++);
				acknowledge();
				while (getContext() == FSMState.SENDING_S) {
				}
				break;
			case SENDING_I:
				System.out.println("SENDING_I: " + C++);
				while (getContext() == FSMState.SENDING_I) {
				}
				break;
			case RECEIVING_S:
				System.out.println("RECEIVING_S: " + C++);
				while (getContext() == FSMState.RECEIVING_S) {
				}
				break;
			case STOPDT:
				System.out.println("STOPDT: " + C++);
				while (getContext() == FSMState.STOPDT) {
				}
				break;
			default:
				break;
			}
		}
	}
	
	private void check() throws IOException {
		InputStream in = getConnection().getInputStream();
		if (in.available() == 1) {
			if (in.read() == 0) {
				setContext(FSMState.DISCONNECTED);
			}
		}	
	}
	public RemoteTerminalUnit() {
		super();
		try {
			server = new ServerConnection(2404);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}