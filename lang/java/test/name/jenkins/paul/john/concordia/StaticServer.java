package name.jenkins.paul.john.concordia;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import name.jenkins.paul.john.concordia.exception.ConcordiaException;

import org.junit.Ignore;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * <p>
 * Starts a HTTP server on the specified ports and gives the user control over
 * the response.
 * </p>
 *
 * @author John Jenkins
 */
@Ignore
public class StaticServer {
	/**
	 * <p>
	 * This is a very lightweight handler for responding to requests. 
	 * </p> 
	 *
	 * @author John Jenkins
	 */
	@Ignore
	private static class RequestHandler implements HttpHandler {
		public int responseCode = 200;
		public String response = "";
		
		/*
		 * (non-Javadoc)
		 * @see com.sun.net.httpserver.HttpHandler#handle(com.sun.net.httpserver.HttpExchange)
		 */
		@Override
		public void handle(final HttpExchange exchange) throws IOException {
			Headers responseHeaders = exchange.getResponseHeaders();
			responseHeaders.add("Content-Type", "application/json");
			
			exchange.sendResponseHeaders(responseCode, response.length());

			OutputStream responseBody = exchange.getResponseBody();
			responseBody.write(response.getBytes());
			responseBody.close();
		}
	}
	
	/**
	 * The port which this private server will connect to.
	 */
	public static final int DEFAULT_PORT = 60000;
	
	/**
	 * The server instance.
	 */
	private final HttpServer server;
	/**
	 * The handler for requests.
	 */
	private final RequestHandler handler;
	
	/**
	 * Creates a new HTTP server at the given port.
	 * 
	 * @param port The port to attach.
	 * 
	 * @throws ConcordiaException There was an error starting the server.
	 */
	public StaticServer(final int port) throws ConcordiaException {
		// Start the server.
		InetSocketAddress addr = new InetSocketAddress(port);
		try {
			server = HttpServer.create(addr, 0);
		}
		catch(IOException e) {
			throw
				new ConcordiaException(
					"There was an error starting the server.",
					e);
		}
		
		// Create the handler and attach to the root of the domain.
		handler = new RequestHandler();
		server.createContext("/", handler);
		
		// Set the thread pool.
		server.setExecutor(Executors.newCachedThreadPool());
		
		// Start the server.
		server.start();
	}
	
	/**
	 * Properly shut down the server.
	 */
	public void shutdown() {
		server.stop(0);
	}
	
	/**
	 * Sets the desired response code.
	 * 
	 * @param responseCode The desired response code.
	 */
	public void setResponseCode(final int responseCode) {
		handler.responseCode = responseCode;
	}
	
	/**
	 * Sets the desired response body.
	 * 
	 * @param response The desired response body.
	 */
	public void setResponse(final String response) {
		handler.response = response;
	}
}