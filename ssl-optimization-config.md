# SSL I/O Bottleneck - Configuration Examples

## Undertow SSL Configuration

### Example 1: Optimized SSL Context Builder

```java
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.protocols.ssl.SslContext;
import org.xnio.Options;
import org.xnio.OptionMap;
import org.xnio.XNIO;
import org.xnio.XnioWorker;

public class OptimizedUndertowServer {
    
    public static void main(String[] args) {
        // Step 1: Create optimized SSL context
        SslContext sslContext = createOptimizedSslContext();
        
        // Step 2: Configure XNIO worker with optimal settings
        XnioWorker worker = createOptimizedWorker();
        
        // Step 3: Build Undertow server with SSL optimizations
        Undertow server = Undertow.builder()
            .addHttpsListener(8443, "0.0.0.0", sslContext)
            .setWorker(worker)
            .setServerOption(UndertowOptions.SSL_USER_CIPHER_SUITES_ORDER, true)
            .setServerOption(UndertowOptions.ENABLE_HTTP2, true)
            .setServerOption(UndertowOptions.ALWAYS_SET_DATE, true)
            .setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, true)
            .setHandler(createHttpHandler())
            .build();
            
        server.start();
    }
    
    private static SslContext createOptimizedSslContext() {
        return SslContext.builder()
            // Prefer TLS 1.3 (more efficient)
            .protocols("TLSv1.3", "TLSv1.2")
            
            // Use hardware-accelerated cipher suites
            .cipherSuites(
                // TLS 1.3 ciphers (most efficient)
                "TLS_AES_256_GCM_SHA384",
                "TLS_AES_128_GCM_SHA256",
                "TLS_CHACHA20_POLY1305_SHA256",
                
                // TLS 1.2 ciphers (ECDHE + AES-GCM for hardware acceleration)
                "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
                "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
                "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256"
            )
            
            // Enable SSL session caching (critical for performance)
            .sessionCacheSize(10000)        // Cache up to 10,000 sessions
            .sessionTimeout(3600)           // 1 hour timeout
            
            // Load your keystore
            .keyManager(createKeyManager())
            .trustManager(createTrustManager())
            
            .build();
    }
    
    private static XnioWorker createOptimizedWorker() {
        XNIO xnio = XNIO.getInstance();
        
        // Calculate optimal thread counts based on CPU cores
        int cpuCores = Runtime.getRuntime().availableProcessors();
        int ioThreads = cpuCores * 2;  // I/O threads (non-blocking)
        int workerThreads = cpuCores * 4;  // Worker threads (blocking operations)
        
        OptionMap.Builder builder = OptionMap.builder()
            // I/O thread configuration
            .set(Options.WORKER_IO_THREADS, ioThreads)
            
            // Worker thread pool for blocking operations
            .set(Options.WORKER_TASK_CORE_THREADS, workerThreads / 2)
            .set(Options.WORKER_TASK_MAX_THREADS, workerThreads)
            .set(Options.WORKER_TASK_KEEP_ALIVE, 60000)
            
            // TCP optimizations
            .set(Options.TCP_NODELAY, true)      // Disable Nagle's algorithm
            .set(Options.KEEP_ALIVE, true)       // Enable TCP keep-alive
            .set(Options.REUSE_ADDRESSES, true) // Reuse addresses
            
            // Timeouts
            .set(Options.READ_TIMEOUT, 30000)    // 30 seconds
            .set(Options.WRITE_TIMEOUT, 30000)   // 30 seconds
            .set(Options.CONNECTION_HIGH_WATER, 10000)
            .set(Options.CONNECTION_LOW_WATER, 1000);
            
        return xnio.createWorker(builder.getMap());
    }
}
```

## JVM System Properties for SSL Optimization

### Add to JVM startup arguments:

```bash
# Use OpenSSL provider if available (faster than default)
-Djava.security.properties=/path/to/security.properties

# Enable SSL session caching
-Djavax.net.ssl.sessionCacheSize=10000
-Djavax.net.ssl.sessionCacheTimeout=3600

# Use hardware-accelerated crypto (if available)
-Dcom.sun.net.ssl.enableECC=true

# SSL debugging (remove in production)
# -Djavax.net.debug=ssl:handshake:verbose

# JVM optimizations for SSL
-XX:+UseG1GC                    # Better for low latency
-XX:MaxGCPauseMillis=200        # Target GC pause time
-XX:+UseStringDeduplication     # Reduce memory usage
```

