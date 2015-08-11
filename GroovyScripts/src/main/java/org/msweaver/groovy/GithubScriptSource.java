package org.msweaver.groovy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scripting.ScriptSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Component
public class GithubScriptSource implements ScriptSource {
	
	protected static final String SCRIPT_BASE_URL = "https://api.github.com/repos/msweaver/GroovyScripts/contents/GroovyScripts/src/main/groovy/";

	private RestTemplate restTemplate;

    private final String scriptName;
    
    private Date lastModifiedDateTime;
    
    @Value("${proxy.url}")
    private String proxyUrl;
    
    @Value("${proxy.port}")
    private String proxyPort;
    
	public GithubScriptSource(String scriptName) {
	    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

	    Proxy proxy= new Proxy(Type.HTTP, new InetSocketAddress(proxyUrl, Integer.parseInt(proxyPort)));
	    requestFactory.setProxy(proxy);
		restTemplate = new RestTemplate(requestFactory);
		
		this.scriptName = scriptName;
	}

	@Override
	public String getScriptAsString() throws IOException {
		System.out.println("pulling groovy script");
		
		ResponseEntity<Map> resp = restTemplate.getForEntity(SCRIPT_BASE_URL + this.scriptName, Map.class);
		lastModifiedDateTime = getLastModifiedFromHeaders(resp);
		
		Map map = resp.getBody();
		byte[] bytes = DatatypeConverter.parseBase64Binary((String) map.get("content"));
		
		return new String(bytes);
	}

	private Date getLastModifiedFromHeaders(ResponseEntity<Map> resp) {
		Date lastModified = null;
		String stringDate = resp.getHeaders().get("Last-Modified").get(0);
		
		DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
		try {
			lastModified = formatter.parse(stringDate);
		} catch (ParseException e) {
			// TODO do better
			e.printStackTrace();
		}
		return lastModified;
	}

	@Override
	public boolean isModified() {
		ResponseEntity<Map> resp = restTemplate.getForEntity(SCRIPT_BASE_URL + this.scriptName, Map.class);

		return lastModifiedDateTime == getLastModifiedFromHeaders(resp);
	}

	@Override
	public String suggestedClassName() {
		return StringUtils.stripFilenameExtension(this.scriptName);
	}

}
