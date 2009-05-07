/****************************************************************************
 * Copyright (c) 1998-2009 AOL LLC. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ****************************************************************************/
package net.oauth.signature;

import java.io.IOException;
import junit.framework.TestCase;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthServiceProvider;

/**
 * @author zhang
 * @author John Kristian
 */
public class PkcsTest extends TestCase {

    protected static final String PEM_FILE_DIR = "net/oauth/signature/pem/";

    protected OAuthConsumer client;
    protected OAuthConsumer server;
    protected ClassLoader classLoader;

    @Override
    protected void setUp() throws Exception {
        OAuthServiceProvider provider = new OAuthServiceProvider("http://www.google.com/url1",
                "http://www.google.com/url2", "http://www.google.com/url3");
        client = new OAuthConsumer(provider.requestTokenURL, "google", null, provider);
        server = new OAuthConsumer(provider.requestTokenURL, "google", null, provider);
        classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null)
            classLoader = PkcsTest.class.getClassLoader();
    }

    private String readResource(String name) throws IOException {
        return new String(Echo.readAll(classLoader.getResourceAsStream(PEM_FILE_DIR + name)), "UTF-8");
    }

    /**
     * Sign with Private Key PKCS1 PEM file Verify with Certificate X509 PEM
     * file
     * 
     * @throws Exception
     */
    public void testPkcs1() throws Exception {
        client.setProperty(RSA_SHA1.PRIVATE_KEY, readResource("private_pkcs1.pem"));
        server.setProperty(RSA_SHA1.X509_CERTIFICATE, readResource("certificate_x509.pem"));
        RSA_SHA1SignatureTest.signAndVerify(client, server);
    }

    /**
     * Sign with Private Key PKCS8 PEM file Verify with Public Key X509 PEM file
     * 
     * @throws Exception
     */
    public void testPkcs8() throws Exception {
        client.setProperty(RSA_SHA1.PRIVATE_KEY, readResource("private_pkcs8.pem"));
        server.setProperty(RSA_SHA1.PUBLIC_KEY, readResource("public_x509.pem"));
        RSA_SHA1SignatureTest.signAndVerify(client, server);
    }
}
