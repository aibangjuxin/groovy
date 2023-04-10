To flow iptable nat packets and get the source IP or domain host, you can use the following command:

iptables -t nat -A POSTROUTING -j LOG --log-prefix "NAT packet: " --log-level 4

This will log all NAT packets with a prefix of "NAT packet:" and a log level of 4. You can then check the system logs (usually found in /var/log/messages or /var/log/syslog) to see the logged information, which should include the source IP or domain host.