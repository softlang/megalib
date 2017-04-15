package org.java.megalib.checker.services;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.java.megalib.models.MegaModel;

public class Linker {

	
	
	protected static boolean isResolvable(String link, MegaModel model){
		if(link.contains("::")){
            String ns = link.split("::")[0];
            link = link.replace(ns + "::", model.getNamespace(ns) + "/");
        }
        if(link.startsWith("file://")){
        	return isValidFilePath(link);
            
        }else if(link.startsWith("http")){
        	initializeTrustManagement();
            try{
                return isValidURL(new URL(link));
            }catch(MalformedURLException e){
                return false;
            }
        }
		
		return false;
	}
	
	private static boolean isValidFilePath(String link) {
		if(new File(link).exists())
				return true;
		else{
			String[] parts = link.split("\\.");
			if(parts.length==3){
				return true; //TODO: Implement linking to checker.jar/org/main/antlr/techdocgrammar/Megalib.g4
			}
		}
		return false;
	}

	private static boolean isValidURL(URL u) {
		boolean result = true;
        HttpURLConnection huc = null;
        try{
            huc = (HttpURLConnection) u.openConnection();
            huc.setRequestMethod("HEAD");
        }catch(IOException e){
            result = false;
        }
        int tries = 5;
        while(tries > 0){
            try{
                if(huc.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND){
                    System.err.println("----Error at Link to '" + u.toString() + "' : Link not working " + huc.getResponseCode());
                }
                break;
            }catch(IOException e){
                tries--;
            }
        }
        if(tries == 0){
            result = false;
        }
        huc.disconnect();
        return result;
    }

    private static void initializeTrustManagement() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                java.security.cert.X509Certificate[] chck = null;
                return chck;
            }

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] arg0,
                                           String arg1) throws java.security.cert.CertificateException {}

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] arg0,
                                           String arg1) throws java.security.cert.CertificateException {}
        }};

        // Install the all-trusting trust manager

        SSLContext sc = null;
        try{
            sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
        }catch(NoSuchAlgorithmException | KeyManagementException e1){
            e1.printStackTrace();
        }

        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
}
