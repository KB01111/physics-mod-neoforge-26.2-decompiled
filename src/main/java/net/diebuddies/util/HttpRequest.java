package net.diebuddies.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest {
   public static String get(String urlToRead) throws IOException {
      StringBuilder result = new StringBuilder();
      URL url = new URL(urlToRead);
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      connection.setConnectTimeout(30000);
      connection.setReadTimeout(30000);
      connection.setRequestMethod("GET");
      connection.setInstanceFollowRedirects(true);

      String line;
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
         while ((line = reader.readLine()) != null) {
            result.append(line);
         }
      }

      return result.toString();
   }

   public static String post(String urlToRead, String body) throws IOException {
      StringBuilder result = new StringBuilder();
      URL url = new URL(urlToRead);
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      connection.setConnectTimeout(30000);
      connection.setReadTimeout(30000);
      connection.setRequestMethod("POST");
      connection.setInstanceFollowRedirects(true);
      connection.setDoOutput(true);
      OutputStream outStream = connection.getOutputStream();
      OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, "UTF-8");
      outStreamWriter.write(body);
      outStreamWriter.flush();
      outStreamWriter.close();
      outStream.close();

      String line;
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
         while ((line = reader.readLine()) != null) {
            result.append(line);
         }
      }

      return result.toString();
   }

   public static InputStream getStream(String urlToRead) throws IOException {
      URL url = new URL(urlToRead);
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      connection.setConnectTimeout(30000);
      connection.setReadTimeout(30000);
      connection.setRequestMethod("GET");
      connection.setInstanceFollowRedirects(true);
      return connection.getInputStream();
   }
}
