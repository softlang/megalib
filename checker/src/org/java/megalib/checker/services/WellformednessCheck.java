package org.java.megalib.checker.services;

import java.io.File;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.java.megalib.models.Function;
import org.java.megalib.models.MegaModel;
import org.java.megalib.models.Relation;

public class WellformednessCheck {

    private MegaModel model;
    private List<String> warnings;
    private boolean nocon; // disable checks that need internet connection

    public WellformednessCheck(MegaModel model){
        doChecks(model, false);
    }

    public WellformednessCheck(MegaModel model, boolean nocon){
        doChecks(model, nocon);
    }

    private void doChecks(MegaModel model, boolean nocon) {
        this.model = model;
        warnings = new LinkedList<>();
        this.nocon = nocon;
        if(!nocon){
            initializeTrustManagement();
        }

        instanceChecks();
        subtypeChecks();
        partOfCheck();
        fragmentPartOfCheck();
        partOfFragmentCheck();
        transientIsInputOrOutput();
        cyclicSubtypingChecks();
        cyclicRelationChecks("subsetOf");
        cyclicRelationChecks("partOf");
        cyclicRelationChecks("conformsTo");
        model.getFunctionDeclarations().keySet().forEach(f -> checkFunction(f));
        checkLinks();
    }

    private void instanceChecks() {
        Map<String,String> map = model.getInstanceOfMap();
        map.keySet().stream().filter(e -> !e.startsWith("?")).forEach(e -> concreteInstanceChecks(e));
        map.keySet().stream().filter(e -> model.isInstanceOf(e, "Artifact")).forEach(e -> {
            if(!model.getElementOfMap().containsKey(e)){
                warnings.add("Language missing for artifact " + e);
            }
            if(!model.getRelationships().containsKey("manifestsAs")
               || model.getRelationships().get("manifestsAs").parallelStream()
                       .noneMatch(r -> r.getSubject().equals(e))){
                warnings.add("Manifestation missing for " + e);
            }
        });
    }

    private void concreteInstanceChecks(String e) {
        Map<String,String> map = model.getInstanceOfMap();
        if(map.get(e).equals("Technology")){
            warnings.add("State a specific subtype of Technology for " + e + ".");
        }
        if(model.isInstanceOf(e, "Technology")){
            Optional<Set<Relation>> o = Optional.ofNullable(model.getRelationships().get("uses"))
                    .filter(set -> set.parallelStream()
                                      .anyMatch(r -> r.getSubject().equals(e)
                                                      && model.isInstanceOf(r.getObject(), "Language")));
            if(!o.isPresent()){
                warnings.add("State a used language for " + e);
            }
        }
        if(map.get(e).equals("Language")){
            warnings.add("State a specific subtype of Language for " + e + ".");
        }

        if(model.isInstanceOf(e, "Artifact")){
            Optional<Set<Relation>> o = Optional.ofNullable(model.getRelationships().get("hasRole"))
                    .filter(set -> set.parallelStream().anyMatch(r -> r.getSubject().equals(e)));
            if(!o.isPresent()){
                warnings.add("Role missing for " + e + ".");
            }

            long bindcount = Optional.ofNullable(model.getRelationships().get("~="))
                                     .map(set -> set.parallelStream().filter(r -> r.getSubject().equals(e)).count())
                                     .orElse(Integer.toUnsignedLong(0));
            if(bindcount < 1){
                warnings.add("Binding missing for Artifact " + e + ".");
            }
            if(bindcount > 1){
                warnings.add("More than 1 binding for Artifact " + e + ".");
            }
        }else{
            Optional<Set<Relation>> o = Optional.ofNullable(model.getRelationships().get("="))
                                                .filter(set -> set.parallelStream()
                                                                  .anyMatch(r -> r.getSubject().equals(e)
                                                                                 || model.isInstanceOf(e, "Function")));
            if(!o.isPresent()){
                warnings.add("Link missing for entity " + e + ".");
            }
            o = Optional.ofNullable(model.getRelationships().get("~="))
                        .filter(set -> set.parallelStream().anyMatch(r -> r.getSubject().equals(e)));
            if(o.isPresent()){
                warnings.add("Do not bind entities other than artifacts. See " + e + ".");
            }
        }
    }

