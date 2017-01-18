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

import org.java.megalib.models.Function;
import org.java.megalib.models.MegaModel;
import org.java.megalib.models.Relation;

public class Check {

    private MegaModel model;
    private List<String> warnings;
    private boolean nocon; // flag to disable checks that need internet
                           // connection

    public Check(MegaModel model) {
        doChecks(model, false);
    }

    public Check(MegaModel model, boolean nocon) {
        doChecks(model, nocon);
    }

    private void doChecks(MegaModel model, boolean nocon) {
        this.model = model;
        warnings = new LinkedList<>();
        this.nocon = nocon;
        if (!nocon) {
            initializeTrustManagement();
        }

        instanceChecks();
        subtypeChecks();
        partOfCheck();
        fragmentPartOfCheck();
        partOfFragmentCheck();
        languageDefinedOrImplemented();
        transientIsInputOrOutput();
        cyclicSubtypingChecks();
        cyclicRelationChecks("subsetOf");
        cyclicRelationChecks("partOf");
        cyclicRelationChecks("conformsTo");
        model.getFunctionDeclarations().keySet().forEach(f -> checkFunction(f));
        checkLinks();
    }

    private void instanceChecks() {
        Map<String, String> map = model.getInstanceOfMap();
        for (String inst : map.keySet()) {
            if (map.get(inst).equals("Technology")) {
                warnings.add("The entity " + inst
                             + " is underspecified. Please state a specific subtype of Technology.");
            }
            if (map.get(inst).equals("Language")) {
                warnings.add("The entity " + inst + " is underspecified. Please state a specific subtype of Language.");
            }
            if (!(inst.startsWith("?") || model.getLinkMap().containsKey(inst) || map.get(inst).equals("Function"))) {
                warnings.add("The entity " + inst + " misses a Link for further reading.");
            }
            if (model.isInstanceOf(inst, "Technology") && !inst.startsWith("?")) {
                Set<Relation> usesSet = model.getRelationshipInstanceMap().get("uses");
                if (null == usesSet) {
                    warnings.add("The technology " + inst + " does not use any language. Please state language usage.");
                    continue;
                }
                Set<Relation> fset = usesSet.parallelStream()
                                            .filter(r -> r.getSubject().equals(inst)
                                                         && model.isInstanceOf(r.getObject(), "Language"))
                                            .collect(Collectors.toSet());
                if (fset.isEmpty()) {
                    warnings.add("The technology " + inst + " does not use any language. Please state language usage.");
                }
            }
            if (model.isInstanceOf(inst, "Artifact")) {
                if (!model.getElementOfMap().containsKey(inst)) {
                    warnings.add("Language missing for artifact " + inst);
                }
                Set<Relation> manifestSet = model.getRelationshipInstanceMap().get("manifestsAs");
                if (null == manifestSet) {
                    warnings.add("Manifestation misssing for " + inst);
                    continue;
                }
                Set<Relation> fset = manifestSet.parallelStream().filter(r -> r.getSubject().equals(inst))
                                                .collect(Collectors.toSet());
                if (fset.isEmpty()) {
                    warnings.add("Manifestation missing for " + inst);
                }

                Set<Relation> roleSet = model.getRelationshipInstanceMap().get("hasRole");
                if (null == roleSet) {
                    warnings.add("Role misssing for " + inst);
                    continue;
                }
                fset = roleSet.parallelStream().filter(r -> r.getSubject().equals(inst)).collect(Collectors.toSet());
                if (fset.isEmpty()) {
                    warnings.add("Role misssing for " + inst);
                }
            }
        }
    }

    private void subtypeChecks() {
        model.getSubtypesMap().forEach((k, v) -> {
            if (!model.getLinkMap().containsKey(k)) {
                warnings.add("Link missing for subtype " + k);
            }
        });
    }

