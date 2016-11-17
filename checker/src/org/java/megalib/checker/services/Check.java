package org.java.megalib.checker.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.java.megalib.models.MegaModel;
import org.java.megalib.models.Relation;

public class Check {

	private MegaModel model;
	private List<String> warnings;
	
	public Check(MegaModel model) {
		this.model = model;
		doChecks();
	}
	
	public List<String> getWarnings() {
		return warnings;
	}

	private void doChecks() {
		warnings = new LinkedList<String>();
		
		instanceChecks();
		cyclicSubtypingChecks();
		cyclicRelationChecks("subsetOf");
		cyclicRelationChecks("partOf");
		cyclicRelationChecks("conformsTo");
		model.getFunctionDeclarations().keySet().forEach(f -> checkFunction(f));
		checkLinks();
	}

	

	private void instanceChecks() {
		Map<String, String> map = model.getInstanceOfMap();
		for(String inst : map.keySet()){
			if(map.get(inst).equals("Technology"))
				warnings.add("The entity "+inst+" is underspecified. Please state a specific subtype of Technology.");
			if(map.get(inst).equals("Language"))
				warnings.add("The entity "+inst+" is underspecified. Please state a specific subtype of Language.");
			if(!(inst.startsWith("?")||model.getLinkMap().containsKey(inst)||map.get(inst).equals("Function")))
				warnings.add("The entity "+inst+" misses a Link for further reading.");
			if(model.isInstanceOf(inst, "Technology")){
				Set<Relation> usesSet = model.getRelationshipInstanceMap().get("uses");
				if(null==usesSet){
					warnings.add("The technology "+inst+" does not use any language. Please state language usage.");
					continue;
				}
				Set<Relation> fset = usesSet.parallelStream()
						.filter(r->r.getSubject().equals(inst)
							   	&&model.isInstanceOf(r.getObject(),"Language"))
											   .collect(Collectors.toSet());
				if(fset.isEmpty())
					warnings.add("The technology "+inst+" does not use any language. Please state language usage.");
			}
			if(model.isInstanceOf(inst, "Artifact")){
				if(!model.getElementOfMap().containsKey(inst)){
					warnings.add("Language missing for artifact "+inst);
				}
				Set<Relation> manifestSet = model.getRelationshipInstanceMap().get("manifestsAs");
				if(null==manifestSet){
					warnings.add("Manifestation misssing for "+inst);
					continue;
				}
				Set<Relation> fset = manifestSet.parallelStream()
											   .filter(r->r.getSubject().equals(inst))
											   .collect(Collectors.toSet());
				if(fset.isEmpty())
					warnings.add("Manifestation misssing for "+inst);
				
				Set<Relation> roleSet = model.getRelationshipInstanceMap().get("hasRole");
				if(null==roleSet){
					warnings.add("Role misssing for "+inst);
					continue;
				}
				fset = roleSet.parallelStream()
											   .filter(r->r.getSubject().equals(inst))
											   .collect(Collectors.toSet());
				if(fset.isEmpty())
					warnings.add("Role misssing for "+inst);
			}
		}
	}
	
	private void cyclicSubtypingChecks() {
		Map<String, String> map = new HashMap<>();
		map.putAll(model.getSubtypesMap());
		while(true){
			Set<String> subtypeset = map.keySet();
			Set<String> typeset = map.values().stream().collect(Collectors.toSet());
			Set<String> diff = new HashSet<String>(subtypeset);
			diff.removeAll(typeset);
			if(diff.isEmpty())
				break;
			for(String t : diff){
				map.remove(t);
			}
		}
		if(!map.isEmpty()){
			warnings.add("Cycles exist in the subtyping hierarchy within the following entries :"+map);
		}
	}
	
	private void cyclicRelationChecks(String name) {
		if(!model.getRelationshipInstanceMap().containsKey(name))
			return;
		Set<Relation> rels = model.getRelationshipInstanceMap().get(name);
		Set<String> subjects;
		Set<String> objects;
		
		while(true){
			subjects = rels.parallelStream().map(r -> r.getSubject()).collect(Collectors.toSet());
			objects = rels.parallelStream().map(r -> r.getObject()).collect(Collectors.toSet());
			Set<String> diff = new HashSet<String>(subjects);
			diff.removeAll(objects);
			if(diff.isEmpty())
				break;
			rels = rels.parallelStream().filter(r -> !diff.contains(r.getSubject())).collect(Collectors.toSet());
		}
		Set<String> result = new HashSet<>(objects);
		result.retainAll(subjects);
		if(!result.isEmpty()){
			warnings.add("Cycles exist concerning the relationship "+name+" involving the following entities :"+result);
		}
		
	}
	
	private void checkFunction(String name){
		//check implements existence
		Set<Relation> implementsSet = model.getRelationshipInstanceMap().get("implements");
		if(null==implementsSet){
			warnings.add("The function "+name+" is not implemented. Please state what implements it.");
		}else{
			Set<Relation> fset = implementsSet.parallelStream()
									   .filter(r->r.getObject().equals(name))
									   .collect(Collectors.toSet());
			if(fset.isEmpty())
				warnings.add("The function "+name+" is not implemented. Please state what implements it.");
		}
		//check application existence
		if(!model.getFunctionApplications().containsKey(name)){
			warnings.add("The function "+name+" is not applied yet. Please state an actual application.");
		}
	}

	private void checkLinks() {
		Map<String, List<String>> links = model.getLinkMap();
		for (String name : links.keySet()) {
			links.get(name).forEach(l->checkLinkWorking(l));
		}
	}
	
	private void checkLinkWorking(String l) {
		URL u ;
		try {
			u = new URL(l);
		} catch (MalformedURLException e) {
			warnings.add("Error at Link to '"+l+"' : The URL is malformed!");
			return;
		}
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
		     public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		         java.security.cert.X509Certificate[] chck = null;
		         return chck;
		     }
		     public void checkClientTrusted(
		             java.security.cert.X509Certificate[] arg0, String arg1)
		                     throws java.security.cert.CertificateException {}
		     public void checkServerTrusted(
		             java.security.cert.X509Certificate[] arg0, String arg1)
		                     throws java.security.cert.CertificateException {}
		 } };

		 // Install the all-trusting trust manager
		 
		     SSLContext sc = null;
			try {
				sc = SSLContext.getInstance("TLS");
				sc.init(null, trustAllCerts, new SecureRandom());
			} catch (NoSuchAlgorithmException | KeyManagementException e1) {
				e1.printStackTrace();
			}
		     
		     HttpsURLConnection
		     .setDefaultSSLSocketFactory(sc.getSocketFactory());
		 
		HttpURLConnection huc;
		try {
			huc = (HttpURLConnection) u.openConnection();
		} catch (IOException e) {
			warnings.add("Error at Link to '"+l+"' : The URL connection failed!");
			return;
		}
		try {
			huc.setRequestMethod("HEAD");
		} catch (ProtocolException e) {
			warnings.add("Error at Link to '"+l+"' : ProtocolException!");
			return;
		}
		int tries = 5;
		while(tries>0){
			try {
				if(!(huc.getResponseCode()==HttpURLConnection.HTTP_OK)){
					warnings.add("Error at Link to '"+l+"' : Link not working "+huc.getResponseCode());
				}
				break;
			} catch (IOException e) {
				tries--;
			}
		}
		if(tries==0)
			warnings.add("Error at Link to '"+l+"' : Connection failed!");
		huc.disconnect();
		
	}
	
}