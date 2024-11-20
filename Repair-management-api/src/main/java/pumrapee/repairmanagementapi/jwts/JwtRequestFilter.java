package pumrapee.repairmanagementapi.jwts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pumrapee.repairmanagementapi.exceptions.ErrorResponse;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String requestMethod = request.getMethod();
        String requestURI = request.getRequestURI();
        if (requestMethod.equalsIgnoreCase("GET") || requestURI.equals("/login") || requestURI.equals("/register")) {
            chain.doFilter(request, response);
            return;
        }
        try {
            final String requestTokenHeader = request.getHeader("Authorization");
            String username = null;
            String jwtToken = null;
            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                jwtToken = requestTokenHeader.substring(7);
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                setAuthenticationIfValid(request, username, jwtToken);
                chain.doFilter(request, response);
            } else {
                throw new BadCredentialsException("Authorization header missing");
            }
        } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | SignatureException | IllegalArgumentException ex) {
            buildErrorResponse(response, ex, HttpStatus.UNAUTHORIZED, request);
        }
    }

    private void setAuthenticationIfValid(HttpServletRequest request, String username, String jwtToken) {
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                setAuthentication(request, userDetails);
            }
        }
    }

    private void setAuthentication(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private void buildErrorResponse(HttpServletResponse response, Exception exception,
                                    HttpStatus httpStatus, HttpServletRequest request) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), exception.getMessage(), request.getRequestURI());
        response.setStatus(httpStatus.value());
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(errorResponse));
    }
}