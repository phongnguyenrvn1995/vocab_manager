package com.vocab.utils;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


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
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
				bw.write(urlParameters);
				bw.flush();
				bw.close();
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
			return new String(resp,  StandardCharsets.UTF_8);
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
