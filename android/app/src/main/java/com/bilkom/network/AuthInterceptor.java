package com.bilkom.network;

import java.io.IOException;
import okhttp3.*;
import com.bilkom.network.*;

/**
 * AuthInterceptor is an implementation of the {@link Interceptor} interface
 * that adds an Authorization header with a Bearer token to outgoing HTTP requests.
 * The token is retrieved from the {@link TokenProvider}.
 *
 * <p>This interceptor ensures that authenticated requests are sent to the server
 * by including the appropriate token in the request headers.</p>
 *
 * @author Sıla Bozkurt
 */
public class AuthInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = TokenProvider.getToken();
        Request request = chain.request();
        if (token != null && !token.isEmpty()) {
            request = request.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
        }
        return chain.proceed(request);
    }
}
