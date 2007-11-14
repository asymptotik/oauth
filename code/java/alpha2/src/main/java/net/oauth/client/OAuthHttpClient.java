/*
 * Copyright 2007 Netflix, Inc.
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
 */

package net.oauth.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.oauth.OAuth;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;

/** Utility methods for an OAuth client based on the Jakarta Commons HTTP client. */
public class OAuthHttpClient {

    /**
         * Check whether a response indicates an OAuth problem. If so, throw an
         * OAuthProblemException.
         */
    public static void checkResponse(HttpMethod method) throws IOException,
	    OAuthProblemException {
	int statusCode = method.getStatusCode();
	if (statusCode != HttpStatus.SC_OK) {
	    OAuthProblemException problem = new OAuthProblemException();
	    problem.getParameters().put(OAuthProblemException.HTTP_STATUS_CODE,
		    new Integer(statusCode));
	    problem.getParameters().put(OAuthProblemException.HTTP_STATUS_TEXT,
		    method.getStatusText());
	    problem.getParameters().put("URL", method.getURI().toString());
	    problem.getParameters().putAll(
		    OAuth.newMap(getResponseParameters(method)));
	    throw problem;
	}
    }

    /**
         * Construct an OAuthMessage from the HTTP response, including
         * parameters from OAuth WWW-Authenticate headers and the body if it's
         * form encoded. The header parameters come first, followed by the ones
         * from the response body.
         */
    public static OAuthMessage getResponseMessage(HttpMethod method)
	    throws IOException {
	return new OAuthMessage(method.getName(), method.getURI().toString(),
		getResponseParameters(method));
    }

    /**
         * Gather all the response parameters, including OAuth WWW-Authenticate
         * headers and parameters from the body. The header parameters come
         * first, followed by the ones from the body.
         */
    private static List<OAuth.Parameter> getResponseParameters(HttpMethod method)
	    throws IOException {
	List<OAuth.Parameter> list = new ArrayList<OAuth.Parameter>();
	for (Header header : method.getResponseHeaders("WWW-Authenticate")) {
	    for (OAuth.Parameter parameter : OAuthMessage
		    .decodeAuthorization(header.getValue())) {
		if (!"realm".equalsIgnoreCase(parameter.getKey())) {
		    list.add(parameter);
		}
	    }
	}
	list.addAll(OAuth.decodeForm(method.getResponseBodyAsString()));
	return list;
    }

}