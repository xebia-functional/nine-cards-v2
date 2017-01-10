---
layout: docs
title: Authentication
section: docs
---
# Authentication and Authorization

The 9 Cards Backend Application authenticates almost all of the endpoints.

# Client Signup in the Backend

We'll now describe the process of interactions performed for a client (user and device) to sign-up in the NCBE.
In essence, this process is just a third-party authentication (the third party being the NCBE) following the [OAuth 2.0 protocol](https://developers.google.com/identity/protocols/OAuth2).
It consists of an interaction between the NCBE, the **Google Account Services** (GAC), and the **Client**, which is the 9 Cards Android application running from the user's device.

1. **Grant Google Account Permissions.**
   The *Client* sends a request to the GAC to open an authorization token. The request carries the user's `email` and the `deviceId`,
   which uniquely identifies the instance of the NineCardsClient issuing the request.
   If the request is accepted, the GAC responds with success and includes in the response a `tokenId`, which identifies a short-lived OAuth session within the GAC.

2. **Client signup in NCBE.**
   The *Client* sends a HTTP request to the NCBE's endpoint `POST {apiRoot}/login`, to register it.
   This request carries (as a JSON object in the body entity) three fields:
   * the `email` that serves as an external identifier for the user running the client
   * the `androidId`, that identifies the Android device in which the user is running the client application
   * the `tokenId` that the client received from the GAC in Step 1.

   If the request succeeds, the NCBE records the client's user and device and returns a response to the client app.
   The response carries (in a JSON object in the request body) two fields: a `sessionToken` and an `apiKey`.
   * The `sessionToken` is a unique identifier of the user within the NCBE instead of the user's email.
   * The `apiKey` is the private cryptographic key that the client uses after signup to authenticate in the NCBE.

3. **NCBE access to GAC.**
   To carry out the process of endpoint `POST {apiRoot}/login`, the NCBE communicates to the GAC to validate the `tokenId` from the request body.
   If the `tokenId` is validated, the GAC returns a successful response.
   
# User Authentication

All NCBE endpoints, except the one to read the API documentation and the one to signup,
carry out an authentication step to check that the client app sending the requests is acting for a registered user.
The information for user authentication is carried in the HTTP headers `X-Android-ID`, `X-Auth-Token`, and `X-Session-Token`.

* The `X-Android-ID` should give the `androidId` of the client's device. Note that, since the GAC process in the signup
  involves a user and device, it is the device itself and not just the user that should be signed up beforehand.

* The `X-Session-Token` should carry the user's `sessionToken` that the NCBE generated and gave to the client in Step 3 of the signup process.
  This value acts as a way to identify the user within the NCBE.

* The `X-Auth-Token` is a [Hash-based message authentication code](https://en.wikipedia.org/wiki/Hash-based_message_authentication_code), which is used for authenticating the request.
  It ensures that the client which is using the `sessionToken` is the one that is acting for that user.

The value of the `X-Auth-Token` header is computed as follows.
The *message* to be signed is just the full [URI](https://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.2.2) of the request, including protocol, host, port, path, and query.
The *cryptographic key* used is the user's `apiKey`, which the NCBE generates and gives to the client (with the `sessionToken`) at the end of the signup process.
The code is then calculated using the `sha512` hash function.
To calculate the value, you can use one of these methods:

* The command `openssl` can generate the HMAC digest of a message. For example, to digest the URI `http://localhost:8080/collections/a` with the key `foo`, you would run:

        echo -n "http://localhost:8080/collections/a" | openssl dgst -sha512 -hmac "foo" | awk '{print $2}'

* This [web page](http://www.freeformatter.com/hmac-generator.html) offers a graphic dialog to generate the digest.
  The request URL would be put into the `message` dialog, the user's `apiKey` would go into the `SecretKey` text box,
  and the algorithm would be "SHA512". The result would be the digest.

# Manually obtaining a TokenId

Sometimes, you may want to manually carry out the first step of the signup, which is the communication between the
Client and the Google Account Services.

Google provides an [`OAuth 2.0 Playground`](https://developers.google.com/oauthplayground/), which can be used to generate a Google ID Token.
We use it to generate an OAuth token that, within the Google API, allows us to read the information needed for the client.
This information is the user's email, so the scope used is `https://www.googleapis.com/auth/userinfo.email`.

1. Open the [OAuth 2.0 Playground page](https://developers.google.com/oauthplayground/).
   On this page, look in the lateral pane for the menu `Google+ API v1`, from this menu, mark the scope `https://www.googleapis.com/auth/userinfo.email`,
   and push the `Authorize APIs` button.
2. If you have several Google accounts stored in the browser, you may be asked to select one. You will then
   be presented with a Google permissions dialog, asking you to allow an application to _Read your email address_.
    Press _Allow_.
3. After pressing _Allow_, the playground page will change to a new view. The modified page shows you `Request/Response`.
   In the left pane there is a text field labeled _Authorization Code_, and a button labeled _Exchange authorization code for tokens_.
   Press this button. Google OAuth playground then generates a new token, which is shown in the API response in the right pane, in a field named as _id_token_.
   This _id_token_ identifies a session within the _Google Account Services_, which is to expire within the hour.
4. Copy the value of the _id_token_ generated. A request to login endpoint `POST {apiRoot}/login` should include a JSON object with the field `tokenId` in the body. The value of this field should be the _id_token_.