    private void subtypeChecks() {
        model.getSubtypesMap().forEach((k, v) -> {
            if(model.getRelationships().get("=").parallelStream().noneMatch(r -> r.getSubject().equals(k))){
                warnings.add("Link missing for subtype " + k);
            }
        });
    }

    /**
     * Every subject of a part-hood relationship cannot be subject of another
     * part-hood relationship.
     */
    private void partOfCheck() {
        if(!model.getRelationships().containsKey("partOf"))
            return;
        Set<Relation> partOfs = model.getRelationships().get("partOf");
        Set<String> subjects = new HashSet<>();
        for(Relation p : partOfs){
            if(!subjects.contains(p.getSubject())){
                subjects.add(p.getSubject());
            }else{
                warnings.add("Error at " + p.getSubject() + " partOf " + p.getObject() + ": " + "" + p.getSubject()
                             + " is already part of another composite.");
            }
        }
    }

    /**
     * All artifacts that manifest as fragments are part of another artifact
     * that is not a fragment.
     */
    private void fragmentPartOfCheck() {
        if(!model.getRelationships().containsKey("manifestsAs"))
            return;
        List<String> fragments = model.getRelationships().get("manifestsAs").parallelStream()
                                      .filter(r -> r.getObject().equals("Fragment")).map(r -> r.getSubject())
                                      .collect(Collectors.toList());
        for(String f : fragments){
            if(model.getRelationships().get("partOf").parallelStream()
                    .noneMatch(r -> r.getSubject().equals(f))){
                warnings.add("Composite missing for fragment " + f);
            }
        }
    }

    /**
     * All composite fragments do not have any part that is not a fragment.
     */
    private void partOfFragmentCheck() {
        if(!model.getRelationships().containsKey("manifestsAs"))
            return;
        Set<String> fset = model.getRelationships().get("manifestsAs").parallelStream()
                                .filter(r -> r.getObject().equals("Fragment")).map(r -> r.getSubject())
                                .collect(Collectors.toSet());
        for(String f : fset){
            Set<String> parts = model.getRelationships().get("partOf").parallelStream()
                                     .filter(r -> r.getObject().equals(f)).map(r -> r.getSubject())
                                     .collect(Collectors.toSet());
            Set<String> diff = new HashSet<>();
            diff.addAll(parts);
            diff.removeAll(fset);
            if(!parts.isEmpty() && !diff.isEmpty()){
                warnings.add("The following parts of the fragment " + f + " are not fragments and are thus invalid:"
                             + diff.toString());
            }
        }
    }

    private void transientIsInputOrOutput() {
        if(!model.getRelationships().containsKey("manifestsAs"))
            return;

        Set<String> tset = model.getRelationships().get("manifestsAs").parallelStream()
                                .filter(r -> r.getObject().equals("Transient") && !isPart(r.getSubject()))
                                .map(r -> r.getSubject()).collect(Collectors.toSet());
        Set<String> iovalues = new HashSet<>();
        Set<Function> pairs = new HashSet<>();
        model.getFunctionApplications().forEach((k, v) -> pairs.addAll(v));
        for(Function p : pairs){
            iovalues.addAll(p.getInputs());
            iovalues.addAll(p.getOutputs());
        }
        tset.removeAll(iovalues);
        if(!tset.isEmpty()){
            warnings.add("The following transients are neither input nor output of a function application: "
                         + tset.toString());
        }
    }

    private boolean isPart(String t) {
        if(!model.getRelationships().containsKey("partOf"))
            return false;
        Set<Relation> partOfs = model.getRelationships().get("partOf");
        return !partOfs.parallelStream().noneMatch(r -> r.getSubject().equals(t));
    }

