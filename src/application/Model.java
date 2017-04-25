package application;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import connectivity.ControllingCentre;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class Model {
	ControllingCentre cc;
	ExecutorService launchService, connectionService, updateUIService;

	Model() {
		cc = new ControllingCentre();
		launchService = Executors.newSingleThreadExecutor();
		connectionService = Executors.newSingleThreadExecutor();
		updateUIService = Executors.newSingleThreadExecutor();
		launchService.submit(new Runnable() {
			@Override
			public void run() {
				cc.launch();
			}
		});
	}

	void connect(String ip) {
		connectionService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					cc.connect(ip);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void disconnect() {
		connectionService.shutdownNow();
		connectionService = Executors.newSingleThreadExecutor();
		connectionService.submit(new Runnable() {
			@Override
			public void run() {
				cc.disconnect();
			}
		});
	}

	public void send() {
		// TODO this sends ASDUs of the type 'sit-point command'.
		
	}

	public void shutdownAllServices() {
		launchService.shutdownNow();
		connectionService.shutdownNow();
		updateUIService.shutdownNow();
	}
}