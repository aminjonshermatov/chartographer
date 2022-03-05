package com.shermatov.chartographer.router;

import com.shermatov.chartographer.handler.ChartasHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.shermatov.chartographer.constants.ChartasConstants.CHARTAS_ENDPOINT;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ChartasRouter {

    @Bean
    public RouterFunction<ServerResponse> chartaRoute(ChartasHandler chartasHandler) {
        return RouterFunctions
                .route(
                        POST(CHARTAS_ENDPOINT).and(accept(MediaType.APPLICATION_JSON)),
                        chartasHandler::createCharta
                )
                .andRoute(
                        GET(CHARTAS_ENDPOINT + "/{id}").and(accept(MediaType.APPLICATION_JSON)),
                        chartasHandler::getCharta
                );
    }

}
