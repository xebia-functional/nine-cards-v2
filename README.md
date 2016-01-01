# nine-cards-v2 
[![Codacy
Badge](https://api.codacy.com/project/badge/grade/473a0aeaf6734503b3b0f67a44b78887)](https://www.codacy.com)
[![Codacy
Badge](https://api.codacy.com/project/badge/coverage/473a0aeaf6734503b3b0f67a44b78887)](https://www.codacy.com)

Compile
======

To compile the project:

* Install sbt
* Configure the Android SDK on your computer
* Set `ANDROID_HOME` environment variable
* Publish our `sbt-s3` project in local (Follow the steps in `Publish sbt-s3 in local` section)
* Add `debug.properties` file (Follow the steps in `Add Debug Keys` section)
* Clone this GitHub project to your computer

* From project root directory run:

```
$ sbt
```

* Connect your phone and execute:

```
> run
```

Publish sbt-s3 in local
========

We have made some changes in `sbt-s3` project and you need publish it in local. Try that:

`git clone -b generate-link-version https://github.com/47deg/sbt-s3.git`

`cd sbt-s3 && sbt publishLocal && cd ..`

Add Debug Keys
========

You need to add a `debug.properties` file to the root project with the necessary keys to compile. The content should be:

```
appsly.url=****
appsly.appid=****
appsly.appkey=****
google_client_id=****
google_oauth_scopes=****
crashlytics.apikey=****
crashlytics.apisecret=****

```

Known errors in Ubuntu
========

If you are using `Ubuntu` it's possible that you have some problems and these comments can help you

##ProcessException

When you compile the project it's possible that you have this error:

`com.android.ide.common.process.ProcessException`

It's a problem in 64-bit system and you need to install the `ia32-libs`. You should install it the following next:

`sudo apt-get install lib32stdc++6 lib32z1`

More information [here](http://stackoverflow.com/questions/22701405/aapt-ioexception-error-2-no-such-file-or-directory-why-cant-i-build-my-grad)

##Launching IntelliJ from unity panel

If you are using IntelliJ from unity panel it's possible that the app don't find the `ANDROID_HOME` environment variable.

Unity launcher doesn't source the users environment from `.bashrc` and you shoud include the `ANDROID_HOME` in `/etc/environment` and IntelliJ will work fine