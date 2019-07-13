# yatspec-wiremock

[![](https://jitpack.io/v/alfanse/yatspec-wiremock.svg)](https://jitpack.io/#alfanse/yatspec-wiremock)

Helping render sequence diagrams from the requests / responses of:  
`com.github.tomakehurst.wiremock.WireMockServer`

### com.github.tomakehurst.wiremock.http.RequestListener 
implemented by `com.yatspec.wiremock.YatspecWiremockReqestListener`  
example test: `com.yatspec.wiremock.YatspecWiremockReqestListenerTest`  
example test output: [YatspecWiremockReqestListenerTest.html](documentation/YatspecWiremockReqestListenerTest.html)

### com.github.tomakehurst.wiremock.http.trafficlistener.WiremockNetworkTrafficListener
implemented by: `com.yatspec.wiremock.YatspecWiremockTrafficListener`  
example test: `com.yatspec.wiremock.YatspecWiremockTrafficListenerTest`  
example test output: testa output: [YatspecWiremockReqestListenerTest.html](documentation/YatspecWiremockReqestListenerTest.html)

# add as dependency
https://jitpack.io/#alfanse/yatspec-wiremock/0.1.0

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
	        implementation 'com.github.alfanse:yatspec-wiremock:0.1.0'
	}

# My Notes
How to publish the jar to jitpack, https://jitpack.io/docs/BUILDING    
Tag the build: https://git-scm.com/book/en/v2/Git-Basics-Tagging  
`$ git tag -a v1.4 -m "my version 1.4"`

push the tag  
`$ git push origin --tags`  

wait for jitpack to build, it uses `jitpack.yml`