    private void cyclicSubtypingChecks() {
        Map<String,String> map = new HashMap<>();
        map.putAll(model.getSubtypesMap());
        while(true){
            Set<String> subtypeset = map.keySet();
            Set<String> typeset = map.values().stream().collect(Collectors.toSet());
            Set<String> diff = new HashSet<>(subtypeset);
            diff.removeAll(typeset);
            if(diff.isEmpty()){
                break;
            }
            for(String t : diff){
                map.remove(t);
            }
        }
        if(!map.isEmpty()){
            warnings.add("Cycles exist in the subtyping hierarchy within the following entries :" + map);
        }
    }

    private void cyclicRelationChecks(String name) {
        if(!model.getRelationships().containsKey(name))
            return;
        Set<Relation> rels = model.getRelationships().get(name);
        Set<String> subjects;
        Set<String> objects;

        while(true){
            subjects = rels.parallelStream().map(r -> r.getSubject()).collect(Collectors.toSet());
            objects = rels.parallelStream().map(r -> r.getObject()).collect(Collectors.toSet());
            Set<String> diff = new HashSet<>(subjects);
            diff.removeAll(objects);
            if(diff.isEmpty()){
                break;
            }
            rels = rels.parallelStream().filter(r -> !diff.contains(r.getSubject())).collect(Collectors.toSet());
        }
        Set<String> result = new HashSet<>(objects);
        result.retainAll(subjects);
        if(!result.isEmpty()){
            warnings.add("Cycles exist concerning the relationship " + name + " involving the following entities :"
                         + result);
        }

    }

    private void checkFunction(String name) {
        // check implements existence
        Set<Relation> implementsSet = model.getRelationships().get("implements");
        if(null == implementsSet){
            warnings.add("The function " + name + " is not implemented. Please state what implements it.");
        }else{
            Set<Relation> fset = implementsSet.parallelStream().filter(r -> r.getObject().equals(name))
                                              .collect(Collectors.toSet());
            if(fset.isEmpty()){
                warnings.add("The function " + name + " is not implemented. Please state what implements it.");
            }
        }
        // check application existence
        if(!model.getFunctionApplications().containsKey(name)){
            warnings.add("The function " + name + " is not applied yet. Please state an actual application.");
        }
    }

    private void checkLinks() {
        Set<Relation> links = model.getRelationships().get("=");
        if(model.getRelationships().containsKey("~=")){
            links.addAll(model.getRelationships().get("~="));
        }
        links.parallelStream().forEach(r -> checkLinkWorking(r.getObject()));
    }

    private void checkLinkWorking(String link) {
        if(link.contains("::")){
            String ns = link.split("::")[0];
            String fulllink = model.getNamespace(ns);
            link = fulllink + "/" + link.split("::")[1];
        }
        if(link.startsWith("file://")){
            if(!new File(link.substring(7)).exists()){
                warnings.add("Cannot link to '" + link + "' : The file does not exist!");
            }
        }else{
            if(!nocon){
                try{
                    checkConnection(new URL(link), link);
                }catch(MalformedURLException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkConnection(URL u, String link) {
        HttpURLConnection huc;
        try{
            huc = (HttpURLConnection) u.openConnection();
        }catch(IOException e){
            warnings.add("Error at Link to '" + link + "' : The URL connection failed!");
            return;
        }
        try{
            huc.setRequestMethod("HEAD");
        }catch(ProtocolException e){
            warnings.add("Error at Link to '" + link + "' : ProtocolException!");
            return;
        }
        int tries = 5;
        while(tries > 0){
            try{
                if(huc.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND){
                    warnings.add("Error at Link to '" + link + "' : Link not working " + huc.getResponseCode());
                }
                break;
            }catch(IOException e){
                tries--;
            }
        }
        if(tries == 0){
            warnings.add("Error at Link to '" + link + "' : Connection failed!");
        }
        huc.disconnect();
    }

    private void initializeTrustManagement() {
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

    public List<String> getWarnings() {
        return warnings;
    }

}