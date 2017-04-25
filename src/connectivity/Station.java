package connectivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import converters.Bit;
import converters.Converter;
import converters.Octet;
import exceptions.StdException;
import iec_60870_5_104.apdu.APDU;
import iec_60870_5_104.apdu.ASDU;

public abstract class Station {
	protected Stack<APDU> apduList;
	protected FSMState context;
	protected Stack<ASDU> asduList;
	protected Socket connection;
	protected int Vs, Vr, Ack, k, w, timeout, t1, t2, t3, n, st68hc;
	protected ExecutorService service;

	private int getSt68hc() {
		return st68hc;
	}

	private void setSt68hc(int st68hc) {
		this.st68hc = st68hc;
	}

	protected boolean overflow;
	protected boolean undisturbed;

	protected Station() {
		service = Executors.newFixedThreadPool(4);
		setContext(FSMState.DISCONNECTED);
		setT1(2500);
		setT2(5000);
		setT3(10000);
		setTimeout(0);
		setVs(0);
		setVr(0);
		setAck(0);
		setK(600);
		setN(1);
		setW(400);
		setAsduList(new Stack<>());
		setSt68hc(1);
	}

	/**
	 * a main method to be called by the user to connect to a server. commands:
	 * DISCONNECTED state: connect: this attempts to connect to a server on the
	 * specified network. CONNECTED state: start: starts data transmission
	 * procedure. STARTDT state: send: sends data from sendList (to be filled by
	 * user manually) stop: stops data transmission procedure. STOPDT state:
	 * NOTE: this is a DEAD method! It does not reflect manipulation of
	 * instances of this class.
	 * 
	 * @param args
	 */
	public void launch() {
		service.submit(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (getContext() != FSMState.DISCONNECTED) {
						try {
							listen();
						} catch (NullPointerException | IOException e) {
							//e.printStackTrace();
						}
					}
				}
			}
		});
		service.submit(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (getContext() != FSMState.DISCONNECTED) {
						try {
							process();
						} catch (NullPointerException ex) {
							//ex.printStackTrace();
						}
					}
				}
			}
		});
	}

	public Socket getConnection() {
		return connection;
	}

	protected synchronized Stack<APDU> getApduList() {
		return apduList;
	}

	protected synchronized void setApduList(Stack<APDU> apduList) {
		this.apduList = apduList;
	}

	protected synchronized int getT1() {
		return t1;
	}

	protected synchronized void setT1(int t1) {
		this.t1 = t1;
	}

	protected synchronized int getAck() {
		return Ack;
	}

	protected synchronized void setAck(int ack) {
		Ack = ack;
	}

	protected synchronized int getVs() {
		return Vs;
	}

	protected synchronized void setVs(int vs) {
		Vs = vs;
	}

	protected synchronized int getW() {
		return w;
	}

	protected synchronized void setW(int w) {
		this.w = w;
	}

	protected synchronized Stack<ASDU> getAsduList() {
		return asduList;
	}

	protected synchronized void setAsduList(Stack<ASDU> receiveList) {
		this.asduList = receiveList;
	}

	protected synchronized int getVr() {
		return Vr;
	}

	protected synchronized void setVr(int vr) {
		Vr = vr;
	}

	protected synchronized int getT2() {
		return t2;
	}

	protected synchronized void setT2(int t2) {
		this.t2 = t2;
	}

	protected synchronized FSMState getContext() {
		return context;
	}

	protected synchronized void setContext(FSMState context) {
		this.context = context;
	}

	protected synchronized void setConnection(Socket connection) {
		this.connection = connection;
	}

	protected synchronized boolean isOverflow() {
		return overflow;
	}

	protected synchronized void setOverflow(boolean overflow) {
		this.overflow = overflow;
	}

	protected synchronized int getN() {
		return n;
	}

	protected synchronized void setN(int n) {
		this.n = n;
	}

	protected synchronized int getK() {
		return k;
	}

	protected synchronized void setK(int k) {
		this.k = k;
	}

	protected synchronized int getT3() {
		return t3;
	}

	protected synchronized void setT3(int t3) {
		this.t3 = t3;
	}

	protected synchronized int getTimeout() {
		return timeout;
	}

	protected synchronized void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public synchronized void startDTact() {
		try {
			OutputStream out = connection.getOutputStream();
			out.flush();
			byte[] output = new byte[6];
			for (int i = 0; i < output.length; i++) {
				output[i] = 0 - 128;
			}
			output[0] = 1 - 128;
			output[1] = 6 - 128;
			output[2] = 7 - 128;

			out.write(output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void stopDTact() {
		try {
			OutputStream os = connection.getOutputStream();
			byte[] output = new byte[6];
			for (int i = 0; i < output.length; i++) {
				output[i] = 0 - 128;
			}
			output[0] = 1 - 128;
			output[1] = 6 - 128;
			output[2] = 19 - 128;

			os.write(output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void testFRact() {
		try {
			OutputStream os = connection.getOutputStream();
			byte[] output = new byte[6];
			for (int i = 0; i < output.length; i++) {
				output[i] = 0 - 128;
			}
			output[0] = 1 - 128;
			output[1] = 6 - 128;
			output[2] = 67 - 128;

			os.write(output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected synchronized void startDTcon() {
		try {
			OutputStream os = connection.getOutputStream();
			byte[] output = new byte[6];
			for (int i = 0; i < output.length; i++) {
				output[i] = 0 - 128;
			}
			output[0] = 1 - 128;
			output[1] = 6 - 128;
			output[2] = 9 - 128;

			os.write(output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected synchronized void stopDTcon() {
		try {
			OutputStream out = connection.getOutputStream();
			byte[] output = new byte[6];

			output[0] = 1 - 128;
			output[1] = 6 - 128;
			output[2] = 35 - 128;
			for (int i = 3; i < output.length; i++) {
				output[i] = 0 - 128;
			}
			out.write(output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected synchronized void testFRcon() {
		try {
			OutputStream os = connection.getOutputStream();
			byte[] output = new byte[6];
			for (int i = 0; i < output.length; i++) {
				output[i] = 0 - 128;
			}
			output[0] = 1 - 128;
			output[1] = 6 - 128;
			output[2] = 131 - 128;

			os.write(output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * listens to Apdu data
	 * 
	 * @throws SocketTimeoutException
	 * @throws StdException
	 * @throws NullPointerException
	 * @throws IOException
	 */
	protected void listen() throws IOException {

		InputStream in = connection.getInputStream();
		if (in.available() >= 6) {
			byte[] info = new byte[in.available()];
			in.read(info);
			String bitstring = "";
			for (int i : info) {
				bitstring += Octet.getBits(i + 128, 8);
			}
			try {
				getApduList().push(Converter.decode(bitstring));
			} catch (StdException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * processes one Apdu from the retreived Apdus
	 */
	protected void process() {
		APDU temp = getApduList().pop();
		switch (temp.getType()) {
		case I_FORMAT:
			if (getContext() == FSMState.SENDING_I || getContext() == FSMState.RECEIVING_I
					|| getContext() == FSMState.SENDING_S) {
				Ack = temp.getReceiveSequenceNumber();
				setVr(Vr + 1);
				if (Ack == Vs) {
					clear_buffer();
				}
				// TODO questionable!
				if (Ack < Vs) {
					setContext(FSMState.DISCONNECTED);
					break;
				}
				setOverflow(n == w - 1);
				setContext(FSMState.RECEIVING_I);
				if (getContext() == FSMState.RECEIVING_I) {
					setN(getN() + 1);
					if (isOverflow()) {
						setContext(FSMState.SENDING_S);
					}
				}
				getAsduList().push(temp.getAsdu());
			} else {
				setContext(FSMState.DISCONNECTED);
			}
			break;
		case S_FORMAT:
			if (getContext() == FSMState.SENDING_I) {
				Ack = temp.getReceiveSequenceNumber();
				if (Ack == Vs) {
					clear_buffer();
				}
				setContext(FSMState.RECEIVING_S);
			} else {
				setContext(FSMState.DISCONNECTED);
			}
			break;
		case U_FORMAT:
			if (getContext() == FSMState.STARTDT) {
				if (temp.getSTARTDTcon()) {
					setContext(FSMState.SENDING_I);
				} else {
					setContext(FSMState.DISCONNECTED);
				}
			} else if (getContext() == FSMState.STOPDT) {
				if (temp.getSTOPDTcon()) {
					setContext(FSMState.CONNECTED);
				} else {
					setContext(FSMState.DISCONNECTED);
				}
			} else {
				setContext(FSMState.DISCONNECTED);
			}
			break;
		default:
			break;
		}
	}

	protected synchronized void clear_buffer() {
		getApduList().clear();
	}

	/**
	 * sends an Apdu to the server
	 * 
	 * @param sendable
	 */
	public void send(ASDU data) {
		if (getContext() == FSMState.STARTDT || getContext() == FSMState.SENDING_I
				|| getContext() == FSMState.RECEIVING_I || getContext() == FSMState.SENDING_S
				|| getContext() == FSMState.RECEIVING_S) {
			try {
				OutputStream out = connection.getOutputStream();
				APDU sendable = new APDU("I_FORMAT", getVs(), getVr(), getSt68hc(), data);
				setSt68hc(data.getData().length + 5 + getSt68hc());
				byte[] output = Converter.encode(sendable);
				out.write(output);
				setVs(getVs() + 1);
			} catch (IOException | NullPointerException | StdException e) {
				e.printStackTrace();
			}
		}
	}

	protected synchronized void acknowledge() {
		try {
			OutputStream out = connection.getOutputStream();
			byte[] output = new byte[6];
			output[0] = 1 - 128;
			output[1] = 6 - 128;
			output[2] = 1 - 128;
			output[3] = 0 - 128;
			Bit[] upper, lower;
			int i = getVr();
			String s = "";
			while (i > 1) {
				if (i % 2 == 1) {
					s = "1" + s;
				} else {
					s = "0" + s;
				}
				i = i / 2;
			}
			while (s.length() < 15) {
				s = "0" + s;
			}
			char[] ch = s.toCharArray();
			char[] up, down;
			down = new char[8];
			up = new char[8];
			for (int z = 0; z < 7; z++) {
				down[z] = ch[z];
				up[z] = ch[z + 8];
			}
			down[7] = ch[7];
			up[7] = '0';
			lower = Converter.lex(down);
			upper = Converter.lex(up);
			output[4] = (byte) (Octet.getDecimal(lower) - 128);
			output[5] = (byte) (Octet.getDecimal(upper) - 128);

			out.write(output);
		} catch (IOException | StdException | NullPointerException e) {
			e.printStackTrace();
		}
	}
}