## Security Properties File

Create `/path/to/security.properties`:

```properties
# Enable OpenSSL provider (if Conscrypt is available)
ssl.SocketFactory.provider=org.conscrypt.OpenSSLSocketFactoryImpl
ssl.ServerSocketFactory.provider=org.conscrypt.OpenSSLServerSocketFactoryImpl

# Or use default with optimizations
ssl.SocketFactory.provider=sun.security.ssl.SSLSocketFactoryImpl
ssl.ServerSocketFactory.provider=sun.security.ssl.SSLServerSocketFactoryImpl
```

## Application-Level Optimizations

### Example: Connection Pooling with SSL Session Reuse

```java
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;

public class OptimizedHttpClient {
    
    public static CloseableHttpClient createOptimizedClient() throws Exception {
        // Create SSL context with session caching
        SSLContext sslContext = SSLContextBuilder.create()
            .loadTrustMaterial(null, (chain, authType) -> true)
            .build();
        
        // Configure connection pooling
        PoolingHttpClientConnectionManager connectionManager = 
            new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);           // Max total connections
        connectionManager.setDefaultMaxPerRoute(20);  // Max per route
        
        return HttpClients.custom()
            .setSSLContext(sslContext)
            .setConnectionManager(connectionManager)
            .evictIdleConnections(30, TimeUnit.SECONDS)
            .evictExpiredConnections()
            .build();
    }
}
```

## Monitoring SSL Performance

### Example: SSL Metrics Collection

```java
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class SSLMetrics {
    private static final LongAdder handshakeCount = new LongAdder();
    private static final LongAdder wrapOperations = new LongAdder();
    private static final LongAdder unwrapOperations = new LongAdder();
    private static final AtomicLong totalHandshakeTime = new AtomicLong();
    
    public static void recordHandshake(long durationMs) {
        handshakeCount.increment();
        totalHandshakeTime.addAndGet(durationMs);
    }
    
    public static void recordWrap() {
        wrapOperations.increment();
    }
    
    public static void recordUnwrap() {
        unwrapOperations.increment();
    }
    
    public static void printMetrics() {
        long handshakes = handshakeCount.sum();
        long avgHandshakeTime = handshakes > 0 ? 
            totalHandshakeTime.get() / handshakes : 0;
            
        System.out.println("SSL Metrics:");
        System.out.println("  Handshakes: " + handshakes);
        System.out.println("  Avg Handshake Time: " + avgHandshakeTime + "ms");
        System.out.println("  Wrap Operations: " + wrapOperations.sum());
        System.out.println("  Unwrap Operations: " + unwrapOperations.sum());
    }
}
```

## Quick Fix Checklist

- [ ] Enable SSL session caching (sessionCacheSize, sessionTimeout)
- [ ] Use TLS 1.3 and AES-GCM cipher suites
- [ ] Configure appropriate XNIO worker thread counts
- [ ] Enable HTTP/2 (more efficient than HTTP/1.1)
- [ ] Set TCP_NODELAY and KEEP_ALIVE options
- [ ] Increase SSL buffer sizes if possible
- [ ] Monitor SSL handshake frequency
- [ ] Consider using Conscrypt or OpenSSL provider
- [ ] Implement connection pooling at application level
- [ ] Profile with JProfiler/YourKit to identify hotspots

## Testing Performance Improvements

### Before Optimization:
- CPU Usage: 97.71%
- SSL Operation Time: 4397ms
- Handshakes per second: High

### After Optimization (Expected):
- CPU Usage: <50%
- SSL Operation Time: <2000ms
- Handshakes per second: Reduced (due to session reuse)

## Notes

1. **Session Caching is Critical**: The biggest performance gain comes from reusing SSL sessions instead of performing full handshakes.

2. **Cipher Suite Selection**: AES-GCM is hardware-accelerated on modern CPUs, making it much faster than CBC mode.

3. **TLS 1.3 Benefits**: TLS 1.3 reduces handshake overhead and improves performance compared to TLS 1.2.

4. **Thread Pool Sizing**: Balance I/O threads (non-blocking) with worker threads (blocking operations). Too many threads can cause context switching overhead.

5. **Monitoring**: Always monitor SSL metrics in production to validate improvements and catch regressions.
