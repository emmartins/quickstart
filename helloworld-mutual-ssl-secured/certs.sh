#!/bin/sh

# Generate a self-signed keystore with the specified DN fields
expect <<EOF
set timeout 10
spawn keytool -genkey -keystore client.keystore -storepass secret -validity 365 -keyalg RSA -keysize 2048 -storetype pkcs12

expect "What is your first and last name?"
send "quickstartUser\r"
expect "What is the name of your organizational unit?"
send "Sales\r"
expect "What is the name of your organization?"
send "My Organization\r"
expect "What is the name of your City or Locality?"
send "Sao Paulo\r"
expect "What is the name of your State or Province?"
send "Sao Paulo\r"
expect "What is the two-letter country code for this unit?"
send "BR\r"
expect "Is CN=quickstartUser, OU=Sales, O=My Organization, L=Sao Paulo, ST=Sao Paulo, C=BR correct?"
send "yes\r"
expect eof
EOF

# Export the certificate
keytool -exportcert -keystore client.keystore  -storetype pkcs12 -storepass secret -keypass secret -file client.crt

# Import the certificate into a truststore
expect <<EOF
set timeout 10
spawn keytool -import -file client.crt -alias quickstartUser -keystore client.truststore -storepass secret

expect "Trust this certificate? [no]: "
send "yes\r"
expect eof
EOF

# Create a new PKCS12 keystore with the same certificate
keytool -importkeystore -srckeystore client.keystore -srcstorepass secret -destkeystore clientCert.p12 -srcstoretype PKCS12 -deststoretype PKCS12 -deststorepass secret