package org.ringojs.test;

import org.ringojs.repository.Repository;
import org.ringojs.repository.Resource;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;


/**
 *  A Resource which can modified at runtime with setContent()
 */

public class MockResource implements Resource {
    private String content;
    private String name;

    public MockResource(String name, String content) {
        this.content = content;
        this.name = name;
    }

    public void setContent(String newContent) {
        content = newContent;
    }

    @Override
    public long getChecksum() throws IOException {
        return content.hashCode();
    }

    @Override
    public long getLength() {
        return content.length();
    }

    @Override
    public String getContent() throws IOException {
        return content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content.getBytes());
    }

    @Override
    public Reader getReader(String encoding) throws IOException {
        return null;
    }

    @Override
    public Reader getReader() throws IOException {
        return new StringReader(content);
    }

    @Override
    public String getBaseName() {
        return name;
    }

    @Override
    public String getRelativePath() {
        return null;
    }

    @Override
    public void setAbsolute(boolean absolute) {

    }

    @Override
    public boolean isAbsolute() {
        return false;
    }

    @Override
    public boolean getStripShebang() {
        return false;
    }

    @Override
    public void setStripShebang(boolean stripShebang) {

    }

    @Override
    public int getLineNumber() {
        return 0;
    }


    @Override
    public String getContent(String encoding) throws IOException {
        return getContent();
    }

    @Override
    public long lastModified() {
        return 0;
    }


    @Override
    public boolean exists() throws IOException {
        return true;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public URL getUrl() throws UnsupportedOperationException, MalformedURLException {
        return null;
    }

    @Override
    public Repository getParentRepository() {
        return null;
    }

    @Override
    public Repository getRootRepository() {
        return null;
    }

    @Override
    public String getModuleName() {
        return name;
    }
}