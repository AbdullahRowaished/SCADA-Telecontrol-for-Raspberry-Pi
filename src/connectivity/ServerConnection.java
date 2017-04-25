package connectivity;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerConnection extends ServerSocket {

	public ServerConnection(int port) throws IOException {
		super(port);
	}

}
