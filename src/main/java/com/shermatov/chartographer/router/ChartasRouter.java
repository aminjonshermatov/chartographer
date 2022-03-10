package com.shermatov.chartographer.router;

import com.shermatov.chartographer.exception.BadRequestException;
import com.shermatov.chartographer.exception.ChartaNotFoundException;
import com.shermatov.chartographer.handler.ChartasHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.WebFilter;

import static com.shermatov.chartographer.constants.ChartasConstants.CHARTAS_ENDPOINT;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ChartasRouter {

    @Bean
    public RouterFunction<ServerResponse> chartaRoute(ChartasHandler chartasHandler) {
        return RouterFunctions
                // POST /chartas/?width={width}&height={height}
                .route(
                        POST(CHARTAS_ENDPOINT),
                        chartasHandler::createCharta
                )
                // GET /chartas/{id}/?x={x}&y={y}&width={width}&height={height}
                .andRoute(
                        GET(CHARTAS_ENDPOINT + "/{id}"),
                        chartasHandler::getCharta
                )
                // `DELETE /chartas/{id}/`
                .andRoute(
                        DELETE(CHARTAS_ENDPOINT + "/{id}"),
                        chartasHandler::deleteCharta
                );
    }

    @Bean
    WebFilter chartaNotFoundExceptionHandler() {
        return ((exchange, chain) -> chain.filter(exchange)
                .onErrorResume(ChartaNotFoundException.class, ignore_ -> {
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(ChartaNotFoundException.httpStatus);
                    return response.setComplete();
                }));
    }

    @Bean
    WebFilter badRequestExceptionHandler() {
        return (((exchange, chain) -> chain.filter(exchange)
                .onErrorResume(BadRequestException.class, ignore_ -> {
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(BadRequestException.httpStatus);
                    return response.setComplete();
                })));
    }

}
