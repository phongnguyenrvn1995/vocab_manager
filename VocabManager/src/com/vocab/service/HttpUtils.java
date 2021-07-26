package com.vocab.service;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {

	public static String request(String targetURL, String method, String contentTpye, String urlParameters) {
		HttpURLConnection connection = null;
		try {
			// Create connection
			URL url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(method);
			connection.setRequestProperty("Content-Type", contentTpye);
			// connection.setRequestProperty("Content-Language", "en-US");
			connection.setUseCaches(false);
			connection.setDoOutput(true);

			// Send request
			if (urlParameters != null && !urlParameters.isEmpty()) {
				connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
				wr.writeBytes(urlParameters);
				wr.close();
			}

			// Get Response
			InputStream is = connection.getInputStream();
			byte[] resp = new byte[is.available()];
			System.out.println(resp.length);
			int bRead;
			int offset = 0;
			while ((bRead = is.read()) != -1) {
				resp[offset++] = (byte) bRead;
			}
			is.close();
			return new String(resp);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
}
