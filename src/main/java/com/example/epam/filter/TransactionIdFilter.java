package com.example.epam.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;
import java.util.UUID;

@Component
public class TransactionIdFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(TransactionIdFilter.class);

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String transactionId = UUID.randomUUID().toString();
        logger.info("Starting request, transactionId: {}, path: {}", transactionId, ((HttpServletRequest) request).getRequestURI());
        TransactionIdRequestWrapper wrappedRequest = new TransactionIdRequestWrapper((HttpServletRequest) request, transactionId);
        chain.doFilter(wrappedRequest, response);
    }

    private static class TransactionIdRequestWrapper extends HttpServletRequestWrapper {
        private final String transactionId;

        public TransactionIdRequestWrapper(HttpServletRequest request, String transactionId) {
            super(request);
            this.transactionId = transactionId;
        }

        public String getTransactionId() {
            return transactionId;
        }
    }
}