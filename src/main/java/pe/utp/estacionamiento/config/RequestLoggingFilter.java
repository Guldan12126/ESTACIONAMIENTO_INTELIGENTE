package pe.utp.estacionamiento.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long started = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long elapsed = System.currentTimeMillis() - started;
            int status = response.getStatus();
            String path = request.getRequestURI();

            if (status >= 500) {
                log.error("Solicitud con error metodo={} path={} status={} tiempoMs={}",
                        request.getMethod(), path, status, elapsed);
            } else if (status >= 400) {
                log.warn("Solicitud advertencia metodo={} path={} status={} tiempoMs={}",
                        request.getMethod(), path, status, elapsed);
            } else {
                log.info("Solicitud atendida metodo={} path={} status={} tiempoMs={}",
                        request.getMethod(), path, status, elapsed);
            }
        }
    }
}
