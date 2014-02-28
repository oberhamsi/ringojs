
package org.ringojs.test;

import org.ringojs.repository.AbstractRepository;
import org.ringojs.repository.Repository;
import org.ringojs.repository.Resource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;


/**
 *  Allows adding resources at runtime
 */
public class MockRepository implements Repository {
    private Hashtable<String, Resource> resources = new Hashtable<String, Resource>();
    public MockRepository() {
    }
    public void addResource(Resource res) {
        resources.put(res.getModuleName(), res);
    }

    public Resource getResource(String name) {
        return resources.get(name);
    }

    public boolean exists() {
        return true;
    }

    @Override
    public String getPath() {
        return "";
    }

    @Override
    public String getName() {
        return "foo";
    }

    @Override
    public String getModuleName() {
        return "mock-repo";
    }

    @Override
    public String getRelativePath() {
        return "";
    }

    @Override
    public URL getUrl() throws UnsupportedOperationException, MalformedURLException {
        return null;
    }

    public AbstractRepository createChildRepository(String name) throws IOException {
        return null;
    }

    public AbstractRepository getParentRepository() {
        return null;
    }

    @Override
    public Repository getRootRepository() {
        return null;
    }

    @Override
    public void setAbsolute(boolean absolute) {
    }

    @Override
    public boolean isAbsolute() {
        return false;
    }

    public long lastModified() {
        return 0;
    }
    public long getChecksum() throws IOException {
        return 0;
    }
    public Resource lookupResource(String name) throws IOException {
        return null;
    }

    @Override
    public Resource[] getResources() throws IOException {
        return new Resource[0];
    }

    @Override
    public Resource[] getResources(boolean recursive) throws IOException {
        return new Resource[0];
    }

    @Override
    public Resource[] getResources(String resourcePath, boolean recursive) throws IOException {
        return new Resource[0];
    }

    @Override
    public Repository[] getRepositories() throws IOException {
        return new Repository[0];
    }

    @Override
    public Repository getChildRepository(String path) throws IOException {
        return null;
    }

    @Override
    public void setRoot() {
    }
}