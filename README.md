# yatspec-wiremock

Helping render sequence diagrams from the requests / responses of a WireMockServer:  
`com.github.tomakehurst.wiremock.WireMockServer`

### com.github.tomakehurst.wiremock.http.RequestListener 
implemented by `com.yatspec.wiremock.YatspecWiremockReqestListener`  
example test: [com.yatspec.wiremock.YatspecWiremockReqestListenerTest](https://github.com/alfanse/yatspec-wiremock/blob/master/src/test/java/com/yatspec/wiremock/YatspecWiremockReqestListenerTest.java)  
example test output: [YatspecWiremockReqestListenerTest.html](documentation/YatspecWiremockReqestListenerTest.html)  

This one works well.

### com.github.tomakehurst.wiremock.http.trafficlistener.WiremockNetworkTrafficListener
implemented by: `com.yatspec.wiremock.YatspecWiremockTrafficListener`  
example test: [com.yatspec.wiremock.YatspecWiremockTrafficListenerTest](https://github.com/alfanse/yatspec-wiremock/blob/master/src/test/java/com/yatspec/wiremock/YatspecWiremockTrafficListenerTest.java)  
example test output: [YatspecWiremockReqestListenerTest.html](documentation/YatspecWiremockReqestListenerTest.html)

This one is clumsy and only works for serial requests, has problems with chunked responses.

# add as dependency

[![](https://jitpack.io/v/alfanse/yatspec-wiremock.svg)](https://jitpack.io/#alfanse/yatspec-wiremock)

Step 1. Add the JitPack repository to your build file  
Add it in your root build.gradle at the end of repositories:  

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.alfanse:yatspec-wiremock:0.1.1'
	}

# My Notes
How to publish the jar to jitpack, https://jitpack.io/docs/BUILDING    
Tag the build: https://git-scm.com/book/en/v2/Git-Basics-Tagging  
`$ git tag -a v1.4 -m "my version 1.4"`

push the tag  
`$ git push origin --tags`  

wait for jitpack to build, it uses `jitpack.yml`
