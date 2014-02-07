package eu.nexwell.fox.api.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import eu.nexwell.fox.api.core.FoxException;
import eu.nexwell.fox.api.core.FoxMessenger;

public class FoxMessengerTcpIp implements FoxMessenger {

	String host = "";
	int port = 0;
	int timeout = 250;
	
	Socket socket;
	PrintWriter toServer;
	BufferedReader fromServer;
	
	public FoxMessengerTcpIp() {
		
	}
	
	public void setHost(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public void setHost(String host) {
		int colon = host.indexOf(":");
		if (colon < 1)
			setHost(host, 23);
		else
			setHost(host.substring(0, colon - 1), Integer.parseInt(host.substring(colon)));
	}
	
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	@Override
	public void open() throws FoxException {
		try {
			socket = new Socket(host, port);
			socket.setSoTimeout(timeout);
			toServer = new PrintWriter(socket.getOutputStream(),true);
			fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String prompt = read();
			if (!prompt.equals("> Fox terminal"))
				throw new FoxException("Wrong prompt received");
		} catch (IOException e) {
			throw new FoxException(e.getMessage());
		}
	}

	@Override
	public void write(String text) throws FoxException {
		toServer.println(text);
		String echo = read();
		if (!echo.equals(text.trim()))
			throw new FoxException("Wrong echo received");
	}

	@Override
	public String read() {
		try {
			String line = fromServer.readLine();
			if (line == null)
				line = "";
			return line;
		} catch (IOException e) {
			return "";
		}
	}

	@Override
	public void close() {
		try {
			toServer.close();
			fromServer.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}