    /**
     * Every subject of a part-hood relationship cannot be subject of another
     * part-hood relationship.
     */
    private void partOfCheck() {
        if (!model.getRelationshipInstanceMap().containsKey("partOf"))
            return;
        Set<Relation> partOfs = model.getRelationshipInstanceMap().get("partOf");
        Set<String> subjects = new HashSet<>();
        for (Relation p : partOfs) {
            if (!subjects.contains(p.getSubject())) {
                subjects.add(p.getSubject());
            } else {
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
        if (!model.getRelationshipInstanceMap().containsKey("manifestsAs"))
            return;
        List<String> fragments = model.getRelationshipInstanceMap().get("manifestsAs").parallelStream()
                                      .filter(r -> r.getObject().equals("Fragment")).map(r -> r.getSubject())
                                      .collect(Collectors.toList());
        for (String f : fragments) {
            List<String> composites = model.getRelationshipInstanceMap().get("partOf").parallelStream()
                                           .filter(r -> r.getSubject().equals(f)).map(r -> r.getObject())
                                           .collect(Collectors.toList());
            if (composites.isEmpty()) {
                warnings.add("Composite missing for fragment " + f);
            } else {
                if (composites.size() > 1) {
                    warnings.add("Multiple composites for fragment " + f);
                } else {
                    continue;
                }
            }
        }
    }

    /**
     * All composite fragments do not have any part that is not a fragment.
     */
    private void partOfFragmentCheck() {
        if (!model.getRelationshipInstanceMap().containsKey("manifestsAs"))
            return;
        Set<String> fset = model.getRelationshipInstanceMap().get("manifestsAs").parallelStream()
                                .filter(r -> r.getObject().equals("Fragment")).map(r -> r.getSubject())
                                .collect(Collectors.toSet());
        for (String f : fset) {
            Set<String> parts = model.getRelationshipInstanceMap().get("partOf").parallelStream()
                                     .filter(r -> r.getObject().equals(f)).map(r -> r.getSubject())
                                     .collect(Collectors.toSet());
            Set<String> diff = new HashSet<>();
            diff.addAll(parts);
            diff.removeAll(fset);
            if (!parts.isEmpty() && !diff.isEmpty()) {
                warnings.add("The following parts of the fragment " + f + " are not fragments and are thus invalid:"
                             + diff.toString());
            }
        }
    }

    /**
     * Every language has to be defined or implemented.
     */
    private void languageDefinedOrImplemented() {
        Set<String> languages = model.getInstanceOfMap().keySet().parallelStream()
                                     .filter(i -> model.isInstanceOf(i, "Language")).collect(Collectors.toSet());
        for (String l : languages) {
            if (l.startsWith("?") || model.getSubsetOfMap().containsKey(l)) {
                continue;
            }
            boolean b = false;
            if (model.getRelationshipInstanceMap().containsKey("defines")) {
                for (Relation def : model.getRelationshipInstanceMap().get("defines"))
                    if (def.getObject().equals(l)) {
                        b = true;
                    }
            }
            if (b) {
                continue;
            }
            if (model.getRelationshipInstanceMap().containsKey("implements")) {
                for (Relation impl : model.getRelationshipInstanceMap().get("implements"))
                    if (impl.getObject().equals(l)) {
                        b = true;
                    }
            }
            if (!b) {
                warnings.add("State a defining artifact or an implementing technology for the language " + l + ".");
            }
        }
    }

    private void transientIsInputOrOutput() {
        if (!model.getRelationshipInstanceMap().containsKey("manifestsAs"))
            return;

        Set<String> tset = model.getRelationshipInstanceMap().get("manifestsAs").parallelStream()
                                .filter(r -> r.getObject().equals("Transient") && !isPart(r.getSubject()))
                                .map(r -> r.getSubject()).collect(Collectors.toSet());
        Set<String> iovalues = new HashSet<>();
        Set<Function> pairs = new HashSet<>();
        model.getFunctionApplications().forEach((k, v) -> pairs.addAll(v));
        for (Function p : pairs) {
            iovalues.addAll(p.getInputs());
            iovalues.addAll(p.getOutputs());
        }
        tset.removeAll(iovalues);
        if (!tset.isEmpty()) {
            warnings.add("The following transients are neither input nor output of a function application: "
                         + tset.toString());
        }
    }

    private boolean isPart(String t) {
        if (!model.getRelationshipInstanceMap().containsKey("partOf"))
            return false;
        Set<Relation> partOfs = model.getRelationshipInstanceMap().get("partOf");
        return !partOfs.parallelStream().noneMatch(r -> r.getSubject().equals(t));
    }

    private void cyclicSubtypingChecks() {
        Map<String, String> map = new HashMap<>();
        map.putAll(model.getSubtypesMap());
        while (true) {
            Set<String> subtypeset = map.keySet();
            Set<String> typeset = map.values().stream().collect(Collectors.toSet());
            Set<String> diff = new HashSet<>(subtypeset);
            diff.removeAll(typeset);
            if (diff.isEmpty()) {
                break;
            }
            for (String t : diff) {
                map.remove(t);
            }
        }
        if (!map.isEmpty()) {
            warnings.add("Cycles exist in the subtyping hierarchy within the following entries :" + map);
        }
    }

    private void cyclicRelationChecks(String name) {
        if (!model.getRelationshipInstanceMap().containsKey(name))
            return;
        Set<Relation> rels = model.getRelationshipInstanceMap().get(name);
        Set<String> subjects;
        Set<String> objects;

        while (true) {
            subjects = rels.parallelStream().map(r -> r.getSubject()).collect(Collectors.toSet());
            objects = rels.parallelStream().map(r -> r.getObject()).collect(Collectors.toSet());
            Set<String> diff = new HashSet<>(subjects);
            diff.removeAll(objects);
            if (diff.isEmpty()) {
                break;
            }
            rels = rels.parallelStream().filter(r -> !diff.contains(r.getSubject())).collect(Collectors.toSet());
        }
        Set<String> result = new HashSet<>(objects);
        result.retainAll(subjects);
        if (!result.isEmpty()) {
            warnings.add("Cycles exist concerning the relationship " + name + " involving the following entities :"
                         + result);
        }

    }

    private void checkFunction(String name) {
        // check implements existence
        Set<Relation> implementsSet = model.getRelationshipInstanceMap().get("implements");
        if (null == implementsSet) {
            warnings.add("The function " + name + " is not implemented. Please state what implements it.");
        } else {
            Set<Relation> fset = implementsSet.parallelStream().filter(r -> r.getObject().equals(name))
                                              .collect(Collectors.toSet());
            if (fset.isEmpty()) {
                warnings.add("The function " + name + " is not implemented. Please state what implements it.");
            }
        }
        // check application existence
        if (!model.getFunctionApplications().containsKey(name)) {
            warnings.add("The function " + name + " is not applied yet. Please state an actual application.");
        }
    }

    private void checkLinks() {
        Map<String, Set<String>> links = model.getLinkMap();
        links.values().parallelStream().forEach(linkset -> linkset.forEach(l -> checkLinkWorking(l)));
    }

    private void checkLinkWorking(String link) {
        URL u = checkURL(link);
        if (u != null && !nocon) {
            checkConnection(u, link);
        }
    }

    private void checkConnection(URL u, String link) {
        HttpURLConnection huc;
        try {
            huc = (HttpURLConnection) u.openConnection();
        }
        catch (IOException e) {
            warnings.add("Error at Link to '" + link + "' : The URL connection failed!");
            return;
        }
        try {
            huc.setRequestMethod("HEAD");
        }
        catch (ProtocolException e) {
            warnings.add("Error at Link to '" + link + "' : ProtocolException!");
            return;
        }
        int tries = 5;
        while (tries > 0) {
            try {
                if (huc.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    warnings.add("Error at Link to '" + link + "' : Link not working " + huc.getResponseCode());
                }
                break;
            }
            catch (IOException e) {
                tries--;
            }
        }
        if (tries == 0) {
            warnings.add("Error at Link to '" + link + "' : Connection failed!");
        }
        huc.disconnect();

    }

    private void initializeTrustManagement() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
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
        } };

        // Install the all-trusting trust manager

        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
        }
        catch (NoSuchAlgorithmException | KeyManagementException e1) {
            e1.printStackTrace();
        }

        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    private URL checkURL(String link) {
        URL u = null;
        try {
            u = new URL(link);
        }
        catch (MalformedURLException e) {
            warnings.add("Error at Link to '" + link + "' : The URL is malformed!");
        }
        return u;
    }

    public List<String> getWarnings() {
        return warnings;
    }

}