package de.rohmio.mtg.mtgtop8.api.endpoints;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;

import de.rohmio.mtg.mtgtop8.api.MtgTop8Api;

public abstract class AbstractEndpoint<T> {

	private static final String URI = "https://mtgtop8.com";

	private static LocalDateTime lastRequestTimestamp = LocalDateTime.now().minus(MtgTop8Api.RATE_LIMIT,
			ChronoUnit.MILLIS);
//	private static final String encoding = "ISO-8859-1";

	protected WebTarget target;
	private GenericType<T> resultType;

	protected AbstractEndpoint(String path, GenericType<T> resultType) {
		Client client = ClientBuilder.newClient();
		target = client.target(URI).register(JacksonJsonProvider.class).path(path);
		this.resultType = resultType;
	}

	protected AbstractEndpoint(String path, Class<T> resultClass) {
		this(path, new GenericType<T>(resultClass));
	}

	protected GenericType<T> getResultType() {
		return resultType;
	}

	private static long between() {
		return ChronoUnit.MILLIS.between(LocalDateTime.now(), lastRequestTimestamp.plus(MtgTop8Api.RATE_LIMIT, ChronoUnit.MILLIS));
	}

	protected static synchronized void waitIfNecessary() {
		long between;
		while ((between = between()) >= 0) {
			try {
				Thread.sleep(between);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(Thread.currentThread() + " will execute");
		lastRequestTimestamp = LocalDateTime.now();
	}

	public T get() {
		waitIfNecessary();
		System.out.println(target.getUri());
		return handleResponse(target
		.request().get());
	}

	protected T handleResponse(Response response) {
		T result = null;
		if(response.getStatus() == Status.OK.getStatusCode()) {
			result = parseReponse(response);
		} else {
			System.err.println(response.getStatus()+" "+response.getStatusInfo());
			return null;
		}
		response.close();
		return result;
	}

	protected T parseReponse(Response response) {
		return response.readEntity(resultType);
	}

	protected void resetQueryParam(String key) {
		target = target.queryParam(key, new Object[0]);
	}

}
