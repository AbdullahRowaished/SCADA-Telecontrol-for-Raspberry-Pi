package connectivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * this is a template class to be extended by other Java classes desiring to be
 * rendered as applications of the 104 standard
 * 
 * @author ar421
 *
 */
public class ControllingCentre extends Station {

	@Override
	public void launch() {
		super.launch();
		int C = 0;
		while (true) {
			switch (getContext()) {
			case DISCONNECTED:
				System.out.println("DISCONNECTED: " + C++);
				while (getContext() == FSMState.DISCONNECTED) {
				}
				break;
			case CONNECTED:
				System.out.println("CONNECTED: " + C++);
				while (getContext() == FSMState.CONNECTED) {
				}
				break;
			case STARTDT:
				System.out.println("STARTDT: " + C++);
				try {
					connection.setSoTimeout(t1);
				} catch (SocketException ex) {
					setContext(FSMState.DISCONNECTED);
				}
				while (getContext() == FSMState.STARTDT) {
				}
				break;
			case RECEIVING_I:
				System.out.println("RECEIVING_I: " + C++);
				try {
					connection.setSoTimeout(t2);
				} catch (SocketException ex) {
					setContext(FSMState.DISCONNECTED);
					break;
				}
				while (getContext() == FSMState.RECEIVING_I) {
				}
				break;
			case RECEIVING_S:
				System.out.println("RECEIVING_S: " + C++);
				while (getContext() == FSMState.RECEIVING_S) {
				}
				break;
			case SENDING_I:
				System.out.println("SENDING_I: " + C++);
				while (getContext() == FSMState.SENDING_I) {
				}
				break;
			case SENDING_S:
				System.out.println("SENDING_S:" + C++);
				acknowledge();
				try {
						connection.setSoTimeout(t3);
					} catch (SocketException e) {
						setContext(FSMState.CONNECTED);
					}
				while (getContext() == FSMState.SENDING_S) {
				}
				break;
			case STOPDT:
				try {
						connection.setSoTimeout(t1);
					} catch (SocketException ex) {
						setContext(FSMState.DISCONNECTED);
						break;
					}
				while (getContext() == FSMState.STOPDT) {
				}
				break;
			default:
				break;
			}
		}

	}
	/**
	 * basic constructor to initiate fields
	 */
	public ControllingCentre() {
		super();
	}

	/**
	 * user process: stop data transmission
	 * @throws IOException
	 */
	public void stopDT() throws IOException {
		if (getContext() == FSMState.SENDING_I)  {
			stopDTact();
			setContext(FSMState.STOPDT);
		} else {
			throw new IOException();
		}
	}

	/**
	 * user process: start data transmission
	 * @throws IOException
	 */
	public void startDT() throws IOException {
		if (getContext() == FSMState.CONNECTED) {
			startDTact();
			setContext(FSMState.STARTDT);
		} else {
			throw new IOException();
		}
	}
	
	/**
	 * connects to the server
	 * @throws IOException
	 */
	public void connect(String ipadd) throws IOException {
		InetAddress IP = null;
		try {
			IP = InetAddress.getByName(ipadd);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		try {
			setConnection(new ClientConnection(IP, 2404));
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		setContext(FSMState.CONNECTED);
	}
	/**
	 * disconnects from the server
	 */
	public void disconnect() {
		try {
			connection.getOutputStream().write((0));
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		setContext(FSMState.DISCONNECTED);
	}

}