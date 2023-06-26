Here are the steps to verify if your network connection supports QUIC:

1. Open a command prompt or terminal window on your computer.

2. Run the following command:

`curl -ILqo /dev/null https://www.google.com`

This will make a HEAD request to Google's website and show the response headers.

3. Look for the `Alt-Svc` header in the response. If QUIC is supported, it will list a QUIC alternative, like this:

`Alt-Svc: h3=":443"; ma=2592000; quic=51303431`

The presence of the `quic=` parameter indicates QUIC support.

4. Alternatively, you can run this command:

`curl -ILqv2o /dev/null --http3 https://www.google.com`

This will explicitly make a QUIC request to Google (using the --http3 flag in curl). If your connection supports QUIC, curl will complete the request successfully.

5. If you see an error like "No QUIC supported version", then your network connection does not support QUIC.

So in summary, look for either the Alt-Svc header with a quic parameter, or successful completion of a --http3 request in curl to determine if QUIC is supported on your network connection.

Hope this helps! Let me know if you have any other questions.
