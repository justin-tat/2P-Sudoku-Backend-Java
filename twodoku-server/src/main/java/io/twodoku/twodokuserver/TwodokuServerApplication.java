package io.twodoku.twodokuserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

@SpringBootApplication
public class TwodokuServerApplication {

	public SocketIOServer sockeIOServer() {
		Configuration config = new Configuration();
		config.setHostname("localhost");
		config.setPort(3000);
		return new SocketIOServer(config);
	}
	public static void main(String[] args) {
		SpringApplication.run(TwodokuServerApplication.class, args);
	}

}
