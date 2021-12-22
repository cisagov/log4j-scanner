# safelog4j
[Announcement](https://www.contrastsecurity.com/security-influencers/instantly-inoculate-your-servers-against-log4j-with-new-open-source-tool)
[Primary Repository](https://github.com/Contrast-Security-OSS/safelog4j)
<p align="center"><b>
<br>
<br>
Safelog4j is an instrumentation-based security tool to help teams<br>
discover, verify, and solve log4shell without scanning or upgrading
<br>
<br>
</b></p>

If you're wrestling with log4shell [CVE-2021-45046](https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2021-45046), the best longterm plan is to upgrade your log4j to the latest secure version.  But if you can't do that for whatever reason, you probably want to be *really* sure that you have a problem and an easy way to fix it.

Safelog4j:
* accurately discovers the use of log4j
* verifies that the log4shell vulnerability is actually present and exploitable
* prevents the log4shell vulnerability from being exploited

Safelog4j doesn't rely on version numbers or filenames. Instead, it instruments the application to find log4j and perform an internal test to prove the app is exploitable (check). Safelog4j also uses instrumentation to disable the JNDI lookup code used by the attack (block). This is the most effective way to inoculate an otherwise vulnerable application or API.

![safelog4j-screenshot](https://github.com/Contrast-Security-OSS/safelog4j/blob/main/resources/safelog4j-screenshot.png?raw=true)


## Why should you use an instrumentation-based approach to log4shell (and other security challenges)

Instrumentation has been around for decades, is widely used in performance tools, debugging and profiling, and app frameworks. Many security tools scan from the 'outside-in' and don't have the full context of the running application.  This leads to false-positives, false-negatives, and long scan times.

Instrumentation allows us to do security analysis from within the running application - by watching the code run.  Directly measuring security from within the running code has speed, coverage, and accuracy benefits.  Using instrumentation to analyze for vulnerabilities is often called IAST (Interactive Application Security Testing). Using instrumentation to identify attacks and prevent exploit is often called RASP (Runtime Application Self-Protection).

Safelog4j provides both IAST and RASP capabilities focused on a single vulnerability: log4shell. IAST verifies that the vulnerability is present and actually exploitable.  RASP prevents it from being exploited.  IAST and RASP can be used for a broad range of vulnerabilities and attacks.  Please reach out if you're interested in applying these techniques to new security chaallenges.

Remember, you may be getting false results from other approaches. Scanning file systems, code repos, or containers could easily fail to detect log4j accurately. Determining exploitability by attempting to test, scan, or fuzz for log4shell is even more inaccurate, requiring exactly right input with the exactly right syntax.

* log4j could be buried in a fat jar, war, or ear
* log4j could be shaded in another jar
* log4j could be included in the appserver, not the code repo
* log4j could be part of dynamically loaded code or plugin
* log4j could be many different versions with different classloaders in a single app
* log4j could be masked by use of slf4j or other layers
* log4j could be renamed, recompiled, or otherwise changed


## Launching a JVM with safelog4j...

Basically you just have to get the latest [safelog4j-1.0.3.jar](https://github.com/Contrast-Security-OSS/safelog4j/releases/download/v1.0.3/safelog4j-1.0.3.jar) and then tell the JVM to use it with the -javaagent flag.

  ```shell
  curl -O https://github.com/Contrast-Security-OSS/safelog4j/releases/download/v1.0.3/safelog4j-1.0.3.jar
  java -javaagent:safelog4j-1.0.3.jar=[check|block|both|none] -jar yourjar.jar
  ```
  -or-
  ```
  curl -O https://github.com/Contrast-Security-OSS/safelog4j/releases/download/v1.0.3/safelog4j-1.0.3.jar
  JAVA_TOOL_OPTIONS=-javaagent:/path/to/safelog4j-1.0.3.jar=[check|block|both|none]
  java -jar yourjar.jar
  ```

## Attaching to a running JVM with safelog4j...

  ```shell
  curl -O https://github.com/Contrast-Security-OSS/safelog4j/releases/download/v1.0.3/safelog4j-1.0.3.jar
  java -javaagent:safelog4j-1.0.3.jar     # will print available JVM processes with PID
  java -javaagent:safelog4j-1.0.3.jar PID [check|block|both|none]
  ```


## Safelog4j Options

* **CHECK** means that safelog4j will actually test every log4j instance for log4shell. This is done by generating a synthetic log message and a sensor to detect it in the vulnerable JndiLookup class within log4j. This is iron clad evidence the application will be exploitable if the application ever logs untrusted data (HTTP header, cookie, parameter, form field, multipart, or any other source of untrusted data.

* **BLOCK** means that safelog4j will stub out all the methods in the vulnerable log4j JndiLookup class.  This is the recommended approach to ensure that log4j can't be exploited. It is harmless, except for the total prevention of this attack.

* **BOTH** simply means that both CHECK and BLOCK will occur.

* **NONE** disables both CHECK and BLOCK, allowing you to keep the agent in place but completely disabled.


## Building and Contributing

We welcome pull requests and issues. Thanks!

   ```shell
   git clone 
   mvn clean install
   java -jar target/safelog4j-x.x.x.jar
   ``` 


## License

This software is licensed under the Apache 2 license

Copyright 2021 Contrast Security - https://contrastsecurity.com

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
