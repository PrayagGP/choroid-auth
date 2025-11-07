const http = require('http');
const https = require('https');
const url = require('url');

const PORT = 3001;
const TARGET_HOST = '25.7.141.58:8100';

// Create proxy server
const server = http.createServer((req, res) => {
    // Set CORS headers
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization, X-Requested-With');
    
    // Handle preflight OPTIONS request
    if (req.method === 'OPTIONS') {
        res.writeHead(200);
        res.end();
        return;
    }
    
    // Parse the request URL
    const parsedUrl = url.parse(req.url);
    
    // Create target URL
    const targetUrl = `http://${TARGET_HOST}${parsedUrl.path}`;
    
    console.log(`Proxying: ${req.method} ${req.url} -> ${targetUrl}`);
    
    // Create proxy request options
    const proxyOptions = {
        method: req.method,
        headers: {
            ...req.headers,
            host: TARGET_HOST
        }
    };
    
    // Remove hop-by-hop headers and problematic browser headers
    delete proxyOptions.headers['connection'];
    delete proxyOptions.headers['upgrade'];
    delete proxyOptions.headers['http2-settings'];
    delete proxyOptions.headers['te'];
    delete proxyOptions.headers['trailer'];
    delete proxyOptions.headers['proxy-authorization'];
    delete proxyOptions.headers['proxy-authenticate'];
    delete proxyOptions.headers['proxy-connection'];
    
    // Remove browser-specific headers that might cause 403
    delete proxyOptions.headers['origin'];
    delete proxyOptions.headers['referer'];
    delete proxyOptions.headers['user-agent'];
    delete proxyOptions.headers['accept-encoding'];
    delete proxyOptions.headers['accept-language'];
    delete proxyOptions.headers['sec-fetch-dest'];
    delete proxyOptions.headers['sec-fetch-mode'];
    delete proxyOptions.headers['sec-fetch-site'];
    delete proxyOptions.headers['sec-ch-ua'];
    delete proxyOptions.headers['sec-ch-ua-mobile'];
    delete proxyOptions.headers['sec-ch-ua-platform'];
    
    // Set simple headers like curl
    proxyOptions.headers['user-agent'] = 'curl/7.68.0';
    proxyOptions.headers['accept'] = '*/*';
    
    // Create the proxy request
    const proxyReq = http.request(targetUrl, proxyOptions, (proxyRes) => {
        // Set response headers
        res.writeHead(proxyRes.statusCode, {
            ...proxyRes.headers,
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
            'Access-Control-Allow-Headers': 'Content-Type, Authorization, X-Requested-With'
        });
        
        // Pipe the response
        proxyRes.pipe(res);
    });
    
    // Handle proxy request errors
    proxyReq.on('error', (err) => {
        console.error('Proxy request error:', err);
        res.writeHead(502, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({
            error: 'Bad Gateway',
            message: 'Failed to connect to target server',
            details: err.message
        }));
    });
    
    // Pipe the request body
    req.pipe(proxyReq);
});

// Handle server errors
server.on('error', (err) => {
    console.error('Server error:', err);
});

// Start the server
server.listen(PORT, () => {
    console.log(`CORS Proxy server running on http://localhost:${PORT}`);
    console.log(`Proxying requests to: http://${TARGET_HOST}`);
    console.log('Use this proxy URL in your frontend: http://localhost:3001/api/auth');
});