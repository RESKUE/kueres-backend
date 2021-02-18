package kueres.auth;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import kueres.utility.Utility;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Value("${keycloak.auth-server-url}")
	private String authServerUrl;
	
	@RequestMapping("/**")
	public ResponseEntity<String> auth(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			
			String httpMethod = request.getMethod();
			
			String requestUrl = request.getRequestURL().toString();
			String[] urlParts = requestUrl.split("/auth");
			if (urlParts.length != 2) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
			}
			String requestRoute = urlParts[1];
			String newRoute = this.authServerUrl + requestRoute;
			
			//Body
			String contentType = request.getContentType();
			Map<String, String> parameters = new HashMap<String, String>();
			
			if (contentType.equals("application/x-www-form-urlencoded")) {
				
				Enumeration<String> parameterNames = request.getParameterNames();
				if (parameterNames != null) {
					while (parameterNames.hasMoreElements()) {
						String parameterName = parameterNames.nextElement();
						parameters.put(parameterName, request.getParameter(parameterName));
					}
				}
			}
			
			
			//Header
			Map<String, String> headers = new HashMap<String, String>();
			Enumeration<String> headerNames = request.getHeaderNames();
			if (headerNames != null) {
				while (headerNames.hasMoreElements()) {
					String headerName = headerNames.nextElement();
					headers.put(headerName, request.getHeader(headerName));
				}
			}
			
			//Query params
			String queryParams = request.getQueryString();
			if (queryParams != null) {
				newRoute += "?" + queryParams;
			}
			
			
			Utility.LOG.info("------------------------");
			Utility.LOG.info("Request URL: {}", request.getRequestURL());
			Utility.LOG.info("Http method: {}", httpMethod);
			Utility.LOG.info("Request route: {}", requestRoute);
			Utility.LOG.info("Query params: {}", queryParams);
			Utility.LOG.info("New route: {}", newRoute);
			Utility.LOG.info("Headers: {}", headers);
			Utility.LOG.info("Content type: {}", contentType);
			Utility.LOG.info("Body: {}", parameters);
			Utility.LOG.info("------------------------");
			
			URL url = new URL(newRoute);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(httpMethod);
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				connection.addRequestProperty(entry.getKey(), entry.getValue());
			}
			
			if (parameters.size() > 0) {
				connection.setDoOutput(true);
				String urlParameters = "";
				for (Map.Entry<String, String> entry : parameters.entrySet()) {
					urlParameters += entry.getKey() + "=" + entry.getValue() + "&";
				}
				urlParameters = urlParameters.substring(0, urlParameters.length() - 1);
				Utility.LOG.info("url parameters: {}", urlParameters);
				byte[] data = urlParameters.getBytes();
				DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
				writer.write(data);
			}

			int status = connection.getResponseCode();
			
			Reader streamReader = null;
			if (status > 299) {
				streamReader = new InputStreamReader(connection.getErrorStream());
			} else {
				streamReader = new InputStreamReader(connection.getInputStream());
			}
			BufferedReader in = new BufferedReader(streamReader);
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();
			connection.disconnect();

			String responseContent = content.toString();
			
			Utility.LOG.info("response: {}", responseContent);
			
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(responseContent);
			
			
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
	}
	
}
