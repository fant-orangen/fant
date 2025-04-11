package stud.ntnu.backend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * <h2>LoggingFilter</h2>
 * <p>Servlet filter that logs incoming HTTP requests.</p>
 * <p>This filter intercepts every incoming HTTP request and logs the HTTP method and the requested URI.</p>
 */
@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

  /**
   * <h3>doFilterInternal</h3>
   * <p>Logs the details of the incoming HTTP request and passes the request and response to the next filter in the chain.</p>
   *
   * @param request     the {@link HttpServletRequest} being processed.
   * @param response    the {@link HttpServletResponse} for the request.
   * @param filterChain the {@link FilterChain} for invoking the next filter or the resource.
   * @throws ServletException if the filter encounters a servlet-specific error.
   * @throws IOException      if an I/O error occurs during the processing of the request.
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    log.info("Incoming HTTP request: {} {}", request.getMethod(), request.getRequestURI());
    filterChain.doFilter(request, response);
  }
}